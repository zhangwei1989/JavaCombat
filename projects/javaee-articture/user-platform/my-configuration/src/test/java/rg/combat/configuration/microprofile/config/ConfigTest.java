package rg.combat.configuration.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author zhangwei
 * @Description org.combat.configuration.microprofile.config.ConfigTest
 * @Date: 2021/3/28 01:29
 */
public class ConfigTest {

    @BeforeClass
    public static void prepare() {

    }

    @Before
    public void init() {

    }

    @Test
    public void testResolveConvertedType() {
        /*ServiceLoader<ConfigProviderResolver> serviceLoader =
                ServiceLoader.load(ConfigProviderResolver.class, Thread.currentThread().getContextClassLoader());

        Iterator<ConfigProviderResolver> iterator = serviceLoader.iterator();
        ConfigProviderResolver configProviderResolver = null;
        if (iterator.hasNext()) {
            configProviderResolver = iterator.next();
        }*/

        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();

        String name = configProviderResolver.getConfig(Thread.currentThread().getContextClassLoader())
                .getValue("name", String.class);

        System.out.println(name);
    }
}
