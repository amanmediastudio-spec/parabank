package com.parabank.automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Enterprise element interaction utilities combining explicit synchronization,
 * retry logic, scrolling, and defensive actions.
 */
public class ElementActions {
    private static final Logger log = LoggerFactory.getLogger(ElementActions.class);
    private final WebDriver driver;
    private final WaitUtils waitUtils;

    public ElementActions(WebDriver driver, WaitUtils waitUtils) {
        this.driver = driver;
        this.waitUtils = waitUtils;
    }

    public void click(By locator) {
        log.info("Clicking on element: {}", locator);
        waitUtils.waitForClickable(locator).click();
    }

    public void jsClick(By locator) {
        log.info("JS clicking on element: {}", locator);
        WebElement element = waitUtils.waitForPresence(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void type(By locator, String text) {
        log.info("Typing '{}' into element: {}", text, locator);
        WebElement element = waitUtils.waitForVisibility(locator);
        element.sendKeys(text);
    }

    public void clearAndType(By locator, String text) {
        log.info("Clearing and typing '{}' into element: {}", text, locator);
        WebElement element = waitUtils.waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator) {
        WebElement element = waitUtils.waitForVisibility(locator);
        String text = element.getText().trim();
        log.debug("Read text '{}' from element: {}", text, locator);
        return text;
    }

    public String getValue(By locator) {
        WebElement element = waitUtils.waitForPresence(locator);
        return element.getAttribute("value");
    }

    public boolean isDisplayed(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void selectByVisibleText(By locator, String visibleText) {
        log.info("Selecting option '{}' from dropdown: {}", visibleText, locator);
        WebElement element = waitUtils.waitForClickable(locator);
        new Select(element).selectByVisibleText(visibleText);
    }

    public void selectByValue(By locator, String value) {
        log.info("Selecting option value '{}' from dropdown: {}", value, locator);
        WebElement element = waitUtils.waitForClickable(locator);
        new Select(element).selectByValue(value);
    }

    public void selectByIndex(By locator, int index) {
        log.info("Selecting option index '{}' from dropdown: {}", index, locator);
        WebElement element = waitUtils.waitForClickable(locator);
        new Select(element).selectByIndex(index);
    }

    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    public void scrollToElement(By locator) {
        WebElement element = waitUtils.waitForPresence(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public static double parseCurrency(String amountStr) {
        if (amountStr == null || amountStr.isBlank()) {
            return 0.0;
        }
        String clean = amountStr.replace("$", "").replace(",", "").trim();
        return Double.parseDouble(clean);
    }
}
