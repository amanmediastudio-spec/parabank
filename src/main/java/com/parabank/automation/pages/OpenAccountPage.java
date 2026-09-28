package com.parabank.automation.pages;

import com.parabank.automation.models.AccountType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for Open New Account page.
 */
public class OpenAccountPage extends BasePage {
    private final By accountTypeSelect = By.id("type");
    private final By fromAccountSelect = By.id("fromAccountId");
    private final By openAccountButton = By.cssSelector("input.button[value='Open New Account']");
    private final By resultContainer = By.id("openAccountResult");
    private final By newAccountIdLink = By.id("newAccountId");

    public OpenAccountPage(WebDriver driver) {
        super(driver);
    }

    public void waitForDropdownsToLoad() {
        waitUtils.waitForVisibility(accountTypeSelect);
        waitUtils.waitForCondition(d -> {
            try {
                Select fromSelect = new Select(d.findElement(fromAccountSelect));
                return !fromSelect.getOptions().isEmpty();
            } catch (Exception e) {
                return false;
            }
        }, 15);
        waitUtils.waitForAjax();
    }

    public void selectAccountType(AccountType type) {
        log.info("Selecting account type: {}", type);
        waitForDropdownsToLoad();
        actions.selectByValue(accountTypeSelect, type.getValue());
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#type').val(arguments[0]).trigger('change');", type.getValue()
        );
    }

    public void selectFromAccount(String accountId) {
        log.info("Selecting source account: {}", accountId);
        waitForDropdownsToLoad();
        actions.selectByVisibleText(fromAccountSelect, accountId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#fromAccountId').val(arguments[0]).trigger('change');", accountId
        );
    }

    public void clickOpenAccount() {
        log.info("Submitting open account request");
        actions.scrollToElement(openAccountButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#type').trigger('change'); $('#fromAccountId').trigger('change'); $('input[value=\"Open New Account\"]').trigger('click');"
        );
        waitUtils.waitForAjax();
        waitUtils.waitForVisibility(resultContainer);
    }

    public String getNewAccountId() {
        WebElement link = waitUtils.waitForVisibility(newAccountIdLink);
        String id = link.getText().trim();
        log.info("Newly created account number: {}", id);
        return id;
    }

    public void clickNewAccount() {
        actions.click(newAccountIdLink);
        waitUtils.waitForAjax();
    }
}
