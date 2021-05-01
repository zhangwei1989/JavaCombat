package org.combat.configuration.microprofile.config.source.spring;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangwei
 * @Description ConfigSourcesAdapter
 * @Date: 2021/5/1 16:46
 */
public class ConfigSourcesAdapter {

    public List<ConfigSource> getConfigSources(Environment environment) {
        List<ConfigSource> configSourceList = new LinkedList<>();
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
            MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
            propertySources.stream()
                    .filter(propertySource -> propertySource instanceof EnumerablePropertySource)
                    .map(EnumerablePropertySource.class::cast)
                    .map(PropertySourceConfigSource::new)
                    .forEach(configSourceList::add);
        }
        return Collections.unmodifiableList(configSourceList);
    }
}
