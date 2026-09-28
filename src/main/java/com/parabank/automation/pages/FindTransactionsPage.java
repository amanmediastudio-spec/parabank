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
    private final By dateInput = By.id("transactionDate");
    private final By fromDateInput = By.id("fromDate");
    private final By toDateInput = By.id("toDate");
    private final By amountInput = By.id("amount");

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

    public void ensureFormVisible() {
        try {
            WebElement form = driver.findElement(By.id("formContainer"));
            if (!form.isDisplayed()) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "$('#resultContainer').hide(); $('#errorContainer').hide(); $('#formContainer').show();"
                );
            }
        } catch (Exception ignored) {
        }
    }

    public void selectAccount(String accountId) {
        log.info("Selecting account for transaction search: {}", accountId);
        ensureFormVisible();
        waitForAccountDropdownToLoad();
        actions.selectByVisibleText(accountSelect, accountId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#accountId').val(arguments[0]).trigger('change');", accountId
        );
    }

    public void searchById(String transactionId) {
        log.info("Searching transactions by ID: {}", transactionId);
        ensureFormVisible();
        actions.clearAndType(transactionIdInput, transactionId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#transactionId').val(arguments[0]); $('#findById').click();", transactionId
        );
        waitUtils.waitForAjax();
    }

    public void searchByDate(String date) {
        log.info("Searching transactions by Date: {}", date);
        ensureFormVisible();
        actions.clearAndType(dateInput, date);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#transactionDate').val(arguments[0]); $('#findByDate').click();", date
        );
        waitUtils.waitForAjax();
    }

    public void searchByDateRange(String fromDate, String toDate) {
        log.info("Searching transactions by Date Range: {} to {}", fromDate, toDate);
        ensureFormVisible();
        actions.clearAndType(fromDateInput, fromDate);
        actions.clearAndType(toDateInput, toDate);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#fromDate').val(arguments[0]); $('#toDate').val(arguments[1]); $('#findByDateRange').click();",
                fromDate, toDate
        );
        waitUtils.waitForAjax();
    }

    public void searchByAmount(double amount) {
        log.info("Searching transactions by Amount: {}", amount);
        ensureFormVisible();
        String amountStr = String.format(java.util.Locale.US, "%.2f", amount);
        actions.clearAndType(amountInput, amountStr);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "$('#amount').val(arguments[0]); $('#findByAmount').click();", amountStr
        );
        waitUtils.waitForAjax();
    }

    public void waitForResults() {
        waitUtils.waitForVisibility(By.id("resultContainer"));
        waitUtils.waitForVisibility(transactionTable);
        waitUtils.waitForPresence(transactionRows);
    }

    public List<String> getMatchingTransactionIds() {
        waitForResults();
        List<WebElement> links = driver.findElements(transactionLinks);
        List<String> ids = new ArrayList<>();
        for (WebElement link : links) {
            String href = link.getDomAttribute("href");
            if (href != null && href.contains("id=")) {
                String id = href.substring(href.indexOf("id=") + 3);
                ids.add(id);
            }
        }
        return ids;
    }

    public boolean hasTransactionWithAmount(double expectedAmount) {
        waitForResults();
        String formatted = String.format(java.util.Locale.US, "%.2f", expectedAmount);
        List<WebElement> rows = driver.findElements(transactionRows);
        for (WebElement row : rows) {
            if (row.getText().contains(formatted)) {
                return true;
            }
        }
        return false;
    }
}
