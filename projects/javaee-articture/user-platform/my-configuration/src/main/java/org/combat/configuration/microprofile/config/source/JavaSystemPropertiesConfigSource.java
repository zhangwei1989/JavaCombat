package org.combat.configuration.microprofile.config.source;

import java.util.Map;

/**
 * @author zhangwei
 * @Description JavaSystemPropertiesConfigSource
 * @Date: 2021/3/18 12:48
 */
public class JavaSystemPropertiesConfigSource extends MapBasedConfigSource {

    public JavaSystemPropertiesConfigSource() {
        super("Java System Properties", 400);
    }

    @Override
    protected void prepareConfigData(Map configMap)  {
        configMap.putAll(System.getProperties());
    }
}
