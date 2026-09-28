package com.parabank.automation.pages;

import com.parabank.automation.utils.ElementActions;
import com.parabank.automation.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enterprise BasePage representing foundational page mechanics,
 * synchronization hooks, and shared navigation components.
 */
public abstract class BasePage {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WaitUtils waitUtils;
    protected final ElementActions actions;
    protected final NavigationMenu navigationMenu;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.actions = new ElementActions(driver, waitUtils);
        this.navigationMenu = new NavigationMenu(waitUtils, actions);
    }

    public NavigationMenu navigation() {
        return this.navigationMenu;
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void refresh() {
        driver.navigate().refresh();
        waitUtils.waitForAjax();
    }
}
