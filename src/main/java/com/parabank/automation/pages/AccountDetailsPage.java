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

/**
 * Migrated Page Object strictly compliant with Platform SDK Core.
 * Original Source: AccountDetailsPage
 */
public class AccountDetailsPage extends BasePage {

    // Registered SDK Page Elements (Self-Healing Enabled)
    public PageElement accountIdText;
    public PageElement accountTypeText;
    public PageElement balanceText;
    public PageElement availableBalanceText;
    public PageElement transactionTable;
    public PageElement transactionRows;
    public PageElement transactionLinks;

    public AccountDetailsPage() {
        super("AccountDetailsPage");
    }

    public AccountDetailsPage(String pageName) {
        super(pageName);
    }

    @Override
    protected void initElements() {
        accountIdText = register("accountIdText", "accountIdText", By.id("accountId"));
        accountTypeText = register("accountTypeText", "accountTypeText", By.id("accountType"));
        balanceText = register("balanceText", "balanceText", By.id("balance"));
        availableBalanceText = register("availableBalanceText", "availableBalanceText", By.id("availableBalance"));
        transactionTable = register("transactionTable", "transactionTable", By.id("transactionTable"));
        transactionRows = register("transactionRows", "transactionRows", By.cssSelector("#transactionTable tbody tr"));
        transactionLinks = register("transactionLinks", "transactionLinks", By.cssSelector("#transactionTable tbody tr td a"));
    }

    public String getAccountId() {
        return actions.getText(accountIdText);
    }

    public String getAccountType() {
        return actions.getText(accountTypeText);
    }

    public double getBalance() {
        return actions.getText(balanceText).parseCurrency();
    }

    public double getAvailableBalance() {
        return actions.getText(availableBalanceText).parseCurrency();
    }

    public void waitForTransactionsToLoad() {
        waitUtils.waitForAjax();
        waitUtils.waitForVisibility(accountIdText);
    }

    public int getTransactionCount() {
        waitForTransactionsToLoad();
        List<WebElement> rows = findElements(transactionRows);
        return rows.size();
    }

    public boolean hasTransaction(String expectedDescription, double expectedAmount) {
        waitForTransactionsToLoad();
        List<WebElement> rows = findElements(transactionRows);
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
