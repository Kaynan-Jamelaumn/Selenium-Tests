package ViewCare;

import base.BaseTest;
import base.CustomTestListener;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Listeners(CustomTestListener.class)
public class BaseEquipmentTest extends BaseTest {

    // Locator Constants
    private static final By STATS_CLASS = By.className("stats");
    private static final By CARD_FOOTER = By.className("card-footer");
    private static final By BUTTON_TAG = By.tagName("button");
    private static final By LOADING_CLASS = By.className("loading");
    private static final By INFO_MESSAGE_ID = By.id("info_mensagem");
    private static final By MODAL_FOOTER = By.className("modal-footer");
    private static final By LINK_TAG = By.tagName("a");

    protected void validateLastInfo(WebElement row) {
        String text = row.findElement(STATS_CLASS).getText();

        Pattern pattern = Pattern.compile("Última informação: (\\d{2}/\\d{2}/\\d{4}) (\\d{2}:\\d{2}:\\d{2})");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String dateStr = matcher.group(1);
            String timeStr = matcher.group(2);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime extractedDateTime = LocalDateTime.parse(dateStr + " " + timeStr, formatter);
            LocalDateTime currentDateTime = LocalDateTime.now();

            Assert.assertEquals(extractedDateTime.toLocalDate(), currentDateTime.toLocalDate(), "The dates do not match.");
            long minutesDifference = ChronoUnit.MINUTES.between(extractedDateTime, currentDateTime);
            Assert.assertTrue(Math.abs(minutesDifference) <= 6, "The time difference is more than 6 minutes.");

            System.out.println("Validated Date and Time: " + extractedDateTime);
            System.out.println("Current Date and Time: " + currentDateTime);
        } else {
            Assert.fail("The date and time information was not found.");
        }
    }

    protected void accessEquipment(WebElement row, String script, String... args) {
        WebElement footer = row.findElement(CARD_FOOTER);
        WebElement button = footer.findElement(BUTTON_TAG);
        Assert.assertNotNull(button, "Element 'button' not found");
        button.click();
        
        WebElement loadingElement = driver.findElement(LOADING_CLASS);
        if (loadingElement.isDisplayed()) {
            waitForElementToDisappear(LOADING_CLASS);
            System.out.println("Loading completed.");
        } else {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            try {
                WebElement offlinePopup = wait.until(ExpectedConditions.visibilityOfElementLocated(INFO_MESSAGE_ID));

                if (offlinePopup.isDisplayed()) {
                    WebElement parentElement = offlinePopup.findElement(By.xpath("../.."));
                    WebElement siblingDivCloseButton = parentElement.findElement(MODAL_FOOTER);
                    WebElement linkElement = siblingDivCloseButton.findElement(LINK_TAG);

                    linkElement.click();
                    Assert.fail("Popup 'offlinePopup' should not be displayed. Equipment is offline.");
                }
            } catch (Exception e) {
                tryWaitLoading();
                System.out.println("Loading completed.");
            }
        }
    }
}
