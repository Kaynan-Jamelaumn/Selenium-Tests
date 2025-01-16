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
public class ValidateControllerRemoteAccessTest extends BaseTest {
    protected Duration TIMEOUT = Duration.ofSeconds(60);
    private int numberOfBiometry = 0;

    // Locator constants
    private static final By CONTROLLER_LOCATION = By.id("localizacaoControlador");
    private static final By CONTROLLER_GRID = By.id("controladorGrid");
    private static final By REMOTE_ACCESS_BUTTON = By.id("abertura_remota");
    private static final By REMOTE_ACCESS_REASON = By.id("acionamento_remoto_motivo");
    private static final By REMOTE_ACCESS_CONFIRM_BUTTON = By.cssSelector("#myModalAberturaRemota .modal-footer .simtro-text-button-alternative");
    private static final By LOADING_SPINNER = By.className("loading");
    private static final By INFO_MESSAGE = By.id("info_mensagem");
    private static final By MODAL_CLOSE_BUTTON = By.cssSelector("#myModal .modal-dialog .modal-content .modal-footer .simtro-text-button-alternative");
    private static final By RANDOM_PASSWORD_TAB = By.cssSelector("#detalhamento_controlador .simtroDivHeader ul li:nth-of-type(3) a");
    private static final By TOGGLE_PASSWORD_BUTTON = By.id("toggle_senha");
    private static final By PASSWORD_LABEL = By.id("codigoSenhaRandomica");
    private static final By GENERATE_PASSWORD_BUTTON = By.id("gerar_senha");

    @Override
    protected void waitForElementToDisappear(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    @Test(priority = 1, dependsOnMethods = {"ViewCare.EnterControllerTest.testAccessEquipmentController"})
    public void testTryRemoteAccess() {
        System.out.println("\n---------------CONTROLLER REMOTE ACCESS---------------\n");

        WebElement controllerLocationElement = driver.findElement(CONTROLLER_LOCATION);
        scrollIntoView(controllerLocationElement);

        String controllerLocation = controllerLocationElement.getText();
        WebElement controllerAccessButtonCard = driver.findElement(CONTROLLER_GRID);
        WebElement controllerAccessButton = controllerAccessButtonCard.findElement(By.tagName("div"))
                .findElement(By.tagName("div"))
                .findElement(By.tagName("a"));

        if (controllerLocation.equals("EXTERNO")) {
            driver.findElement(REMOTE_ACCESS_BUTTON).click();
            WebElement motiveInput = waitForElement(REMOTE_ACCESS_REASON);
            motiveInput.sendKeys("teste automatizado");
            WebElement button = driver.findElement(REMOTE_ACCESS_CONFIRM_BUTTON);
            button.click();
            waitForElement(LOADING_SPINNER);
            waitForElementToDisappear(LOADING_SPINNER);
            WebElement info = waitForElement(INFO_MESSAGE);
            Assert.assertEquals(info.getText().trim(), "Controlador aberto com sucesso.",
                    "Error: Controller did not properly remotely open");
            WebElement closeButton = waitForElement(MODAL_CLOSE_BUTTON);
            closeButton.click();
        } else {
            Assert.assertNull(controllerAccessButton,
                    "Should not be able to remotely access this type of controller");
        }

        System.out.println("TEST testTryRemoteAccess: passed");
    }

    @Test(priority = 2, dependsOnMethods = {"testTryRemoteAccess"})
    public void testTogglePassword() {
        WebElement randomPasswordTab = waitForElement(RANDOM_PASSWORD_TAB);
        randomPasswordTab.click();

        WebElement toggleButton = waitForElement(TOGGLE_PASSWORD_BUTTON);
        String initialText = getPasswordLabel();

        clickAndWait(toggleButton, 1000);
        String newText = getPasswordLabel();

        verifyPasswordChange(initialText, newText);

        System.out.println("TEST testTogglePassword: passed");
    }

    @Test(priority = 3, dependsOnMethods = {"testTogglePassword"})
    public void testGenerateRandomPassword() {
        WebElement toggleButton = waitForElement(TOGGLE_PASSWORD_BUTTON);
        clickAndWait(toggleButton, 1000);

        String currentPassword = getPasswordLabel();

        WebElement generateRandomPasswordButton = waitForElement(GENERATE_PASSWORD_BUTTON);
        generateRandomPasswordButton.click();

        WebElement generatedText = waitForElement(INFO_MESSAGE);
        Assert.assertEquals(generatedText.getText().trim(), "Senha randômica gerada com sucesso.",
                "Unexpected error while generating password.");

        WebElement closeButton = waitForElement(MODAL_CLOSE_BUTTON);
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
        return driver.findElement(PASSWORD_LABEL).getText();
    }

    private void verifyPasswordChange(String initialText, String newText) {
        Assert.assertNotEquals(initialText, newText, "Random Password label did not change");
        Assert.assertTrue(newText.matches("\\d+"), "The label is not a number");
    }
}
