package org.combat.cache;

import org.combat.cache.event.TestCacheEntryListener;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.spi.CachingProvider;
import javax.cache.expiry.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhangwei
 * @Description AbstractCacheTest
 * @Date: 2021/4/18 07:34
 */
public class AbstractCacheTest {

    CachingProvider cachingProvider;

    CacheManager cacheManager;

    MutableConfiguration<String, Integer> config;

    private TestCacheEntryListener<String, Integer> cacheEntryListener;

    Cache<String, Integer> cache;

    private String cacheName = "testCache";

    String key = "test-key";

    Integer value = 2;

    @Before
    public void init() {
        cachingProvider = Caching.getCachingProvider();
        cacheManager = cachingProvider.getCacheManager();
        cacheEntryListener = new TestCacheEntryListener();
        config = new MutableConfiguration<String, Integer>()
                .setTypes(String.class, Integer.class)
                .setReadThrough(true)
                .setWriteThrough(true)
                .setManagementEnabled(true)
                .setStatisticsEnabled(true)
                .addCacheEntryListenerConfiguration(cacheEntryListener);
        this.cache = cacheManager.createCache(cacheName, config);
    }

    @After
    public void clearCache() {
        cache.removeAll();
        Assert.assertFalse(cache.isClosed());
        cacheManager.destroyCache(cacheName);
        Assert.assertTrue(cache.isClosed());
        cache.close();
        Assert.assertTrue(cache.isClosed());
        Assert.assertThrows(IllegalStateException.class, () -> cache.put(key, value));
    }

    @Test
    public void testGetMetadata() {
        Assert.assertEquals("testCache", cache.getName());
        Assert.assertEquals(Caching.getCachingProvider().getCacheManager(), cache.getCacheManager());
    }

    @Test
    public void testGetConfiguration() {
        CompleteConfiguration<String, Integer> configuration = (CompleteConfiguration) cache.getConfiguration(Configuration.class);
        Assert.assertTrue(config.isReadThrough());
        Assert.assertTrue(config.isWriteThrough());
        Assert.assertTrue(config.isStatisticsEnabled());
        Assert.assertTrue(config.isManagementEnabled());
        Assert.assertEquals(String.class, configuration.getKeyType());
        Assert.assertEquals(Integer.class, configuration.getValueType());

        Assert.assertNotNull(config.getCacheEntryListenerConfigurations());
        Class<?> clazz = String.class;
        Assert.assertThrows(IllegalArgumentException.class, () -> cache.getConfiguration((Class<Configuration>) clazz));
    }

    @Test
    public void testContainsKeyAndPut() {
        Assert.assertFalse(cache.containsKey(key));
        cache.put(key, value);
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        Assert.assertTrue(cache.containsKey(key));
    }

    private void assertCacheEntryEvent(EventType eventType, String key, Integer value, Object oldValue) {
        CacheEntryEvent<String, Integer> event = getCacheEntryEvent();
        Assert.assertEquals(cache, event.getSource());
        Assert.assertEquals(eventType, event.getEventType());
        Assert.assertEquals(key, event.getKey());
        Assert.assertEquals(value, event.getValue());
        Assert.assertEquals(oldValue, event.getOldValue());
    }

    private CacheEntryEvent<String, Integer> getCacheEntryEvent() {
        return cacheEntryListener.getCacheEntryEvent();
    }

