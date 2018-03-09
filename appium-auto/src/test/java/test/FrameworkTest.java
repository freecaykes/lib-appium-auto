package test;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import test.androidpage.AndroidPageImpl;
import test.iospage.iOSPageImpl;
import testbase.TestBase;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

public class FrameworkTest extends TestBase {

    private static final Logger logger = LogManager.getLogger(FrameworkTest.class);
    private static final String ANDROID_PHONE_PROFILE = "pixel_xl_api_25.json";
    private static final String IOS_PHONE_PROFILE = "iphone_7.json";

    @BeforeClass
    public static void setUp() throws IOException {
        List<String> allDevices = TestBase.getAvailableDevices();
        assertTrue (allDevices.contains(ANDROID_PHONE_PROFILE));
        assertTrue (allDevices.contains(IOS_PHONE_PROFILE));
    }

    @Test
    public void androidNulltest(){
        TestBase.setAppConfig("apk", "/src/main/resources/apkconfig.xml");
        TestBase.setUpDebug(logger,"4723", ANDROID_PHONE_PROFILE);

        AndroidPageImpl androidPage = new AndroidPageImpl(driver, logger);
        androidPage.clickNew();
    }

    @Test
    public void iosNulltest(){
        TestBase.setAppConfig("ipa", "/src/main/resources/ipaconfig.xml");
        TestBase.setUpDebug(logger,"4723", IOS_PHONE_PROFILE);

        iOSPageImpl iosPage = new iOSPageImpl(driver, logger);
        iosPage.clickNew();
    }

    @After
    public void closeApp(){
        uiAction.restartApp();
    }
}
