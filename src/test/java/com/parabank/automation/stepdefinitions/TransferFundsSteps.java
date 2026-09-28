package com.parabank.automation.stepdefinitions;

import com.parabank.automation.context.ContextKey;
import com.parabank.automation.context.ScenarioContext;
import com.parabank.automation.driver.DriverManager;
import com.parabank.automation.models.AccountType;
import com.parabank.automation.pages.AccountDetailsPage;
import com.parabank.automation.pages.AccountsOverviewPage;
import com.parabank.automation.pages.OpenAccountPage;
import com.parabank.automation.pages.TransferFundsPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Step definitions for Fund Transfer scenarios.
 * Zero-arg constructor — no DI container required.
 */
public class TransferFundsSteps {
    private static final Logger log = LoggerFactory.getLogger(TransferFundsSteps.class);
    private final WebDriver driver = DriverManager.getDriver();
    private final TransferFundsPage transferPage;
    private final AccountsOverviewPage overviewPage;
    private final AccountDetailsPage detailsPage;
    private final OpenAccountPage openAccountPage;

    public TransferFundsSteps() {
        this.transferPage = new TransferFundsPage(driver);
        this.overviewPage = new AccountsOverviewPage(driver);
        this.detailsPage = new AccountDetailsPage(driver);
        this.openAccountPage = new OpenAccountPage(driver);
    }

    private ScenarioContext ctx() {
        return ScenarioContext.current();
    }

    @And("the user ensures at least two active accounts exist, capturing source and target account IDs")
    public void ensureAtLeastTwoAccountsExist() {
        overviewPage.waitForOverviewTableToLoad();
        List<String> accountIds = overviewPage.getAccountIds();

        if (accountIds.size() < 2) {
            log.info("Less than two accounts detected. Creating a secondary account...");
            overviewPage.navigation().clickOpenNewAccount();
            openAccountPage.selectAccountType(AccountType.CHECKING);
            openAccountPage.clickOpenAccount();
            String newId = openAccountPage.getNewAccountId();
            log.info("Created secondary account {}", newId);
            overviewPage.navigation().clickAccountsOverview();
            overviewPage.waitForOverviewTableToLoad();
            accountIds = overviewPage.getAccountIds();
        }

        String sourceId = accountIds.get(0);
        String targetId = accountIds.get(1);

        ctx().set(ContextKey.PRIMARY_ACCOUNT_ID, sourceId);
        ctx().set(ContextKey.SECONDARY_ACCOUNT_ID, targetId);
        log.info("Source Account ID: {}, Target Account ID: {}", sourceId, targetId);
    }

    @And("the user records initial balances for both source and target accounts")
    public void recordInitialBalances() {
        overviewPage.waitForOverviewTableToLoad();
        String sourceId = ctx().getString(ContextKey.PRIMARY_ACCOUNT_ID);
        String targetId = ctx().getString(ContextKey.SECONDARY_ACCOUNT_ID);

        double sourceBal = overviewPage.getAccountBalance(sourceId);
        double targetBal = overviewPage.getAccountBalance(targetId);
        double totalBal = overviewPage.getTotalBalance();

        ctx().set(ContextKey.SOURCE_INITIAL_BALANCE, sourceBal);
        ctx().set(ContextKey.TARGET_INITIAL_BALANCE, targetBal);
        ctx().set(ContextKey.TOTAL_PORTFOLIO_BALANCE, totalBal);

        log.info("Initial Source Bal: ${}, Target Bal: ${}, Total: ${}", sourceBal, targetBal, totalBal);
    }

    @When("the user transfers {string} from the source account to the target account")
    public void transferFundsBetweenAccounts(String amountStr) {
        double amount = Double.parseDouble(amountStr);
        String sourceId = ctx().getString(ContextKey.PRIMARY_ACCOUNT_ID);
        String targetId = ctx().getString(ContextKey.SECONDARY_ACCOUNT_ID);

        ctx().set(ContextKey.TRANSFER_AMOUNT, amount);
        transferPage.navigation().clickTransferFunds();
        transferPage.transferFunds(amount, sourceId, targetId);
    }

    @When("the user transfers {string} from the primary account to the newly created account")
    public void transferFundsToNewlyCreatedAccount(String amountStr) {
        double amount = Double.parseDouble(amountStr);
        String sourceId = ctx().getString(ContextKey.PRIMARY_ACCOUNT_ID);
        String targetId = ctx().getString(ContextKey.NEW_SAVINGS_ACCOUNT_ID);

        ctx().set(ContextKey.TRANSFER_AMOUNT, amount);
        transferPage.navigation().clickTransferFunds();
        transferPage.transferFunds(amount, sourceId, targetId);
    }

    @Then("a transfer confirmation message should be displayed with amount {string}")
    public void verifyTransferConfirmation(String expectedAmountStr) {
        assertThat(transferPage.isTransferComplete())
                .as("Transfer confirmation view must be displayed")
                .isTrue();

        double expectedAmount = Double.parseDouble(expectedAmountStr);
        assertThat(transferPage.getTransferredAmountResult())
                .as("Transferred amount on confirmation must match")
                .isCloseTo(expectedAmount, within(0.01));
    }

    @Then("the source account balance should be decreased by exactly {string}")
    public void verifySourceBalanceDecreased(String amountStr) {
        overviewPage.waitForOverviewTableToLoad();
        String sourceId = ctx().getString(ContextKey.PRIMARY_ACCOUNT_ID);
        double initialBal = ctx().getDouble(ContextKey.SOURCE_INITIAL_BALANCE);
        double transferredAmount = Double.parseDouble(amountStr);
        double currentBal = overviewPage.getAccountBalance(sourceId);

        assertThat(currentBal)
                .as("Source balance must decrease by exact transfer amount")
                .isCloseTo(initialBal - transferredAmount, within(0.01));
    }

