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
import com.parabank.automation.utils.WaitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migrated Page Object strictly compliant with Platform SDK Core.
 * Original Source: NavigationMenu
 */
public class NavigationMenu extends BasePage {

    // Registered SDK Page Elements (Self-Healing Enabled)
    public PageElement openNewAccountLink;
    public PageElement accountsOverviewLink;
    public PageElement transferFundsLink;
    public PageElement billPayLink;
    public PageElement findTransactionsLink;
    public PageElement updateContactInfoLink;
    public PageElement requestLoanLink;
    public PageElement logOutLink;
    public PageElement welcomeUserText;

    public NavigationMenu() {
        super("NavigationMenu");
    }

    public NavigationMenu(String pageName) {
        super(pageName);
    }

    @Override
    protected void initElements() {
        openNewAccountLink = register("openNewAccountLink", "openNewAccountLink", By.xpath("//div[@id='leftPanel']//a[contains(@href, 'openaccount.htm')]"));
        accountsOverviewLink = register("accountsOverviewLink", "accountsOverviewLink", By.xpath("//div[@id='leftPanel']//a[contains(@href, 'overview.htm')]"));
        transferFundsLink = register("transferFundsLink", "transferFundsLink", By.xpath("//div[@id='leftPanel']//a[contains(@href, 'transfer.htm')]"));
        billPayLink = register("billPayLink", "billPayLink", By.xpath("//div[@id='leftPanel']//a[contains(@href, 'billpay.htm')]"));
        findTransactionsLink = register("findTransactionsLink", "findTransactionsLink", By.xpath("//div[@id='leftPanel']//a[contains(@href, 'findtrans.htm')]"));
        updateContactInfoLink = register("updateContactInfoLink", "updateContactInfoLink", By.xpath("//div[@id='leftPanel']//a[contains(@href, 'updateprofile.htm')]"));
        requestLoanLink = register("requestLoanLink", "requestLoanLink", By.xpath("//div[@id='leftPanel']//a[contains(@href, 'requestloan.htm')]"));
        logOutLink = register("logOutLink", "logOutLink", By.xpath("//div[@id='leftPanel']//a[contains(@href, 'logout.htm')]"));
        welcomeUserText = register("welcomeUserText", "welcomeUserText", By.xpath("//div[@id='leftPanel']//p[contains(@class, 'smallText')]"));
    }

    public void navigateTo(By linkLocator, String urlFragment) {
        log.info("Navigating to '{}' using link: {}", urlFragment, linkLocator);
        actions.jsClick(linkLocator);
        waitUtils.waitForUrlContains(urlFragment);
        waitUtils.waitForAjax();
    }

    public void clickOpenNewAccount() {
        navigateTo(openNewAccountLink, "openaccount.htm");
    }

    public void clickAccountsOverview() {
        navigateTo(accountsOverviewLink, "overview.htm");
    }

    public void clickTransferFunds() {
        navigateTo(transferFundsLink, "transfer.htm");
    }

    public void clickBillPay() {
        navigateTo(billPayLink, "billpay.htm");
    }

    public void clickFindTransactions() {
        navigateTo(findTransactionsLink, "findtrans.htm");
    }

    public void clickUpdateContactInfo() {
        navigateTo(updateContactInfoLink, "updateprofile.htm");
    }

    public void clickRequestLoan() {
        navigateTo(requestLoanLink, "requestloan.htm");
    }

    public void clickLogOut() {
        navigateTo(logOutLink, "index.htm");
    }

    public boolean isUserLoggedIn() {
        return actions.isDisplayed(welcomeUserText);
    }

    public String getWelcomeMessage() {
        return actions.getText(welcomeUserText);
    }

}
