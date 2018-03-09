package runner.driver;

import UiInteraction.util.UiAction;
import UiInteraction.util.android.UiActionAndroidImpl;
import UiInteraction.util.ios.UiActionIosImpl;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.Logger;
import runner.EnvConfig;

import java.io.IOException;
import java.net.URL;

public class Driver {

    private static AppiumDriver driver;
    private static UiAction uiAction;

    private Driver(){}

    /**
     * Singleton object of initiating the driver for different phone OS type depending on the one declared on apkconfig.xml
     * @param url - Appium url
     * @param appPath - appPath from apkconfig.xml
     * @param deviceFileName - device type of running ie. Nexus_5X_API_26
     * @param port - Appium port run test with
     * @param logger - log4j Logger for debug outputs
     * @return - Driver to interact with
     */
    public static AppiumDriver getInstance(EnvConfig config, URL url, String appPath, String deviceFileName, int port, Logger logger) {
        try{
            switch (config.getOs()){
                case "ios":
                    IOSDriver iosDriver = DriverFactory.createIOSDriver(config, url, appPath, deviceFileName, port, logger);
                    uiAction = new UiActionIosImpl( iosDriver, logger);
                    driver = iosDriver;
                    break;
                case "android":
                    AndroidDriver androidDriver = DriverFactory.createAndroidDriver(config, url, appPath, deviceFileName, port, logger);
                    uiAction = new UiActionAndroidImpl( androidDriver, logger);
                    driver = androidDriver;
                    break;
            }
        } catch (IOException e){
            logger.error("Failing Test - Cannot find a running Simulator with Profile: " + deviceFileName + " on Appium: " + url.toString());
            e.printStackTrace();
        }

        return driver;
    }

    public static UiAction getUiAction(){
        return uiAction;
    }

}
