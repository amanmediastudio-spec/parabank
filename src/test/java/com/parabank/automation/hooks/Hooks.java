package com.parabank.automation.hooks;

import com.automation.driver.DriverManager;

import com.parabank.automation.config.ConfigReader;
import com.parabank.automation.context.ScenarioContext;
import com.parabank.automation.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enterprise Cucumber Lifecycle Hooks managing driver lifecycle,
 * failure screenshots, and ScenarioContext teardown.
 * <p>
 * Zero-arg constructor — no DI container required.
 */
public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    public Hooks() {
        // Zero-arg: no DI injection needed
    }

    @Before(order = 0)
    public void setupScenario(Scenario scenario) {
        log.info("================================================================================");
        log.info("STARTING SCENARIO: {}", scenario.getName());
        log.info("Tags: {}", scenario.getSourceTagNames());
        log.info("================================================================================");

        WebDriver driver = DriverManager.getDriver();
        driver.get(ConfigReader.getBaseUrl());
    }

    @After(order = 1)
    public void captureFailureScreenshot(Scenario scenario) {
        if (scenario.isFailed()) {
            log.error("Scenario FAILED: {}. Capturing failure screenshot...", scenario.getName());
            WebDriver driver = DriverManager.getDriver();
            if (driver instanceof TakesScreenshot ts) {
                byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failure Screenshot - " + scenario.getName());
            }
        } else {
            log.info("Scenario PASSED: {}", scenario.getName());
        }
    }

    @After(order = 0)
    public void tearDown() {
        log.info("Tearing down WebDriver session and clearing ScenarioContext.");
        ScenarioContext.reset();
        DriverManager.quitDriver();
    }
}
