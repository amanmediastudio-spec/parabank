package com.parabank.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for Find Transactions page supporting multi-criteria search:
 * Transaction ID, Date, Date Range, and Amount.
 */
public class FindTransactionsPage extends BasePage {
    private final By accountSelect = By.id("accountId");
    private final By transactionIdInput = By.id("transactionId");
    private final By findByIdButton = By.id("findById");

    private final By dateInput = By.id("transactionDate");
    private final By findByDateButton = By.id("findByDate");

    private final By fromDateInput = By.id("fromDate");
    private final By toDateInput = By.id("toDate");
    private final By findByDateRangeButton = By.id("findByDateRange");

    private final By amountInput = By.id("amount");
    private final By findByAmountButton = By.id("findByAmount");

    private final By transactionTable = By.id("transactionTable");
    private final By transactionRows = By.cssSelector("#transactionTable tbody tr");
    private final By transactionLinks = By.cssSelector("#transactionTable tbody tr td a");

    public FindTransactionsPage(WebDriver driver) {
        super(driver);
    }

    public void waitForAccountDropdownToLoad() {
        waitUtils.waitForClickable(accountSelect);
        waitUtils.waitForCondition(d -> {
            Select select = new Select(d.findElement(accountSelect));
            return !select.getOptions().isEmpty();
        }, 10);
    }

    public void selectAccount(String accountId) {
        log.info("Selecting account for transaction search: {}", accountId);
        waitForAccountDropdownToLoad();
        actions.selectByVisibleText(accountSelect, accountId);
    }

    public void searchById(String transactionId) {
        log.info("Searching transactions by ID: {}", transactionId);
        actions.clearAndType(transactionIdInput, transactionId);
        actions.click(findByIdButton);
        waitUtils.waitForAjax();
    }

    public void searchByDate(String date) {
        log.info("Searching transactions by Date: {}", date);
        actions.clearAndType(dateInput, date);
        actions.click(findByDateButton);
        waitUtils.waitForAjax();
    }

    public void searchByDateRange(String fromDate, String toDate) {
        log.info("Searching transactions by Date Range: {} to {}", fromDate, toDate);
        actions.clearAndType(fromDateInput, fromDate);
        actions.clearAndType(toDateInput, toDate);
        actions.click(findByDateRangeButton);
        waitUtils.waitForAjax();
    }

    public void searchByAmount(double amount) {
        log.info("Searching transactions by Amount: {}", amount);
        actions.clearAndType(amountInput, String.format("%.2f", amount));
        actions.click(findByAmountButton);
        waitUtils.waitForAjax();
    }

    public void waitForResults() {
        waitUtils.waitForVisibility(transactionTable);
        waitUtils.waitForPresence(transactionRows);
    }

    public List<String> getMatchingTransactionIds() {
        waitForResults();
        List<WebElement> links = driver.findElements(transactionLinks);
        List<String> ids = new ArrayList<>();
        for (WebElement link : links) {
            String text = link.getText().trim();
            if (!text.isEmpty()) {
                ids.add(text);
            }
        }
        return ids;
    }

    public boolean hasTransactionWithAmount(double expectedAmount) {
        waitForResults();
        String formatted = String.format("%.2f", expectedAmount);
        List<WebElement> rows = driver.findElements(transactionRows);
        for (WebElement row : rows) {
            if (row.getText().contains(formatted)) {
                return true;
            }
        }
        return false;
    }
}
