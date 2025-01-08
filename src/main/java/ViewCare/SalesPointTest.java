package ViewCare;
import base.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Listeners(base.CustomTestListener.class)
public class SalesPointTest extends BaseTest {
    private int numberOS;
    
    private void testFilterSalesPoint(String columnId, String errorMessage) {
        WebElement tableBody = waitForElement(By.cssSelector("#listagem_dashboard_view_care table tbody"));
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        Assert.assertFalse(rows.isEmpty(), errorMessage);

        List<String> originalList = rows.stream()
            .map(row -> row.findElement(By.cssSelector("td[data-column-id='" + columnId + "']")).getText())
            .collect(Collectors.toList());

        List<String> sortedList = new ArrayList<>(originalList);
        sortedList.sort(String::compareTo);

        WebElement clientColumn = driver.findElement(By.cssSelector("thead tr th[data-column-id='" + columnId + "']"));
        WebElement sortButton = clientColumn.findElement(By.cssSelector("button.gridjs-sort"));

        sortButton.click();

        waitForElement(By.cssSelector("#listagem_dashboard_view_care table tbody tr"));

        List<WebElement> sortedRowsUI = tableBody.findElements(By.tagName("tr"));
        List<String> uiSortedList = sortedRowsUI.stream()
            .map(row -> row.findElement(By.cssSelector("td[data-column-id='" + columnId + "']")).getText())
            .collect(Collectors.toList());

        Assert.assertEquals(uiSortedList, sortedList, "UI sorting does not match programmatic sorting!");
    }
    
    
    @Test(priority = 2, dependsOnMethods = {"ViewCare.ViewCareTest.testViewCare"})
    public void testOnlineEquipements() {
        try {
            // Wait for the Main Element
            WebElement mainDiv = waitForElement(By.id("monitoramento"));
            Assert.assertNotNull(mainDiv, "Element 'monitoramento' not found");

            // Select The Cards
            List<WebElement> cards = mainDiv.findElements(By.cssSelector(".container-fluid .cardSimtroProject"));
            Assert.assertTrue(cards.size() >= 2, "Less than 2 cards found");

            // Get The Second Card
            WebElement secondCard = cards.get(1);
            WebElement secondInnerDiv = secondCard.findElement(By.xpath(".//div[2]"));

            WebElement totalMonitorados = secondInnerDiv.findElement(By.id("totalMonitorados"));
            Assert.assertTrue(totalMonitorados.isDisplayed(), "Element 'totalMonitorados' is not visible");

            WebElement equipAtivoQtd = secondInnerDiv.findElement(By.id("equipAtivoQtd"));
            Assert.assertTrue(equipAtivoQtd.isDisplayed(), "Element 'equipAtivoQtd' is not visible");

            WebElement equipInativoQtd = secondInnerDiv.findElement(By.id("equipInativoQtd"));
            Assert.assertTrue(equipInativoQtd.isDisplayed(), "Element 'equipInativoQtd' is not visible");

            int numberOfEquipDisabled = Integer.parseInt(equipInativoQtd.getText().trim());
            int numberOfEquipEnabled = Integer.parseInt(equipAtivoQtd.getText().trim());
            int numberOfEqipMonitored = Integer.parseInt(totalMonitorados.getText().trim());

            // Verify Sum of Enabled and Disabled Equipments
            int totalCalculated = numberOfEquipDisabled + numberOfEquipEnabled;

            try {
                Assert.assertEquals(totalCalculated, numberOfEqipMonitored, "Total of Active and Inactive Equipments does not match the Monitored count");
            } catch (AssertionError e) {
                // If the first assertion fails, check if the number of online equipment equals the number of active equipment
                WebElement equipOnlineQtd = secondInnerDiv.findElement(By.id("equipOnlineQtd"));
                Assert.assertTrue(equipOnlineQtd.isDisplayed(), "Element 'equipOnlineQtd' is not visible");
                
                Assert.assertEquals(equipOnlineQtd.getText(), equipAtivoQtd.getText(), "Active and Online equipment don't match");
                throw e;  // Rethrow the exception so the test still fails after the additional check
            }

            WebElement equipOfflineQtd = secondInnerDiv.findElement(By.id("equipOfflineQtd"));
            Assert.assertTrue(equipOfflineQtd.isDisplayed(), "Element 'equipOfflineQtd' is not visible");

        } catch (Exception e) {
            Assert.fail("Test failed due to: " + e.getMessage());
        }
    }

    
    
    
    
    
    
    
    
    
    @Test(priority = 3, dependsOnMethods = {"ViewCare.ViewCareTest.testViewCare"})
    public void testFoundClientSalesPoint() {
        WebElement tableBody = waitForElement(By.cssSelector("#listagem_dashboard_view_care table tbody"));
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        Assert.assertFalse(rows.isEmpty(), "Client sales point list is not found!");

        System.out.println("TEST testFoundClientSalesPoint: Client Sales Point List:");
        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            cols.forEach(col -> System.out.print(col.getText() + "\t"));
            System.out.println();
        }

