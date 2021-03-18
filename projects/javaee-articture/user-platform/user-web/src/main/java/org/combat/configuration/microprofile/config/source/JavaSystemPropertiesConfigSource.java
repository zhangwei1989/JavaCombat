package org.combat.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Map;
import java.util.Set;

/**
 * @author zhangwei
 * @Description JavaSystemPropertiesConfigSource
 * @Date: 2021/3/18 12:48
 */
public class JavaSystemPropertiesConfigSource implements ConfigSource {

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行期不去调整
     * -Dapplication.name=user-web
     */
    private final Map<String, String> properties;

    public JavaSystemPropertiesConfigSource() {
        Map systemProperties = System.getProperties();
        this.properties = systemProperties;
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return "Java System Properties";
    }
}
