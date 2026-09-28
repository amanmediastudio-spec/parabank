package com.parabank.automation.pages;

import com.parabank.automation.utils.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for Account Activity / Account Details page.
 */
public class AccountDetailsPage extends BasePage {
    private final By accountIdText = By.id("accountId");
    private final By accountTypeText = By.id("accountType");
    private final By balanceText = By.id("balance");
    private final By availableBalanceText = By.id("availableBalance");
    private final By transactionRows = By.cssSelector("#transactionTable tbody tr");
    private final By transactionLinks = By.cssSelector("#transactionTable tbody tr td a");

    public AccountDetailsPage(WebDriver driver) {
        super(driver);
    }

    public String getAccountId() {
        return actions.getText(accountIdText);
    }

    public String getAccountType() {
        return actions.getText(accountTypeText);
    }

    public double getBalance() {
        return ElementActions.parseCurrency(actions.getText(balanceText));
    }

    public double getAvailableBalance() {
        return ElementActions.parseCurrency(actions.getText(availableBalanceText));
    }

    public void waitForTransactionsToLoad() {
        waitUtils.waitForAjax();
        waitUtils.waitForVisibility(accountIdText);
    }

    public int getTransactionCount() {
        waitForTransactionsToLoad();
        List<WebElement> rows = driver.findElements(transactionRows);
        return rows.size();
    }

    public boolean hasTransaction(String expectedDescription, double expectedAmount) {
        waitForTransactionsToLoad();
        List<WebElement> rows = driver.findElements(transactionRows);
        for (WebElement row : rows) {
            String text = row.getText();
            if (text.contains(expectedDescription)) {
                // Check if amount is present
                String amountStr = String.format("%.2f", expectedAmount);
                if (text.contains(amountStr)) {
                    log.info("Found matching transaction: {} with amount: {}", expectedDescription, expectedAmount);
                    return true;
                }
            }
        }
        return false;
    }

    public String getFirstTransactionId() {
        waitForTransactionsToLoad();
        WebElement firstLink = waitUtils.waitForVisibility(transactionLinks);
        return firstLink.getText().trim();
    }

    public void clickFirstTransaction() {
        WebElement firstLink = waitUtils.waitForClickable(transactionLinks);
        firstLink.click();
        waitUtils.waitForAjax();
    }
}
