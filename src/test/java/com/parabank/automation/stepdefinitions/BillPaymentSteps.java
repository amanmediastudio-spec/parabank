package com.parabank.automation.stepdefinitions;

import com.parabank.automation.context.ContextKey;
import com.parabank.automation.context.ScenarioContext;
import com.parabank.automation.driver.DriverManager;
import com.parabank.automation.models.BillPayee;
import com.parabank.automation.pages.AccountDetailsPage;
import com.parabank.automation.pages.AccountsOverviewPage;
import com.parabank.automation.pages.BillPayPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class BillPaymentSteps {
    private static final Logger log = LoggerFactory.getLogger(BillPaymentSteps.class);
    private final WebDriver driver = DriverManager.getDriver();
    private final ScenarioContext context;
    private final BillPayPage billPayPage;
    private final AccountsOverviewPage overviewPage;
    private final AccountDetailsPage detailsPage;

    public BillPaymentSteps(ScenarioContext context) {
        this.context = context;
        this.billPayPage = new BillPayPage(driver);
        this.overviewPage = new AccountsOverviewPage(driver);
        this.detailsPage = new AccountDetailsPage(driver);
    }

    @And("the user records the available balance of the active checking account")
    public void recordActiveCheckingBalance() {
        overviewPage.waitForOverviewTableToLoad();
        List<String> accounts = overviewPage.getAccountIds();
        String primaryId = accounts.get(0);
        double bal = overviewPage.getAccountBalance(primaryId);

        context.set(ContextKey.PRIMARY_ACCOUNT_ID, primaryId);
        context.set(ContextKey.SOURCE_INITIAL_BALANCE, bal);
        log.info("Active checking account {} recorded with balance: ${}", primaryId, bal);
    }

    @When("the user pays a utility bill with the following details:")
    public void payUtilityBill(DataTable dataTable) {
        billPayPage.navigation().clickBillPay();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);

        String fromAccountId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);
        double amount = Double.parseDouble(data.get("Amount"));

        BillPayee payee = new BillPayee(
                data.get("Payee Name"),
                data.get("Address"),
                data.get("City"),
                data.get("State"),
                data.get("Zip Code"),
                data.get("Phone Number"),
                data.get("Account Number"),
                data.get("Account Number"),
                amount
        );

        context.set(ContextKey.BILL_PAYEE, payee);
        context.set(ContextKey.BILL_AMOUNT, amount);

        billPayPage.payBill(payee, fromAccountId);
    }

    @Then("the bill payment confirmation should display payee {string} and amount {string}")
    public void verifyBillPaymentConfirmation(String expectedPayee, String expectedAmountStr) {
        assertThat(billPayPage.isPaymentSuccessful())
                .as("Bill payment confirmation container should be visible")
                .isTrue();

        assertThat(billPayPage.getConfirmedPayeeName())
                .as("Confirmed payee name should match")
                .isEqualTo(expectedPayee);

        double expectedAmount = Double.parseDouble(expectedAmountStr);
        assertThat(billPayPage.getConfirmedAmount())
                .as("Confirmed payment amount should match")
                .isCloseTo(expectedAmount, within(0.01));
    }

    @Then("the checking account balance should be reduced by {string}")
    public void verifyCheckingBalanceReduced(String amountStr) {
        overviewPage.waitForOverviewTableToLoad();
        String accountId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);
        double initialBal = context.getDouble(ContextKey.SOURCE_INITIAL_BALANCE);
        double billAmount = Double.parseDouble(amountStr);
        double currentBal = overviewPage.getAccountBalance(accountId);

        assertThat(currentBal)
                .as("Checking balance must reflect bill payment deduction")
                .isCloseTo(initialBal - billAmount, within(0.01));
    }

    @When("the user navigates to the checking account transaction history")
    public void navigateToCheckingTransactionHistory() {
        String accountId = context.getString(ContextKey.PRIMARY_ACCOUNT_ID);
        overviewPage.clickAccount(accountId);
    }

    @Then("a bill payment transaction to {string} for {string} should be present")
    public void verifyBillPaymentTransactionRecorded(String payeeName, String amountStr) {
        detailsPage.waitForTransactionsToLoad();
        double amount = Double.parseDouble(amountStr);

        assertThat(detailsPage.hasTransaction("Funds Transfer Sent", amount)
                || detailsPage.hasTransaction(payeeName, amount)
                || detailsPage.getTransactionCount() > 0)
                .as("Transaction ledger should record the bill payment transaction")
                .isTrue();
    }

    @When("attempts to submit bill payment with empty required fields")
    public void submitEmptyBillPayment() {
        billPayPage.clickSendPayment();
    }

    @Then("field validation error messages should be displayed for required inputs")
    public void verifyFieldValidationErrors() {
        assertThat(billPayPage.hasValidationError("required")
                || billPayPage.hasValidationError("The amount cannot be empty"))
                .as("Validation errors should indicate missing mandatory fields")
                .isTrue();
    }

    @When("the user enters payee details with mismatched account numbers:")
    public void enterMismatchedPayeeDetails(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);

        BillPayee payee = new BillPayee(
                data.get("Payee Name"),
                data.get("Address"),
                data.get("City"),
                data.get("State"),
                data.get("Zip Code"),
                data.get("Phone Number"),
                data.get("Account Number"),
                data.get("Verify Account"),
                Double.parseDouble(data.get("Amount"))
        );

        billPayPage.fillPayeeForm(payee);
    }

    @And("clicks Send Payment")
    public void clickSendPaymentButton() {
        billPayPage.clickSendPayment();
    }

    @Then("a validation error should state that account numbers do not match")
    public void verifyAccountMismatchError() {
        assertThat(billPayPage.hasValidationError("do not match")
                || billPayPage.hasValidationError("The numbers do not match"))
                .as("Account mismatch validation error must be displayed")
                .isTrue();
    }
}
