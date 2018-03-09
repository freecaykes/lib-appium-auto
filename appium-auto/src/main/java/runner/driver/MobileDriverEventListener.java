package runner.driver;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

/**
 *
 */
public class MobileDriverEventListener implements WebDriverEventListener{

    private Logger logger;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void beforeNavigateTo(String s, WebDriver webDriver) {

    }

    @Override
    public void afterNavigateTo(String s, WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateBack(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateBack(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateForward(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateForward(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {

    }

    /**
     * For WebElement.click()
     *
     * @param webElement
     * @param webDriver
     */
    @Override // click()
    public void afterClickOn(WebElement webElement, WebDriver webDriver) {
        String elementID = webElement.getAttribute("id");
        logger.debug(elementID + "clicked");
    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver) {

    }

    /**
     * For WebElement.sendKeys(), WebElement.clear()
     * @param webElement
     * @param webDriver
     */
    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver) {
        String elementID = webElement.getAttribute("id");
        String value = webElement.getAttribute("value");

        if (value.length() > 0) {
            System.out.println(elementID + "sent text: " + value);
            logger.debug(elementID + "sent text: " + value);
        }else {
            System.out.println(elementID + "cleared");
            logger.debug( elementID + "cleared");
        }
    }

    @Override
    public void beforeScript(String s, WebDriver webDriver) {

    }

    @Override
    public void afterScript(String s, WebDriver webDriver) {

    }

    @Override
    public void onException(Throwable throwable, WebDriver webDriver) {

    }

}
