package org.combat.cache.event;

import org.junit.Test;

import javax.cache.Cache;
import javax.cache.event.EventType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zhangwei
 * @Description CacheEntryEventListenerAdapterTest
 * @Date: 2021/4/17 19:22
 */
public class CacheEntryEventListenerAdapterTest {

    @Test
    public void testOnEvent() {
        TestCacheEntryListener listener = new TestCacheEntryListener();
        CacheEntryEventListenerAdapter listenerAdapter = new CacheEntryEventListenerAdapter(listener);

        Cache cache = newCacheProxy();

        listenerAdapter.onEvent(new GenericCacheEntryEvent(cache, EventType.CREATED, "a", null, 1));
        listenerAdapter.onEvent(new GenericCacheEntryEvent(cache, EventType.UPDATED, "a", 1, 2));
        listenerAdapter.onEvent(new GenericCacheEntryEvent(cache, EventType.EXPIRED, "a", 1, 2));
        listenerAdapter.onEvent(new GenericCacheEntryEvent(cache, EventType.REMOVED, "a", 1, 2));
    }

    private Cache newCacheProxy() {
        return (Cache) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Cache.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("getName".equals(method.getName())) {
                    return "My Cache";
                }

                return null;
            }
        });
    }

}
