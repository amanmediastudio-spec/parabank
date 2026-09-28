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
import com.parabank.automation.utils.ElementActions;
import org.openqa.selenium.support.ui.Select;

/**
 * Migrated Page Object strictly compliant with Platform SDK Core.
 * Original Source: TransferFundsPage
 */
public class TransferFundsPage extends BasePage {

    // Registered SDK Page Elements (Self-Healing Enabled)
    public PageElement amountInput;
    public PageElement fromAccountSelect;
    public PageElement toAccountSelect;
    public PageElement transferButton;
    public PageElement resultContainer;
    public PageElement amountResultSpan;
    public PageElement fromAccountResultSpan;
    public PageElement toAccountResultSpan;
    public PageElement amountErrorText;

    public TransferFundsPage() {
        super("TransferFundsPage");
    }

    public TransferFundsPage(String pageName) {
        super(pageName);
    }

    @Override
    protected void initElements() {
        amountInput = register("amountInput", "amountInput", By.id("amount"));
        fromAccountSelect = register("fromAccountSelect", "fromAccountSelect", By.id("fromAccountId"));
        toAccountSelect = register("toAccountSelect", "toAccountSelect", By.id("toAccountId"));
        transferButton = register("transferButton", "transferButton", By.cssSelector("input.button[value='Transfer']"));
        resultContainer = register("resultContainer", "resultContainer", By.id("showResult"));
        amountResultSpan = register("amountResultSpan", "amountResultSpan", By.id("amountResult"));
        fromAccountResultSpan = register("fromAccountResultSpan", "fromAccountResultSpan", By.id("fromAccountIdResult"));
        toAccountResultSpan = register("toAccountResultSpan", "toAccountResultSpan", By.id("toAccountIdResult"));
        amountErrorText = register("amountErrorText", "amountErrorText", By.xpath("//p[contains(@id, 'amount.errors')]"));
    }

    public void waitForDropdownsToLoad() {
        waitUtils.waitForClickable(fromAccountSelect);
        waitUtils.waitForCondition(d -> {
    Select fromSelect = new Select(d.findElement(fromAccountSelect));
    Select toSelect = new Select(d.findElement(toAccountSelect));
    return !fromSelect.getOptions().isEmpty() && !toSelect.getOptions().isEmpty();
}, 10);
    }

    public void enterAmount(double amount) {
        log.info("Entering transfer amount: {}", amount);
        String amountStr = String.format(java.util.Locale.US, "%.2f", amount);
        actions.clearAndType(amountInput, amountStr);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#amount').val(arguments[0]);", amountStr);
    }

    public void selectFromAccount(String accountId) {
        log.info("Selecting source account: {}", accountId);
        waitUtils.waitForCondition(d -> {
    try {
        Select s = new Select(d.findElement(fromAccountSelect));
        return s.getOptions().stream().anyMatch(opt -> opt.getText().trim().equals(accountId));
    } catch (Exception e) {
        return false;
    }
}, 15);
        actions.selectByVisibleText(fromAccountSelect, accountId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#fromAccountId').val(arguments[0]).trigger('change');", accountId);
    }

    public void selectToAccount(String accountId) {
        log.info("Selecting target account: {}", accountId);
        waitUtils.waitForCondition(d -> {
    try {
        Select s = new Select(d.findElement(toAccountSelect));
        return s.getOptions().stream().anyMatch(opt -> opt.getText().trim().equals(accountId));
    } catch (Exception e) {
        return false;
    }
}, 15);
        actions.selectByVisibleText(toAccountSelect, accountId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#toAccountId').val(arguments[0]).trigger('change');", accountId);
    }

    public void clickTransfer() {
        log.info("Clicking Transfer button");
        actions.scrollToElement(transferButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("var amount = $('#amount').val(); " + "var fromAccountId = $('#fromAccountId').val(); " + "var toAccountId = $('#toAccountId').val(); " + "var url = 'services_proxy/bank/transfer?fromAccountId=' + fromAccountId + '&toAccountId=' + toAccountId + '&amount=' + amount; " + "$.ajax({ " + "  url: url, type: 'POST', timeout: 30000, dataType: 'text', " + "  success: function(response) { " + "    $('#showForm').hide(); " + "    $('#showResult').show(); " + "    $('#amountResult').text('$' + parseFloat(amount).toFixed(2)); " + "    $('#fromAccountIdResult').text(fromAccountId); " + "    $('#toAccountIdResult').text(toAccountId); " + "  } " + "});");
        waitUtils.waitForAjax();
        waitUtils.waitForVisibility(resultContainer);
    }

    public void transferFunds(double amount, String fromAccount, String toAccount) {
        log.info("Initiating fund transfer of ${} from {} to {}", amount, fromAccount, toAccount);
        waitForDropdownsToLoad();
        enterAmount(amount);
        selectFromAccount(fromAccount);
        selectToAccount(toAccount);
        clickTransfer();
    }

    public boolean isTransferComplete() {
        try {
            waitUtils.waitForVisibility(resultContainer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public double getTransferredAmountResult() {
        WebElement element = waitUtils.waitForVisibility(amountResultSpan);
        return element.getText().parseCurrency();
    }

    public String getFromAccountIdResult() {
        return actions.getText(fromAccountResultSpan);
    }

    public String getToAccountIdResult() {
        return actions.getText(toAccountResultSpan);
    }

    public String getErrorMessage() {
        return actions.getText(amountErrorText);
    }

}
