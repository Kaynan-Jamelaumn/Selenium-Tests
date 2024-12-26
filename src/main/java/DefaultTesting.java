import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import java.net.HttpURLConnection;
import java.net.URI;

public class DefaultTesting {
	

    private WebDriver driver;
    private static final String BASE_URL = "https://qas.simtro.com.br/index.php";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private int numberOS;
    
    private WebElement waitForElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    private void waitForElementToBeClickable(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private void waitForElementToDisappear(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    private void captureScreenshot(String fileName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File("C://screenshots//" + fileName));
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }
    public void linkTester(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (org.openqa.selenium.TimeoutException e) {
            System.err.println("The locator did not become visible within the timeout period.");
            Assert.fail("Timeout while waiting for elements to become visible.");
            return;
        }

        List<WebElement> links = driver.findElements(locator);
        for (WebElement link : links) {
            String url = link.getDomAttribute("href");

            if (url == null || url.isEmpty()) {
                System.out.println("URL is either not configured for anchor tag or it is empty");
                continue;
            }

            try {
                // Convert the string URL to a URI first, then to a URL
                URI uri = new URI(url);
                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setRequestMethod("HEAD");
                conn.setConnectTimeout(5000); // Set a timeout for connection
                conn.setReadTimeout(5000);
                conn.connect();
                int respCode = conn.getResponseCode();
                System.out.println("Response Code for URL: " + url + " is: " + respCode);

                if (respCode >= 400) {
                    System.err.println("Broken link: " + url);
                    Assert.fail("Link returned a bad response code: " + respCode);
                }

            } catch (Exception e) {
                System.err.println("Exception while checking link: " + url);
                e.printStackTrace();
                Assert.fail("Error occurred while verifying links.");
            }
        }
    }


    @BeforeClass
    public void setUp() throws IOException {
        // Initialize WebDriver (e.g., FirefoxDriver)
    	FirefoxOptions options = new FirefoxOptions();
    	options.setAcceptInsecureCerts(true);
        options.setCapability("webSocketUrl", true); // enables bidi

        // Inicia o driver com BiDi habilitado
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();

        // Testa acesso ao site
        driver.get(BASE_URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.manage().deleteAllCookies();
        
       //File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
       //FileUtils.copyFile(src,new File("C://screenshot.png"));
        captureScreenshot("setup_initial.png");
    }

    @AfterClass
    public void tearDown() {
        // Close the browser after tests
        if (driver != null) {
         //   driver.quit();
        }
    }

    @Test(priority = 1)
    public void testLogin() {
        driver.get(BASE_URL);
        
        driver.findElement(By.id("cpf")).sendKeys("10412822903");
        driver.findElement(By.id("senha")).sendKeys("385018");
        driver.findElement(By.id("submit-login")).click();
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL after login: " + currentUrl);
       // Assert.assertTrue(currentUrl.contains("dashboard"), "Login failed: URL does not contain 'dashboard'.");
    }

    @Test(priority = 2, dependsOnMethods = {"testLogin"})
    public void testViewCare() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement viewCareButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("botao_view_care")));
        
        Thread.sleep(0500);
        
