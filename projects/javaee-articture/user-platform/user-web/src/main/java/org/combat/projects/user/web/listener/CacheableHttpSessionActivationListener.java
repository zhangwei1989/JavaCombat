package org.combat.projects.user.web.listener;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

/**
 * @author zhangwei
 * @Description CacheableHttpSessionActivationListener
 * @Date: 2021/4/20 13:10
 */
public class CacheableHttpSessionActivationListener implements HttpSessionActivationListener {

    @Override
    public void sessionWillPassivate(HttpSessionEvent se) {

    }

    @Override
    public void sessionDidActivate(HttpSessionEvent se) {

    }
}
