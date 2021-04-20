package org.combat.cache;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.combat.cache.configuration.ConfigurationUtils;
import org.combat.cache.event.TestCacheEntryListener;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;

/**
 * @author zhangwei
 * @Description CachingTest
 * @Date: 2021/4/19 14:07
 */
public class CachingTest {

    @Test
    public void testSampleInMemory() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("in-memory://localhost/"), null);

        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setManagementEnabled(true)
                        .setTypes(String.class, Integer.class);

        Cache<String, Integer> cache = cacheManager.createCache("simpleCache", config);

        cache.registerCacheEntryListener(ConfigurationUtils.cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        String key = "key";
        Integer value1 = 1;
        cache.put(key, value1);

        value1 = 2;
        cache.put(key, value1);

        Integer value2 = cache.get(key);
        Assert.assertEquals(value1, value2);
        cache.remove(key);
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void testSampleRedis() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("redis://127.0.0.1:6379/"), null);

        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setTypes(String.class, Integer.class);

        Cache<String, Integer> cache = cacheManager.createCache("redisCache", config);

        cache.registerCacheEntryListener(ConfigurationUtils.cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        String key = "redis-key";
        Integer value1 = 1;
        cache.put(key, value1);

        value1 = 2;
        cache.put(key, value1);

        Integer value2 = cache.get(key);
        Assert.assertEquals(value1, value2);
        cache.remove(key);
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void testLettuce() {
        RedisClient redisClient = RedisClient.create("redis://localhost:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        syncCommands.set("key", "1");

        System.out.println(syncCommands.get("key"));

        connection.close();
        redisClient.shutdown();
    }

    @Test
    public void testJedis() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            jedis.set("foo", "123");
            String foobar = jedis.get("foo");
            System.out.println(foobar);
        }

        pool.close();
    }
}
