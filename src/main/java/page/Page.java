package page;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Page {

    protected static AppiumDriver driver;
    protected static Logger logger;

    private static final int POLL_TIME_SECONDS = 2;

    private enum WaitFunction{
        PRESENCE, CLICKABLE
    }

    public Page(AppiumDriver driver, Logger logger){
        this.driver = driver;
        this.logger = logger;
    }

    public AppiumDriver getDriver() {
        return driver;
    }

    public void setDriver(AppiumDriver driver) {
        this.driver = driver;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setDriverLogger(AppiumDriver driver, Logger logger) {
        this.logger = logger;
    }


    // logging -> Moving this to AndroidDriverEventListener
    public void logclick(By element){
        logger.debug(element.getClass().getName() + "clicked");
    }

    public void logsendkeys(By element, String text){
        logger.debug(element.getClass().getName() + " sent text: " + text);
    }

    public void logclear(By element){
        logger.debug(element.getClass().getName() + "cleared");
    }

    public void logKeyboardPress(String key){logger.debug("Android keyboard sent: " + key);}

    public boolean checkElementPresent(By by){
        return driver.findElements(by).size() > 0;
    }


    protected static void enterText(By byId, String text){
        WebElement element = driver.findElement(byId);
        element.click();
        element.clear();
        element.sendKeys(text);
        try {
            driver.hideKeyboard();
        } catch (Exception e){
            logger.debug("No Keyboard was found.");
        }
    }


    // waits
    protected static WebElement waitForElementPresence(By byId, int implicitSeconds){
        return waitFor(byId,implicitSeconds, WaitFunction.PRESENCE);
    }


    protected static WebElement waitForElementClickable(By byId, int implicitSeconds){
        return waitFor(byId,implicitSeconds, WaitFunction.CLICKABLE);
    }

    private static WebElement waitFor(By byId, int implicitSeconds, WaitFunction waitF){
        Wait<AppiumDriver> wait = new FluentWait<AppiumDriver>(driver)
                .withTimeout(implicitSeconds, TimeUnit.SECONDS)
                .pollingEvery(POLL_TIME_SECONDS, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

        switch (waitF){
            case PRESENCE:
                return wait.until(ExpectedConditions.presenceOfElementLocated(byId));
            case CLICKABLE:
                return wait.until(ExpectedConditions.elementToBeClickable(byId));
            default:
                return wait.until(ExpectedConditions.presenceOfElementLocated(byId));
        }
    }

    protected static boolean waitForElementThreadSleep(By id, int maxTries, int interval){
        int tries = 0;
        while (driver.findElements(id).size() > 0){
            try { // necessary wait until logged in or calling BottomNavigationBar will error out causing False-negatives
                if (tries < maxTries) {
                    Thread.sleep(interval);
                    tries++;
                } else {
                    return true;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }
        return false;
    }

    public boolean checkContextHandles(String contextToCheck){
        Set<String> pageContextNames = driver.getContextHandles();

        for (String contextElement : pageContextNames){
            if (contextElement.contains(contextToCheck)){
                return true;
            }
        }

        return false;
    }

    public static HashMap<String, WebElement> getMenuOptions(By moreMenuId, By options) throws UnsupportedOperationException{
        //TODO: refactor, slower than direct
        if( driver.findElements(options).size() < 1){
            if( driver.findElements(moreMenuId).size() < 1) {
                throw new UnsupportedOperationException("This page does not include an element with More menu");
            } else {
                driver.findElement(moreMenuId).click();
            }
        }

        waitForElementPresence(options,3);
        HashMap<String, WebElement> hashOptions = new HashMap<>();
        List<WebElement> listGroupPictureOptions = driver.findElements(options);
        for (WebElement option : listGroupPictureOptions){
            hashOptions.put(option.getText(), option);
        }
        return hashOptions;
    }

    public static HashMap<String, WebElement> getContainerViews(By containerId, By optionsId){
        //TODO: refactor, slower than direct
        WebElement searchContactsOptions = driver.findElement(containerId);
        List<WebElement> menu = searchContactsOptions.findElements(optionsId);

        HashMap<String, WebElement> menuMap = new HashMap<>();

        for (WebElement menuItem : menu){
            menuMap.put(menuItem.getText().replaceAll("[^a-zA-Z0-9]", ""), menuItem);
        }
        return menuMap;
    }

    public static List<WebElement> getContainerViewsList(By containerId, By optionsId){
        WebElement searchContactsOptions = driver.findElement(containerId);
        List<WebElement> menu = searchContactsOptions.findElements(optionsId);

        return menu;
    }

    public String getClassName(){
        Class<?> enclosingClass = getClass().getEnclosingClass();
        if (enclosingClass != null) {
            return enclosingClass.getName();
        } else {
            return getClass().getName();
        }
    }

}
