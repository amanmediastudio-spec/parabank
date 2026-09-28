package com.parabank.automation.pages;

import com.parabank.automation.utils.ElementActions;
import com.parabank.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates Parabank's left navigation panel component.
 */
public class NavigationMenu {
    private static final Logger log = LoggerFactory.getLogger(NavigationMenu.class);
    private final WebDriver driver;
    private final WaitUtils waitUtils;
    private final ElementActions actions;

    private final By openNewAccountLink = By.xpath("//div[@id='leftPanel']//a[contains(@href, 'openaccount.htm')]");
    private final By accountsOverviewLink = By.xpath("//div[@id='leftPanel']//a[contains(@href, 'overview.htm')]");
    private final By transferFundsLink = By.xpath("//div[@id='leftPanel']//a[contains(@href, 'transfer.htm')]");
    private final By billPayLink = By.xpath("//div[@id='leftPanel']//a[contains(@href, 'billpay.htm')]");
    private final By findTransactionsLink = By.xpath("//div[@id='leftPanel']//a[contains(@href, 'findtrans.htm')]");
    private final By updateContactInfoLink = By.xpath("//div[@id='leftPanel']//a[contains(@href, 'updateprofile.htm')]");
    private final By requestLoanLink = By.xpath("//div[@id='leftPanel']//a[contains(@href, 'requestloan.htm')]");
    private final By logOutLink = By.xpath("//div[@id='leftPanel']//a[contains(@href, 'logout.htm')]");
    private final By welcomeUserText = By.xpath("//div[@id='leftPanel']//p[contains(@class, 'smallText')]");

    public NavigationMenu(WebDriver driver, WaitUtils waitUtils, ElementActions actions) {
        this.driver = driver;
        this.waitUtils = waitUtils;
        this.actions = actions;
    }

    private void navigateTo(By linkLocator, String urlFragment) {
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
