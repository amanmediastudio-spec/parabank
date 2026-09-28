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
import com.parabank.automation.models.AccountType;
import org.openqa.selenium.support.ui.Select;

/**
 * Migrated Page Object strictly compliant with Platform SDK Core.
 * Original Source: OpenAccountPage
 */
public class OpenAccountPage extends BasePage {

    // Registered SDK Page Elements (Self-Healing Enabled)
    public PageElement accountTypeSelect;
    public PageElement fromAccountSelect;
    public PageElement openAccountButton;
    public PageElement resultContainer;
    public PageElement newAccountIdLink;

    public OpenAccountPage() {
        super("OpenAccountPage");
    }

    public OpenAccountPage(String pageName) {
        super(pageName);
    }

    @Override
    protected void initElements() {
        accountTypeSelect = register("accountTypeSelect", "accountTypeSelect", By.id("type"));
        fromAccountSelect = register("fromAccountSelect", "fromAccountSelect", By.id("fromAccountId"));
        openAccountButton = register("openAccountButton", "openAccountButton", By.cssSelector("input.button[value='Open New Account']"));
        resultContainer = register("resultContainer", "resultContainer", By.id("openAccountResult"));
        newAccountIdLink = register("newAccountIdLink", "newAccountIdLink", By.id("newAccountId"));
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
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#type').val(arguments[0]).trigger('change');", type.getValue());
    }

    public void selectFromAccount(String accountId) {
        log.info("Selecting source account: {}", accountId);
        waitForDropdownsToLoad();
        actions.selectByVisibleText(fromAccountSelect, accountId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#fromAccountId').val(arguments[0]).trigger('change');", accountId);
    }

    public void clickOpenAccount() {
        log.info("Submitting open account request");
        actions.scrollToElement(openAccountButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#type').trigger('change'); $('#fromAccountId').trigger('change'); $('input[value=\"Open New Account\"]').trigger('click');");
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

    public String getFirstAvailableSourceAccountId() {
        waitForDropdownsToLoad();
        Select fromSelect = new Select(com.automation.driver.DriverManager.getDriver().findElement(fromAccountSelect));
        java.util.List<WebElement> options = fromSelect.getOptions();
        if (!options.isEmpty()) {
            return options.get(0).getText().trim();
        }
        return null;
    }

}
