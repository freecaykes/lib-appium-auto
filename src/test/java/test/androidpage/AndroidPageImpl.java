package test.androidpage;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import page.Page;

public class AndroidPageImpl extends Page {

    private static final By byXpath = By.xpath("//android.widget.TextView[@text=\"I'M A NEW USER\"]");

    public AndroidPageImpl(AppiumDriver driver, Logger logger) {
        super(driver, logger);
    }

    public void clickNew(){
        try {
            Thread.sleep(1000);
            waitForElementClickable(byXpath, 2);
            driver.findElement(byXpath).click();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
