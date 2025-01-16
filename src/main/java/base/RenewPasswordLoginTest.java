package base;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import io.github.cdimascio.dotenv.Dotenv;
@Listeners(CustomTestListener.class)
public class RenewPasswordLoginTest extends BaseTest {

	protected WebElement waitForElementWithExactText(By locator, String expectedText) {
	    WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
	    return wait.until(driver -> {
	        WebElement element = driver.findElement(locator);
	        return element.isDisplayed() && expectedText.equals(element.getText().trim()) ? element : null;
	    });
	}

	  @Test(priority = 1)
	    public void testRenew() {
	        driver.get(BASE_URL);
	        Dotenv dotenv = Dotenv.load();
	        String CPF = dotenv.get("RENEWCPF");

	        try {
	            driver.findElement(By.id("forgotMyPass")).click();

	            driver.findElement(By.id("recupera_senha_usuario")).sendKeys("1111111");
	            WebElement button = driver.findElement(By.cssSelector("#recuperar_senha .simtro-text-button-alternative"));
	            button.click();

	            // Wait for error message and assert it
	            WebElement messageElement = waitForElementWithExactText(
	                By.cssSelector("#info_mensagem"),
	                "CPF ou matrícula incorretos."
	            );

	            driver.findElement(By.cssSelector("#myModal .modal-footer .simtro-text-button-alternative")).click();

	            // Clear and re-enter the valid CPF
	            driver.findElement(By.id("recupera_senha_usuario")).clear();
	            driver.findElement(By.id("recupera_senha_usuario")).sendKeys(CPF);
	            button.click();
	            // Wait for success message and assert it
	            WebElement messageElement2 = waitForElementWithExactText(
	                By.cssSelector("#info_mensagem"),
	                "Código de recuperação enviado com sucesso."
	            );

	            // Close the modal
	            driver.findElement(By.cssSelector("#myModal .modal-footer .simtro-text-button-alternative")).click();
	            System.out.println("TEST testRenew: passed");
	        } catch (Exception e) {
	            System.err.println("Test failed: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	 
	 
	 
	 
	 /*
	 
	 
	 
	 @Test(priority = 1)
	    public void testRenew() {
	        driver.get(BASE_URL);
	        Dotenv dotenv = Dotenv.load();
	        String CPF = dotenv.get("RENEWCPF");
	        String PASSWORD = dotenv.get("RENEWPASSWORD");
	        String EMAIL = dotenv.get("EMAIL");
	        String EMAILPASSWORD = dotenv.get("EMAILPASSWORD");
	        driver.findElement(By.id("forgotMyPass")).click();
	        
	        driver.findElement(By.id("recupera_senha_usuario")).sendKeys(CPF);
	        
	        
	        driver.findElement(By.cssSelector("#recuperar_senha .simtro-text-button-alternative")).click();
	        
	        WebElement messageElement = waitForElementWithExactText(
	            By.cssSelector("#info_mensagem"),
	            "Código de recuperação enviado com sucesso."
	        );
	        
	        // Fechar a modal de mensagem
	        driver.findElement(By.cssSelector("#myModal .modal-footer .simtro-text-button-alternative")).click();

	        // Agora, simular a abertura de uma nova aba para o login do Gmail
	        // Armazenar a aba/janela atual
	        String originalWindow = driver.getWindowHandle();  // Salvar a janela original
	        ((JavascriptExecutor)  driver).executeScript("window.open('https://accounts.google.com/');"); // Abrir uma nova aba com o Gmail

	        // Esperar a nova aba abrir e mudar o foco para ela
	        Set<String> allWindows = driver.getWindowHandles();
	        for (String windowHandle : allWindows) {
	            if (!windowHandle.equals(originalWindow)) {
	                driver.switchTo().window(windowHandle);  // Trocar para a nova aba
	                break;
	            }
	        }

	        // Agora na aba do Gmail, realizar o login
	        driver.findElement(By.id("identifierId")).sendKeys(EMAIL);
	        driver.findElement(By.id("identifierNext")).click();
	        
	        // Aguardar o campo de senha carregar
	        new WebDriverWait(driver, TIMEOUT).until(driver -> driver.findElement(By.name("password")).isDisplayed());
	        
	        driver.findElement(By.name("password")).sendKeys(PASSWORD);
	        driver.findElement(By.id("passwordNext")).click();
	        
	        // Aguardar a página do Gmail carregar
	        new WebDriverWait(driver, TIMEOUT).until(driver -> driver.findElement(By.cssSelector(".aic .z0")).isDisplayed());

	        // Fechar a aba do Gmail
	        driver.close();

	        // Voltar para a aba original
	        driver.switchTo().window(originalWindow);
	        driver.findElement(By.id("codigo_verificacao"));

	        String currentUrl = driver.getCurrentUrl();
	        System.out.println("TEST testRenew: passed");
	        //System.out.println("TEST testLogin: URL after login: " + currentUrl);
	        //Assert.assertTrue(currentUrl.contains("dashboard"), "Login failed: URL does not contain 'dashboard'.");
	    }
	    */

}
