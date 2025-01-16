package ViewCare;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

public class EnterControllerTest extends BaseEquipmentTest {

    private static final By TABLE_EQUIPMENT = By.cssSelector("#dados_equipamentos_tabela");
    private static final By CARD_STATS = By.cssSelector(".card.card-stats");

    @Test(priority = 1, dependsOnMethods = {"ViewCare.EnterSalesPointTest.testFetchListAndClickTheTestSalesPoint"})
    public void testLastInfoFirstMonitorableIsValidController() {
        System.out.println("\n---------------CONTROLLER CARD---------------\n");
        WebElement tableEquipment = waitForElement(TABLE_EQUIPMENT);
        List<WebElement> rows = tableEquipment.findElements(CARD_STATS);
        Assert.assertFalse(rows.isEmpty(), "Client equipment not found!");
        validateLastInfo(rows.get(0));
    }

    @Test(priority = 2, dependsOnMethods = {"testLastInfoFirstMonitorableIsValidController"})
    public void testAccessEquipmentController() {
        WebElement tableEquipment = waitForElement(TABLE_EQUIPMENT);
        List<WebElement> rows = tableEquipment.findElements(CARD_STATS);
        Assert.assertFalse(rows.isEmpty(), "Client equipment not found!");
        accessEquipment(rows.get(0), "carregarControlador('3006099150085066');");
    }
}
