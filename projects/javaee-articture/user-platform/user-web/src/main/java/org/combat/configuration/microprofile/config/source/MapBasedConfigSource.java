package org.combat.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangwei
 * @Description MapBasedConfigSource
 * @Date: 2021/3/24 23:50
 */
public abstract class MapBasedConfigSource implements ConfigSource {

    private final String name;

    private final int ordinal;

    private final Map<String, String> source;

    protected MapBasedConfigSource(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
        this.source = getProperties();
    }

    public final Map<String, String> getProperties() {
        Map<String, String> configMap = new HashMap<>();

        try {
            prepareConfigData(configMap);
        } catch (Throwable throwable) {
            throw new IllegalStateException("准备配置数据发生错误", throwable);
        }

        return Collections.unmodifiableMap(configMap);
    }

    protected abstract void prepareConfigData(Map configMap) throws Throwable;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public Set<String> getPropertyNames() {
        return source.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return source.get(propertyName);
    }
}
