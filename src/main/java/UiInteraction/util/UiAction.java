package UiInteraction.util;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.Alert;

/**
 * Created by oserhiy on 2017-08-04.
 */
public interface UiAction {

    // Simple actions
    void airplaneMode(boolean on);
    Alert switchToAlert();
    void switchToWebView();
    void clickAcceptAlert();
    void clickDismissAlert();
    void clickButtonByName(String name);
    void clickTextField(String textFieldName);
    void clearTextField(String textFieldName);
    void clickCloseButton();
    WebElement findElementOnScreenByAccessibilityId(String accessibilityId);
    WebElement findElementOnScreenByName(String name);
    void swipeLeftToRight();
    void swipeRightToLeft();
    void scrollDown(int distance); // distance in px
    void scrollUp(int distance);
    void scrollToFind(String text);
    void typeText(String text);
    void typeTextInField(String text, String textFieldId);
    void waitForElementById(String elementId);
    void restartApp();

}
