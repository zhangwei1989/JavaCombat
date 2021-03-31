package org.combat.configuration.microprofile.config;

import org.combat.configuration.microprofile.config.converter.Converters;
import org.combat.configuration.microprofile.config.source.ConfigSources;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * @author zhangwei
 * @Description DefaultConfig
 * @Date: 2021/3/21 21:30
 */
public class DefaultConfig implements Config {

    private ConfigSources configSources;

    private Converters converters;

    DefaultConfig(ConfigSources configSources, Converters converters) {
        this.configSources = configSources;
        this.converters = converters;
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // String 转换成目标类型
        Converter<T> converter = doGetConverter(propertyType);
        return converter == null ? null : converter.convert(propertyValue);
    }

    private <T> Converter<T> doGetConverter(Class<T> propertyType) {
        List<Converter> converters = this.converters.getConverters(propertyType);
        return converters.isEmpty() ? null : converters.get(0);
    }

    private String getPropertyValue(String propertyName) {
        String propertyValue = null;

        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }

        return propertyValue;
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        return null;
    }

    @Override
    public <T> List<T> getValues(String propertyName, Class<T> propertyType) {
        return null;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    @Override
    public <T> Optional<List<T>> getOptionalValues(String propertyName, Class<T> propertyType) {
        return Optional.empty();
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return StreamSupport.stream(configSources.spliterator(), false)
                .map(ConfigSource::getPropertyNames)
                .collect(LinkedHashSet::new, Set::addAll, Set::addAll);
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return this.configSources;
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        Converter<T> converter = doGetConverter(forType);
        return converter == null ? Optional.empty() : Optional.of(converter);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }
}
