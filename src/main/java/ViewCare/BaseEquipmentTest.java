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

    protected void validateLastInfo(WebElement row) {
        String text = row.findElement(By.className("stats")).getText();

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
        WebElement footer = row.findElement(By.className("card-footer"));
        WebElement button = footer.findElement(By.tagName("button"));
        Assert.assertNotNull(button, "Element 'button' not found");
        button.click();
        
        WebElement loadingElement = driver.findElement(By.className("loading"));
	    if (loadingElement.isDisplayed()) {
	    	waitForElementToDisappear(By.className("loading"));
            System.out.println("Loading completed.");
	    }
	    else {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            WebElement offlinePopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("info_mensagem")));

            if (offlinePopup.isDisplayed()) {
                WebElement parentElement = offlinePopup.findElement(By.xpath("../.."));
                WebElement siblingDivCloseButton = parentElement.findElement(By.className("modal-footer"));
                WebElement linkElement = siblingDivCloseButton.findElement(By.tagName("a"));

                linkElement.click();
                Assert.fail("Popup 'offlinePopup' should not be displayed. Equipment is offline.");
            }
        } catch (Exception e) {
        	tryWaitLoading();
            //waitForElement(By.className("loading"));
            //waitForElementToDisappear(By.className("loading"));
            System.out.println("Loading completed.");
        }
        }
    }
}
