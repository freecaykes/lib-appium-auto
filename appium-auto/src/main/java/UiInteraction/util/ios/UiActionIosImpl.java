package UiInteraction.util.ios;

import UiInteraction.util.UiAction;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Set;

/**
 * Created by oserhiy on 2017-08-04.
 */
public class UiActionIosImpl implements UiAction {

    private IOSDriver driver;
    private WebDriverWait wait;

    private Logger logger;

    public UiActionIosImpl(IOSDriver driver, WebDriverWait wait, Logger logger) {
        this.driver = driver;
        this.wait = wait;
        this.logger = logger;
    }

    public UiActionIosImpl(IOSDriver iosDriver, Logger logger) {
        this.driver = iosDriver;
        this.logger = logger;
        this.wait = new WebDriverWait(driver,0,250);
    }

    @Override
    public void typeTextInField(String text, String textFieldId) {
        logger.debug("typeTextInField: Text - " + text + "; Field: " + textFieldId);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(textFieldId)));
        driver.findElement(By.id(textFieldId)).sendKeys(text);
    }

    @Override
    public WebElement findElementOnScreenByName(String elementName) {
        logger.debug("findElementOnScreenByName: " + elementName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(elementName)));
        return driver.findElement(By.name(elementName));
    }

    @Override
    public WebElement findElementOnScreenByAccessibilityId(String elementId) {
        logger.debug("findElementOnScreenByAccessibilityId: " + elementId);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        return driver.findElement(By.id(elementId));
    }

    @Override
    public void clickButtonByName(String buttonName) {
        logger.debug("clickButtonByName");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(buttonName)));
        driver.findElementByName(buttonName).click();
    }

    @Override
    public void clickTextField(String textFieldName) {
        logger.debug("clickTextField: " + textFieldName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(textFieldName)));
        driver.findElement(By.id(textFieldName)).click();
    }

    @Override
    public void clearTextField(String textFieldName) {
        logger.debug("clearTextField: " + textFieldName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(textFieldName)));
        driver.findElement(By.id(textFieldName)).clear();
    }

    @Override
    public void clickCloseButton() {
        logger.debug("clickCloseButton");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("xIcon")));
        driver.findElement(By.id("xIcon")).click();
    }

    @Override
    public void typeText(String text) {
        logger.debug("typeText: " + text);
        driver.getKeyboard().pressKey(text);
    }

    @Override
    public void airplaneMode(boolean on) {

    }

    @Override
    public Alert switchToAlert() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Alert alert = null;
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            alert = driver.switchTo().alert();
        } catch (Exception e) {
            System.err.println("no alert visible after 10 sec.");
        }
        return alert;
    }

    @Override
    public void switchToWebView(){
        Set<String> contextNames = driver.getContextHandles();
        String webview = "";
        for (String contextName : contextNames) {
            if (contextName.contains("WEBVIEW")){
                webview = contextName;
                break;
            }
        }
        driver.context(webview); // set context to WEBVIEW_1
    }

    @Override
    public void clickAcceptAlert() {
        logger.debug("clickAcceptAlert");
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    @Override
    public void clickDismissAlert() {
        logger.debug("clickDismissAlert");
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
    }

    @Override
    public void swipeRightToLeft() {
        logger.debug("swipeRightToLeft");
        int width = driver.manage().window().getSize().getWidth();
        int heightHalf = driver.manage().window().getSize().getHeight()/2;
        driver.swipe(width * 80/100, heightHalf, width * 20/100, heightHalf, 10);
    }

    @Override
    public void scrollDown(int distance) {

    }

    @Override
    public void scrollUp(int distance) {

    }

    @Override
    public void scrollToFind(String text) {

    }

    @Override
    public void swipeLeftToRight() {
        logger.debug("swipeLeftToRight");
        int width = driver.manage().window().getSize().getWidth();
        int heightHalf = driver.manage().window().getSize().getHeight()/2;
        driver.swipe(width * 20/100, heightHalf, width * 80/100, heightHalf, 10);
    }

    @Override
    public void waitForElementById(String elementId) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
    }

    @Override
    public void restartApp() {
        try{
            driver.closeApp();
            driver.launchApp();
        }catch (Exception e) {}
    }

}
