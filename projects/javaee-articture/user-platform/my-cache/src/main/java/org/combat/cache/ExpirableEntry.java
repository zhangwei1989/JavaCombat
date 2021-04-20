package org.combat.cache;

import javax.cache.Cache;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhangwei
 * @Description ExpirableEntry
 * @Date: 2021/4/14 13:06
 */
public class ExpirableEntry<K, V> implements Cache.Entry<K, V>, Serializable {

    private final K key;

    private V value;

    private long timestamp;

    public ExpirableEntry(K key, V value) throws NullPointerException {
        requireKeyNotNull(key);
        this.key = key;
        this.setValue(value);
        this.timestamp = Long.MAX_VALUE;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        T value;
        try {
            value = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return value;
    }

    @Override
    public String toString() {
        return "ExpirableEntry{" +
                "key=" + key +
                ", value=" + value +
                ", timestamp=" + timestamp +
                "}";
    }

    public static <K, V> ExpirableEntry<K, V> of(Map.Entry<K, V> entry) {
        return new ExpirableEntry(entry.getKey(), entry.getValue());
    }

    public static <K, V> ExpirableEntry<K, V> of(K key, V value) {
        return new ExpirableEntry(key, value);
    }

    public void setValue(V value) {
        requireValueNotNull(value);
        this.value = value;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= timestamp;
    }

    public static <K> void requireKeyNotNull(K key) {
        Objects.requireNonNull(key, "The key must not be null.");
    }

    public static <V> void requireValueNotNull(V value) {
        Objects.requireNonNull(value, "The value must not be null.");
    }
}
