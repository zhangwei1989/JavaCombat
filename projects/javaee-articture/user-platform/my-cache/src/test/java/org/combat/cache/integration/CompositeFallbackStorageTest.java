package org.combat.cache.integration;

import org.combat.cache.ExpirableEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * @author zhangwei
 * @Description CompositeFallbackStorageTest
 * @Date: 2021/4/18 07:31
 */
public class CompositeFallbackStorageTest {

    private CompositeFallbackStorage instance = new CompositeFallbackStorage();

    @Test
    public void writeAllAndLoadAll() {
        instance.writeAll(Arrays.asList(ExpirableEntry.of("a", 1), ExpirableEntry.of("b", 2), ExpirableEntry.of("c", 3)));

        Map map = instance.loadAll(Arrays.asList("a", "b", "c"));
        Assert.assertEquals(1, map.get("a"));
        Assert.assertEquals(2, map.get("b"));
        Assert.assertEquals(3, map.get("c"));
    }

    @After
    public void deleteAll() {
        instance.deleteAll(Arrays.asList("a", "b", "c"));
    }
}
