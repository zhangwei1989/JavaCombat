package org.combat.cache.processor;

import javax.cache.Cache;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.processor.MutableEntry;

/**
 * @author zhangwei
 * @Description MutableEntryAdapter
 * @Date: 2021/4/14 20:34
 */
public class MutableEntryAdapter<K, V> implements MutableEntry<K, V> {

    private final K key;

    private final Cache<K, V> cache;

    public MutableEntryAdapter(K key, Cache<K, V> cache) {
        this.key = key;
        this.cache = cache;
    }

    @Override
    public boolean exists() {
        return cache.containsKey(getKey());
    }

    @Override
    public void remove() {
        cache.remove(getKey());
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        CompleteConfiguration configuration = cache.getConfiguration(CompleteConfiguration.class);
        return configuration.isReadThrough() ? null : cache.get(key);
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return cache.unwrap(clazz);
    }

    @Override
    public void setValue(V value) {
        cache.put(key, value);
    }

    public static <K, V> MutableEntry<K, V> of(K key, Cache<K, V> cache) {
        return new MutableEntryAdapter<>(key, cache);
    }

}
