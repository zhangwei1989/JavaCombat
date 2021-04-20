package org.combat.cache;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhangwei
 * @Description KeyValueTypePairTest
 * @Date: 2021/4/19 14:29
 */
public class KeyValueTypePairTest {

    @Test
    public void testResolve() {
        KeyValueTypePair keyValueTypePair = KeyValueTypePair.resolve(InMemoryCache.class);
        Assert.assertEquals(Object.class, keyValueTypePair.getKeyType());
        Assert.assertEquals(Object.class, keyValueTypePair.getValueType());
    }
}
