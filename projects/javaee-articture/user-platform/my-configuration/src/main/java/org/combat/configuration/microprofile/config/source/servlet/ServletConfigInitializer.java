package org.combat.configuration.microprofile.config.source.servlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * @author zhangwei
 * @Description ServletConfigInitializer
 * @Date: 2021/3/31 10:18
 */
public class ServletConfigInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        servletContext.addListener(ServletContextConfigInitializer.class);
    }

}
