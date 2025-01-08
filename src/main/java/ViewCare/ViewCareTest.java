package ViewCare;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import org.openqa.selenium.support.ui.ExpectedConditions;

@Listeners(base.CustomTestListener.class)
public class ViewCareTest extends BaseTest {

	 @Test(priority = 1, dependsOnMethods = {"base.LoginTest.testLogin"})
    public void testViewCare() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        WebElement viewCareButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("botao_view_care")));

        Thread.sleep(500); // adding slight delay for stability

        viewCareButton.click();
        //Assert.assertTrue(driver.getCurrentUrl().contains("view-care"), "View Care navigation failed.");
    }
}
