package runner.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.events.EventFiringWebDriver;
import runner.EnvConfig;

public class DriverFactory {

    private static final String JSON_EXT = "json";

    private static final String DEVICE_LIST_RESOURCE = "src/main/resources/devices/";

    /**
     * Create an IOSDriver instance with default wait time of 5 seconds
     *
     * @param url - Appium url
     * @param appPath - appPath from apkconfig.xml
     * @param deviceFileName - device type of running ir. Nexus_5X_API_26
     * @param port - Appium port run test with
     * @param logger - log4j Logger for debug outputs
     * @return
     * @throws IOException
     */
    public static IOSDriver createIOSDriver(EnvConfig config, URL url, String appPath, String deviceFileName, int port, Logger logger) throws IOException {
        DesiredCapabilities driverCaps = EnvConfig.readDeviceSettings(config, deviceFileName, appPath);
        return new IOSDriver(url, driverCaps);
    }

    /**
     * Create an AndroidDriver instance with default wait time of 5 seconds
     *
     * @param url - Appium url
     * @param appPath - appPath from apkconfig.xml
     * @param deviceFileName - device type of running ir. Nexus_5X_API_26
     * @param port - Appium port run test with
     * @param logger - log4j Logger for debug outputs
     * @return
     * @throws IOException
     */
    public static AndroidDriver createAndroidDriver(EnvConfig config, URL url, String appPath, String deviceFileName, int port, Logger logger) throws IOException {
        DesiredCapabilities driverCaps = EnvConfig.readDeviceSettings(config, deviceFileName, appPath);
        return new AndroidDriver(url, driverCaps);
    }

    /**
     * Create an AppiumDriver instance based on running device from (deviceFileName) and running appium server (appium_url/hub/port)
     *
     * @param url - Appium url
     * @param appPath - appPath from apkconfig.xml
     * @param deviceFileName - device type of running ir. Nexus_5X_API_26
     * @param port - Appium port run test with
     * @param logger - log4j Logger for debug outputs
     * @return
     * @throws IOException
     */
    protected static AppiumDriver initDriver(AppiumDriver driver, URL url, String appPath, String deviceFileName, int implicitWaiTime, int port, Logger logger) throws IOException {
        driver.manage().timeouts().implicitlyWait(implicitWaiTime,  TimeUnit.SECONDS); //implicit wait
        return (AppiumDriver) setListener(driver, logger).getWrappedDriver();
    }

    /**
     * EventFiringWebDriver is attached to all WebElement Actions of and is created logged by Logger
     *
     * @param driver - Driver attached to Listener
     * @param logger - Logger attach to Listener
     * @return
     */
    private static EventFiringWebDriver setListener(AppiumDriver driver, Logger logger){
        MobileDriverEventListener eventListener = new MobileDriverEventListener();
        eventListener.setLogger(logger);
        EventFiringWebDriver eventHandler = new EventFiringWebDriver(driver);
        eventHandler.register(eventListener);

        return eventHandler;
    }

}
