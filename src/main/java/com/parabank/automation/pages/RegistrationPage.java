package com.parabank.automation.pages;

import com.parabank.automation.models.CustomerProfile;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for Parabank Registration Page.
 */
public class RegistrationPage extends BasePage {
    private final By firstNameInput = By.id("customer.firstName");
    private final By lastNameInput = By.id("customer.lastName");
    private final By streetInput = By.id("customer.address.street");
    private final By cityInput = By.id("customer.address.city");
    private final By stateInput = By.id("customer.address.state");
    private final By zipCodeInput = By.id("customer.address.zipCode");
    private final By phoneInput = By.id("customer.phoneNumber");
    private final By ssnInput = By.id("customer.ssn");
    private final By usernameInput = By.id("customer.username");
    private final By passwordInput = By.id("customer.password");
    private final By confirmPasswordInput = By.id("repeatedPassword");
    private final By registerButton = By.cssSelector("input.button[value='Register']");
    private final By successTitle = By.xpath("//div[@id='rightPanel']//h1[@class='title']");
    private final By successDescription = By.xpath("//div[@id='rightPanel']//p[contains(text(),'Your account was created successfully')]");
    private final By usernameError = By.xpath("//span[contains(@id,'customer.username.errors')]");

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    public void register(CustomerProfile profile) {
        log.info("Filling registration form for user: {}", profile.username());
        actions.clearAndType(firstNameInput, profile.firstName());
        actions.clearAndType(lastNameInput, profile.lastName());
        actions.clearAndType(streetInput, profile.address());
        actions.clearAndType(cityInput, profile.city());
        actions.clearAndType(stateInput, profile.state());
        actions.clearAndType(zipCodeInput, profile.zipCode());
        actions.clearAndType(phoneInput, profile.phoneNumber());
        actions.clearAndType(ssnInput, profile.ssn());
        actions.clearAndType(usernameInput, profile.username());
        actions.clearAndType(passwordInput, profile.password());
        actions.clearAndType(confirmPasswordInput, profile.password());
        actions.click(registerButton);
        waitUtils.waitForAjax();
    }

    public boolean isRegistrationSuccessful() {
        return actions.isDisplayed(successDescription);
    }

    public String getSuccessTitle() {
        return actions.getText(successTitle);
    }

    public String getUsernameErrorMessage() {
        return actions.getText(usernameError);
    }
}
