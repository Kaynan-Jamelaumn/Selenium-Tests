package ViewCare;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;


public class EnterControllerTest extends BaseEquipmentTest {

    @Test(priority = 1, dependsOnMethods = {"ViewCare.EnterSalesPoint.testFetchListAndClickTheTestSalesPoint"})
    public void testLastInfoFirstMonitorableIsValidController() {
        WebElement tableEquipement = waitForElement(By.cssSelector("#dados_equipamentos_tabela"));
        List<WebElement> rows = tableEquipement.findElements(By.cssSelector(".card.card-stats"));
        Assert.assertFalse(rows.isEmpty(), "Client equipment not found!");
        validateLastInfo(rows.get(0));
    }

    @Test(priority = 2, dependsOnMethods = {"testLastInfoFirstMonitorableIsValidController"})
    public void testAcessEquipmentController() {
        WebElement tableEquipment = waitForElement(By.cssSelector("#dados_equipamentos_tabela"));
        List<WebElement> rows = tableEquipment.findElements(By.cssSelector(".card.card-stats"));
        Assert.assertFalse(rows.isEmpty(), "Client equipment not found!");
        accessEquipment(rows.get(0), "carregarControlador('3006099150085066');");
    }

    @Test(priority = 3)
    public void testGoBackFromController() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("changeMain('monitoramento_agencia');");
    }
}
