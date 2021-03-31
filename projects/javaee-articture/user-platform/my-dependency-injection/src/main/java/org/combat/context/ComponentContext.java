package org.combat.context;

import java.util.List;

public interface ComponentContext {

    // 生命周期方法
    void init();

    void destroy();

    // 组件操作方法
    <C> C getComponent(String name);

    List<String> getComponentNames();
}