    @Test
    public void testPutIfAbsent() {
        Assert.assertTrue(cache.putIfAbsent(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        Assert.assertFalse(cache.putIfAbsent(key, value));
        Assert.assertNull(getCacheEntryEvent());
    }

    @Test
    public void testPutAll() {
        cache.putAll(Collections.singletonMap(key, value));
        Assert.assertTrue(cache.containsKey(key));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        Assert.assertNull(getCacheEntryEvent());
    }

    @Test
    public void testGetOps() {
        Assert.assertNull(cache.get(key));

        Assert.assertNull(cache.getAndPut(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        Assert.assertEquals(value, cache.getAndPut(key, value));
        assertCacheEntryEvent(EventType.UPDATED, key, value, value);

        Assert.assertEquals(value, cache.getAndRemove(key));
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);

        Assert.assertNull(cache.getAndReplace(key, value));
        Assert.assertNull(getCacheEntryEvent());
        Assert.assertNull(cache.getAndPut(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        Assert.assertEquals(value, cache.getAndReplace(key, 1));
        assertCacheEntryEvent(EventType.UPDATED, key, 1, value);

        Assert.assertEquals(Collections.singletonMap(key, value), cache.getAll(Collections.singleton(key)));
    }

    @Test
    public void testRemove() {
        cache.put(key, value);
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        Assert.assertTrue(cache.remove(key));
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);

        cache.put(key, value);
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        Assert.assertFalse(cache.remove(key, 1));
        Assert.assertNull(getCacheEntryEvent());

        Assert.assertTrue(cache.remove(key, value));
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);
    }

    @Test
    public void testRemoveAll() {
        Assert.assertTrue(cache.putIfAbsent(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        cache.removeAll();
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);
        Assert.assertFalse(cache.containsKey(key));

        Assert.assertTrue(cache.putIfAbsent(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        cache.removeAll(Collections.singleton(key));
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);
        Assert.assertFalse(cache.containsKey(key));
    }

    @Test
    public void testReplace() {
        cache.put(key, value);
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        Assert.assertFalse(cache.replace("#", 1));
        Assert.assertNull(getCacheEntryEvent());

        Assert.assertTrue(cache.replace(key, 1));
        assertCacheEntryEvent(EventType.UPDATED, key, 1, value);

        Assert.assertEquals(Integer.valueOf(1), cache.get(key));
        Assert.assertFalse(cache.replace(key, value, value));
        Assert.assertNull(getCacheEntryEvent());

        Assert.assertTrue(cache.replace(key, 1, value));
        assertCacheEntryEvent(EventType.UPDATED, key, value, 1);
    }

    @Test
    public void testIterator() {
        cache.put(key, value);
        Iterator<Cache.Entry<String, Integer>> iterator = cache.iterator();

        while (iterator.hasNext()) {
            Cache.Entry<String, Integer> entry = iterator.next();
            Assert.assertEquals(key, entry.getKey());
            Assert.assertEquals(value, entry.getValue());
        }
    }

    @Test
    public void testUnwrap() {
        Assert.assertEquals("", cache.unwrap(String.class));
    }

    @Test
    public void testLoadAll() {
        MutableConfiguration<String, Integer> config = new MutableConfiguration<>(this.config)
                .setCacheLoaderFactory(() -> new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws CacheLoaderException {
                        if (AbstractCacheTest.this.key.equals(key)) {
                            return 1;
                        }

                        return null;
                    }

                    @Override
                    public Map<String, Integer> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
                        return null;
                    }
                });

        Cache<String, Integer> cache = cacheManager.createCache("testCache1", config);

        AtomicBoolean completed = new AtomicBoolean(false);
        AtomicReference<Exception> exceptionAtomicReference = new AtomicReference<>();

        CompletionListener listener = new CompletionListener() {
            @Override
            public void onCompletion() {
                completed.set(true);
                onCompletion();
            }

            @Override
            public void onException(Exception e) {
                exceptionAtomicReference.set(e);
                onCompletion();
            }
        };

        boolean replaceExistingValues = true;
        cache.loadAll(Collections.singleton(key), replaceExistingValues, listener);
        while (!completed.get()) {
        }

        Assert.assertFalse(cache.containsKey(key));
        Assert.assertEquals(Integer.valueOf(1), cache.get(key));
        Assert.assertNull(exceptionAtomicReference.get());

        completed.set(false);
        replaceExistingValues = false;
        cache.loadAll(Collections.singleton(key), replaceExistingValues, listener);
        while (!completed.get()) {
        }

        Assert.assertTrue(cache.containsKey(key));
        Assert.assertEquals(Integer.valueOf(1), cache.get(key));
        Assert.assertNull(exceptionAtomicReference.get());

        cache.clear();
        completed.set(false);
        config.setReadThrough(false);
        cache.loadAll(Collections.singleton(key), replaceExistingValues, listener);
        while (!completed.get()) {
        }

        Assert.assertFalse(cache.containsKey(key));
        Assert.assertNull(cache.get(key));
        Assert.assertNull(exceptionAtomicReference.get());

        cache.clear();
        cache.close();

        completed.set(false);
        config.setReadThrough(true);
        config.setCacheLoaderFactory(() -> new CacheLoader<String, Integer>() {
            @Override
            public Integer load(String key) throws CacheLoaderException {
                throw new CacheLoaderException("Testing...");
            }

            @Override
            public Map<String, Integer> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
                return null;
            }
        });

        cache = cacheManager.createCache("testCache-exception", config);

        cache.loadAll(Collections.singleton(key), replaceExistingValues, listener);
        while (!completed.get()) {
        }

        Assert.assertFalse(cache.containsKey(key));
        Assert.assertEquals("Testing...", exceptionAtomicReference.get().getMessage());

        cache.clear();
        cache.close();
    }

    @Test
    public void testInvoke() {
        cache.put(key, value);

        Object result = cache.invoke(key, (entry, args) -> entry.getKey());
        Assert.assertEquals(key, result);

        result = cache.invoke(key, (entry, args) -> entry.getValue());
        Assert.assertNull(result);

        config.setReadThrough(false);
        result = cache.invoke(key, (entry, args) -> entry.getValue());
        Assert.assertEquals(value, result);

        result = cache.invoke(key, (entry, args) -> {
            entry.setValue(1);
            return null;
        });

        Assert.assertNull(result);
        Assert.assertEquals(Integer.valueOf(1), cache.get(key));

        result = cache.invoke(key, (entry, args) -> entry.exists());
        Assert.assertEquals(Boolean.TRUE, result);

        result = cache.invoke(key, (entry, args) -> {
            entry.remove();
            return null;
        });

        Assert.assertNull(result);
        Assert.assertFalse(cache.containsKey(key));

        result = cache.invoke(key, (entry, args) -> entry.unwrap(String.class));
        Assert.assertEquals("", result);
    }

    @Test
    public void testInvokeAll() {
        Map<String, EntryProcessorResult<Object[]>> resultMap = cache.invokeAll(Collections.singleton(key), (entry, args) -> args, "1", "2");
        Assert.assertTrue(resultMap.containsKey(key));
        Assert.assertArrayEquals(new String[]{"1", "2"}, resultMap.get(key).get());
    }

    @Test
    public void testDeregisterCacheEntryListener() {
        cache.deregisterCacheEntryListener(cacheEntryListener);
    }

    @Test
    public void testExpiryPolicy() throws Exception {
        MutableConfiguration<String, Integer> config = new MutableConfiguration<>(this.config)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ZERO));
        cacheManager.destroyCache(cacheName);
        cache = cacheManager.createCache(cacheName, config);

        cache.put(key, value);
        Assert.assertFalse(cache.containsKey(key));
        Assert.assertNull(cache.get(key));
        Assert.assertNull(getCacheEntryEvent());

        config = new MutableConfiguration<>(this.config)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 1L)));
        cacheManager.destroyCache(cacheName);
        cache = cacheManager.createCache(cacheName, config);

        cache.put(key, value);
        Assert.assertTrue((cache.containsKey(key)));
        Assert.assertEquals(value, cache.get(key));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        Thread.sleep(TimeUnit.SECONDS.toMillis(2));

        Assert.assertTrue(cache.containsKey(key));
        Assert.assertNull(cache.get(key));

        assertCacheEntryEvent(EventType.EXPIRED, key, value, value);
    }

}
