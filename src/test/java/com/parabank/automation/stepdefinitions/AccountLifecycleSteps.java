package com.parabank.automation.stepdefinitions;

import com.parabank.automation.context.ContextKey;
import com.parabank.automation.context.ScenarioContext;
import com.parabank.automation.driver.DriverManager;
import com.parabank.automation.models.AccountType;
import com.parabank.automation.pages.AccountDetailsPage;
import com.parabank.automation.pages.AccountsOverviewPage;
import com.parabank.automation.pages.OpenAccountPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class AccountLifecycleSteps {
    private static final Logger log = LoggerFactory.getLogger(AccountLifecycleSteps.class);
    private final WebDriver driver = DriverManager.getDriver();
    private final ScenarioContext context;
    private final AccountsOverviewPage overviewPage;
    private final OpenAccountPage openAccountPage;
    private final AccountDetailsPage detailsPage;

    public AccountLifecycleSteps(ScenarioContext context) {
        this.context = context;
        this.overviewPage = new AccountsOverviewPage(driver);
        this.openAccountPage = new OpenAccountPage(driver);
        this.detailsPage = new AccountDetailsPage(driver);
    }

    @And("the user navigates to Accounts Overview and records the primary account ID and initial balance")
    public void recordPrimaryAccountAndInitialBalance() {
        overviewPage.waitForOverviewTableToLoad();
        List<String> accountIds = overviewPage.getAccountIds();
        assertThat(accountIds).as("User must have at least one active primary account").isNotEmpty();

        String primaryId = accountIds.get(0);
        double initialBalance = overviewPage.getAccountBalance(primaryId);
        double portfolioTotal = overviewPage.getTotalBalance();

        context.set(ContextKey.PRIMARY_ACCOUNT_ID, primaryId);
        context.set(ContextKey.SOURCE_INITIAL_BALANCE, initialBalance);
        context.set(ContextKey.TOTAL_PORTFOLIO_BALANCE, portfolioTotal);

        log.info("Primary Account ID: {}, Initial Balance: ${}, Portfolio Total: ${}",
                primaryId, initialBalance, portfolioTotal);
    }

    @When("the user opens a new {string} account with funds transferred from the primary account")
    public void openNewAccountWithFunds(String accountTypeStr) {
        AccountType type = AccountType.fromString(accountTypeStr);
        String primaryAccountId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);

        openAccountPage.selectAccountType(type);
        if (primaryAccountId != null) {
            openAccountPage.selectFromAccount(primaryAccountId);
        }
        openAccountPage.clickOpenAccount();
    }

    @Then("a new account number is generated and captured")
    public void captureNewAccountNumber() {
        String newAccountId = openAccountPage.getNewAccountId();
        assertThat(newAccountId).as("New account number must not be null or empty").isNotBlank();
        context.set(ContextKey.NEW_SAVINGS_ACCOUNT_ID, newAccountId);
        log.info("Successfully created and captured new account ID: {}", newAccountId);
    }

    @Then("the new savings account should appear in the portfolio with the initial deposit")
    public void verifyNewSavingsAccountInPortfolio() {
        overviewPage.waitForOverviewTableToLoad();
        String newId = context.getString(ContextKey.NEW_SAVINGS_ACCOUNT_ID);
        List<String> accountIds = overviewPage.getAccountIds();

        assertThat(accountIds).as("Newly created savings account must be present in portfolio").contains(newId);

        double newAccountBalance = overviewPage.getAccountBalance(newId);
        assertThat(newAccountBalance).as("New account should have non-zero initial deposit").isGreaterThan(0.0);
        log.info("New account {} verified with initial deposit of ${}", newId, newAccountBalance);
    }

    @And("the primary account balance should be debited by the initial deposit")
    public void verifyPrimaryAccountDebitedByDeposit() {
        overviewPage.waitForOverviewTableToLoad();
        String primaryId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);
        String newId = context.getString(ContextKey.NEW_SAVINGS_ACCOUNT_ID);

        double initialPrimaryBal = context.getDouble(ContextKey.SOURCE_INITIAL_BALANCE);
        double currentPrimaryBal = overviewPage.getAccountBalance(primaryId);
        double depositAmount = overviewPage.getAccountBalance(newId);

        assertThat(currentPrimaryBal)
                .as("Primary account balance should be reduced by the initial deposit amount")
                .isCloseTo(initialPrimaryBal - depositAmount, within(0.01));
    }

    @And("the portfolio total balance should equal the mathematical sum of all accounts")
    public void verifyPortfolioTotalEqualsSum() {
        overviewPage.waitForOverviewTableToLoad();
        List<String> accountIds = overviewPage.getAccountIds();
        double sum = 0.0;
        for (String id : accountIds) {
            sum += overviewPage.getAccountBalance(id);
        }
        double totalBalance = overviewPage.getTotalBalance();

        assertThat(totalBalance)
                .as("Total portfolio balance must match the exact sum of individual accounts")
                .isCloseTo(sum, within(0.02));
    }

    @When("the user opens the transaction details of the newly created savings account")
    public void openNewAccountTransactionDetails() {
        String newId = context.getString(ContextKey.NEW_SAVINGS_ACCOUNT_ID);
        overviewPage.clickAccount(newId);
    }

    @Then("the account details should display account type {string} and initial deposit transaction")
    public void verifyAccountTypeAndInitialTransaction(String expectedType) {
        detailsPage.waitForTransactionsToLoad();
        assertThat(detailsPage.getAccountType())
                .as("Account type should match expected")
                .containsIgnoringCase(expectedType);

        assertThat(detailsPage.getTransactionCount())
                .as("Newly opened account should record at least one initial deposit transaction")
                .isGreaterThanOrEqualTo(1);
    }

    @And("the user opens a secondary {string} account funded from primary account")
    public void openSecondaryAccount(String accountTypeStr) {
        overviewPage.navigation().clickOpenNewAccount();
        AccountType type = AccountType.fromString(accountTypeStr);
        String primaryAccountId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);

        openAccountPage.selectAccountType(type);
        if (primaryAccountId != null) {
            openAccountPage.selectFromAccount(primaryAccountId);
        }
        openAccountPage.clickOpenAccount();
        String secondaryId = openAccountPage.getNewAccountId();
        context.set(ContextKey.SECONDARY_ACCOUNT_ID, secondaryId);
        log.info("Opened secondary account: {}", secondaryId);
    }

    @And("the user opens a third {string} account funded from primary account")
    public void openTertiaryAccount(String accountTypeStr) {
        overviewPage.navigation().clickOpenNewAccount();
        AccountType type = AccountType.fromString(accountTypeStr);
        String primaryAccountId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);

        openAccountPage.selectAccountType(type);
        if (primaryAccountId != null) {
            openAccountPage.selectFromAccount(primaryAccountId);
        }
        openAccountPage.clickOpenAccount();
        String tertiaryId = openAccountPage.getNewAccountId();
        context.set(ContextKey.NEW_SAVINGS_ACCOUNT_ID, tertiaryId);
        log.info("Opened tertiary account: {}", tertiaryId);
    }

    @And("the user records balances of primary, secondary, and tertiary accounts")
    public void recordBalancesOfThreeAccounts() {
        overviewPage.navigation().clickAccountsOverview();
        overviewPage.waitForOverviewTableToLoad();

        String primaryId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);
        String secondaryId = context.getString(ContextKey.SECONDARY_ACCOUNT_ID);
        String tertiaryId = context.getString(ContextKey.NEW_SAVINGS_ACCOUNT_ID);

        double b1 = overviewPage.getAccountBalance(primaryId);
        double b2 = overviewPage.getAccountBalance(secondaryId);
        double b3 = overviewPage.getAccountBalance(tertiaryId);

        context.set(ContextKey.SOURCE_INITIAL_BALANCE, b1);
        context.set(ContextKey.TARGET_INITIAL_BALANCE, b2);
        context.set(ContextKey.TOTAL_PORTFOLIO_BALANCE, overviewPage.getTotalBalance());

        log.info("Recorded initial trio balances: Primary=${}, Secondary=${}, Tertiary=${}", b1, b2, b3);
    }

    @Then("the newly created account balance should equal {string}")
    public void verifyNewlyCreatedAccountBalance(String expectedBalanceStr) {
        overviewPage.waitForOverviewTableToLoad();
        String newId = context.getString(ContextKey.NEW_SAVINGS_ACCOUNT_ID);
        double currentBal = overviewPage.getAccountBalance(newId);
        double expectedBal = Double.parseDouble(expectedBalanceStr);

        assertThat(currentBal)
                .as("Newly created account balance must equal " + expectedBalanceStr)
                .isCloseTo(expectedBal, within(0.01));
    }
}
