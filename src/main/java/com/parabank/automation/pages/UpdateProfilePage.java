package com.parabank.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for Update Profile / Contact Info.
 */
public class UpdateProfilePage extends BasePage {
    private final By firstNameInput = By.id("customer.firstName");
    private final By lastNameInput = By.id("customer.lastName");
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
                String first = d.findElement(firstNameInput).getAttribute("value");
                return first != null && !first.isBlank();
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
                "$('input[value=\"Update Profile\"]').trigger('click');"
        );
        waitUtils.waitForAjax();
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
        return actions.getValue(streetInput);
    }

    public String getCity() {
        waitForFormToPopulate();
        return actions.getValue(cityInput);
    }

    public String getState() {
        waitForFormToPopulate();
        return actions.getValue(stateInput);
    }

    public String getZipCode() {
        waitForFormToPopulate();
        return actions.getValue(zipCodeInput);
    }

    public String getPhoneNumber() {
        waitForFormToPopulate();
        return actions.getValue(phoneInput);
    }
}
