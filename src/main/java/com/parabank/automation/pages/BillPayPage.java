package com.parabank.automation.pages;

import com.parabank.automation.models.BillPayee;
import com.parabank.automation.utils.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object for Bill Pay service.
 */
public class BillPayPage extends BasePage {
    private final By payeeNameInput = By.name("payee.name");
    private final By addressInput = By.name("payee.address.street");
    private final By cityInput = By.name("payee.address.city");
    private final By stateInput = By.name("payee.address.state");
    private final By zipCodeInput = By.name("payee.address.zipCode");
    private final By phoneInput = By.name("payee.phoneNumber");
    private final By accountInput = By.name("payee.accountNumber");
    private final By verifyAccountInput = By.name("verifyAccount");
    private final By amountInput = By.name("amount");
    private final By fromAccountIdSelect = By.name("fromAccountId");
    private final By sendPaymentButton = By.cssSelector("input.button[value='Send Payment']");

    private final By resultContainer = By.id("billpayResult");
    private final By payeeNameResult = By.id("payeeName");
    private final By amountResult = By.id("amount");
    private final By fromAccountResult = By.id("fromAccountId");

    public BillPayPage(WebDriver driver) {
        super(driver);
    }

    public void waitForAccountDropdownToLoad() {
        waitUtils.waitForClickable(fromAccountIdSelect);
        waitUtils.waitForCondition(d -> {
            Select select = new Select(d.findElement(fromAccountIdSelect));
            return !select.getOptions().isEmpty();
        }, 10);
    }

    public void fillPayeeForm(BillPayee payee) {
        log.info("Filling Bill Pay form for payee: {}", payee.name());
        actions.clearAndType(payeeNameInput, payee.name());
        actions.clearAndType(addressInput, payee.address());
        actions.clearAndType(cityInput, payee.city());
        actions.clearAndType(stateInput, payee.state());
        actions.clearAndType(zipCodeInput, payee.zipCode());
        actions.clearAndType(phoneInput, payee.phoneNumber());
        actions.clearAndType(accountInput, payee.accountNumber());
        actions.clearAndType(verifyAccountInput, payee.verifyAccount());
        String amountStr = payee.amount() > 0 ? String.format(java.util.Locale.US, "%.2f", payee.amount()) : "";
        if (!amountStr.isEmpty()) {
            actions.clearAndType(amountInput, amountStr);
        }
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('[name=\"payee.name\"]').val(arguments[0]).trigger('change');" +
                "$('[name=\"payee.address.street\"]').val(arguments[1]).trigger('change');" +
                "$('[name=\"payee.address.city\"]').val(arguments[2]).trigger('change');" +
                "$('[name=\"payee.address.state\"]').val(arguments[3]).trigger('change');" +
                "$('[name=\"payee.address.zipCode\"]').val(arguments[4]).trigger('change');" +
                "$('[name=\"payee.phoneNumber\"]').val(arguments[5]).trigger('change');" +
                "$('[name=\"payee.accountNumber\"]').val(arguments[6]).trigger('change');" +
                "$('[name=\"verifyAccount\"]').val(arguments[7]).trigger('change');" +
                (amountStr.isEmpty() ? "" : "$('[name=\"amount\"]').val(arguments[8]).trigger('change');"),
                payee.name(), payee.address(), payee.city(), payee.state(), payee.zipCode(),
                payee.phoneNumber(), payee.accountNumber(), payee.verifyAccount(), amountStr
        );
    }

    public void setAmount(String amount) {
        actions.clearAndType(amountInput, amount);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('[name=\"amount\"]').val(arguments[0]).trigger('change');", amount
        );
    }

    public void setVerifyAccount(String verifyAccount) {
        actions.clearAndType(verifyAccountInput, verifyAccount);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('[name=\"verifyAccount\"]').val(arguments[0]).trigger('change');", verifyAccount
        );
    }

    public void selectFromAccount(String accountId) {
        waitForAccountDropdownToLoad();
        actions.selectByVisibleText(fromAccountIdSelect, accountId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('[name=\"fromAccountId\"]').val(arguments[0]).trigger('change');", accountId
        );
    }

    public void clickSendPayment() {
        log.info("Clicking Send Payment button");
        actions.scrollToElement(sendPaymentButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('input[type=button]').click();"
        );
        waitUtils.waitForAjax();
    }

    public void payBill(BillPayee payee, String fromAccount) {
        fillPayeeForm(payee);
        selectFromAccount(fromAccount);
        clickSendPayment();
    }

    public boolean isPaymentSuccessful() {
        try {
            waitUtils.waitForVisibility(resultContainer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getConfirmedPayeeName() {
        return actions.getText(payeeNameResult);
    }

    public double getConfirmedAmount() {
        return ElementActions.parseCurrency(actions.getText(amountResult));
    }

    public String getConfirmedFromAccountId() {
        return actions.getText(fromAccountResult);
    }

    public boolean hasValidationError(String partialErrorMessage) {
        List<WebElement> errors = driver.findElements(By.cssSelector("span.error, [id^=validationModel]"));
        for (WebElement err : errors) {
            if (err.isDisplayed() && err.getText().toLowerCase().contains(partialErrorMessage.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
