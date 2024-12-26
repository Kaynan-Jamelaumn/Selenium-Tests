import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
//import org.testng.Assert;
public class SelIntroduction {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//chrome
		//System.setProperty("webdriver.chrome.driver", "")
		//WebDriver driver = new ChromeDriver();
		
		
		
		WebDriver driver = new FirefoxDriver();
		
		
		
		driver.get("https://qas.simtro.com.br/index.php");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		System.out.println(driver.getTitle());
		System.out.println(driver.getCurrentUrl());
		//firefox 
		//driver.close();
		//driver.quit();
		driver.findElement(By.id("cpf")).sendKeys("teste");
		//Assert.assertFalse(driver.findElement(By.cssSelector("input[id*='discount']")).isSelected());
		System.out.println(driver.switchTo().alert().getText());
		driver.switchTo().alert().accept();
	}

}
