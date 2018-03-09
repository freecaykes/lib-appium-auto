package UiInteraction.util.server;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.ServerSocket;

public class AppiumServer {

    private AppiumDriverLocalService service;
    private AppiumServiceBuilder builder;
    private DesiredCapabilities cap;

    public AppiumServer(DesiredCapabilities cap, String url, int port) {

        //Build the Appium service
        builder = new AppiumServiceBuilder();
//        builder.usingAnyFreePort();
        builder.withIPAddress(url);
        builder.usingPort(port);
        builder.withCapabilities(cap);
        builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
        builder.withArgument(GeneralServerFlag.LOG_LEVEL,"error");

        //Start the server with the builder
        try {
            service = AppiumDriverLocalService.buildService(builder);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start() { service.start();}

    public void stop() {
        service.stop();
    }

    public boolean running(int port) {

        boolean isServerRunning = false;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (IOException e) {
            //if control comes here, then it means that the port is in use
            isServerRunning = true;
        } finally {
            serverSocket = null;
        }
        return isServerRunning;
    }

}
