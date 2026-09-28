package com.parabank.automation;

import com.parabank.automation.driver.DriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class InspectBillPayTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = DriverManager.getDriver();
    }

    @AfterEach
    public void tearDown() {
        DriverManager.quitDriver();
    }

    @Test
    public void inspectBillPay() throws InterruptedException {
        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        driver.findElement(By.name("username")).sendKeys("HellBound");
        driver.findElement(By.name("password")).sendKeys("HellBound");
        driver.findElement(By.cssSelector("input.button[value='Log In']")).click();
        Thread.sleep(2000);

        driver.get("https://parabank.parasoft.com/parabank/billpay.htm");
        Thread.sleep(2000);

        driver.findElement(By.name("payee.name")).sendKeys("Apex Energy Grid");
        driver.findElement(By.name("payee.address.street")).sendKeys("742 Evergreen Terr");
        driver.findElement(By.name("payee.address.city")).sendKeys("Metro");
        driver.findElement(By.name("payee.address.state")).sendKeys("NY");
        driver.findElement(By.name("payee.address.zipCode")).sendKeys("10001");
        driver.findElement(By.name("payee.phoneNumber")).sendKeys("555-019-2834");
        driver.findElement(By.name("payee.accountNumber")).sendKeys("99887766");
        driver.findElement(By.name("verifyAccount")).sendKeys("99887766");
        driver.findElement(By.name("amount")).sendKeys("45.50");

        // Check what triggering click does
        Object evalResult = ((JavascriptExecutor) driver).executeScript(
                "var btn = $('input[type=button]'); " +
                "btn.click(); " +
                "return $('[id^=validationModel]').map(function() { return this.id + ' (vis: ' + $(this).is(':visible') + ', txt: ' + $(this).text().trim() + ')'; }).get().join('; ');"
        );
        System.out.println("Validation elements after click: " + evalResult);

        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
            Boolean visible = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return $('#billpayResult').is(':visible');"
            );
            Boolean errorVisible = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return $('#billpayError').is(':visible');"
            );
            System.out.println("Second " + (i+1) + " -> result: " + visible + ", error: " + errorVisible);
            if (Boolean.TRUE.equals(visible) || Boolean.TRUE.equals(errorVisible)) {
                break;
            }
        }

        String resultHtml = (String) ((JavascriptExecutor) driver).executeScript(
                "return 'result: ' + $('#billpayResult').is(':visible') + ' | error: ' + $('#billpayError').is(':visible') + ' | payeeName: ' + $('#payeeName').text() + ' | amount: ' + $('#amount').text() + ' | fromAccount: ' + $('#fromAccountId').text();"
        );
        System.out.println("=== FINAL RESULT ===");
        System.out.println(resultHtml);
        System.out.println("====================");
    }
}
