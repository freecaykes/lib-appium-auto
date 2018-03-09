package UiInteraction.util.android;

import UiInteraction.util.UiAction;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

import io.appium.java_client.android.Connection;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Set;

/**
 * Created by oserhiy on 2017-08-11.
 */
public class UiActionAndroidImpl implements UiAction {

    private AndroidDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;
    private Logger logger;

    public UiActionAndroidImpl(AndroidDriver driver, WebDriverWait wait, Logger logger) {
        this.driver = driver;
        this.wait = wait;
        this.logger = logger;
        shortWait = new WebDriverWait(driver,0,250);
    }

    public UiActionAndroidImpl(AndroidDriver driver, Logger logger) {
        this.driver = driver;
        this.logger = logger;
        shortWait = new WebDriverWait(driver,0,250);
    }

    @Override
    public WebElement findElementOnScreenByName(String elementName) {
        logger.debug("findElementOnScreenByName: " + elementName);
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(elementName)));
        return driver.findElement(By.name(elementName));
    }

    @Override
    public WebElement findElementOnScreenByAccessibilityId(String elementId) {
        logger.debug("findElementOnScreenByAccessibilityId: " + elementId);
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        return driver.findElement(By.id(elementId));
    }

    @Override
    public void clickButtonByName(String buttonName) {
        logger.debug("clickButtonByName: " + buttonName);
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(buttonName)));
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
        //TODO: is this button relevant here (in android)?
    }

    @Override
    public void typeText(String text) {
        logger.debug("typeText: " + text);
        driver.getKeyboard().pressKey(text);
    }

    @Override
    public void typeTextInField(String text, String textFieldId) {
        logger.debug("typeTextInField: Text - " + text + "; Field: " + textFieldId);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(textFieldId)));
        driver.findElement(By.id(textFieldId)).sendKeys(text);
    }

    @Override
    public void waitForElementById(String elementId) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));

    }

    @Override
    public void airplaneMode(boolean on) {// does not work on emulators with 7.*
        Connection connection = on ? Connection.NONE : Connection.ALL;
        driver.setConnection(connection);
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
        // TODO
    }

    @Override
    public void clickDismissAlert() {
        logger.debug("clickDismissAlert");
        // TODO
    }

    @Override
    public void swipeRightToLeft() {
        logger.debug("swipeRightToLeft");
        int width = driver.manage().window().getSize().getWidth();
        int heightHalf = driver.manage().window().getSize().getHeight()/2;
        driver.swipe(width * 80/100, heightHalf, width * 20/100, heightHalf, 10);
    }

    @Override
    public void swipeLeftToRight() {
        logger.debug("swipeLeftToRight");
        int width = driver.manage().window().getSize().getWidth();
        int heightHalf = driver.manage().window().getSize().getHeight()/2;
        driver.swipe(width * 20/100, heightHalf, width * 80/100, heightHalf, 10);
    }

    @Override
    public void scrollDown(int distance) {
        Dimension size = driver.manage().window().getSize();
        distance = Math.min( distance, (int) Math.floor(size.getHeight() / 2 ) );
        driver.swipe( (int) Math.floor(size.getWidth() / 2 ) , (int) Math.floor(size.getHeight() / 2 ) , (int) Math.floor(size.getWidth() / 2 ), (int) ( Math.floor(size.getHeight() / 2 ) - distance + 1 ), 0);
    }

    @Override
    public void scrollUp(int distance) {
        Dimension size = driver.manage().window().getSize();
        distance = Math.min( distance, (int) Math.floor(size.getHeight() / 2 ) );
        driver.swipe( (int) Math.floor(size.getWidth() / 2 ) , (int) Math.floor(size.getHeight() / 2 ) , (int) Math.floor(size.getWidth() / 2 ), (int) ( Math.floor(size.getHeight() / 2 ) + distance - 1), 0);

    }
    @Override
    public void scrollToFind(String text){
        driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"" + text + "\").instance(0))");
    }


    @Override
    public void restartApp(){
        try {
            driver.closeApp();
            driver.launchApp();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void idClick(String id) {
    		logger.debug("idClick: " + id);
    		driver.findElement(By.id(id)).click();
    }
    public WebElement id(String id) {
    		logger.debug("id: " + id);
    		return driver.findElement(By.id(id));
    }
    
    public void idKeys(String id, String keys) {
    		logger.debug("idKeys - id: " + id + " keys: " + keys);
    		driver.findElement(By.id(id)).sendKeys(keys);
    }
    
    public MobileElement name(String text) {
    		logger.debug("name: " + text);
    		return (MobileElement)(driver.findElement(By.name(text)));
    }
    
    public MobileElement contentDesc(String desc) {
    		logger.debug("content-desc: " + desc);
    		return (MobileElement)driver.findElementByAccessibilityId(desc);
    }
    
    public WebElement xpath(String s) {
    		logger.debug("xpath: " + s);
    		return driver.findElement(By.xpath(s));
    }
    
}
