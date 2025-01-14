package base;
import java.time.Duration;
import java.util.Set;
import java.util.ArrayList;
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
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import java.net.HttpURLConnection;
import java.net.URI;
import org.openqa.selenium.support.ui.ExpectedCondition;
public class BaseTest {
	

    protected static WebDriver driver;
    protected static final String BASE_URL = "https://qas.simtro.com.br/index.php";
    protected  Duration TIMEOUT = Duration.ofSeconds(10);
    protected static int numberOS;
    protected static final String DOWNLOAD_DIR = "C:\\Users\\enricky.hipolito\\Downloads\\";
    
    protected WebElement waitForElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    protected List<WebElement> waitForElements(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    
    protected void waitForElementToBeClickable(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void waitForElementToDisappear(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    
    protected void captureScreenshot(String fileName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File("C://screenshots//" + fileName));
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }
    protected void linkTester(By locator) {
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
                //System.out.println("Response Code for URL: " + url + " is: " + respCode);

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
    protected void setUp() throws IOException {
        // Initialize WebDriver (e.g., FirefoxDriver)
        if (driver == null) {
            FirefoxOptions options = new FirefoxOptions();
            options.setAcceptInsecureCerts(true);
            options.setCapability("webSocketUrl", true); // enables bidi

            
            // Set up the download directory
            options.addPreference("browser.download.folderList", 2);
            options.addPreference("browser.download.dir", DOWNLOAD_DIR);
            options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");
            
            driver = new FirefoxDriver(options);
            driver.manage().window().maximize();
            
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
            driver.get(BASE_URL);
            driver.manage().deleteAllCookies();
            
            //File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            //FileUtils.copyFile(src,new File("C://screenshot.png"));
            //captureScreenshot("setup_initial.png");
        }
    }

    @AfterClass
    protected void tearDown() {
        // Close the browser after tests
        if (driver != null) {
         //   driver.quit();
        }
    }
    

    protected void closeExtraTabs() {
        String mainWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window).close();
            }
        }
        driver.switchTo().window(mainWindow);
    }
    
    protected void waitLoading() {
        waitForElement(By.className("loadingClass"));
        waitForElementToDisappear(By.className("loadingClass"));
    	
    }
    protected void tryWaitLoading() {
        try {
            waitForElement(By.className("loadingClass"));
            waitForElementToDisappear(By.className("loadingClass"));
        } catch (Exception e) {
            // Handle the exception or log it
           // e.printStackTrace();
        }
    }
    
    protected boolean verifyDownloadStarted(By downloadButtonLocator) {
        File downloadDir = new File(DOWNLOAD_DIR);
        long initialFileCount = downloadDir.listFiles().length;

        // Click the download button
        WebElement downloadButton = waitForElement(downloadButtonLocator);
        downloadButton.click();

        // Wait for a new file to appear in the download directory
        long timeout = 30; // seconds
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeout * 1000) {
            long currentFileCount = downloadDir.listFiles().length;
            if (currentFileCount > initialFileCount) {
                System.out.println("Download has started");
                return true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.err.println("Download did not start within the timeout period");
        return false;
    }
    
    
}