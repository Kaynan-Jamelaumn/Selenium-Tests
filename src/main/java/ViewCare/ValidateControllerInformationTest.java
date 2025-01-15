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
    
    @Override
    protected void waitForElementToDisappear(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }


    @Test(priority = 2, dependsOnMethods = {"ViewCare.EnterControllerTest.testAcessEquipmentController"})
    public void testValidateNumberOfUsers() {
    	System.out.println();
    	System.out.println("---------------CONTROLLER INFO---------------");
    	System.out.println();
        validateNumberOfElements(
            "#detalhamento_controlador #usuariosCadastrados",
            "#detalhamento_controlador .cardSimtroProject:nth-of-type(1) .simtTitle",
            "#tableUsuarios #listagem_usuarios_controlador #table_usuarios_controlador",
            "loading",
            "Users",
            false
        );
    }

    @Test(priority = 2, dependsOnMethods = {"testValidateNumberOfUsers"})
    public void testValidateNumberOfBiometry(){
        validateNumberOfElements(
            "#detalhamento_controlador #digitaisCadastradas",
            "#detalhamento_controlador .cardSimtroProject:nth-of-type(2) .simtTitle",
            "#tableDigitais #listagem_digitais_controlador #table_biometrias_controlador",
            "loading",
            "Biometry",
            true
        );
    }


    @Test(priority = 3, dependsOnMethods = {"ViewCare.EnterControllerTest.testAcessEquipmentController"})
    public void testValidateNumberOfTime() {
        validateNumberOfElements(
            "#detalhamento_controlador #horariosCadastrados",
            "#detalhamento_controlador .cardSimtroProject:nth-of-type(3) .simtTitle",
            "#tableHorarios #listagem_horarios_controlador #table_horarios_controlador",
            "loading",
            "Time",
            false
        );
    }
    @Test(priority = 4, dependsOnMethods = {"ViewCare.EnterControllerTest.testAcessEquipmentController"})
    public void testValidateNumberOfAcess() {
        final String rowsSelector = "#listagem_acessos_controlador #table_acessos_controlador";
        
        validateNumberOfElements(
            "#detalhamento_controlador #acessosCadastrados",
            "#detalhamento_controlador .cardSimtroProject:nth-of-type(4) .simtTitle",
            rowsSelector,
            "loading",
            "Access",
            false
        );

        // Test filter by date
        filterByDate("2025-01-30", "2025-01-31", rowsSelector);
        System.out.println("TEST validateNumberOfAcess: passed by filtering access by date");

        // Test filter by user
        filterByUser("58", rowsSelector);
        System.out.println("TEST validateNumberOfAcess: passed by filtering access by user");

        // Test filter by time
        filterByTime("7", rowsSelector);
        System.out.println("TEST validateNumberOfAcess: passed by filtering access by time");
        

    }
    @Test(priority = 5, dependsOnMethods = {"testValidateNumberOfAcess"})
    public void testExportAccess() {
        boolean didItStart = verifyDownloadStarted( By.cssSelector("#acessos_equipamento div:nth-of-type(6) button:nth-of-type(1)"));
        
        WebElement exportCloseButton = driver.findElement(By.cssSelector("#myModal .simtro-text-button-alternative"));
        exportCloseButton.click();
        
        Assert.assertTrue(didItStart, "Test ExportAccess: Failed, download did not start");
        System.out.println("TEST testExportAccess: passed");
    }


    

    private void filterByDate(String startDate, String endDate, String rowsSelector) {
        setDateFilter(startDate, endDate);
        clickFilterButton();
        tryWaitLoading();
        validateRows(rowsSelector, 1, "Could not filter the access by date", "Wrong number filtered in access date");
    }

    private void filterByUser(String userValue, String rowsSelector) {
        selectDropdownOption("#filtra_usuario_controlador", userValue);
        clickFilterButton();
        tryWaitLoading();
        validateRows(rowsSelector, 1, "Could not filter the access by user", "Wrong number filtered in access user");
    }

    private void filterByTime(String timeValue, String rowsSelector) {
        selectDropdownOption("#filtra_horario_controlador", timeValue);
        clickFilterButton();
        tryWaitLoading();
        validateRows(rowsSelector, 1, "Could not filter the access by time", "Wrong number filtered in access time");
    }

    private void setDateFilter(String startDate, String endDate) {
        WebElement inicialDate = driver.findElement(By.cssSelector("#data_inicial_acessos_controlador"));
        WebElement finalDate = driver.findElement(By.cssSelector("#data_final_acessos_controlador"));
        inicialDate.sendKeys(startDate);
        finalDate.sendKeys(endDate);
    }

    private void clickFilterButton() {
        WebElement button = driver.findElement(By.cssSelector("#acessos_equipamento div:nth-of-type(6) button:nth-of-type(2)"));
        button.click();
    }

    private void selectDropdownOption(String selector, String value) {
        WebElement dropdown = driver.findElement(By.cssSelector(selector));
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }

    private void validateRows(String rowsSelector, int expectedSize, String emptyMessage, String sizeMessage) {
        List<WebElement> rows = waitForElements(By.cssSelector(rowsSelector + " tr"));
        Assert.assertFalse(rows.isEmpty(), emptyMessage);
        Assert.assertEquals(rows.size(), expectedSize, sizeMessage);
    }

    public void accessRowsFailed(String rowsSelector) {
        setDateFilter("2024-01-30", "2024-01-31");
        clickFilterButton();
        tryWaitLoading();
        List<WebElement> rows = waitForElements(By.cssSelector(rowsSelector + " tr"));
        if (rows.size() == 1) {
            WebElement td = rows.get(0).findElement(By.cssSelector("td"));
            String tdText = td.getText();
            Assert.assertTrue(tdText.contains("Não há nenhum acesso registrado"), "Failed UnSuccesfully");
        }
    }
    
    protected void validateNumberOfElements(String numberOfElementsSelector, String buttonSelector, String rowsSelector, String loadingClass, String elementDescription, boolean directButton) {
        String numberOfElementsText = waitForElement(By.cssSelector(numberOfElementsSelector)).getText();
        int numberOfElements = Integer.parseInt(numberOfElementsText);
        
        if (directButton == true) {
        	((JavascriptExecutor)  driver).executeScript("carregarDigitais()");
        }
        else {
        	
        WebElement button = waitForElement(By.cssSelector(buttonSelector));
        button.click();
        }

        waitForElement(By.className(loadingClass));
        waitForElementToDisappear(By.className(loadingClass));

       // System.out.println(buttonSelector + " Loading completed.");
        WebElement element = driver.findElement(By.cssSelector(rowsSelector));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);

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
    	}
        else {
        Assert.assertEquals(rows.size(), numberOfElements, "Number of " + elementDescription + " shown and the actual list of " + elementDescription + " are different");
        System.out.println("TEST " + elementDescription + ": passed numberOf " + elementDescription + " " + numberOfElements);
        }
    }
}

/*

@Test(priority = 10)
public void testGoBackFromController() {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("changeMain('monitoramento_agencia');");
}*/