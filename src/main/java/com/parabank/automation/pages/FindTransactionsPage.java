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
 * Original Source: FindTransactionsPage
 */
public class FindTransactionsPage extends BasePage {

    // Registered SDK Page Elements (Self-Healing Enabled)
    public PageElement accountSelect;
    public PageElement transactionIdInput;
    public PageElement findByIdButton;
    public PageElement dateInput;
    public PageElement findByDateButton;
    public PageElement fromDateInput;
    public PageElement toDateInput;
    public PageElement findByDateRangeButton;
    public PageElement amountInput;
    public PageElement findByAmountButton;
    public PageElement transactionTable;
    public PageElement transactionRows;
    public PageElement transactionLinks;

    public FindTransactionsPage() {
        super("FindTransactionsPage");
    }

    public FindTransactionsPage(String pageName) {
        super(pageName);
    }

    @Override
    protected void initElements() {
        accountSelect = register("accountSelect", "accountSelect", By.id("accountId"));
        transactionIdInput = register("transactionIdInput", "transactionIdInput", By.id("transactionId"));
        findByIdButton = register("findByIdButton", "findByIdButton", By.id("findById"));
        dateInput = register("dateInput", "dateInput", By.id("transactionDate"));
        findByDateButton = register("findByDateButton", "findByDateButton", By.id("findByDate"));
        fromDateInput = register("fromDateInput", "fromDateInput", By.id("fromDate"));
        toDateInput = register("toDateInput", "toDateInput", By.id("toDate"));
        findByDateRangeButton = register("findByDateRangeButton", "findByDateRangeButton", By.id("findByDateRange"));
        amountInput = register("amountInput", "amountInput", By.id("amount"));
        findByAmountButton = register("findByAmountButton", "findByAmountButton", By.id("findByAmount"));
        transactionTable = register("transactionTable", "transactionTable", By.id("transactionTable"));
        transactionRows = register("transactionRows", "transactionRows", By.cssSelector("#transactionTable tbody tr"));
        transactionLinks = register("transactionLinks", "transactionLinks", By.cssSelector("#transactionTable tbody tr td a"));
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
            WebElement form = com.automation.driver.DriverManager.getDriver().findElement(By.id("formContainer"));
            if (!form.isDisplayed()) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#resultContainer').hide(); $('#errorContainer').hide(); $('#formContainer').show();");
            }
        } catch (Exception ignored) {
        }
    }

    public void selectAccount(String accountId) {
        log.info("Selecting account for transaction search: {}", accountId);
        ensureFormVisible();
        waitForAccountDropdownToLoad();
        actions.selectByVisibleText(accountSelect, accountId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#accountId').val(arguments[0]).trigger('change');", accountId);
    }

    public void searchById(String transactionId) {
        log.info("Searching transactions by ID: {}", transactionId);
        ensureFormVisible();
        actions.clearAndType(transactionIdInput, transactionId);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#transactionId').val(arguments[0]); $('#findById').click();", transactionId);
        waitUtils.waitForAjax();
    }

    public void searchByDate(String date) {
        log.info("Searching transactions by Date: {}", date);
        ensureFormVisible();
        actions.clearAndType(dateInput, date);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#transactionDate').val(arguments[0]); $('#findByDate').click();", date);
        waitUtils.waitForAjax();
    }

    public void searchByDateRange(String fromDate, String toDate) {
        log.info("Searching transactions by Date Range: {} to {}", fromDate, toDate);
        ensureFormVisible();
        actions.clearAndType(fromDateInput, fromDate);
        actions.clearAndType(toDateInput, toDate);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#fromDate').val(arguments[0]); $('#toDate').val(arguments[1]); $('#findByDateRange').click();", fromDate, toDate);
        waitUtils.waitForAjax();
    }

    public void searchByAmount(double amount) {
        log.info("Searching transactions by Amount: {}", amount);
        ensureFormVisible();
        String amountStr = String.format(java.util.Locale.US, "%.2f", amount);
        actions.clearAndType(amountInput, amountStr);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("$('#amount').val(arguments[0]); $('#findByAmount').click();", amountStr);
        waitUtils.waitForAjax();
    }

    public void waitForResults() {
        waitUtils.waitForVisibility(By.id("resultContainer"));
        waitUtils.waitForVisibility(transactionTable);
        waitUtils.waitForPresence(transactionRows);
    }

    public List<String> getMatchingTransactionIds() {
        waitForResults();
        List<WebElement> links = findElements(transactionLinks);
        List<String> ids = new ArrayList<>();
        for (WebElement link : links) {
            String href = link.getAttribute("href");
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
        List<WebElement> rows = findElements(transactionRows);
        for (WebElement row : rows) {
            if (row.getText().contains(formatted)) {
                return true;
            }
        }
        return false;
    }

}
