package com.parabank.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Enterprise ConfigReader managing test configuration settings
 * with priority given to JVM System Properties (-D) and environment variables.
 */
public final class ConfigReader {
    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private ConfigReader() {
        // Utility class
    }

    private static void loadProperties() {
        String propFileName = "config.properties";
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(propFileName)) {
            if (inputStream != null) {
                properties.load(inputStream);
                log.info("Successfully loaded configuration properties from {}", propFileName);
            } else {
                log.warn("Property file '{}' not found in classpath. Relying on System properties.", propFileName);
            }
        } catch (IOException e) {
            log.error("Failed to load configuration properties from {}: {}", propFileName, e.getMessage());
            throw new RuntimeException("Could not load test configuration", e);
        }
    }

    public static String getProperty(String key) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp;
        }
        String envKey = key.toUpperCase().replace('.', '_');
        String envProp = System.getenv(envKey);
        if (envProp != null && !envProp.isBlank()) {
            return envProp;
        }
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }

    public static String getBaseUrl() {
        return getProperty("base.url", "https://parabank.parasoft.com/parabank/index.htm");
    }

    public static String getAdminUrl() {
        return getProperty("admin.url", "https://parabank.parasoft.com/parabank/admin.htm");
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome").toLowerCase();
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "true"));
    }

    public static int getExplicitWaitSeconds() {
        return Integer.parseInt(getProperty("explicit.wait.seconds", "15"));
    }

    public static int getPageLoadTimeoutSeconds() {
        return Integer.parseInt(getProperty("page.load.timeout.seconds", "30"));
    }

    public static String getDefaultUsername() {
        return getProperty("default.username", "HellBound");
    }

    public static String getDefaultPassword() {
        return getProperty("default.password", "HellBound");
    }
}
