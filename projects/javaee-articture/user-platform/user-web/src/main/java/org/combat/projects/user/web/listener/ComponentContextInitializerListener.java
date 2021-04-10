package org.combat.projects.user.web.listener;

import org.combat.context.ClassicComponentContext;
import org.combat.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ComponentContextInitializerListener implements ServletContextListener {

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ClassicComponentContext componentContext = new ClassicComponentContext();
        componentContext.init(this.servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
//        ClassicComponentContext.getInstance().destroy();
    }
}
