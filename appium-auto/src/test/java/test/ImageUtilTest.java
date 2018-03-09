package test;

import UiInteraction.util.ImageUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import testbase.TestBase;

import java.io.IOException;

public class ImageUtilTest extends TestBase {

    @BeforeClass
    public static void setUp() throws IOException {
        // load OpenCV library
        ImageUtil.loadCV();
    }

    @Test
    public void nulltest(){

    }

}
