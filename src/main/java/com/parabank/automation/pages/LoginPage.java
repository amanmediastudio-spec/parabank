package com.parabank.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for Parabank Login Page.
 */
public class LoginPage extends BasePage {
    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.cssSelector("input.button[value='Log In']");
    private final By registerLink = By.linkText("Register");
    private final By errorMessage = By.cssSelector("p.error");
    private final By loginPanel = By.id("loginPanel");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo(String url) {
        log.info("Navigating to URL: {}", url);
        driver.get(url);
        waitUtils.waitForAjax();
    }

    public void enterUsername(String username) {
        actions.clearAndType(usernameInput, username);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('input[name=username]').val(arguments[0]);", username
        );
    }

    public void enterPassword(String password) {
        actions.clearAndType(passwordInput, password);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('input[name=password]').val(arguments[0]);", password
        );
    }

    public void clickLogin() {
        log.info("Submitting login form");
        actions.scrollToElement(loginButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('form[name=login]').submit();"
        );
        waitUtils.waitForAjax();
        try {
            waitUtils.waitForCondition(d -> {
                List<WebElement> logoutLinks = d.findElements(By.xpath("//div[@id='leftPanel']//a[contains(@href, 'logout.htm')]"));
                if (!logoutLinks.isEmpty() && logoutLinks.get(0).isDisplayed()) {
                    return true;
                }
                return d.findElements(errorMessage).stream().anyMatch(WebElement::isDisplayed);
            }, 15);
        } catch (Exception e) {
            log.debug("Wait for login transition completed or timed out: {}", e.getMessage());
        }
    }

    public void login(String username, String password) {
        log.info("Logging in with username: {}", username);
        if (navigationMenu.isUserLoggedIn()) {
            if (navigationMenu.getWelcomeMessage().contains(username)) {
                log.info("User '{}' is already logged in. Proceeding.", username);
                return;
            } else {
                log.info("Different user logged in. Logging out first.");
                navigationMenu.clickLogOut();
            }
        }
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public void clickRegister() {
        log.info("Clicking on Register link");
        actions.click(registerLink);
        waitUtils.waitForAjax();
    }

    public String getErrorMessage() {
        return actions.getText(errorMessage);
    }

    public boolean isLoginFormPresent() {
        return actions.isDisplayed(usernameInput) && actions.isDisplayed(passwordInput);
    }
}
