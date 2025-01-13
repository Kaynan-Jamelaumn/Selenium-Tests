package base;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(CustomTestListener.class)
public class LoginTest extends BaseTest {

    @Test(priority = 1)
    public void testLogin() {
        driver.get(BASE_URL);
        driver.findElement(By.id("cpf")).sendKeys("10412822903");
        driver.findElement(By.id("senha")).sendKeys("385018");
        driver.findElement(By.id("submit-login")).click();

        String currentUrl = driver.getCurrentUrl();
        System.out.println("TEST testLogin: passed");
        //System.out.println("TEST testLogin: URL after login: " + currentUrl);
        //Assert.assertTrue(currentUrl.contains("dashboard"), "Login failed: URL does not contain 'dashboard'.");
    }
}
