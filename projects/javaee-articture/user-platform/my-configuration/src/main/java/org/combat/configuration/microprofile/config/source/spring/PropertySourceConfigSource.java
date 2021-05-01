package org.combat.configuration.microprofile.config.source.spring;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.springframework.core.Ordered;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangwei
 * @Description PropertySourceConfigSource
 * @Date: 2021/5/1 16:07
 */
public class PropertySourceConfigSource implements ConfigSource {

    private final EnumerablePropertySource propertySource;

    private int ordinal;

    public PropertySourceConfigSource(EnumerablePropertySource propertySource) {
        this.propertySource = propertySource;
        if (propertySource instanceof Ordered) {
            this.setOrdinal(((Ordered) propertySource).getOrder());
        } else {
            this.setOrdinal(ConfigSource.super.getOrdinal());
        }
    }

    @Override
    public Map<String, String> getProperties() {
        return ConfigSource.super.getProperties();
    }

    @Override
    public Set<String> getPropertyNames() {
        String[] propertyNames = propertySource.getPropertyNames();
        Set<String> propertyNamesSet = new LinkedHashSet<>();
        for (String propertyName : propertyNames) {
            propertyNamesSet.add(propertyName);
        }

        return Collections.unmodifiableSet(propertyNamesSet);
    }

    @Override
    public int getOrdinal() {
        return this.ordinal;
    }

    @Override
    public String getValue(String propertyName) {
        Object propertyValue = propertySource.getProperty(propertyName);
        return propertyValue instanceof String ? String.valueOf(propertyValue) : null;
    }

    @Override
    public String getName() {
        return propertySource.getName();
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
}
