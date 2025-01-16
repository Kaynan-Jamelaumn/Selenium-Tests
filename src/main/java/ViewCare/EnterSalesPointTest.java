package ViewCare;
import base.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Listeners(base.CustomTestListener.class)
public class EnterSalesPointTest extends BaseTest {
    
    private static final By SALES_POINT_TABLE_BODY = By.cssSelector("#listagem_dashboard_view_care table tbody");
    private static final By SALES_POINT_COLUMN_INSTITUICAO = By.cssSelector("td[data-column-id='instituicao']");
    private static final By SIMTRO_TEXT_BUTTON = By.className("simtro-text-button");
    private static final By NAME_AGENCIA = By.id("nome_agencia");
    private static final By NAME_AGENCIA2 = By.id("nome_agencia2");
    private static final By RESPONSABLE_AGENCIA = By.id("dados_responsavel_agencia");
    private static final By NUMBER_OS_SETED = By.cssSelector(".simtroDivHeaderTitles div:nth-of-type(2) small:nth-of-type(2)");

    @Test(priority = 1, dependsOnMethods = {"ViewCare.SalesPointTest.testFoundClientSalesPoint"})
    public void testFetchListAndClickTheTestSalesPoint() {
        System.out.println("\n---------------SALES POINTS---------------\n");
        WebElement tableBody = waitForElement(SALES_POINT_TABLE_BODY);
        
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        Assert.assertFalse(rows.isEmpty(), "Client sales point list is not found!");
        
        // Filtra as linhas com a instituição "SIMTRO TESTE MONITORAMENTO (NÃO ALTERAR)"
        WebElement targetRow = null;
        for (WebElement row : rows) {
            String instituicao = row.findElement(SALES_POINT_COLUMN_INSTITUICAO).getText();
            if ("SIMTRO TESTE MONITORAMENTO (NÃO ALTERAR)".equals(instituicao)) {
                targetRow = row;
                break;
            }
        }
        
        // Verifica se encontrou a linha com a instituição específica
        Assert.assertNotNull(targetRow, "Sales point with Client 'SIMTRO TESTE MONITORAMENTO (NÃO ALTERAR)' not found!");
        
        // Encontra o botão na linha selecionada e clica
        WebElement button = targetRow.findElement(SIMTRO_TEXT_BUTTON);
        Assert.assertNotNull(button, "The 'simtro-text-button' was not found in the target row!");
        
        button.click();
        System.out.println("TEST testFetchListAndClickTheTestSalesPoint: Clicked the 'simtro-text-button' in the row with 'SIMTRO TESTE MONITORAMENTO (NÃO ALTERAR)'.");
    }

    @Test(priority = 2, dependsOnMethods = {"testFetchListAndClickTheTestSalesPoint"})
    public void testValidateNameSalesPoint() {
        String nameAgency = waitForElement(NAME_AGENCIA).getText();
        String nameSalesPoint = waitForElement(NAME_AGENCIA2).getText();
        String responsableAgency = waitForElement(RESPONSABLE_AGENCIA).getText();
        
        Assert.assertNotNull(nameAgency);
        Assert.assertNotNull(nameSalesPoint);
        Assert.assertNotNull(responsableAgency);
        System.out.println("TEST testValidateNameSalesPoint: passed");
    }

    @Test(priority = 2, dependsOnMethods = {"testFetchListAndClickTheTestSalesPoint"})
    public void testValidatesNumberOfOS() {
        WebElement numberOSSeted = waitForElement(NUMBER_OS_SETED);
        int number;

        try {
            number = Integer.parseInt(numberOSSeted.getText());
        } catch (NumberFormatException e) {
            number = 0; 
        }
        
        Assert.assertEquals(number, numberOS, "OS Number Does Not Match");
        System.out.println("TEST testValidatesNumberOfOS: passed");
    }
}
