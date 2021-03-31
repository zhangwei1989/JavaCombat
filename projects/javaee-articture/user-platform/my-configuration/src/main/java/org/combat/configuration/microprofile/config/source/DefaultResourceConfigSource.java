package org.combat.configuration.microprofile.config.source;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author zhangwei
 * @Description DefaultResourceConfigSource
 * @Date: 2021/3/24 23:47
 */
public class DefaultResourceConfigSource extends MapBasedConfigSource {

    private static final String configFileLocation = "META-INF/microprofile-config.properties";

    private final Logger logger = Logger.getLogger(DefaultResourceConfigSource.class.getName());

    protected DefaultResourceConfigSource() {
        super("Default Config File", 100);
    }

    @Override
    protected void prepareConfigData(Map configMap) throws Throwable {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(configFileLocation);
        if (resource == null) {
            logger.info("The default config file can't be found in the classpath: " + configFileLocation);
            return;
        }

        try (InputStream inputStream = resource.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            configMap.putAll(properties);
        }
    }

}
