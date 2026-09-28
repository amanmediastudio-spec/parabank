package com.parabank.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for Request Loan service.
 */
public class RequestLoanPage extends BasePage {
    private final By amountInput = By.id("amount");
    private final By downPaymentInput = By.id("downPayment");
    private final By fromAccountIdSelect = By.id("fromAccountId");
    private final By applyNowButton = By.cssSelector("input.button[value='Apply Now']");

    private final By resultContainer = By.id("requestLoanResult");
    private final By loanStatusText = By.id("loanStatus");
    private final By newAccountIdLink = By.id("newAccountId");
    private final By loanDeniedError = By.cssSelector("#loanRequestDenied p.error, div#loanRequestDenied p");

    public RequestLoanPage(WebDriver driver) {
        super(driver);
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
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#amount').val(arguments[0]); " +
                "$('#downPayment').val(arguments[1]); " +
                "$('#fromAccountId').val(arguments[2]).trigger('change'); " +
                "$('input[type=button]').click();",
                amountStr, downPaymentStr, fromAccountId
        );
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
