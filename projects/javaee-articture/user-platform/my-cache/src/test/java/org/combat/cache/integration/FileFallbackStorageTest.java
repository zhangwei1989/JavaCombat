package org.combat.cache.integration;

import org.combat.cache.ExpirableEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * @author zhangwei
 * @Description FileFallbackStorageTest
 * @Date: 2021/4/17 19:43
 */
public class FileFallbackStorageTest {

    private FileFallbackStorage instance = new FileFallbackStorage();

    @Test
    public void writeAllAndLoadAll() {
        Assert.assertNull(instance.load("a"));
        Assert.assertNull(instance.load("b"));
        Assert.assertNull(instance.load("c"));

        instance.writeAll(Arrays.asList(ExpirableEntry.of("a", 1), ExpirableEntry.of("b", 2), ExpirableEntry.of("c", 3)));

        Map map = instance.loadAll(Arrays.asList("a", "b", "c"));
        Assert.assertEquals(1, map.get("a"));
        Assert.assertEquals(2, map.get("b"));
        Assert.assertEquals(3, map.get("c"));

        instance.write(ExpirableEntry.of("a", new Object()));
    }

    @After
    public void deleteAll() {
        instance.deleteAll(Arrays.asList("a", "b", "c"));
    }

}