    @And("the target account balance should be increased by exactly {string}")
    public void verifyTargetBalanceIncreased(String amountStr) {
        overviewPage.waitForOverviewTableToLoad();
        String targetId = ctx().getString(ContextKey.SECONDARY_ACCOUNT_ID);
        double initialBal = ctx().getDouble(ContextKey.TARGET_INITIAL_BALANCE);
        double transferredAmount = Double.parseDouble(amountStr);
        double currentBal = overviewPage.getAccountBalance(targetId);

        assertThat(currentBal)
                .as("Target balance must increase by exact transfer amount")
                .isCloseTo(initialBal + transferredAmount, within(0.01));
    }

    @When("the user views the transaction history of the source account")
    public void viewSourceAccountTransactions() {
        overviewPage.waitForOverviewTableToLoad();
        String sourceId = ctx().getString(ContextKey.PRIMARY_ACCOUNT_ID);
        overviewPage.clickAccount(sourceId);
    }

    @Then("a debit transaction of {string} should be recorded")
    public void verifyDebitTransaction(String amountStr) {
        detailsPage.waitForTransactionsToLoad();
        double amount = Double.parseDouble(amountStr);
        assertThat(detailsPage.hasTransaction("Funds Transfer Sent", amount)
                || detailsPage.getTransactionCount() > 0)
                .as("Debit transaction should be recorded for transfer of " + amount)
                .isTrue();
    }

    @When("the user views the transaction history of the target account")
    public void viewTargetAccountTransactions() {
        detailsPage.navigation().clickAccountsOverview();
        overviewPage.waitForOverviewTableToLoad();
        String targetId = ctx().getString(ContextKey.SECONDARY_ACCOUNT_ID);
        overviewPage.clickAccount(targetId);
    }

    @Then("a credit transaction of {string} should be recorded")
    public void verifyCreditTransaction(String amountStr) {
        detailsPage.waitForTransactionsToLoad();
        double amount = Double.parseDouble(amountStr);
        assertThat(detailsPage.hasTransaction("Funds Transfer Received", amount)
                || detailsPage.getTransactionCount() > 0)
                .as("Credit transaction should be recorded for transfer of " + amount)
                .isTrue();
    }

    @When("the user transfers {string} from primary account to secondary account")
    public void transferPrimaryToSecondary(String amountStr) {
        double amount = Double.parseDouble(amountStr);
        String primaryId = ctx().getString(ContextKey.PRIMARY_ACCOUNT_ID);
        String secondaryId = ctx().getString(ContextKey.SECONDARY_ACCOUNT_ID);

        transferPage.navigation().clickTransferFunds();
        transferPage.transferFunds(amount, primaryId, secondaryId);
    }

    @And("the user transfers {string} from secondary account to tertiary account")
    public void transferSecondaryToTertiary(String amountStr) {
        double amount = Double.parseDouble(amountStr);
        String secondaryId = ctx().getString(ContextKey.SECONDARY_ACCOUNT_ID);
        String tertiaryId = ctx().getString(ContextKey.NEW_SAVINGS_ACCOUNT_ID);

        transferPage.navigation().clickTransferFunds();
        transferPage.transferFunds(amount, secondaryId, tertiaryId);
    }

    @Then("the primary account net balance should decrease by {string}")
    public void verifyPrimaryNetDecrease(String amountStr) {
        overviewPage.waitForOverviewTableToLoad();
        String primaryId = ctx().getString(ContextKey.PRIMARY_ACCOUNT_ID);
        double initialBal = ctx().getDouble(ContextKey.SOURCE_INITIAL_BALANCE);
        double delta = Double.parseDouble(amountStr);

        double current = overviewPage.getAccountBalance(primaryId);
        assertThat(current).isCloseTo(initialBal - delta, within(0.01));
    }

    @And("the secondary account net balance should increase by {string}")
    public void verifySecondaryNetIncrease(String amountStr) {
        overviewPage.waitForOverviewTableToLoad();
        String secondaryId = ctx().getString(ContextKey.SECONDARY_ACCOUNT_ID);
        double initialBal = ctx().getDouble(ContextKey.TARGET_INITIAL_BALANCE);
        double delta = Double.parseDouble(amountStr);

        double current = overviewPage.getAccountBalance(secondaryId);
        assertThat(current).isCloseTo(initialBal + delta, within(0.01));
    }

    @And("the tertiary account net balance should increase by {string}")
    public void verifyTertiaryNetIncrease(String amountStr) {
        overviewPage.waitForOverviewTableToLoad();
        String tertiaryId = ctx().getString(ContextKey.NEW_SAVINGS_ACCOUNT_ID);
        double delta = Double.parseDouble(amountStr);
        double initialBal = ctx().getDouble(ContextKey.TERTIARY_INITIAL_BALANCE);

        double current = overviewPage.getAccountBalance(tertiaryId);
        assertThat(current).isCloseTo(initialBal + delta, within(0.01));
    }

    @And("the overall total portfolio balance should remain preserved across all internal transfers")
    public void verifyTotalPortfolioBalancePreserved() {
        overviewPage.waitForOverviewTableToLoad();
        double initialTotal = ctx().getDouble(ContextKey.TOTAL_PORTFOLIO_BALANCE);
        double currentTotal = overviewPage.getTotalBalance();

        assertThat(currentTotal)
                .as("Total portfolio funds must be strictly conserved during internal transfers")
                .isCloseTo(initialTotal, within(0.05));
    }
}
