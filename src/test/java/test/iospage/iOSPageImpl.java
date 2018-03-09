package test.iospage;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import page.Page;

public class iOSPageImpl extends Page {

    private static final By byXpath = By.xpath("//XCUIElementTypeOther[@name=\"I'm a new user\"]");

    public iOSPageImpl(AppiumDriver driver, Logger logger) {
        super(driver, logger);
    }

    public void clickNew(){
        waitForElementClickable(byXpath, 2);
        driver.findElement(byXpath).click();
    }
}
