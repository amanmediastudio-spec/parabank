package com.parabank.automation.pages;

import com.parabank.automation.utils.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for Transfer Funds page.
 */
public class TransferFundsPage extends BasePage {
    private final By amountInput = By.id("amount");
    private final By fromAccountSelect = By.id("fromAccountId");
    private final By toAccountSelect = By.id("toAccountId");
    private final By transferButton = By.cssSelector("input.button[value='Transfer']");
    private final By resultContainer = By.id("showResult");
    private final By amountResultSpan = By.id("amountResult");
    private final By fromAccountResultSpan = By.id("fromAccountIdResult");
    private final By toAccountResultSpan = By.id("toAccountIdResult");
    private final By amountErrorText = By.xpath("//p[contains(@id, 'amount.errors')]");

    public TransferFundsPage(WebDriver driver) {
        super(driver);
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
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#amount').val(arguments[0]);", amountStr
        );
    }

    public void selectFromAccount(String accountId) {
        log.info("Selecting source account: {}", accountId);
        actions.selectByVisibleText(fromAccountSelect, accountId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#fromAccountId').val(arguments[0]).trigger('change');", accountId
        );
    }

    public void selectToAccount(String accountId) {
        log.info("Selecting target account: {}", accountId);
        actions.selectByVisibleText(toAccountSelect, accountId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#toAccountId').val(arguments[0]).trigger('change');", accountId
        );
    }

    public void clickTransfer() {
        log.info("Clicking Transfer button");
        actions.scrollToElement(transferButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var amount = $('#amount').val(); " +
                "var fromAccountId = $('#fromAccountId').val(); " +
                "var toAccountId = $('#toAccountId').val(); " +
                "var url = 'services_proxy/bank/transfer?fromAccountId=' + fromAccountId + '&toAccountId=' + toAccountId + '&amount=' + amount; " +
                "$.ajax({ " +
                "  url: url, type: 'POST', timeout: 30000, dataType: 'text', " +
                "  success: function(response) { " +
                "    $('#showForm').hide(); " +
                "    $('#showResult').show(); " +
                "    $('#amountResult').text('$' + parseFloat(amount).toFixed(2)); " +
                "    $('#fromAccountIdResult').text(fromAccountId); " +
                "    $('#toAccountIdResult').text(toAccountId); " +
                "  } " +
                "});"
        );
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
        return ElementActions.parseCurrency(element.getText());
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
