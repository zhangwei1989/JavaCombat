package org.combat.cache;

import org.combat.cache.configuration.ConfigurationUtils;
import org.combat.cache.event.CacheEntryEventPublisher;
import org.combat.cache.event.GenericCacheEntryEvent;
import org.combat.cache.integration.CompositeFallbackStorage;
import org.combat.cache.integration.FallbackStorage;
import org.combat.cache.management.ManagementUtils;
import org.combat.cache.processor.MutableEntryAdapter;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.*;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * @author zhangwei
 * @Description org.combat.cache.AbstractCache
 * @Date: 2021/4/13 18:24
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final CacheManager cacheManager;

    private final String cacheName;

    private final MutableConfiguration<K, V> configuration;

    private final ExpiryPolicy expiryPolicy;

    private final CacheLoader<K, V> cacheLoader;

    private final CacheWriter<K, V> cacheWriter;

    private final FallbackStorage defaultFallbackStorage;

    private final CacheEntryEventPublisher cacheEntryEventPublisher;

    private final Executor executor;

    private volatile boolean closed = false;

    public AbstractCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        this.configuration = ConfigurationUtils.mutableConfiguration(configuration);
        this.expiryPolicy = resolveExpiryPolicy(getConfiguration());
        this.defaultFallbackStorage = new CompositeFallbackStorage(getClassLoader());
        this.cacheLoader = resolveCacheLoader(getConfiguration(), getClassLoader());
        this.cacheWriter = resolveCacheWriter(getConfiguration(), getClassLoader());
        this.cacheEntryEventPublisher = new CacheEntryEventPublisher();
        this.executor = ForkJoinPool.commonPool();
        registerCacheEntryListenersFromConfiguration();
        ManagementUtils.registerCacheMXBeanIfRequired(this);
    }

    @Override
    public V get(K key) {
        assertNotClosed();
        ExpirableEntry.requireKeyNotNull(key);

        ExpirableEntry<K, V> entry = null;
        try {
            entry = getEntry(key);
            if (handleExpiryPolicyForAccess(entry)) {
                return null;
            }
        } catch (Throwable e) {
            logger.severe(e.getMessage());
        }

        if (entry == null && isReadThrough()) {
            return loadValue(key, true);
        }
        return getValue(entry);
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    @Override
    public boolean containsKey(K key) {
        assertNotClosed();
        return containsEntry(key);
    }

    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        assertNotClosed();

        if (!configuration.isReadThrough()) {
            completionListener.onCompletion();
            return;
        }

        CompletableFuture.runAsync(() -> {
            for (K key : keys) {
                V value = loadValue(key, false);
                if (replaceExistingValues) {
                    replace(key, value);
                } else {
                    put(key, value);
                }
            }
        }, executor).whenComplete((v, e) -> {
            if (completionListener != null) {
                if (e instanceof Exception && e.getCause() instanceof Exception) {
                    completionListener.onException((Exception) e.getCause());
                } else {
                    completionListener.onCompletion();
                }
            }
        });
    }

    @Override
    public void put(K key, V value) {
        assertNotClosed();
        Entry<K, V> entry = null;
        try {
            if (!containsKey(key)) {
                entry = createAndPutEntry(key, value);
            } else {
                entry = updateEntry(key, value);
            }
        } finally {
            writeEntryIfWriteThrough(entry);
        }
    }

    @Override
    public V getAndPut(K key, V value) {
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        put(key, value);
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(K key) {
        assertNotClosed();
        Objects.requireNonNull(key);
        boolean removed = false;
        try {
            ExpirableEntry<K, V> oldEntry = removeEntry(key);
            removed = oldEntry != null;
            if (removed) {
                publishRemovedEvent(key, oldEntry.getValue());
            }
        } finally {
            deleteIfWriteThrough(key);
        }
        return removed;
    }

    @Override
    public boolean remove(K key, V oldValue) {
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public V getAndRemove(K key) {
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        remove(key);
        return oldValue;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        ExpirableEntry.requireValueNotNull(oldValue);
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            put(key, newValue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean replace(K key, V value) {
        if (containsEntry(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public V getAndReplace(K key, V value) {
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        if (oldValue != null) {
            put(key, value);
        }
        return oldValue;
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
        for (K key : keys) {
            remove(key);
        }
    }

    @Override
    public void removeAll() {
        removeAll(keySet());
    }

    @Override
    public void clear() {
        assertNotClosed();
        clearEntries();
        defaultFallbackStorage.destroy();
    }

    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        if (!Configuration.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("The class must be inherited of " + Configuration.class.getName());
        }
        return (C) ConfigurationUtils.immutableConfiguration(getConfiguration());
    }

    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
        MutableEntry<K, V> mutableEntry = MutableEntryAdapter.of(key, this);
        return entryProcessor.process(mutableEntry, arguments);
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        Map<K, EntryProcessorResult<T>> resultMap = new LinkedHashMap<>();
        for (K key : keys) {
            resultMap.put(key, () -> invoke(key, entryProcessor, arguments));
        }

        return resultMap;
    }

    @Override
    public String getName() {
        return this.cacheName;
    }

    @Override
    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }

        doClose();

        closed = true;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return getCacheManager().unwrap(clazz);
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        cacheEntryEventPublisher.registerCacheEntryListener(cacheEntryListenerConfiguration);
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        cacheEntryEventPublisher.deregisterCacheEntryListener(cacheEntryListenerConfiguration);
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        assertNotClosed();
        List<Entry<K, V>> entries = new LinkedList<>();
        for (K key : keySet()) {
            V value = get(key);
            entries.add(ExpirableEntry.of(key, value));
        }

        return entries.iterator();
    }

    protected abstract boolean containsEntry(K key) throws CacheException, ClassCastException;

    protected abstract ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException;

    protected abstract ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException;

    protected abstract void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException;

    protected abstract void clearEntries() throws CacheException;

    protected abstract Set<K> keySet();

    private final CompleteConfiguration<K, V> getConfiguration() {
        return this.configuration;
    }

    protected CacheLoader<K, V> getCacheLoader() {
        return this.cacheLoader;
    }

    private static <K, V> V getValue(Entry<K, V> entry) {
        return entry == null ? null : entry.getValue();
    }

    private CacheWriter<K, V> getCacheWriter() {
        return this.cacheWriter;
    }

    protected ClassLoader getClassLoader() {
        return getCacheManager().getClassLoader();
    }

    private V loadValue(K key, boolean storedEntry) {
        V value = loadValue(key);
        if (storedEntry && value != null) {
            put(key, value);
        }

        return value;
    }

    private V loadValue(K key) {
        return getCacheLoader().load(key);
    }

    private Entry<K, V> createAndPutEntry(K key, V value) {
        ExpirableEntry<K, V> newEntry = createEntry(key, value);
        if (handleExpiryPolicyForCreation(newEntry)) {
            return null;
        }

        putEntry(newEntry);
        publishCreatedEvent(key, value);

        return newEntry;
    }

    protected Duration getExpiryForAccess() {
        return getDuration(expiryPolicy::getExpiryForAccess);
    }

    private Duration getExpiryForCreation() {
        return getDuration(expiryPolicy::getExpiryForCreation);
    }

    private Duration getDuration(Supplier<Duration> durationSupplier) {
        Duration duration = null;
        try {
            duration = durationSupplier.get();
        } catch (Throwable ignored) {
            duration = Duration.ETERNAL;
        }

        return duration;
    }

    protected void doClose() {};

    private ExpirableEntry<K, V> createEntry(K key, V value) {
        return ExpirableEntry.of(key, value);
    }

    private Entry<K, V> updateEntry(K key, V value) {
        ExpirableEntry<K, V> oldEntry = getEntry(key);

        V oldValue = oldEntry.getValue();
        oldEntry.setValue(value);
        putEntry(oldEntry);
        publishUpdatedEvent(key, oldValue, value);

        if (handleExpiryPolicyForUpdate(oldEntry)) {
            return null;
        }

        return oldEntry;
    }

    private boolean handleExpiryPolicyForAccess(ExpirableEntry<K, V> entry) {
        return handleExpiryPolicy(entry, getExpiryForAccess(), true);
    }

    private boolean handleExpiryPolicyForCreation(ExpirableEntry<K, V> newEntry) {
        return handleExpiryPolicy(newEntry, getExpiryForCreation(), false);
    }

    private boolean handleExpiryPolicyForUpdate(ExpirableEntry<K, V> oldEntry) {
        return handleExpiryPolicy(oldEntry, getExpiryForUpdate(), true);
    }

    private boolean handleExpiryPolicy(ExpirableEntry<K, V> entry, Duration duration, boolean removedExpiredEntry) {
        if (entry == null) {
            return false;
        }

        boolean expired = false;

        if (entry.isExpired()) {
            expired = true;
        } else if (duration != null) {
            if (duration.isZero()) {
                expired = true;
            } else {
                long timestmap = duration.getAdjustedTime(System.currentTimeMillis());
                entry.setTimestamp(timestmap);
            }
        }

        if (removedExpiredEntry && expired) {
            K key = entry.getKey();
            V value = entry.getValue();
            removeEntry(key);
            publishExpiredEvent(key, value);
        }

        return expired;
    }

    private Duration getExpiryForUpdate() {
        return getDuration(expiryPolicy::getExpiryForUpdate);
    }

    private void publishCreatedEvent(K key, V value) {
        cacheEntryEventPublisher.publish(GenericCacheEntryEvent.createdEvent(this, key, value));
    }

    private void publishUpdatedEvent(K key, V oldValue, V value) {
        cacheEntryEventPublisher.publish(GenericCacheEntryEvent.updatedEvent(this, key, oldValue, value));
    }

    private void publishExpiredEvent(K key, V oldValue) {
        cacheEntryEventPublisher.publish(GenericCacheEntryEvent.expiredEvent(this, key, oldValue));
    }

    private void publishRemovedEvent(K key, V oldValue) {
        cacheEntryEventPublisher.publish(GenericCacheEntryEvent.removedEvent(this, key, oldValue));
    }

    private final boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    private final boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }

    private void writeEntryIfWriteThrough(Entry<K, V> entry) {
        if (entry != null && isWriteThrough()) {
            getCacheWriter().write(entry);
        }
    }

    private void deleteIfWriteThrough(K key) {
        if (isWriteThrough()) {
            getCacheWriter().delete(key);
        }
    }

    private CacheLoader<K, V> resolveCacheLoader(CompleteConfiguration<K, V> configuration, ClassLoader classLoader) {
        Factory<CacheLoader<K, V>> cacheLoaderFactory = configuration.getCacheLoaderFactory();
        CacheLoader<K, V> cacheLoader = null;

        if (cacheLoaderFactory != null) {
            cacheLoader = cacheLoaderFactory.create();
        }

        if (cacheLoader == null) {
            cacheLoader = this.defaultFallbackStorage;
        }

        return cacheLoader;
    }

    private CacheWriter<K, V> resolveCacheWriter(CompleteConfiguration<K, V> configuration, ClassLoader classLoader) {
        Factory<CacheWriter<? super K, ? super V>> cacheWriterFactory = configuration.getCacheWriterFactory();
        CacheWriter<K, V> cacheWriter = null;

        if (cacheWriterFactory != null) {
            cacheWriter = (CacheWriter<K, V>) cacheWriterFactory.create();
        }

        if (cacheWriter == null) {
            cacheWriter = this.defaultFallbackStorage;
        }

        return cacheWriter;
    }

    private ExpiryPolicy resolveExpiryPolicy(CompleteConfiguration<K, V> configuration) {
        Factory<ExpiryPolicy> expiryPolicyFactory = configuration.getExpiryPolicyFactory();
        if (expiryPolicyFactory == null) {
            expiryPolicyFactory = EternalExpiryPolicy::new;
        }
        return expiryPolicyFactory.create();
    }

    private void registerCacheEntryListenersFromConfiguration() {
        this.configuration.getCacheEntryListenerConfigurations()
                .forEach(this::registerCacheEntryListener);
    }

    protected void assertNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("Current cache has been closed! No operation should be executed.");
        }
    }
}
