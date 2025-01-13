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
    


    @Test(priority = 1, dependsOnMethods = {"ViewCare.SalesPointTest.testFoundClientSalesPoint"})
    public void testFetchListAndClickTheTestSalesPoint() {
    	System.out.println();
      	 System.out.println("---------------SALES POINTS---------------");
      	System.out.println();
        WebElement tableBody = waitForElement(By.cssSelector("#listagem_dashboard_view_care table tbody"));
        
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        Assert.assertFalse(rows.isEmpty(), "Client sales point list is not found!");
        
        // Filtra as linhas com a instituição "PV MONITORAMENTO (NÃO ALTERAR)"
        WebElement targetRow = null;
        for (WebElement row : rows) {
            String instituicao = row.findElement(By.cssSelector("td[data-column-id='instituicao']")).getText();
            if ("PV MONITORAMENTO (NÃO ALTERAR)".equals(instituicao)) {
                targetRow = row;
                break;
            }
        }
        
        // Verifica se encontrou a linha com a instituição específica
        Assert.assertNotNull(targetRow, "Sales point with Client 'PV MONITORAMENTO (NÃO ALTERAR)' not found!");
        
        // Encontra o botão na linha selecionada e clica
        WebElement button = targetRow.findElement(By.className("simtro-text-button"));
        Assert.assertNotNull(button, "The 'simtro-text-button' was not found in the target row!");
        
        button.click();
        System.out.println("TEST testFetchListAndClickTheTestSalesPoint: Clicked the 'simtro-text-button' in the row with 'PV MONITORAMENTO (NÃO ALTERAR)'.");
    }

    @Test(priority = 2, dependsOnMethods = {"testFetchListAndClickTheTestSalesPoint"})
    public void testValidateNameSalesPoint() {
    	String nameAgency = waitForElement(By.id("nome_agencia")).getText();
    	String nameSalesPoint = waitForElement(By.id("nome_agencia2")).getText();
       	String responsableAgency = waitForElement(By.id("dados_responsavel_agencia")).getText();
    	Assert.assertNotNull(nameAgency);
    	Assert.assertNotNull(nameSalesPoint);
      	Assert.assertNotNull(responsableAgency);
        System.out.println("TEST testValidateNameSalesPoint: passed");
    }

    
    @Test(priority = 2, dependsOnMethods = {"testFetchListAndClickTheTestSalesPoint"})
    public void testValidatesNumberOfOS() {
    	WebElement numberOSSeted = waitForElement(By.cssSelector(".simtroDivHeaderTitles div:nth-of-type(2) small:nth-of-type(2)"));
    	int number;

    	try {
    	    number = Integer.parseInt(numberOSSeted.getText());
    	} catch (NumberFormatException e) {
    	    number = 0; 
    	}
    	Assert.assertEquals(number, numberOS, "OS Number Does Not Match" );   
        System.out.println("TEST testValidatesNumberOfOS: passed");
    }



}
