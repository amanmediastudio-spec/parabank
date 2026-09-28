package com.parabank.automation.stepdefinitions;

import com.parabank.automation.driver.DriverManager;
import com.parabank.automation.pages.LoginPage;
import com.parabank.automation.pages.UpdateProfilePage;
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

/**
 * Step definitions for Profile Management scenarios.
 * Zero-arg constructor — no DI container required.
 * (This class does not share cross-step state so ScenarioContext is not needed.)
 */
public class ProfileManagementSteps {
    private static final Logger log = LoggerFactory.getLogger(ProfileManagementSteps.class);
    private final WebDriver driver = DriverManager.getDriver();
    private final UpdateProfilePage profilePage;
    private final LoginPage loginPage;

    public ProfileManagementSteps() {
        this.profilePage = new UpdateProfilePage();
        this.loginPage = new LoginPage();
    }

    @And("updates the profile with new contact information:")
    public void updateContactInformation(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);

        String street = data.get("Street");
        String city = data.get("City");
        String state = data.get("State");
        String zip = data.get("Zip Code");
        String phone = data.get("Phone Number");

        profilePage.updateAddressDetails(street, city, state, zip, phone);
    }

    @Then("the profile update confirmation message should be displayed")
    public void verifyProfileUpdateConfirmation() {
        assertThat(profilePage.isProfileUpdateSuccessful())
                .as("Profile update confirmation banner should be visible")
                .isTrue();
    }

    @When("logs back in with credentials {string} and {string}")
    public void logBackInWithCredentials(String username, String password) {
        loginPage.login(username, password);
    }

    @Then("the persisted street address should be {string}")
    public void verifyPersistedStreet(String expectedStreet) {
        assertThat(profilePage.getStreet())
                .as("Street address should persist across session")
                .isEqualTo(expectedStreet);
    }

    @And("the persisted city should be {string}")
    public void verifyPersistedCity(String expectedCity) {
        assertThat(profilePage.getCity())
                .as("City should persist across session")
                .isEqualTo(expectedCity);
    }

    @And("the persisted state should be {string}")
    public void verifyPersistedState(String expectedState) {
        assertThat(profilePage.getState())
                .as("State should persist across session")
                .isEqualTo(expectedState);
    }

    @And("the persisted zip code should be {string}")
    public void verifyPersistedZipCode(String expectedZip) {
        assertThat(profilePage.getZipCode())
                .as("Zip code should persist across session")
                .isEqualTo(expectedZip);
    }

    @And("the persisted phone number should be {string}")
    public void verifyPersistedPhoneNumber(String expectedPhone) {
        assertThat(profilePage.getPhoneNumber())
                .as("Phone number should persist across session")
                .isEqualTo(expectedPhone);
    }
}
