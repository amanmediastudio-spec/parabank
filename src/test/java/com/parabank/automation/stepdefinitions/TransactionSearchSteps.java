package com.parabank.automation.stepdefinitions;

import com.parabank.automation.context.ContextKey;
import com.parabank.automation.context.ScenarioContext;
import com.parabank.automation.driver.DriverManager;
import com.parabank.automation.pages.AccountsOverviewPage;
import com.parabank.automation.pages.FindTransactionsPage;
import com.parabank.automation.pages.TransferFundsPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionSearchSteps {
    private static final Logger log = LoggerFactory.getLogger(TransactionSearchSteps.class);
    private final WebDriver driver = DriverManager.getDriver();
    private final ScenarioContext context;
    private final FindTransactionsPage findTransPage;
    private final TransferFundsPage transferPage;
    private final AccountsOverviewPage overviewPage;

    public TransactionSearchSteps(ScenarioContext context) {
        this.context = context;
        this.findTransPage = new FindTransactionsPage(driver);
        this.transferPage = new TransferFundsPage(driver);
        this.overviewPage = new AccountsOverviewPage(driver);
    }

    @And("the user executes a unique transfer of {string} to generate an identifiable transaction")
    public void executeUniqueTransfer(String amountStr) {
        overviewPage.waitForOverviewTableToLoad();
        List<String> accounts = overviewPage.getAccountIds();
        String primaryId = accounts.get(0);
        String targetId = accounts.size() > 1 ? accounts.get(1) : primaryId;

        context.set(ContextKey.PRIMARY_ACCOUNT_ID, primaryId);
        double amount = Double.parseDouble(amountStr);
        context.set(ContextKey.TRANSACTION_AMOUNT, amount);

        transferPage.navigation().clickTransferFunds();
        transferPage.transferFunds(amount, primaryId, targetId);
        log.info("Executed unique transaction generator transfer of ${}", amount);
    }

    @When("searches for transactions with amount {string}")
    public void searchTransactionsByAmount(String amountStr) {
        String accountId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);
        if (accountId != null) {
            findTransPage.selectAccount(accountId);
        }
        double amount = Double.parseDouble(amountStr);
        findTransPage.searchByAmount(amount);
    }

    @Then("the search results should contain the transaction with amount {string}")
    public void verifySearchResultsContainAmount(String amountStr) {
        double amount = Double.parseDouble(amountStr);
        assertThat(findTransPage.hasTransactionWithAmount(amount))
                .as("Search results must contain transaction with amount $" + amount)
                .isTrue();
    }

    @When("the user captures the transaction ID from the results")
    public void captureTransactionIdFromResults() {
        List<String> ids = findTransPage.getMatchingTransactionIds();
        assertThat(ids).as("At least one matching transaction ID must be returned").isNotEmpty();

        String transactionId = ids.get(0);
        context.set(ContextKey.TRANSACTION_ID, transactionId);
        log.info("Captured Transaction ID: {}", transactionId);
    }

    @And("searches for the transaction by its exact ID")
    public void searchByExactTransactionId() {
        String transactionId = context.getString(ContextKey.TRANSACTION_ID);
        findTransPage.searchById(transactionId);
    }

    @Then("the search results should contain the matching transaction ID")
    public void verifyMatchingTransactionIdInResults() {
        String transactionId = context.getString(ContextKey.TRANSACTION_ID);
        List<String> ids = findTransPage.getMatchingTransactionIds();

        assertThat(ids)
                .as("Exact transaction search must return the matching transaction ID " + transactionId)
                .contains(transactionId);
    }
}
