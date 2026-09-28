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

/**
 * Migrated Page Object strictly compliant with Platform SDK Core.
 * Original Source: UpdateProfilePage
 */
public class UpdateProfilePage extends BasePage {

    // Registered SDK Page Elements (Self-Healing Enabled)
    public PageElement firstNameInput;
    public PageElement lastNameInput;
    public PageElement streetInput;
    public PageElement cityInput;
    public PageElement stateInput;
    public PageElement zipCodeInput;
    public PageElement phoneInput;
    public PageElement updateProfileButton;
    public PageElement resultContainer;

    public UpdateProfilePage() {
        super("UpdateProfilePage");
    }

    public UpdateProfilePage(String pageName) {
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
        updateProfileButton = register("updateProfileButton", "updateProfileButton", By.cssSelector("input.button[value='Update Profile']"));
        resultContainer = register("resultContainer", "resultContainer", By.id("updateProfileResult"));
    }

    public void waitForFormToPopulate() {
        waitUtils.waitForVisibility(streetInput);
        try {
            waitUtils.waitForCondition(d -> {
    String first = (String) ((org.openqa.selenium.JavascriptExecutor) d).executeScript("return $('#customer\\\\.firstName').val();");
    return first != null && !first.trim().isEmpty();
}, 10);
        } catch (Exception e) {
            log.debug("Form population wait finished: {}", e.getMessage());
        }
        waitUtils.waitForAjax();
    }

    public void updateAddressDetails(String street, String city, String state, String zipCode, String phone) {
        log.info("Updating contact info: Street='{}', City='{}', State='{}', Zip='{}', Phone='{}'", street, city, state, zipCode, phone);
        waitForFormToPopulate();
        actions.clearAndType(streetInput, street);
        actions.clearAndType(cityInput, city);
        actions.clearAndType(stateInput, state);
        actions.clearAndType(zipCodeInput, zipCode);
        actions.clearAndType(phoneInput, phone);
        actions.scrollToElement(updateProfileButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#customer\\\\.address\\\\.street').val(arguments[0]); " + "$('#customer\\\\.address\\\\.city').val(arguments[1]); " + "$('#customer\\\\.address\\\\.state').val(arguments[2]); " + "$('#customer\\\\.address\\\\.zipCode').val(arguments[3]); " + "$('#customer\\\\.phoneNumber').val(arguments[4]); " + "$('input[type=button]').click();", street, city, state, zipCode, phone);
        waitUtils.waitForAjax();
        waitUtils.waitForVisibility(resultContainer);
    }

    public boolean isProfileUpdateSuccessful() {
        try {
            waitUtils.waitForVisibility(resultContainer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getStreet() {
        waitForFormToPopulate();
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return $('#customer\\\\.address\\\\.street').val();");
    }

    public String getCity() {
        waitForFormToPopulate();
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return $('#customer\\\\.address\\\\.city').val();");
    }

    public String getState() {
        waitForFormToPopulate();
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return $('#customer\\\\.address\\\\.state').val();");
    }

    public String getZipCode() {
        waitForFormToPopulate();
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return $('#customer\\\\.address\\\\.zipCode').val();");
    }

    public String getPhoneNumber() {
        waitForFormToPopulate();
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return $('#customer\\\\.phoneNumber').val();");
    }

}
