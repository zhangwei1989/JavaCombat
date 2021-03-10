package org.combat.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * 组件上下文（Web 应用全局使用）
 */
public class ComponentContext {

    public static final String CONTEXT_NAME = ComponentContext.class.getName();

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    private static final Logger logger = Logger.getLogger(CONTEXT_NAME);

    private static ServletContext servletContext;

    private Context envContext;

    public void init(ServletContext servletContext) throws RuntimeException {
        try {
            Context initCtx = new InitialContext();
            this.envContext = (Context) initCtx.lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        servletContext.setAttribute(CONTEXT_NAME, this);
        this.servletContext = servletContext;
    }

    public static ComponentContext getInstance() {
        return (ComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    public <C> C getComponent(String name) throws NoSuchElementException {
        C component;

        try {
            component = (C) this.envContext.lookup(name);
        } catch (NamingException e) {
            throw new NoSuchElementException(e.getClass().getName());
        }

        return component;
    }

    public void destroy() throws RuntimeException {
        try {
            this.envContext.close();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