        WebElement firstRow = rows.get(0);
        numberOS = Integer.parseInt(firstRow.findElement(By.xpath(".//*[@data-column-id='total_ativo']")).getText());
        System.out.println("TEST testFoundClientSalesPoint: NÚMERO OS " + numberOS);
    }

    @Test(priority = 3, dependsOnMethods = {"testFoundClientSalesPoint"})
    public void testFilterSalesPointByID() {
        testFilterSalesPoint("id", "Sales Point ID list is not found!");
    }

    @Test(priority = 3, dependsOnMethods = {"testFoundClientSalesPoint"})
    public void testFilterSalesPointBySalesPoint() {
        testFilterSalesPoint("agencia", "Sales Point Name list is not found!");
    }

    @Test(priority = 3, dependsOnMethods = {"testFoundClientSalesPoint"})
    public void testFilterSalesPointByLastMessage() {
        testFilterSalesPoint("ultimo_monitoramento", "Last Message list is not found!");
    }

    @Test(priority = 3, dependsOnMethods = {"testFoundClientSalesPoint"})
    public void testFilterSalesPointByTotalEquipment() {
        testFilterSalesPoint("total_equip", "Total equipment list is not found!");
    }

    @Test(priority = 3, dependsOnMethods = {"testFoundClientSalesPoint"})
    public void testFilterSalesPointByServiceOrder() {
        testFilterSalesPoint("total_ativo", "Service Order list is not found!");
    }

    @Test(priority = 3, dependsOnMethods = {"testFoundClientSalesPoint"})
    public void testFilterSalesPointByOnlineEquipment() {
        testFilterSalesPoint("info_qtd_online", "Online equipment list is not found!");
    }

    @Test(priority = 3, dependsOnMethods = {"testFoundClientSalesPoint"})
    public void testFilterSalesPointByClient() {
        testFilterSalesPoint("instituicao", "Client sales point list is not found!");
    }

    @Test(priority = 4, dependsOnMethods = {"testFoundClientSalesPoint"})
    public void testAssertNumberOfCLients(){
    	WebElement tableBody = waitForElement(By.cssSelector("#listagem_dashboard_view_care table tbody"));
    	WebElement pontosDeVendaQt = waitForElement(By.id("pontosDeVendaQtd"));
    	int numberOfSalesPoints = Integer.parseInt(pontosDeVendaQt.getText().trim());
        
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        Assert.assertFalse(rows.isEmpty(), "Client sales point list is not found!");
        Assert.assertEquals(numberOfSalesPoints, rows.size(), "Number of Shown Sales Points And Number of Sales Points Are Different");

    }

    @Test(priority = 4, dependsOnMethods = {"testFoundClientSalesPoint"})
    public void testAssertNumberOfShowedResults(){
    	WebElement tableBody = waitForElement(By.cssSelector("#listagem_dashboard_view_care table tbody"));
    	List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
    	Assert.assertFalse(rows.isEmpty(), "Client sales point list is not found!");
    	
    	WebElement showedResults = driver.findElement(By.cssSelector("#listagem_dashboard_view_care .gridjs-footer .gridjs-summary"));
    	List<WebElement> boldElements = showedResults.findElements(By.tagName("b"));
    	Assert.assertTrue(boldElements.size() >= 3, "Not Enough Bold Elements");
    	 
    	String  numberOfShowedResultsString =  boldElements.get(2).getText().trim();
    	int numberOfShowedResults = Integer.parseInt(numberOfShowedResultsString);
        Assert.assertEquals(numberOfShowedResults, rows.size(), "Number of Shown Sales Points And Number of Sales Points Are Different");

    }
    

}
