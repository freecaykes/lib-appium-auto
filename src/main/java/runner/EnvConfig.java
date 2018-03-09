package runner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

public class EnvConfig {

	private final String ACCOUNT_KEY = "account";
	private final String APPPATH_KEY = "apppath";
	private final String APPIUMSERVERURL_KEY = "appiumserverurl";

	private static final String JSON_EXT = "json";
    private static String DEVICE_LIST_RESOURCE = "src/main/resources/devices/";

	private final String XPATH_ACCOUNT_VALUE = "account[@user]";
	private final String XPATH_OS_VALUE = "//account/os[@value]";
	private final String XPATH_APPPATH_VALUE = "//account/apppath[@value]";
	//Android Specific
	private final String XPATH_APPPACKAGE_VALUE = "//account/apppackage[@value]";
	private final String XPATH_APPACTIVITY_VALUE = "//account/appactivity[@value]";
	private final String XPATH_APPWAITACTIVITY_VALUE = "//account/appwaitactivity[@value]";

	private String account;
	private String os;
	private URL url;
	private String appPath;
	//Android Specific
	private String appPackage;
	private String appActivity;
	private String appWaitActivity;



	/**
	 * Loads configuration of apk/ipaconfig.xml into this EnvConfig object
	 *
	 * @param log - log4j Logger for debug outputs
	 */
	public EnvConfig(String APPCONFIG_XML_LOCATION, Logger log) {
		readAppConfig(APPCONFIG_XML_LOCATION, log);
		System.out.println( "Configuration set to - account: " + getAccount() + " os: " + getOs() + " apppath: " + getPath());
	}

	/**
	 * Reads from apk/ipaconfig.xml to class variables of String account, String os, URL url, String appPath
	 *
	 * @param log - log4j Logger for debug outputs
	 */
	private void readAppConfig(String APPCONFIG_XML_LOCATION, Logger log){
		File fXmlFile = new File(APPCONFIG_XML_LOCATION);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			XPath xpath = XPathFactory.newInstance().newXPath();

            account = getValueAsString(xpath, doc, XPATH_ACCOUNT_VALUE,0, "value");
            appPath = getValueAsString(xpath, doc, XPATH_APPPATH_VALUE, 0, "value");
            os = getValueAsString(xpath, doc, XPATH_OS_VALUE,0, "value");

			if(os.equals( "android") ){
				XPathExpression apppackage_path = xpath.compile(XPATH_APPPACKAGE_VALUE);
				XPathExpression appactivity_path = xpath.compile(XPATH_APPACTIVITY_VALUE);
				XPathExpression appwaitactivity_path = xpath.compile(XPATH_APPWAITACTIVITY_VALUE);

				NodeList appaackage_nl = (NodeList) apppackage_path.evaluate(doc, XPathConstants.NODESET);
				appPackage = appaackage_nl.item(0).getAttributes().getNamedItem("value").getNodeValue();
				NodeList appactivity_nl = (NodeList) appactivity_path.evaluate(doc, XPathConstants.NODESET);
				appActivity = appactivity_nl.item(0).getAttributes().getNamedItem("value").getNodeValue();
				NodeList appwaitactivity_nl = (NodeList) appwaitactivity_path.evaluate(doc, XPathConstants.NODESET);
				appWaitActivity = appwaitactivity_nl.item(0).getAttributes().getNamedItem("value").getNodeValue();
			}

		} catch (SAXException e) {
			log.fatal("XML formatting error on: " + APPCONFIG_XML_LOCATION);
			e.printStackTrace();
		} catch (IOException e) {
			log.fatal(APPCONFIG_XML_LOCATION + "Not found or corrupted");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}


	private String getValueAsString(XPath xpath, Document document, String xpathValue, int index, String attribute) throws XPathExpressionException {
        XPathExpression xpathEvaluatedExpression = xpath.compile(xpathValue);
        NodeList nodelist = (NodeList) xpathEvaluatedExpression.evaluate(document, XPathConstants.NODESET);
        return nodelist.item(index).getAttributes().getNamedItem(attribute).getNodeValue();
    }


    /**
     * Load a device profile in .json format into driver
     *
     * @param deviceFileName - from resources/devices
     * @param appPath - appPath from apkconfig.xml
     * @return
     * @throws IOException
     */
    public static DesiredCapabilities readDeviceSettings(EnvConfig config, String deviceFileName, String appPath) throws IOException {
        String[] deviceJsonFile = deviceFileName.split("\\.");

        String extension = deviceJsonFile[ deviceJsonFile.length - 1]; // last split is extension

        if (!extension.equals(JSON_EXT)){
            throw new IOException("Device Capabilities are not in JSON format");
        }

        DesiredCapabilities caps = new DesiredCapabilities();
        JSONParser parser = new JSONParser();

        try {
            JSONObject device = (JSONObject) parser.parse(new FileReader(DEVICE_LIST_RESOURCE + deviceFileName));
            // rest of the capabilities
            Set<String> keys = device.keySet();
            for(String capName : keys){
                String value = (String) device.get(capName); // just to be safe create a - String object first
                caps.setCapability(capName, value);
            }

            // application path is specific to the Machine running it - defined in apkconfig.xml
            caps.setCapability("app", appPath);

            if (config.getOs() == "android"){
                caps.setCapability("appPackage", config.getAppPackage());
                caps.setCapability("appActivity", config.getAppActivity());
                caps.setCapability("appWaitActivity", config.getAppWaitActivity());
            }

            if (config.getOs() == "ios"){
                
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return caps;
    }

	/**
	 * Getters for each xml field
	 *
	 * @return - as String
	 */
	public String getPath() {
		return appPath;
	}

	public String getAccount() {
		return account;
	}

	public String getOs() {
		return os;
	}

	public URL getUrl() {
		return url;
	}


	/**
	 * Android Specific
	 *
	 * @return - as String
	 */

	public String getAppPackage() {
		if (os == "android"){ return appPackage; }
		return "";
	}

	public String getAppActivity() {
		if (os == "android"){ return appActivity; }
		return "";
	}

	public String getAppWaitActivity() {
		if (os == "android"){ return appWaitActivity; }
		return "";
	}



}
