package com.parabank.automation.driver;

import com.parabank.automation.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Enterprise ThreadLocal WebDriver Manager supporting parallel execution,
 * multiple browser engines, and configurable execution profiles.
 */
public final class DriverManager {
    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {
        // Prevent instantiation
    }

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initDriver(ConfigReader.getBrowser(), ConfigReader.isHeadless());
        }
        return driverThreadLocal.get();
    }

    public static void initDriver(String browser, boolean headless) {
        log.info("Initializing WebDriver instance for browser: {}, headless: {}", browser, headless);
        WebDriver driver;

        switch (browser.toLowerCase()) {
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                options.addArguments("--width=1920", "--height=1080");
                driver = new FirefoxDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--start-maximized", "--disable-gpu", "--no-sandbox");
                driver = new EdgeDriver(options);
            }
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments(
                        "--start-maximized",
                        "--window-size=1920,1080",
                        "--disable-gpu",
                        "--no-sandbox",
                        "--disable-dev-shm-usage",
                        "--remote-allow-origins=*",
                        "--disable-notifications",
                        "--disable-popup-blocking"
                );
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                options.setExperimentalOption("prefs", prefs);

                driver = new ChromeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeoutSeconds()));
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
        log.info("WebDriver initialized successfully on thread {}", Thread.currentThread().getName());
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("WebDriver successfully terminated on thread {}", Thread.currentThread().getName());
            } catch (Exception e) {
                log.error("Exception while quitting WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
