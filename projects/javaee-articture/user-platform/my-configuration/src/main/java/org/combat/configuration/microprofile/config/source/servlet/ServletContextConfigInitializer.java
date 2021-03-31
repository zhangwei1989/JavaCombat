package org.combat.configuration.microprofile.config.source.servlet;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author zhangwei
 * @Description ServletContextConfigInitializer
 * @Date: 2021/3/31 09:57
 */
public class ServletContextConfigInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        ServletContextConfigSource servletContextConfigSource = new ServletContextConfigSource(servletContext);
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        ConfigBuilder configBuilder = configProviderResolver.getBuilder();
        configBuilder.forClassLoader(classLoader);
        configBuilder.addDefaultSources();
        configBuilder.addDiscoveredSources();
        configBuilder.withSources(servletContextConfigSource);
        configBuilder.addDiscoveredConverters();
        Config config = configBuilder.build();
        configProviderResolver.registerConfig(config, classLoader);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
//        ServletContext servletContext = servletContextEvent.getServletContext();
//        ClassLoader classLoader = servletContext.getClassLoader();
//        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
//        Config config = configProviderResolver.getConfig(classLoader);
    }
}
