package ViewCare;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import base.BaseTest;
import base.CustomTestListener;
@Listeners(CustomTestListener.class)
public class ValidateControllerRemoteAccessTest extends BaseTest  {
	 protected Duration TIMEOUT = Duration.ofSeconds(60);
	    private int numberOfBiometry = 0;
	    
	    @Override
	    protected void waitForElementToDisappear(By locator) {
	        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
	        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	    }

	
	@Test(priority = 1, dependsOnMethods = {"ViewCare.EnterControllerTest.testAcessEquipmentController"})
    public void testTryRemoteAccess() {
    	System.out.println();
    	System.out.println("---------------CONTROLLER REMOTE ACCESS---------------");
    	System.out.println();
        WebElement controllerLocationElement =  driver.findElement(By.id("localizacaoControlador"));
        
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", controllerLocationElement);
        
        String controllerLocation =  controllerLocationElement.getText();
        WebElement controllerAccessButtonCard = driver.findElement(By.id("controladorGrid"));
        WebElement  controllerAccessButton = controllerAccessButtonCard.findElement(By.tagName("div")).findElement(By.tagName("div")).findElement(By.tagName("a"));
        if (controllerLocation.equals("EXTERNO"))
        {
        	driver.findElement(By.id("abertura_remota")).click();
        	WebElement motiveInput = waitForElement(By.id("acionamento_remoto_motivo"));
        	motiveInput.sendKeys("teste automatizado");
        	WebElement button = driver.findElement(By.cssSelector("#myModalAberturaRemota .modal-footer .simtro-text-button-alternative"));
        	button.click();
            waitForElement(By.className("loading"));
            waitForElementToDisappear(By.className("loading"));
            WebElement info =  waitForElement(By.id("info_mensagem"));
            Assert.assertEquals(info.getText().trim(), "Controlador aberto com sucesso.", "Error: Controller did not properly remotely open");
            WebElement closeButton = waitForElement(By.cssSelector("#myModal .modal-dialog .modal-content .modal-footer .simtro-text-button-alternative"));
            closeButton.click();
        }	
        else {
        		Assert.assertNull(controllerAccessButton, "Should not be able to remotely access this type of controller");
        	}
        System.out.println("TEST testTryRemoteAccess: passed");
        
    }

    @Test(priority = 2, dependsOnMethods = {"testTryRemoteAccess"})
    public void testTogglePassword() {
        WebElement randomPasswordTab = waitForElement(By.cssSelector("#detalhamento_controlador .simtroDivHeader ul li:nth-of-type(3) a"));
        randomPasswordTab.click();

        WebElement toggleButton = waitForElement(By.id("toggle_senha"));
        String initialText = getPasswordLabel();

        clickAndWait(toggleButton, 1000);
        String newText = getPasswordLabel();

        verifyPasswordChange(initialText, newText);

        System.out.println("TEST testTogglePassword: passed");
    }

    @Test(priority = 3, dependsOnMethods = {"testTogglePassword"})
    public void testGenerateRandomPassword() {
        WebElement toggleButton = waitForElement(By.id("toggle_senha"));
        clickAndWait(toggleButton, 1000);

        String currentPassword = getPasswordLabel();

        WebElement generateRandomPasswordButton = waitForElement(By.id("gerar_senha"));
        generateRandomPasswordButton.click();

        WebElement generatedText = waitForElement(By.id("info_mensagem"));
        Assert.assertEquals(generatedText.getText().trim(), "Senha randômica gerada com sucesso.", "Unexpected error while generating password.");

        WebElement closeButton = waitForElement(By.cssSelector("#myModal .modal-footer .simtro-text-button-alternative"));
        closeButton.click();

        clickAndWait(toggleButton, 1000);
        String newPassword = getPasswordLabel();

        verifyPasswordChange(currentPassword, newPassword);

        System.out.println("TEST testGenerateRandomPassword: passed");
    }
    
    
    private void clickAndWait(WebElement element, int waitMillis) {
        element.click();
        try {
            Thread.sleep(waitMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getPasswordLabel() {
        return driver.findElement(By.id("codigoSenhaRandomica")).getText();
    }

    private void verifyPasswordChange(String initialText, String newText) {
        Assert.assertNotEquals(initialText, newText, "Random Password label did not change");
        Assert.assertTrue(newText.matches("\\d+"), "The label is not a number");
    }

}
