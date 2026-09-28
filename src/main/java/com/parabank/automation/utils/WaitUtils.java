package com.parabank.automation.utils;

import com.parabank.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Enterprise synchronization utilities handling explicit waits,
 * dynamic JavaScript/AJAX completion, and DOM stability.
 */
public class WaitUtils {
    private static final Logger log = LoggerFactory.getLogger(WaitUtils.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWaitSeconds()));
    }

    public WebElement waitForVisibility(By locator) {
        log.debug("Waiting for visibility of element: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        log.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public List<WebElement> waitForPresenceOfAll(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public boolean waitForTextToBePresent(By locator, String text) {
        log.debug("Waiting for text '{}' to be present in element: {}", text, locator);
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public boolean waitForUrlContains(String fraction) {
        log.debug("Waiting for URL to contain '{}'", fraction);
        return wait.until(ExpectedConditions.urlContains(fraction));
    }

    public void waitForAjax() {
        try {
            wait.until(d -> {
                JavascriptExecutor js = (JavascriptExecutor) d;
                boolean isPageReady = "complete".equals(js.executeScript("return document.readyState"));
                Boolean isJqueryInactive = (Boolean) js.executeScript(
                        "return (window.jQuery != undefined) ? (jQuery.active === 0) : true;"
                );
                return isPageReady && Boolean.TRUE.equals(isJqueryInactive);
            });
        } catch (Exception e) {
            log.debug("AJAX/Page readiness wait exception (continuing): {}", e.getMessage());
        }
    }

    public void waitForCondition(java.util.function.Function<WebDriver, Boolean> condition, long timeoutSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(condition);
    }
}
