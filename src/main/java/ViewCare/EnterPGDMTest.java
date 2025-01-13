package ViewCare;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;



public class EnterPGDMTest extends BaseEquipmentTest {
/*
    @Test(priority = 1, dependsOnMethods = {"ViewCare.EnterSalesPointTest.testFetchListAndClickTheTestSalesPoint"})
    public void testLastInfoFirstMonitorableIsValidPGDM() {
        WebElement tableEquipement = waitForElement(By.cssSelector("#dados_equipamentos_tabela"));
        List<WebElement> rows = tableEquipement.findElements(By.cssSelector(".card.card-stats"));
        Assert.assertFalse(rows.isEmpty(), "Client Pgdm not found!");
        validateLastInfo(rows.get(1));
    }

    @Test(priority = 2, dependsOnMethods = {"testLastInfoFirstMonitorableIsValidPGDM"})
    public void testAcessEquipmentPGDM() {
        WebElement tableEquipment = waitForElement(By.cssSelector("#dados_equipamentos_tabela"));
        List<WebElement> rows = tableEquipment.findElements(By.cssSelector(".card.card-stats"));
        Assert.assertFalse(rows.isEmpty(), "Client equipment not found!");
        accessEquipment(rows.get(1), "carregarPgdm('000000', 78326);");
    }

    @Test(priority = 3, dependsOnMethods = {"testAcessEquipmentPGDM"})
    public void testGoBackFromEquipmentPGDM() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("changeMain('monitoramento_agencia');");
    }

    @Test(priority = 4, dependsOnMethods = {"testGoBackFromEquipmentPGDM"})
    public void testGoBackFromSalesPoint() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement voltarElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[text()='Voltar']/..")));
        voltarElement.click();
    }*/
}
