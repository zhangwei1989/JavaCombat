package rg.combat.configuration.microprofile.config.source.spring;

import org.combat.configuration.microprofile.config.source.spring.PropertySourceConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * @author zhangwei
 * @Description PropertySourceConfigSourceTest
 * @Date: 2021/5/1 16:20
 */
public class PropertySourceConfigSourceTest {

    @Test
    public void test() {
        EnumerablePropertySource propertySource = new PropertiesPropertySource("systemProperties", System.getProperties());
        PropertySourceConfigSource configSource = new PropertySourceConfigSource(propertySource);

        Assert.assertEquals("systemProperties", configSource.getName());
        Assert.assertEquals(ConfigSource.DEFAULT_ORDINAL, configSource.getOrdinal());
        Assert.assertNotNull(configSource.getPropertyNames());
        configSource.getPropertyNames().forEach(propertyName ->
                Assert.assertEquals(System.getProperty(propertyName), configSource.getValue(propertyName))
        );
        Assert.assertEquals(System.getProperties(), configSource.getProperties());
    }

}
