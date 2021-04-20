package org.combat.cache.integration;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import java.util.Comparator;

/**
 * @author zhangwei
 * @Description FallbackStorage
 * @Date: 2021/4/13 21:27
 */
public interface FallbackStorage<K, V> extends CacheLoader<K, V>, CacheWriter<K, V> {

    int getPriority();

    void destroy();

    Comparator<FallbackStorage> PRIORITY_COMPARATOR = new PriorityComparator();

    class PriorityComparator implements Comparator<FallbackStorage> {

        @Override
        public int compare(FallbackStorage o1, FallbackStorage o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    }

}
