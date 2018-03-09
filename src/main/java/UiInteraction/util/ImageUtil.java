package UiInteraction.util;

import com.google.common.base.Preconditions;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.OutputType;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class ImageUtil {

    public static void loadCV(){
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * Locate an element and takes a screen capture of the element by cropping the screenshot based on element position
     * within the visible screen
     *
     * @param driver
     * @param logger
     * @param element - element to find
     * @return - cropped buffer image of element
     */
    public static BufferedImage captureElementImage(AppiumDriver driver, Logger logger, WebElement element) {
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        BufferedImage  fullImg = null;
        try {
            fullImg = ImageIO.read(screenshot);
            // Get the location of element on the page
            org.openqa.selenium.Point point = element.getLocation();

            // Get width and height of the element
            int eleWidth = element.getSize().getWidth();
            int eleHeight = element.getSize().getHeight();
            // Crop the entire page screenshot to get only element screenshot
            return fullImg.getSubimage(point.getX(), point.getY(),eleWidth, eleHeight);
        } catch (IOException e) {
            logger.error(screenshot.getAbsolutePath() + "not found");
        }

        return null;
    }

    public static BufferedImage loadToBufferedImage(String filepath) {
        File getimge = new File(filepath);
        BufferedImage loadedimg = null;
        try {
            loadedimg = ImageIO.read( getimge );
        } catch (IOException e) {
            System.out.println(getimge.getAbsolutePath() + "not found");
            System.out.println(e.getMessage());
        }
        return loadedimg;
    }


    public static Mat loadToMat(String filepath, int width, int length){
        Mat loadedimg = Imgcodecs.imread(filepath);
        return resize(loadedimg, width, length);
    }

    public static Mat bufferedImageToMat(BufferedImage image){
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);

        return mat;
    }

    public static Mat resize(Mat imagemat, int width, int length){
        Mat resized = new Mat();
        Imgproc.resize( imagemat, resized, new Size(width - 1, length - 1) );

        return resized;
    }

    public static double match(Mat capture, Mat loadedimg, Logger logger){
        // greyscale
        Mat captureMat = greyscale(capture);
        Mat loadedimgMat = greyscale(loadedimg);

        //TODO: add edge detection for more robustness

        if (captureMat.rows() <= loadedimgMat.rows() && captureMat.cols() <= loadedimgMat.cols() ) {// loadedimage contains capturemat in size
            return match(captureMat, loadedimgMat);
        } else if (captureMat.rows() > loadedimgMat.rows() && captureMat.cols() > loadedimgMat.cols() ) { // loadedimage contains capturemat in size
            return match(loadedimgMat, captureMat);
        }

        return -1;
    }

    protected static double match(Mat template, Mat image){
        // / Create the result matrix
        int result_cols = image.cols() - template.cols() + 1;
        int result_rows = image.rows() - template.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(image, template, result, Imgproc.TM_SQDIFF);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // contains location of the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        org.opencv.core.Point matchLocation = mmr.minLoc; // leave this conflicts with org.openqa.selenium.Point;

        // quality of template in img
        return mmr.minVal;
    }


    protected static Mat greyscale(Mat image){
        // convert mat to greyscale
        Mat matgrey = new Mat(image.rows(),image.cols(),CvType.CV_8UC1);
        Imgproc.cvtColor(image, matgrey, Imgproc.COLOR_RGB2GRAY);
        byte[] data1 = new byte[matgrey.rows() * matgrey.cols() * (int)(matgrey.elemSize())];
        matgrey.get(0, 0, data1);

        return matgrey;
    }

    public static void uploadImage(AndroidDriver driver,String localfilepath,  String remotePath){
        File file = new File(localfilepath);
        try {
            driver.pushFile(remotePath, file);
        } catch (IOException e) {
            System.out.print(e.getMessage() + "\n" );
            System.out.println(file.getAbsolutePath() + " not found");
        }
    }
}
