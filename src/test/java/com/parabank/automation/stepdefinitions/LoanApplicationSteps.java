package com.parabank.automation.stepdefinitions;

import com.parabank.automation.context.ContextKey;
import com.parabank.automation.context.ScenarioContext;
import com.parabank.automation.driver.DriverManager;
import com.parabank.automation.pages.AccountsOverviewPage;
import com.parabank.automation.pages.RequestLoanPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class LoanApplicationSteps {
    private static final Logger log = LoggerFactory.getLogger(LoanApplicationSteps.class);
    private final WebDriver driver = DriverManager.getDriver();
    private final ScenarioContext context;
    private final RequestLoanPage loanPage;
    private final AccountsOverviewPage overviewPage;

    public LoanApplicationSteps(ScenarioContext context) {
        this.context = context;
        this.loanPage = new RequestLoanPage(driver);
        this.overviewPage = new AccountsOverviewPage(driver);
    }

    @And("the user identifies an eligible account with sufficient funds for down payment")
    public void identifyEligibleAccount() {
        overviewPage.waitForOverviewTableToLoad();
        List<String> accounts = overviewPage.getAccountIds();
        assertThat(accounts).isNotEmpty();

        String accountId = accounts.get(0);
        double balance = overviewPage.getAccountBalance(accountId);
        context.set(ContextKey.PRIMARY_ACCOUNT_ID, accountId);
        context.set(ContextKey.SOURCE_INITIAL_BALANCE, balance);
        log.info("Eligible account identified: {} with balance ${}", accountId, balance);
    }

    @When("the user applies for a loan with amount {string} and down payment {string}")
    public void applyForLoan(String amountStr, String downPaymentStr) {
        loanPage.navigation().clickRequestLoan();
        String accountId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);
        double amount = Double.parseDouble(amountStr);
        double downPayment = Double.parseDouble(downPaymentStr);

        loanPage.applyForLoan(amount, downPayment, accountId);
    }

    @Then("the loan request should be processed with status {string}")
    public void verifyLoanProcessedStatus(String expectedStatus) {
        assertThat(loanPage.isLoanResultDisplayed())
                .as("Loan result container must be visible")
                .isTrue();

        assertThat(loanPage.getLoanStatus())
                .as("Loan processing status should be " + expectedStatus)
                .isEqualToIgnoringCase(expectedStatus);
    }

    @Then("the loan request status should be {string}")
    public void verifyLoanRequestStatus(String expectedStatus) {
        verifyLoanProcessedStatus(expectedStatus);
    }

    @And("a new loan account number should be generated")
    public void captureNewLoanAccountId() {
        String loanAccountId = loanPage.getNewLoanAccountId();
        assertThat(loanAccountId)
                .as("Approved loan must generate a valid new account number")
                .isNotBlank();
        context.set(ContextKey.LOAN_ACCOUNT_ID, loanAccountId);
        log.info("Generated new Loan Account ID: {}", loanAccountId);
    }

    @Then("the new loan account should be present in the accounts table")
    public void verifyNewLoanAccountInOverview() {
        overviewPage.waitForOverviewTableToLoad();
        String loanId = context.getString(ContextKey.LOAN_ACCOUNT_ID);
        List<String> accounts = overviewPage.getAccountIds();

        assertThat(accounts)
                .as("Portfolio should include the new loan account " + loanId)
                .contains(loanId);
    }

    @And("the user notes the primary account number and current balance")
    public void notePrimaryAccountAndBalance() {
        identifyEligibleAccount();
    }

    @When("the user applies for an excessive loan of {string} with down payment {string}")
    public void applyForExcessiveLoan(String amountStr, String downPaymentStr) {
        applyForLoan(amountStr, downPaymentStr);
    }

    @And("the system should display an error message stating insufficient funds or down payment")
    public void verifyLoanDenialErrorMessage() {
        String error = loanPage.getDeniedErrorMessage();
        assertThat(error)
                .as("Denied loan error message should explain reason")
                .containsIgnoringCase("cannot grant a loan");
    }

    @Then("no new loan account should have been added to the customer portfolio")
    public void verifyNoNewLoanAccountAdded() {
        overviewPage.waitForOverviewTableToLoad();
        String deniedLoanId = context.getString(ContextKey.LOAN_ACCOUNT_ID);
        if (deniedLoanId != null) {
            assertThat(overviewPage.getAccountIds()).doesNotContain(deniedLoanId);
        }
    }

    @And("the primary account balance should remain completely unchanged")
    public void verifyPrimaryBalanceUnchanged() {
        overviewPage.waitForOverviewTableToLoad();
        String primaryId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);
        double initialBal = context.getDouble(ContextKey.SOURCE_INITIAL_BALANCE);
        double currentBal = overviewPage.getAccountBalance(primaryId);

        assertThat(currentBal)
                .as("Primary account balance must remain unchanged following rejected loan")
                .isCloseTo(initialBal, within(0.01));
    }
}
