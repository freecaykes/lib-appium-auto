package testbase;

import UiInteraction.util.UiAction;
import UiInteraction.util.android.UiActionAndroidImpl;
import io.appium.java_client.AppiumDriver;

import runner.EnvConfig;
import runner.driver.Driver;
import UiInteraction.util.ios.UiActionIosImpl;
import UiInteraction.util.SystemUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

//@RunWith(Parallelized.class)
public class TestBase {
    private static String APPCONFIG_XML= "src/main/resources/apkconfig.xml";

    private static String deviceProfile;
    private static String port;

    protected static AppiumDriver driver;
    protected static UiAction uiAction;

    protected static AndroidDriver androidDriver;
    protected static IOSDriver iosDriver;
    protected static  Logger logger;

    // Android native actions
    protected static UiActionAndroidImpl androidNativePhone;
    protected static UiActionIosImpl iosNativePhone;

    private static final String DEVICE_LIST_RESOURCE = "src/main/resources/devices/";
    private static String APPIUM_SERVER_URL_BASE = "http://0.0.0.0:port/wd/hub";
    private static int appiumPid;
    private static final int APPIUM_DEFAULT_START_PORT = 4723;

    public TestBase(){}
    public TestBase(String device, String port ){
        this.deviceProfile = device;
        this.port = port;
    }

    /**
     * Get all Device profiles that is currently supported by this framework
     * @return - all Device profiles (iOS + Android)
     */
    public static List<String> getAvailableDevices(){ // string[] match constructor
       return SystemUtil.getFileListInDirectory(DEVICE_LIST_RESOURCE);
    }

    /**
     *  Set location of apkconfig.xml to find the apk / api file to test with
     * @param location - location of designated configfile for the frameword
     */
    public static void setAppConfig(String type, String location){
        APPCONFIG_XML = location;
        File appconfigFile = new File(APPCONFIG_XML);
        if (!appconfigFile.exists() || location.toLowerCase() == "default"){
            Path resoure = Paths.get("src", "main", "resources");
            switch (type){
                case "apk":
                    APPCONFIG_XML = resoure.toString() + "/apkconfig.xml";
                    break;
                case "ipa":
                    APPCONFIG_XML = resoure.toString() + "/ipaconfig.xml";
                    break;
            }
        }
    }

    /**
     * Called in Testbase to connect to Appium server and runing Simulator
     * @param logger
     * @throws IOException
     */
    public static void setUp(Logger logger) {
        String appiumPort = System.getProperty("appiumPort");
        String deviceProfile = System.getProperty("deviceProfile");
        setUpDebug(logger,appiumPort,deviceProfile);
    }

    /**
     * For local runs only - if run on Jenkins use setUp(Logger logger)
     * @param logger
     * @param appiumPort - portNumber Appium is running on
     * @param deviceProfile - selected device profile from resources/devices list
     * @throws IOException
     */
    public static void setUpDebug(Logger logger, String appiumPort, String deviceProfile) {
        EnvConfig appConfig = new EnvConfig(APPCONFIG_XML, logger);
        URL urlWithPort = null;
        try {
            if(appiumPort == "" || appiumPort.length() == 0){
                appiumPort = Integer.toString( APPIUM_DEFAULT_START_PORT );
            }
            urlWithPort = new URL( APPIUM_SERVER_URL_BASE.replace("port",  appiumPort ));
        } catch (MalformedURLException e) {
            logger.error("Appium ");
            e.printStackTrace();
        }
        System.out.println("On " + deviceProfile + " port: " + appiumPort + " " + urlWithPort.toString());

        driver = Driver.getInstance( appConfig, urlWithPort, appConfig.getPath(), deviceProfile, Integer.parseInt(appiumPort), logger);
        uiAction = Driver.getUiAction();
    }

    /** called after each TestClass - to quit the app
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if(driver != null) {
            driver.quit();
        }
    }


}
