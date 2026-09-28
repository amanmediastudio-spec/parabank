package com.parabank.automation.pages;

import com.automation.pages.BasePage;
import com.automation.ai.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.ArrayList;
import com.automation.utils.ElementActions;
import com.automation.utils.WaitUtils;
import com.automation.driver.DriverManager;
import com.parabank.automation.models.CustomerProfile;

/**
 * Migrated Page Object strictly compliant with Platform SDK Core.
 * Original Source: RegistrationPage
 */
public class RegistrationPage extends BasePage {

    // Registered SDK Page Elements (Self-Healing Enabled)
    public PageElement firstNameInput;
    public PageElement lastNameInput;
    public PageElement streetInput;
    public PageElement cityInput;
    public PageElement stateInput;
    public PageElement zipCodeInput;
    public PageElement phoneInput;
    public PageElement ssnInput;
    public PageElement usernameInput;
    public PageElement passwordInput;
    public PageElement confirmPasswordInput;
    public PageElement registerButton;
    public PageElement successTitle;
    public PageElement successDescription;
    public PageElement usernameError;

    public RegistrationPage() {
        super("RegistrationPage");
    }

    public RegistrationPage(String pageName) {
        super(pageName);
    }

    @Override
    protected void initElements() {
        firstNameInput = register("firstNameInput", "firstNameInput", By.id("customer.firstName"));
        lastNameInput = register("lastNameInput", "lastNameInput", By.id("customer.lastName"));
        streetInput = register("streetInput", "streetInput", By.id("customer.address.street"));
        cityInput = register("cityInput", "cityInput", By.id("customer.address.city"));
        stateInput = register("stateInput", "stateInput", By.id("customer.address.state"));
        zipCodeInput = register("zipCodeInput", "zipCodeInput", By.id("customer.address.zipCode"));
        phoneInput = register("phoneInput", "phoneInput", By.id("customer.phoneNumber"));
        ssnInput = register("ssnInput", "ssnInput", By.id("customer.ssn"));
        usernameInput = register("usernameInput", "usernameInput", By.id("customer.username"));
        passwordInput = register("passwordInput", "passwordInput", By.id("customer.password"));
        confirmPasswordInput = register("confirmPasswordInput", "confirmPasswordInput", By.id("repeatedPassword"));
        registerButton = register("registerButton", "registerButton", By.cssSelector("input.button[value='Register']"));
        successTitle = register("successTitle", "successTitle", By.xpath("//div[@id='rightPanel']//h1[@class='title']"));
        successDescription = register("successDescription", "successDescription", By.xpath("//div[@id='rightPanel']//p[contains(text(),'Your account was created successfully')]"));
        usernameError = register("usernameError", "usernameError", By.xpath("//span[contains(@id,'customer.username.errors')]"));
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
