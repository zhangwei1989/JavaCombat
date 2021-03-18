package org.combat.projects.user.logging;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author zhangwei
 * @Description UserWebLoggingConfiguration
 * @Date: 2021/3/19 00:58
 */
public class UserWebLoggingConfiguration {

    public UserWebLoggingConfiguration() throws UnsupportedEncodingException {
        System.out.println("UserWebLoggingConfiguration");
        Logger logger = Logger.getLogger("org.combat");
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setEncoding("UTF-8");
        consoleHandler.setLevel(Level.WARNING);
        logger.addHandler(consoleHandler);
    }

    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger("org.combat");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("META-INF/logging.properties")) {
            LogManager logManager = LogManager.getLogManager();
            logManager.readConfiguration(inputStream);
        }

        logger.info("Hello, Info");
        logger.warning("Hello, Warning");
    }
}
