package ViewCare;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import base.BaseTest;
import base.CustomTestListener;

@Listeners(CustomTestListener.class)
public class ValidateControllerInformationTest extends BaseTest {
    protected Duration TIMEOUT = Duration.ofSeconds(120);
    private int numberOfBiometry = 0;

    // Locator Constants
    private static final String NUMBER_OF_USERS_SELECTOR = "#detalhamento_controlador #usuariosCadastrados";
    private static final String USERS_CARD_SELECTOR = "#detalhamento_controlador .cardSimtroProject:nth-of-type(1) .simtTitle";
    private static final String USERS_TABLE_SELECTOR = "#tableUsuarios #listagem_usuarios_controlador #table_usuarios_controlador";
    private static final String LOADING_CLASS = "loading";

    private static final String NUMBER_OF_BIOMETRY_SELECTOR = "#detalhamento_controlador #digitaisCadastradas";
    private static final String BIOMETRY_CARD_SELECTOR = "#detalhamento_controlador .cardSimtroProject:nth-of-type(2) .simtTitle";
    private static final String BIOMETRY_TABLE_SELECTOR = "#tableDigitais #listagem_digitais_controlador #table_biometrias_controlador";

    private static final String NUMBER_OF_TIME_SELECTOR = "#detalhamento_controlador #horariosCadastrados";
    private static final String TIME_CARD_SELECTOR = "#detalhamento_controlador .cardSimtroProject:nth-of-type(3) .simtTitle";
    private static final String TIME_TABLE_SELECTOR = "#tableHorarios #listagem_horarios_controlador #table_horarios_controlador";

    private static final String NUMBER_OF_ACCESS_SELECTOR = "#detalhamento_controlador #acessosCadastrados";
    private static final String ACCESS_CARD_SELECTOR = "#detalhamento_controlador .cardSimtroProject:nth-of-type(4) .simtTitle";
    private static final String ACCESS_TABLE_SELECTOR = "#listagem_acessos_controlador #table_acessos_controlador";

    private static final String EXPORT_BUTTON_SELECTOR = "#acessos_equipamento div:nth-of-type(6) button:nth-of-type(1)";
    private static final String EXPORT_CLOSE_BUTTON_SELECTOR = "#myModal .simtro-text-button-alternative";

    private static final String DATE_START_SELECTOR = "#data_inicial_acessos_controlador";
    private static final String DATE_END_SELECTOR = "#data_final_acessos_controlador";
    private static final String FILTER_BUTTON_SELECTOR = "#acessos_equipamento div:nth-of-type(6) button:nth-of-type(2)";
    private static final String FILTER_USER_SELECTOR = "#filtra_usuario_controlador";
    private static final String FILTER_TIME_SELECTOR = "#filtra_horario_controlador";

