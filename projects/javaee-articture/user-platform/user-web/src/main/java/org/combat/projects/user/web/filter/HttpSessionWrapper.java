package org.combat.projects.user.web.filter;

import javax.cache.Cache;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author zhangwei
 * @Description HttpSessionWrapper
 * @Date: 2021/4/20 13:17
 */
public class HttpSessionWrapper implements HttpSession {

    private final HttpSession source;

    private final Cache<String, Map<String, Object>> cache;

    public HttpSessionWrapper(HttpSession source, Cache<String, Map<String, Object>> cache) {
        this.source = source;
        this.cache = cache;
    }

    @Override
    public long getCreationTime() {
        return source.getCreationTime();
    }

    @Override
    public String getId() {
        return source.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return source.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return source.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        source.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return source.getMaxInactiveInterval();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return source.getSessionContext();
    }

    @Override
    public Object getAttribute(String name) {
        Object value = source.getAttribute(name);
        if (value == null) {
            String id = getId();
            Map<String, Object> attributesMap = cache.get(id);
            if (attributesMap != null) {
                value = attributesMap.get(name);
                if (value != null) {
                    source.setAttribute(name, value);
                }
            }
        }

        return value;
    }

    @Override
    public Object getValue(String name) {
        return source.getValue(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return source.getAttributeNames();
    }

    @Override
    public String[] getValueNames() {
        return source.getValueNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        source.setAttribute(name, value);
        String id = getId();
        Map<String, Object> attributesMap = cache.get(id);
        if (attributesMap != null) {
            attributesMap.put(name, value);
            cache.put(id, attributesMap);
        }
    }

    @Override
    public void putValue(String name, Object value) {
        source.putValue(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        source.removeAttribute(name);
    }

    @Override
    public void removeValue(String name) {
        source.removeValue(name);
    }

    @Override
    public void invalidate() {
        source.invalidate();
    }

    @Override
    public boolean isNew() {
        return source.isNew();
    }
}
