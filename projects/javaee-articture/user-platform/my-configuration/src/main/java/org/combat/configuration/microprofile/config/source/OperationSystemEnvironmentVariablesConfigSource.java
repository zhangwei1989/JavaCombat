package org.combat.configuration.microprofile.config.source;

import java.util.Map;

/**
 * @author zhangwei
 * @Description OperationSystemEnvironmentVariablesConfigSource
 * @Date: 2021/3/24 23:46
 */
public class OperationSystemEnvironmentVariablesConfigSource extends MapBasedConfigSource {

    public OperationSystemEnvironmentVariablesConfigSource() {
        super("Operation System Environment Variables", 300);
    }

    @Override
    protected void prepareConfigData(Map configMap) {
        configMap.putAll(System.getenv());
    }
}
