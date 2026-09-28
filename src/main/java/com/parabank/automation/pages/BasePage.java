package com.parabank.automation.pages;

import org.openqa.selenium.WebDriver;

/**
 * Base Page offering reusable abstractions, delegating to AI Automation Framework Core SDK BasePage.
 */
public abstract class BasePage extends com.automation.pages.BasePage {

    public BasePage() {
        super();
    }

    public BasePage(String pageName) {
        super(pageName);
    }

    public BasePage(WebDriver driver) {
        super(driver);
    }

    public BasePage(String pageName, WebDriver driver) {
        super(pageName, driver);
    }
}