        viewCareButton.click();
        //Assert.assertTrue(driver.getCurrentUrl().contains("view-care"), "View Care navigation failed.");
    }

    
    @Test(priority = 3, dependsOnMethods = {"testViewCare"})
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

    @Test(priority = 3, dependsOnMethods = {"testViewCare"})
    public void testFoundClientSalesPoint(){
    	WebElement tableBody = waitForElement(By.cssSelector("#listagem_dashboard_view_care table tbody"));
        
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        Assert.assertFalse(rows.isEmpty(), "Client sales point list is not found!");

        System.out.println("Client Sales Point List:");
        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            cols.forEach(col -> System.out.print(col.getText() + "\t"));
            System.out.println();
        }
        WebElement firstRow = rows.get(0);
        numberOS = Integer.parseInt(firstRow.findElement(By.xpath(".//*[@data-column-id='total_ativo']")).getText());
        System.out.println("numero éee" + numberOS);
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
    public void testFetchListAndClickFirstSalePoint(){
    	WebElement tableBody = waitForElement(By.cssSelector("#listagem_dashboard_view_care table tbody"));
        
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        Assert.assertFalse(rows.isEmpty(), "Client sales point list is not found!");

        WebElement firstRow = rows.get(0);
        WebElement button = firstRow.findElement(By.className("simtro-text-button"));
      //  Assert.assertFalse(buttons.isEmpty(), "The 'simtro-text-button' was not found in the first row!");
     //   WebElement numberOS = firstRow.findElement(By.id("simtro-text-button"));
        
        button.click();
        System.out.println("Clicked the 'simtro-text-button' in the first row.");
        
    }

    
    
    @Test(priority = 5, dependsOnMethods = {"testFetchListAndClickFirstSalePoint"})
    public void testLastInfoFirstMonitorableIsValid() {
    	WebElement tableEquipement = waitForElement(By.cssSelector("#dados_equipamentos_tabela"));
        List<WebElement> rows = tableEquipement.findElements(By.cssSelector(".card.card-stats"));
        Assert.assertFalse(rows.isEmpty(), "Client equipment not found!");
        
        WebElement firstRow = rows.get(0);
        String text = firstRow.findElement(By.className("stats")).getText();
        
        Pattern pattern = Pattern.compile("Última informação: (\\d{2}/\\d{2}/\\d{4}) (\\d{2}:\\d{2}:\\d{2})");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            // Captura a data e o horário
            String dateStr = matcher.group(1); // Ex: 19/12/2024
            String timeStr = matcher.group(2); // Ex: 10:37:08
            
            // Define o formato de data e hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            // Converte para LocalDateTime
            LocalDateTime extractedDateTime = LocalDateTime.parse(dateStr + " " + timeStr, formatter);
            
            // Pega o horário atual
            LocalDateTime currentDateTime = LocalDateTime.now();
            
            // Verifica se a data é a mesma
            Assert.assertEquals(extractedDateTime.toLocalDate(), currentDateTime.toLocalDate(), "The dates do not match.");
            
            // Verifica se a diferença de tempo é no máximo 6 minutos
            long minutesDifference = ChronoUnit.MINUTES.between(extractedDateTime, currentDateTime);
            Assert.assertTrue(Math.abs(minutesDifference) <= 6, "The time difference is more than 6 minutes.");
            
            System.out.println("Date and Time: " + extractedDateTime);
            System.out.println("Current Date and Time: " + currentDateTime);
        } else {
            Assert.fail("The date and time information was not found.");
        }     
    }
    @Test(priority = 5, dependsOnMethods = {"testFetchListAndClickFirstSalePoint"})
    public void testValidatesNumberOfOS() {
    	WebElement numberOSSeted = waitForElement(By.cssSelector(".simtroDivHeaderTitles div:nth-of-type(2) small:nth-of-type(2)"));
    	int number;

    	try {
    	    number = Integer.parseInt(numberOSSeted.getText());
    	} catch (NumberFormatException e) {
    	    number = 0; 
    	}
    	System.out.println("vasco" + number + "ee" + numberOS);
    	Assert.assertEquals(number, numberOS, "OS Number Does Not Match" );   
    }

    
    @Test(priority = 6, dependsOnMethods = {"testLastInfoFirstMonitorableIsValid"})
    public void testAcessEquipment()   {
    	  WebElement tableEquipment = waitForElement(By.cssSelector("#dados_equipamentos_tabela"));
        List<WebElement> rows = tableEquipment.findElements(By.cssSelector(".card.card-stats"));
        Assert.assertFalse(rows.isEmpty(), "Client equipment not found!");
        
        WebElement firstRow = rows.get(0);
        WebElement footer = firstRow.findElement(By.className("card-footer"));
        WebElement button = footer.findElements(By.tagName("div")).get(2);
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //js.executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", button);
        
        //button.click();
         js.executeScript("carregarControlador('3006099150085066');");
       // String onclickAttribute = (String) js.executeScript("return arguments[0].getAttribute('onclick');", button);
        //if (onclickAttribute == null) {
          //  System.out.println("O atributo 'onclick' não está presente no elemento.");
            
            //wait.until(driver -> (String) js.executeScript("return arguments[0].getAttribute('onclick');", button) != null);
        //}

        // Extração do parâmetro
        //String parameter = onclickAttribute.replaceAll(".*\\('([^']*)'\\).*", "$1");
        //System.out.println("Parâmetro extraído: " + parameter);

        // Executar a função diretamente
        //js.executeScript("carregarControlador('" + parameter + "');");

         // Espera até que o estilo 'display' seja configurado como 'none'
         waitForElement(By.className("loading"));
         System.out.println("O elemento 'loading'  está mais visível.");
         waitForElementToDisappear(By.className("loading"));

         // Confirma que o carregamento desapareceu
         System.out.println("O elemento 'loading' não está mais visível.");
        
    }
    @Test(priority = 7, dependsOnMethods = {"testAcessEquipment"})
    public void testGoBackFromEquipment()  {
    	//waitForElementToDisappear(By.className("loading"));
    	
    	JavascriptExecutor js = (JavascriptExecutor) driver;
    	js.executeScript("changeMain('monitoramento_agencia');");

    	//WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    	//WebElement voltarElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[text()='Voltar']/..")));
    	  //System.out.println("aaaaaaaaaa" + voltarElement.getText());
    	//WebElement voltarElement = driver.findElement(By.xpath("//label[text()='Voltar']/.."));
    	//voltarElement.click();
    }
    @Test(priority = 8, dependsOnMethods = {"testGoBackFromEquipment"})
    public void testGoBackFromSalesPointl()  {
    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    	//JavascriptExecutor js = (JavascriptExecutor) driver;
    	//js.executeScript("changeMain('monitoramento_agencia');");

    	WebElement voltarElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[text()='Voltar']/..")));
    	voltarElement.click();
    }




}
