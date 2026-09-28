package com.parabank.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for Update Profile / Contact Info.
 */
public class UpdateProfilePage extends BasePage {
    private final By streetInput = By.id("customer.address.street");
    private final By cityInput = By.id("customer.address.city");
    private final By stateInput = By.id("customer.address.state");
    private final By zipCodeInput = By.id("customer.address.zipCode");
    private final By phoneInput = By.id("customer.phoneNumber");
    private final By updateProfileButton = By.cssSelector("input.button[value='Update Profile']");
    private final By resultContainer = By.id("updateProfileResult");

    public UpdateProfilePage(WebDriver driver) {
        super(driver);
    }

    public void waitForFormToPopulate() {
        waitUtils.waitForVisibility(streetInput);
        try {
            waitUtils.waitForCondition(d -> {
                String first = (String) ((org.openqa.selenium.JavascriptExecutor) d).executeScript(
                        "return $('#customer\\\\.firstName').val();"
                );
                return first != null && !first.trim().isEmpty();
            }, 10);
        } catch (Exception e) {
            log.debug("Form population wait finished: {}", e.getMessage());
        }
        waitUtils.waitForAjax();
    }

    public void updateAddressDetails(String street, String city, String state, String zipCode, String phone) {
        log.info("Updating contact info: Street='{}', City='{}', State='{}', Zip='{}', Phone='{}'",
                street, city, state, zipCode, phone);
        waitForFormToPopulate();
        actions.clearAndType(streetInput, street);
        actions.clearAndType(cityInput, city);
        actions.clearAndType(stateInput, state);
        actions.clearAndType(zipCodeInput, zipCode);
        actions.clearAndType(phoneInput, phone);
        actions.scrollToElement(updateProfileButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#customer\\\\.address\\\\.street').val(arguments[0]); " +
                "$('#customer\\\\.address\\\\.city').val(arguments[1]); " +
                "$('#customer\\\\.address\\\\.state').val(arguments[2]); " +
                "$('#customer\\\\.address\\\\.zipCode').val(arguments[3]); " +
                "$('#customer\\\\.phoneNumber').val(arguments[4]); " +
                "$('input[type=button]').click();",
                street, city, state, zipCode, phone
        );
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
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "return $('#customer\\\\.address\\\\.street').val();"
        );
    }

    public String getCity() {
        waitForFormToPopulate();
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "return $('#customer\\\\.address\\\\.city').val();"
        );
    }

    public String getState() {
        waitForFormToPopulate();
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "return $('#customer\\\\.address\\\\.state').val();"
        );
    }

    public String getZipCode() {
        waitForFormToPopulate();
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "return $('#customer\\\\.address\\\\.zipCode').val();"
        );
    }

    public String getPhoneNumber() {
        waitForFormToPopulate();
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "return $('#customer\\\\.phoneNumber').val();"
        );
    }
}
