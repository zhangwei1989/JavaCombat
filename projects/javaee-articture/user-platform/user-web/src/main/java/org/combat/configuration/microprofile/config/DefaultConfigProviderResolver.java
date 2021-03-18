package org.combat.configuration.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author zhangwei
 * @Description DefaultConfigProviderResolver
 * @Date: 2021/3/18 14:37
 */
public class DefaultConfigProviderResolver extends ConfigProviderResolver {

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        ClassLoader classLoader = loader;

        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        ServiceLoader<Config> serviceLoader = ServiceLoader.load(Config.class, classLoader);
        Iterator<Config> iterator = serviceLoader.iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        }

        throw new IllegalStateException("No Config implement Found!");
    }

    @Override
    public ConfigBuilder getBuilder() {
        return null;
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {

    }

    @Override
    public void releaseConfig(Config config) {

    }
}
