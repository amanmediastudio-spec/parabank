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
import org.openqa.selenium.support.ui.Select;

/**
 * Migrated Page Object strictly compliant with Platform SDK Core.
 * Original Source: RequestLoanPage
 */
public class RequestLoanPage extends BasePage {

    // Registered SDK Page Elements (Self-Healing Enabled)
    public PageElement amountInput;
    public PageElement downPaymentInput;
    public PageElement fromAccountIdSelect;
    public PageElement applyNowButton;
    public PageElement resultContainer;
    public PageElement loanStatusText;
    public PageElement newAccountIdLink;
    public PageElement loanDeniedError;

    public RequestLoanPage() {
        super("RequestLoanPage");
    }

    public RequestLoanPage(String pageName) {
        super(pageName);
    }

    @Override
    protected void initElements() {
        amountInput = register("amountInput", "amountInput", By.id("amount"));
        downPaymentInput = register("downPaymentInput", "downPaymentInput", By.id("downPayment"));
        fromAccountIdSelect = register("fromAccountIdSelect", "fromAccountIdSelect", By.id("fromAccountId"));
        applyNowButton = register("applyNowButton", "applyNowButton", By.cssSelector("input.button[value='Apply Now']"));
        resultContainer = register("resultContainer", "resultContainer", By.id("requestLoanResult"));
        loanStatusText = register("loanStatusText", "loanStatusText", By.id("loanStatus"));
        newAccountIdLink = register("newAccountIdLink", "newAccountIdLink", By.id("newAccountId"));
        loanDeniedError = register("loanDeniedError", "loanDeniedError", By.cssSelector("#loanRequestDenied p.error, div#loanRequestDenied p"));
    }

    public void waitForAccountDropdownToLoad() {
        waitUtils.waitForClickable(fromAccountIdSelect);
        waitUtils.waitForCondition(d -> {
    Select select = new Select(d.findElement(fromAccountIdSelect));
    return !select.getOptions().isEmpty();
}, 10);
    }

    public void applyForLoan(double amount, double downPayment, String fromAccountId) {
        log.info("Applying for loan: Amount=${}, DownPayment=${}, FromAccount={}", amount, downPayment, fromAccountId);
        waitForAccountDropdownToLoad();
        String amountStr = String.format(java.util.Locale.US, "%.2f", amount);
        String downPaymentStr = String.format(java.util.Locale.US, "%.2f", downPayment);
        actions.clearAndType(amountInput, amountStr);
        actions.clearAndType(downPaymentInput, downPaymentStr);
        actions.selectByVisibleText(fromAccountIdSelect, fromAccountId);
        actions.scrollToElement(applyNowButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#amount').val(arguments[0]); " + "$('#downPayment').val(arguments[1]); " + "$('#fromAccountId').val(arguments[2]).trigger('change'); " + "$('input[type=button]').click();", amountStr, downPaymentStr, fromAccountId);
        waitUtils.waitForAjax();
        waitUtils.waitForVisibility(resultContainer);
    }

    public String getLoanStatus() {
        WebElement element = waitUtils.waitForVisibility(loanStatusText);
        return element.getText().trim();
    }

    public String getNewLoanAccountId() {
        WebElement link = waitUtils.waitForVisibility(newAccountIdLink);
        return link.getText().trim();
    }

    public String getDeniedErrorMessage() {
        WebElement error = waitUtils.waitForVisibility(loanDeniedError);
        return error.getText().trim();
    }

    public boolean isLoanResultDisplayed() {
        try {
            waitUtils.waitForVisibility(resultContainer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
