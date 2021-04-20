package org.combat.cache;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * @author zhangwei
 * @Description InMemoryCacheManager
 * @Date: 2021/4/17 19:17
 */
public class InMemoryCacheManager extends AbstractCacheManager{

    public InMemoryCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        return new InMemoryCache<K, V>(this, cacheName, configuration);
    }
}
