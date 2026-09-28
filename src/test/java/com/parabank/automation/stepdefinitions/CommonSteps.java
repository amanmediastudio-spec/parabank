package com.parabank.automation.stepdefinitions;

import com.automation.utils.ElementActions;

import com.parabank.automation.config.ConfigReader;
import com.parabank.automation.driver.DriverManager;
import com.parabank.automation.pages.LoginPage;
import com.parabank.automation.pages.NavigationMenu;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Common step definitions shared across all feature scenarios.
 * Zero-arg constructor — no DI container required.
 */
public class CommonSteps {
    private static final Logger log = LoggerFactory.getLogger(CommonSteps.class);
    private final WebDriver driver = DriverManager.getDriver();
    private final LoginPage loginPage;
    private final NavigationMenu navigationMenu;

    public CommonSteps() {
        this.loginPage = new LoginPage();
        this.navigationMenu = loginPage.navigation();
    }

    @Given("the user is on the Parabank landing page")
    public void userIsOnLandingPage() {
        ElementActions.navigateToUrl(ConfigReader.getBaseUrl());
    }

    @Given("the user logs into Parabank with credentials {string} and {string}")
    public void userLogsInWithCredentials(String username, String password) {
        log.info("Logging into Parabank with username '{}'", username);
        ElementActions.navigateToUrl(ConfigReader.getBaseUrl());
        loginPage.login(username, password);

        // Self-healing fallback if database was reset in demo environment
        if (loginPage.isLoginFormPresent() && loginPage.getErrorMessage().contains("could not be verified")) {
            log.warn("Account '{}' not found. Executing automatic registration self-healing...", username);
            loginPage.clickRegister();
            new com.parabank.automation.pages.RegistrationPage(driver).register(
                    com.parabank.automation.models.CustomerProfile.defaultProfile()
            );
        }
    }

    @When("the user navigates to Open New Account")
    public void userNavigatesToOpenNewAccount() {
        navigationMenu.clickOpenNewAccount();
    }

    @When("the user navigates to Accounts Overview")
    public void userNavigatesToAccountsOverview() {
        navigationMenu.clickAccountsOverview();
    }

    @When("the user navigates to Transfer Funds")
    public void userNavigatesToTransferFunds() {
        navigationMenu.clickTransferFunds();
    }

    @When("the user navigates to Bill Pay")
    public void userNavigatesToBillPay() {
        navigationMenu.clickBillPay();
    }

    @When("the user navigates to Find Transactions")
    public void userNavigatesToFindTransactions() {
        navigationMenu.clickFindTransactions();
    }

    @When("the user navigates to Update Contact Info")
    public void userNavigatesToUpdateContactInfo() {
        navigationMenu.clickUpdateContactInfo();
    }

    @When("the user navigates to Request Loan")
    public void userNavigatesToRequestLoan() {
        navigationMenu.clickRequestLoan();
    }

    @When("the user logs out of Parabank")
    public void userLogsOutOfParabank() {
        navigationMenu.clickLogOut();
    }

    @When("attempts direct deep-link access to secure URL {string}")
    public void userAttemptsDeepLinkAccess(String pageRelativeUrl) {
        String fullUrl = ConfigReader.getBaseUrl().replace("index.htm", pageRelativeUrl);
        log.info("Attempting unauthorized access to {}", fullUrl);
        ElementActions.navigateToUrl(fullUrl);
    }

    @Then("the user should see the login form indicating session termination")
    public void userShouldSeeLoginForm() {
        assertThat(loginPage.isLoginFormPresent())
                .as("Login form should be displayed following session termination")
                .isTrue();
    }
}
