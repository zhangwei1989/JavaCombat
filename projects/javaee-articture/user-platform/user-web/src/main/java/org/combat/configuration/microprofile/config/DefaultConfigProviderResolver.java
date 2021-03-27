package org.combat.configuration.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhangwei
 * @Description DefaultConfigProviderResolver
 * @Date: 2021/3/18 14:37
 */
public class DefaultConfigProviderResolver extends ConfigProviderResolver {

    private ConcurrentMap<ClassLoader, Config> configsRepository = new ConcurrentHashMap<>();

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        /*ClassLoader classLoader = loader;

        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        ServiceLoader<Config> serviceLoader = ServiceLoader.load(Config.class, classLoader);
        Iterator<Config> iterator = serviceLoader.iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        }

        throw new IllegalStateException("No Config implement Found!");*/
        return configsRepository.computeIfAbsent(loader, this::newConfig);
    }

    protected Config newConfig(ClassLoader classLoader) {
        return newConfigBuilder(classLoader).build();
    }

    protected ConfigBuilder newConfigBuilder(ClassLoader classLoader) {
        return new DefaultConfigBuilder(resolveClassLoader(classLoader));
    }

    private ClassLoader resolveClassLoader(ClassLoader classLoader) {
        return classLoader == null ? this.getClass().getClassLoader() : classLoader;
    }

    @Override
    public ConfigBuilder getBuilder() {
        return null;
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        configsRepository.put(classLoader, config);
    }

    @Override
    public void releaseConfig(Config config) {
        List<ClassLoader> targetKeys = new LinkedList<>();
        for (Map.Entry<ClassLoader, Config> entry : configsRepository.entrySet()) {
            if (Objects.equals(config, entry.getValue())) {
                targetKeys.add(entry.getKey());
            }
        }
        targetKeys.forEach(configsRepository::remove);
    }
}