    @Override
    protected void waitForElementToDisappear(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    @Test(priority = 2, dependsOnMethods = {"ViewCare.ValidateControllerRemoteAccessTest.testTryRemoteAccess"})
   // @Test(priority = 2, dependsOnMethods = {"ViewCare.EnterControllerTest.testAccessEquipmentController"})
    public void testValidateNumberOfUsers() {
        System.out.println("\n---------------CONTROLLER INFO---------------\n");
        validateNumberOfElements(
            NUMBER_OF_USERS_SELECTOR,
            USERS_CARD_SELECTOR,
            USERS_TABLE_SELECTOR,
            LOADING_CLASS,
            "Users",
            false
        );
    }

    @Test(priority = 2, dependsOnMethods = {"testValidateNumberOfUsers"})
    public void testValidateNumberOfBiometry() {
        validateNumberOfElements(
            NUMBER_OF_BIOMETRY_SELECTOR,
            BIOMETRY_CARD_SELECTOR,
            BIOMETRY_TABLE_SELECTOR,
            LOADING_CLASS,
            "Biometry",
            true
        );
    }

    @Test(priority = 3, dependsOnMethods = {"ViewCare.EnterControllerTest.testAccessEquipmentController"})
    public void testValidateNumberOfTime() {
        validateNumberOfElements(
            NUMBER_OF_TIME_SELECTOR,
            TIME_CARD_SELECTOR,
            TIME_TABLE_SELECTOR,
            LOADING_CLASS,
            "Time",
            false
        );
    }

    @Test(priority = 4, dependsOnMethods = {"ViewCare.EnterControllerTest.testAccessEquipmentController"})
    public void testValidateNumberOfAccess() {
        validateNumberOfElements(
            NUMBER_OF_ACCESS_SELECTOR,
            ACCESS_CARD_SELECTOR,
            ACCESS_TABLE_SELECTOR,
            LOADING_CLASS,
            "Access",
            false
        );

        filterByDate("2025-01-30", "2025-01-31", ACCESS_TABLE_SELECTOR);
        System.out.println("TEST validateNumberOfAcess: passed by filtering access by date");

        filterByUser("58", ACCESS_TABLE_SELECTOR);
        System.out.println("TEST validateNumberOfAcess: passed by filtering access by user");

        filterByTime("7", ACCESS_TABLE_SELECTOR);
        System.out.println("TEST validateNumberOfAcess: passed by filtering access by time");
    }

    @Test(priority = 5, dependsOnMethods = {"testValidateNumberOfAccess"})
    public void testExportAccess() {
        boolean didItStart = verifyDownloadStarted(By.cssSelector(EXPORT_BUTTON_SELECTOR));

        WebElement exportCloseButton = driver.findElement(By.cssSelector(EXPORT_CLOSE_BUTTON_SELECTOR));
        exportCloseButton.click();

        Assert.assertTrue(didItStart, "Test ExportAccess: Failed, download did not start");
        System.out.println("TEST testExportAccess: passed");
    }

    private void filterByDate(String startDate, String endDate, String rowsSelector) {
        setDateFilter(startDate, endDate);
        clickButton(FILTER_BUTTON_SELECTOR);
        tryWaitLoading();
        validateRows(rowsSelector, 1, "Could not filter the access by date", "Wrong number filtered in access date");
    }

    private void filterByUser(String userValue, String rowsSelector) {
        selectDropdownOption(FILTER_USER_SELECTOR, userValue);
        clickButton(FILTER_BUTTON_SELECTOR);
        tryWaitLoading();
        validateRows(rowsSelector, 1, "Could not filter the access by user", "Wrong number filtered in access user");
    }

    private void filterByTime(String timeValue, String rowsSelector) {
        selectDropdownOption(FILTER_TIME_SELECTOR, timeValue);
        clickButton(FILTER_BUTTON_SELECTOR);
        tryWaitLoading();
        validateRows(rowsSelector, 1, "Could not filter the access by time", "Wrong number filtered in access time");
    }

    private void setDateFilter(String startDate, String endDate) {
        driver.findElement(By.cssSelector(DATE_START_SELECTOR)).sendKeys(startDate);
        driver.findElement(By.cssSelector(DATE_END_SELECTOR)).sendKeys(endDate);
    }

    private void clickButton(String selector) {
        driver.findElement(By.cssSelector(selector)).click();
    }

    private void selectDropdownOption(String selector, String value) {
        WebElement dropdown = driver.findElement(By.cssSelector(selector));
        new Select(dropdown).selectByValue(value);
    }

    private void validateRows(String rowsSelector, int expectedSize, String emptyMessage, String sizeMessage) {
        List<WebElement> rows = waitForElements(By.cssSelector(rowsSelector + " tr"));
        Assert.assertFalse(rows.isEmpty(), emptyMessage);
        Assert.assertEquals(rows.size(), expectedSize, sizeMessage);
    }
    protected void validateNumberOfElements(String numberOfElementsSelector, String buttonSelector, String rowsSelector, String loadingClass, String elementDescription, boolean directButton) {
        String numberOfElementsText = waitForElement(By.cssSelector(numberOfElementsSelector)).getText();
        int numberOfElements = Integer.parseInt(numberOfElementsText);

        if (directButton) {
            ((JavascriptExecutor) driver).executeScript("carregarDigitais()");
        } else {
            WebElement button = driver.findElement(By.cssSelector(buttonSelector));
            scrollIntoView(button);
            button.click();
        }

        waitForElement(By.className(loadingClass));
        waitForElementToDisappear(By.className(loadingClass));

        WebElement element = driver.findElement(By.cssSelector(rowsSelector));
        scrollIntoView(element);

        List<WebElement> rows = waitForElements(By.cssSelector(rowsSelector + " tr"));
        Assert.assertFalse(rows.isEmpty(), elementDescription + " List Not Found");

        if (elementDescription.equals("Users")) {
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() > 9 && "Sim".equals(cells.get(9).getText())) {
                    numberOfBiometry++;
                }
            }
        }
        if (elementDescription.equals("Biometry")) {
            Assert.assertEquals(numberOfBiometry, numberOfElements, "Number of Users with Biometry and the actual list of " + elementDescription + " are different");
        } else {
            Assert.assertEquals(rows.size(), numberOfElements, "Number of " + elementDescription + " shown and the actual list of " + elementDescription + " are different");
            System.out.println("TEST " + elementDescription + ": passed numberOf " + elementDescription + " " + numberOfElements);
        }
    }
}
