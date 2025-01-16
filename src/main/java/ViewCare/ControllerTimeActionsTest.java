package ViewCare;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import base.BaseTest;
import base.CustomTestListener;

@Listeners(CustomTestListener.class)
public class ControllerTimeActionsTest extends BaseTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    // Locators
    private static final By TIME_TABLE = By.cssSelector("#tableHorarios #listagem_horarios_controlador #table_horarios_controlador");
    private static final By TIME_ROWS = By.cssSelector("#tableHorarios #listagem_horarios_controlador #table_horarios_controlador tr");
    private static final By CREATE_TIME_BUTTON = By.cssSelector("#botoes_horarios_controlador .simtro-text-button:nth-of-type(1)");
    private static final By MODAL_CONTENT = By.cssSelector("#myModalAdicionarHorarioControlador .modal-content");
    private static final By MODAL_CLOSE_BUTTON = By.cssSelector("#myModalAdicionarHorarioControlador .close");
    private static final By MODAL_FOOTER_BUTTON = By.cssSelector("#myModalAdicionarHorarioControlador .modal-footer .simtro-text-button");
    private static final By INFO_MESSAGE = By.cssSelector("#myModal #info_mensagem");
    private static final By MODAL_CONFIRM_BUTTON = By.cssSelector("#myModal .simtro-text-button-alternative");
    private static final By LOADING_SPINNER = By.className("loading");
    private static final String NEW_TIME_CREATED_NAME = "Teste Automatizado";
    private static final By CONTROLLER_TIMES_CARD = By.cssSelector("#detalhamento_controlador .cardSimtroProject:nth-of-type(3) .simtTitle");
    private static final By ADD_TIME_BUTTON = By.id("botao_horario_modal");
    private static final By MODAL_NAME_INPUT = By.id("controlador_horario_nome");
    private static final By MODAL_START_TIME_INPUT = By.id("controlador_horario_inicio");
    private static final By MODAL_END_TIME_INPUT = By.id("controlador_horario_fim");
    private static final By EVERY_DAY_AVAILABLE = By.cssSelector(".modal-body .row:nth-of-type(5) div:nth-of-type(4) label");

    @Override
    protected void waitForElementToDisappear(By locator) {
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    @Test(priority = 1, dependsOnMethods = {"ViewCare.ValidateControllerInformationTest.testValidateNumberOfAccess"})
    public void testCreateNewTime() {
        System.out.println("\n---------------CONTROLLER TIME ACTIONS---------------\n");

        navigateToControllerTimes();

        WebElement timeTable = waitForElement(TIME_TABLE);
        scrollIntoView(timeTable);

        List<WebElement> initialRows = waitForElements(TIME_ROWS);
        Assert.assertFalse(initialRows.isEmpty(), "Time list should not be empty.");

        WebElement createTimeButton = driver.findElement(CREATE_TIME_BUTTON);

        handleModal(createTimeButton, MODAL_CLOSE_BUTTON, false); // Close using X button
        handleModal(createTimeButton, MODAL_FOOTER_BUTTON, true); // Close using footer button

        createNewTime(createTimeButton, NEW_TIME_CREATED_NAME, "00:00", "23:59");

        List<WebElement> newRows = waitForElements(TIME_ROWS);
        Assert.assertEquals(newRows.size(), initialRows.size() + 1, "New time entry count mismatch.");

        validateNewTimeEntry(newRows, NEW_TIME_CREATED_NAME);
        System.out.println("TEST testCreateNewTime: passed");
    }

    private void navigateToControllerTimes() {
        WebElement controllerTimes = driver.findElement(CONTROLLER_TIMES_CARD);
        scrollIntoView(controllerTimes);
        controllerTimes.click();
    }

    private void handleModal(WebElement createTimeButton, By closeButtonLocator, boolean useFooter) {
        createTimeButton.click();
        waitForElement(MODAL_CONTENT);
        WebElement closeButton = driver.findElement(closeButtonLocator);
        closeButton.click();
        waitForElementToDisappear(MODAL_CONTENT);
    }

    private void createNewTime(WebElement createTimeButton, String name, String startTime, String endTime) {
        WebElement addTimeButton = driver.findElement(ADD_TIME_BUTTON);
        createTimeButton.click();

        WebElement modalContent = waitForElement(MODAL_CONTENT);

        fillModalInput(modalContent, addTimeButton, MODAL_NAME_INPUT, name, "Por favor, digite um nome para o horário.");
        fillModalInput(modalContent, addTimeButton, MODAL_START_TIME_INPUT, startTime, "Por favor, digite o horário de início.");
        fillModalInput(modalContent, addTimeButton, MODAL_END_TIME_INPUT, endTime, "Por favor, digite o horário de final.");

        modalContent.findElement(EVERY_DAY_AVAILABLE).click();

        addTimeButton.click();
        waitForElement(LOADING_SPINNER);
        waitForElementToDisappear(LOADING_SPINNER);

        Assert.assertEquals(waitForElement(INFO_MESSAGE).getText(), "Horário Cadastrado com sucesso.", "Failed to save new time.");

        driver.findElement(MODAL_CONFIRM_BUTTON).click();
    }

    private void fillModalInput(WebElement modalContent, WebElement addTimeButton, By inputId, String value, String validationMessage) {
        addTimeButton.click();
        Assert.assertEquals(waitForElement(INFO_MESSAGE).getText(), validationMessage, "Validation message mismatch.");
        driver.findElement(MODAL_CONFIRM_BUTTON).click();

        modalContent.findElement(inputId).sendKeys(value);
    }

    private void validateNewTimeEntry(List<WebElement> newRows, String expectedName) {
        boolean found = newRows.stream().anyMatch(row -> {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 13 && cells.get(0).getText().equals(expectedName)) {
                boolean everyDayIsAvailable = cells.subList(3, 13).stream()
                    .allMatch(cell -> cell.getText().equalsIgnoreCase("sim"));
                Assert.assertTrue(everyDayIsAvailable, "Not all days are marked as available.");
                return true;
            }
            return false;
        });

        Assert.assertTrue(found, "The new time entry '" + expectedName + "' was not found in the time list.");
    }
}
