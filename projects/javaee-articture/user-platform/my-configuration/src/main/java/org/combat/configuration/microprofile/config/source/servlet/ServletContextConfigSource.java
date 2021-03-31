package org.combat.configuration.microprofile.config.source.servlet;

import org.combat.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author zhangwei
 * @Description ServletContextConfigSource
 * @Date: 2021/3/31 09:59
 */
public class ServletContextConfigSource extends MapBasedConfigSource {

    private final ServletContext servletContext;

    protected ServletContextConfigSource(ServletContext servletContext) {
        super("ServletContext Init Parameters", 500);
        this.servletContext = servletContext;
    }

    @Override
    protected void prepareConfigData(Map configMap) throws Throwable {
        Enumeration<String> parameterNames = this.servletContext.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            configMap.put(parameterName, this.servletContext.getInitParameter(parameterName));
        }
    }
}
