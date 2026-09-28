package com.parabank.automation.pages;

import com.parabank.automation.utils.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for Accounts Overview.
 * Handles dynamically populated asynchronous balance table.
 */
public class AccountsOverviewPage extends BasePage {
    private final By accountTable = By.id("accountTable");
    private final By accountRows = By.cssSelector("#accountTable tbody tr");
    private final By accountLinks = By.cssSelector("#accountTable tbody tr td a[href*='activity.htm']");
    private final By totalBalanceCell = By.xpath("//table[@id='accountTable']//tr[td[contains(., 'Total')]]/td[2] | //table[@id='accountTable']//b[contains(text(),'$')]");

    public AccountsOverviewPage(WebDriver driver) {
        super(driver);
    }

    public void waitForOverviewTableToLoad() {
        log.info("Waiting for Accounts Overview table to dynamically render...");
        waitUtils.waitForVisibility(accountTable);
        waitUtils.waitForPresence(accountLinks);
        waitUtils.waitForAjax();
    }

    public List<String> getAccountIds() {
        waitForOverviewTableToLoad();
        List<WebElement> links = driver.findElements(accountLinks);
        List<String> ids = new ArrayList<>();
        for (WebElement link : links) {
            String text = link.getText().trim();
            if (!text.isEmpty()) {
                ids.add(text);
            }
        }
        log.info("Found {} active accounts: {}", ids.size(), ids);
        return ids;
    }

    public double getAccountBalance(String accountId) {
        waitForOverviewTableToLoad();
        By balanceLocator = By.xpath(String.format(
                "//table[@id='accountTable']//tr[td/a[normalize-space()='%s']]/td[2]",
                accountId
        ));
        String rawBalance = actions.getText(balanceLocator);
        return ElementActions.parseCurrency(rawBalance);
    }

    public double getAvailableBalance(String accountId) {
        waitForOverviewTableToLoad();
        By availableLocator = By.xpath(String.format(
                "//table[@id='accountTable']//tr[td/a[normalize-space()='%s']]/td[3]",
                accountId
        ));
        String rawAvailable = actions.getText(availableLocator);
        return ElementActions.parseCurrency(rawAvailable);
    }

    public double getTotalBalance() {
        waitForOverviewTableToLoad();
        try {
            WebElement totalElement = waitUtils.waitForVisibility(totalBalanceCell);
            return ElementActions.parseCurrency(totalElement.getText());
        } catch (Exception e) {
            log.warn("Total row not directly accessible via selector, calculating sum of all accounts: {}", e.getMessage());
            double sum = 0.0;
            for (String id : getAccountIds()) {
                sum += getAccountBalance(id);
            }
            return sum;
        }
    }

    public void clickAccount(String accountId) {
        log.info("Clicking on account link: {}", accountId);
        By accountLink = By.xpath(String.format("//table[@id='accountTable']//a[normalize-space()='%s']", accountId));
        actions.scrollToElement(accountLink);
        actions.jsClick(accountLink);
        waitUtils.waitForUrlContains("activity.htm");
        waitUtils.waitForAjax();
    }
}
