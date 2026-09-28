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
 * Original Source: AccountsOverviewPage
 */
public class AccountsOverviewPage extends BasePage {

    // Registered SDK Page Elements (Self-Healing Enabled)
    public PageElement accountTable;
    public PageElement accountRows;
    public PageElement accountLinks;
    public PageElement totalBalanceCell;

    public AccountsOverviewPage() {
        super("AccountsOverviewPage");
    }

    public AccountsOverviewPage(String pageName) {
        super(pageName);
    }

    @Override
    protected void initElements() {
        accountTable = register("accountTable", "accountTable", By.id("accountTable"));
        accountRows = register("accountRows", "accountRows", By.cssSelector("#accountTable tbody tr"));
        accountLinks = register("accountLinks", "accountLinks", By.cssSelector("#accountTable tbody tr td a[href*='activity.htm']"));
        totalBalanceCell = register("totalBalanceCell", "totalBalanceCell", By.xpath("//table[@id='accountTable']//tr[td[contains(., 'Total')]]/td[2] | //table[@id='accountTable']//b[contains(text(),'$')]"));
    }

    public void waitForOverviewTableToLoad() {
        log.info("Waiting for Accounts Overview table to dynamically render...");
        try {
            waitUtils.waitForVisibility(accountTable);
            waitUtils.waitForPresence(accountLinks);
            waitUtils.waitForAjax();
        } catch (org.openqa.selenium.TimeoutException e) {
            log.warn("Accounts Overview table did not appear on first attempt — refreshing page and retrying...");
            com.automation.driver.DriverManager.getDriver().navigate().refresh();
            waitUtils.waitForVisibility(accountTable);
            waitUtils.waitForPresence(accountLinks);
            waitUtils.waitForAjax();
        }
    }

    public List<String> getAccountIds() {
        waitForOverviewTableToLoad();
        List<WebElement> links = findElements(accountLinks);
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
        By balanceLocator = By.xpath(String.format("//table[@id='accountTable']//tr[td/a[normalize-space()='%s']]/td[2]", accountId));
        String rawBalance = actions.getText(balanceLocator);
        return rawBalance.parseCurrency();
    }

    public double getAvailableBalance(String accountId) {
        waitForOverviewTableToLoad();
        By availableLocator = By.xpath(String.format("//table[@id='accountTable']//tr[td/a[normalize-space()='%s']]/td[3]", accountId));
        String rawAvailable = actions.getText(availableLocator);
        return rawAvailable.parseCurrency();
    }

    public double getTotalBalance() {
        waitForOverviewTableToLoad();
        try {
            WebElement totalElement = waitUtils.waitForVisibility(totalBalanceCell);
            return totalElement.getText().parseCurrency();
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
