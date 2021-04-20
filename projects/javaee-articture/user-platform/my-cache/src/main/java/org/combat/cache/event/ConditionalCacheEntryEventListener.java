package org.combat.cache.event;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.EventType;
import java.util.EventListener;
import java.util.Set;
import java.util.concurrent.Executor;

public interface ConditionalCacheEntryEventListener<K, V> extends EventListener {

    boolean supports(CacheEntryEvent<? extends K, ? extends V> event) throws CacheEntryListenerException;

    void onEvent(CacheEntryEvent<? extends K, ? extends V> event);

    default void onEvents(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {
        events.forEach(this::onEvent);
    }

    Set<EventType> getSupportedEventTypes();

    Executor getExecutor();

    @Override
    int hashCode();

    @Override
    boolean equals(Object object);
}
