package org.combat.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author zhangwei
 * @Description ConfigSources
 * @Date: 2021/3/21 21:38
 */
public class ConfigSources implements Iterable<ConfigSource> {

    private ClassLoader classLoader;

    private boolean addDefaultConfigSources;

    private boolean addDiscoveredConfigSources;

    private List<ConfigSource> configSources = new LinkedList<>();

    public ConfigSources() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ConfigSources(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void addDefaultSources() {
        if (addDefaultConfigSources) {
            return;
        }

        addConfigSources(JavaSystemPropertiesConfigSource.class,
                OperationSystemEnvironmentVariablesConfigSource.class,
                DefaultResourceConfigSource.class);

        addDefaultConfigSources = true;
    }

    public void addDiscoveredSources() {
        if (addDiscoveredConfigSources) {
            return;
        }

        addConfigSources(ServiceLoader.load(ConfigSource.class, classLoader));

        addDiscoveredConfigSources = true;
    }

    public void addConfigSources(Class<? extends ConfigSource>... configSourceClasses) {
        addConfigSources(
                Stream.of(configSourceClasses)
                        .map(this::newInstance)
                        .toArray(ConfigSource[]::new)
        );
    }

    public void addConfigSources(ConfigSource... configSources) {
        addConfigSources(Arrays.asList(configSources));
    }

    public void addConfigSources(Iterable<ConfigSource> configSources) {
        configSources.forEach(this.configSources::add);
        Collections.sort(this.configSources, ConfigSourceOrdinalComparator.INSTANCE);
    }

    private ConfigSource newInstance(Class<? extends ConfigSource> configSourceClass) {
        ConfigSource instance;
        try {
            instance = configSourceClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        }

        return instance;
    }

    @Override
    public Iterator<ConfigSource> iterator() {
        return this.configSources.iterator();
    }

}
