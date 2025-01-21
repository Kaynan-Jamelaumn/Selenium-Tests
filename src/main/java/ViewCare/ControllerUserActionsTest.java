package ViewCare;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

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
public class ControllerUserActionsTest extends BaseTest {
	

    private static final Duration TIMEOUT = Duration.ofSeconds(120);
    
    
    private static final By USER_CARD_BUTTON_SELECTOR = By.cssSelector("#detalhamento_controlador .cardSimtroProject:nth-of-type(1) .simtTitle");
    private static final By LOADING_SPINNER = By.cssSelector(".loading");
    private static final By USER_TABLE_SELECTOR = By.id("listagem_usuarios_controlador");
    private static final By CREATE_USER_BUTTON = By.cssSelector("#botoes_usuarios_controlador .simtro-text-button:nth-of-type(1)");
    private static final By MODAL_ADD_USER = By.id("myModalAdicionarUsuarioControlador");
    
    private static final By DELETE_USER_BUTTON = By.cssSelector("#botoes_usuarios_controlador .simtro-text-button:nth-of-type(3)");
    private static final By DELETE_USER_SELECT = By.cssSelector("#controlador_usuario_deletar");
    private static final By CONFIRM_DELETE_USER_BUTTON = By.cssSelector("#myModalExcluirUsuarioControlador .modal-footer a:nth-of-type(2)");
    private static final By CONFIRM_DELETE_X_BUTTON = By.cssSelector("#myModalExcluirUsuarioControlador .close");
    
    private static final By USER_ROWS = By.cssSelector("#table_usuarios_controlador tr");
    private static final By MODAL_CLOSE_BUTTON = By.cssSelector("#myModalAdicionarUsuarioControlador .close");
    private static final By MODAL_FOOTER_BUTTON = By.cssSelector("#myModalAdicionarUsuarioControlador .modal-footer .simtro-text-button");
    private static final By MODAL_ADD_USER_BUTTON = By.id("botao_usuario_modal");
    private static final By MODAL_CONTENT = By.cssSelector("#myModalAdicionarUsuarioControlador .modal-content");
    
    private static final String NEW_USER_NAME = "Teste";
    private static final String NEW_USER_SURNAME = "Automatizado";
    private static final String NEW_USER_REGISTRATION = "2446";
    private static final String NEW_USER_CPF = "930.955.140-26";
    private static final String NEW_USER_BIRTH_DATE = "2003-07-21";
    private static final String NEW_USER_PHONE_NUMBER = "48 999016811";
    private static final String NEW_USER_EMAIL = "kaynanrodrigues.nt@gmail.com";
    private static final String NEW_USER_PERMITED_TIME = "Sempre Liberado";
    
    private static final String NEW_USER_BIRTH_WRONG = "2024-07-21";
    private static final String NEW_USER_EMAIL_WRONG = "A";
    private static final String NEW_USER_CPF_WRONG = "333.33.160-33";
    
    private static final String UPDATE_ADDITIONAL = " Update";
    
    private static final By MODAL_NAME_INPUT = By.id("controlador_usuario_nome");
    private static final By MODAL_SURNAME_INPUT =By.id("controlador_usuario_sobrenome");
    private static final By MODAL_REGISTRATION_INPUT = By.id("controlador_usuario_matricula");
    private static final By MODAL_CPF_INPUT = By.id("controlador_usuario_cpf");
    private static final By MODAL_BIRTH_DATE_INPUT =By.id("controlador_usuario_nascimento");
    private static final By MODAL_PHONE_NUMBER_INPUT = By.id("controlador_usuario_celular");
    private static final By MODAL_EMAIL_INPUT = By.id("controlador_usuario_email");
    private static final By MODAL_PERMITED_TIME_INPUT = By.id("controlador_usuario_horario");
    
    private static final By INFO_MESSAGE = By.cssSelector("#myModal #info_mensagem");
    private static final By MODAL_CONFIRM_BUTTON = By.cssSelector("#myModal .simtro-text-button-alternative");
    
    @Override
    protected void waitForElementToDisappear(By locator) {
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    @Test(priority = 1, dependsOnMethods = {"ViewCare.EnterControllerTest.testAccessEquipmentController"})
    public void testCreateNewUser() {
        System.out.println("\n---------------CONTROLLER USER ACTIONS---------------\n");

        navigateToControllerUser();
    

	
	    WebElement element = driver.findElement(USER_TABLE_SELECTOR);
	    scrollIntoView(element);

	    
	    
	    List<WebElement> initialRows = waitForElements(USER_ROWS);
        Assert.assertFalse(initialRows.isEmpty(), "User list should not be empty.");

        WebElement createUserButton = driver.findElement(CREATE_USER_BUTTON);

        handleModal(createUserButton, MODAL_CLOSE_BUTTON, false); // Close using X button
        handleModal(createUserButton, MODAL_FOOTER_BUTTON, true); // Close using footer button

        createNewUser(createUserButton, NEW_USER_NAME, NEW_USER_SURNAME, NEW_USER_REGISTRATION, 
        		NEW_USER_CPF, NEW_USER_BIRTH_DATE, NEW_USER_PHONE_NUMBER, NEW_USER_EMAIL, NEW_USER_PERMITED_TIME);

        List<WebElement> newRows = waitForElements(USER_ROWS);
        Assert.assertEquals(newRows.size(), initialRows.size() + 1, "New time entry count mismatch.");

        validateNewUserEntry(newRows, NEW_USER_NAME);
	        System.out.println("TEST testCreateNewUser: passed");
    }
    @Test(priority = 2, dependsOnMethods = {"testCreateNewUser"})
    public void testUpdateUser() {

        WebElement user = findUserByName(USER_ROWS, NEW_USER_NAME);
        clickElement(user);

        waitForElement(MODAL_ADD_USER);
        validateModalFields();

        updateUserFields(UPDATE_ADDITIONAL);

        clickButton(MODAL_ADD_USER_BUTTON);
        waitForLoadingSpinner();

        Assert.assertEquals(waitForElement(INFO_MESSAGE).getText(), "Usuário atualizado com sucesso.", "Failed to update user.");
        clickButton(MODAL_CONFIRM_BUTTON);

        WebElement updatedUser = findUserByName(USER_ROWS, NEW_USER_NAME + UPDATE_ADDITIONAL);
        clickElement(updatedUser);

        waitForElement(MODAL_ADD_USER);
        validateUpdatedFields(UPDATE_ADDITIONAL);

        clickButton(MODAL_CLOSE_BUTTON);

        System.out.println("TEST testUpdateUser: passed");
    }

    private WebElement findUserByName(By rowSelector, String userName) {
        List<WebElement> rows = waitForElements(rowSelector);
        return rows.stream()
                .filter(row -> {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    return cells.get(1).getText().equals(userName);
                })
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User not found with name: " + userName));
    }

    private void clickElement(WebElement element) {
        scrollIntoView(element);
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.elementToBeClickable(element));
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void validateModalFields() {
        validateFieldValue(MODAL_NAME_INPUT, NEW_USER_NAME, "New User Name");
        validateFieldValue(MODAL_SURNAME_INPUT, NEW_USER_SURNAME, "New User Surname");
        validateFieldValue(MODAL_REGISTRATION_INPUT, NEW_USER_REGISTRATION, "New Registration Name");
        validateFieldValue(MODAL_EMAIL_INPUT, NEW_USER_EMAIL, "New User Email");
        validateFieldValue(MODAL_CPF_INPUT, NEW_USER_CPF, "New User CPF");
        validateFieldValue(MODAL_BIRTH_DATE_INPUT, NEW_USER_BIRTH_DATE, "New User BirthDate");
       // validateFieldValue(MODAL_PHONE_NUMBER_INPUT, NEW_USER_PHONE_NUMBER, "New User Phone Number");
        validateDropdownValue(MODAL_PERMITED_TIME_INPUT, NEW_USER_PERMITED_TIME, "New User Permitted Time");
    }

    private void validateUpdatedFields(String updateValue) {
        validateFieldValue(MODAL_NAME_INPUT, NEW_USER_NAME + updateValue, "Updated User Name");
        validateFieldValue(MODAL_SURNAME_INPUT, NEW_USER_SURNAME + updateValue, "Updated User Surname");
        validateFieldValue(MODAL_REGISTRATION_INPUT, NEW_USER_REGISTRATION + updateValue, "Updated Registration Name");
        validateFieldValue(MODAL_EMAIL_INPUT, NEW_USER_EMAIL + updateValue, "Updated User Email");
        validateFieldValue(MODAL_CPF_INPUT, NEW_USER_CPF, "Updated User CPF");
        validateFieldValue(MODAL_BIRTH_DATE_INPUT, NEW_USER_BIRTH_DATE , "Updated User BirthDate");
       // validateFieldValue(MODAL_PHONE_NUMBER_INPUT, NEW_USER_PHONE_NUMBER + updateValue, "Updated User Phone Number");
        validateDropdownValue(MODAL_PERMITED_TIME_INPUT, NEW_USER_PERMITED_TIME, "Updated User Permitted Time");
    }

    private void validateFieldValue(By fieldLocator, String expectedValue, String fieldName) {
        WebElement field = driver.findElement(fieldLocator);
        Assert.assertEquals(field.getAttribute("value"), expectedValue, "Fields Didn't Match (" + fieldName + ")");
    }

    private void validateDropdownValue(By dropdownLocator, String expectedValue, String fieldName) {
        Select dropdown = new Select(driver.findElement(dropdownLocator));
        Assert.assertEquals(dropdown.getFirstSelectedOption().getText(), expectedValue, "Fields Didn't Match (" + fieldName + ")");
    }

    private void updateUserFields(String updateValue) {
        fillField(MODAL_NAME_INPUT, updateValue);
        fillField(MODAL_SURNAME_INPUT, updateValue);
        fillField(MODAL_REGISTRATION_INPUT, updateValue);
        fillField(MODAL_EMAIL_INPUT, updateValue);
    }

    private void fillField(By fieldLocator, String value) {
        WebElement field = driver.findElement(fieldLocator);
        //field.clear();
        field.sendKeys(value);
    }

    private void waitForLoadingSpinner() {
        waitForElement(LOADING_SPINNER);
        waitForElementToDisappear(LOADING_SPINNER);
    }

    @Test(priority = 2, dependsOnMethods = {"testUpdateUser"})
    public void testCreateUserPassword() {

    }
    
    
    @Test(priority = 8, dependsOnMethods = {"testCreateNewUser"})
    public void testDeleteUser() {
       
    	clickButton(DELETE_USER_BUTTON);
    	
    	selectDropdownOptionOnText(DELETE_USER_SELECT, (NEW_USER_NAME + UPDATE_ADDITIONAL + " " + NEW_USER_SURNAME + UPDATE_ADDITIONAL));
    	
    	clickButton(CONFIRM_DELETE_USER_BUTTON);
    	
        waitForElement(LOADING_SPINNER);
        waitForElementToDisappear(LOADING_SPINNER);
        
        Assert.assertEquals(waitForElement(INFO_MESSAGE).getText(), "Usuário excluído com sucesso.", "Failed to delete new user.");
        
        clickButton(MODAL_CONFIRM_BUTTON);
    	clickButton(CONFIRM_DELETE_X_BUTTON);
        System.out.println("TEST testDeleteUser: passed");
    }
    
    
    private void createNewUser(WebElement createUserButton, String name, String surname, String registrationn, String cpf, String birthDate, String phoneNumber, String email, String permitedTime) {
        WebElement addUserButton = driver.findElement(MODAL_ADD_USER_BUTTON);
        createUserButton.click();

        WebElement modalContent = waitForElement(MODAL_CONTENT);

        fillModalInput(modalContent, addUserButton, MODAL_NAME_INPUT, name, "Por favor, digite o nome do usuário.", null , null);
        fillModalInput(modalContent, addUserButton, MODAL_SURNAME_INPUT, surname, "Por favor, digite o sobrenome do usuário.", null , null);
        fillModalInput(modalContent, addUserButton, MODAL_REGISTRATION_INPUT, registrationn, "Por favor, digite a matrícula do usuário.", null , null);
        fillModalInput(modalContent, addUserButton, MODAL_EMAIL_INPUT, email, "Por favor, digite o e-mail do usuário.", null, null);// NEW_USER_EMAIL_WRONG 
        fillModalInput(modalContent, addUserButton, MODAL_CPF_INPUT, cpf, "Por favor, digite o CPF do usuário.", NEW_USER_CPF_WRONG ,"CPF inválido.");
        fillModalInput(modalContent, addUserButton, MODAL_BIRTH_DATE_INPUT, birthDate, "Por favor, digite a data de nascimento do usuário.", NEW_USER_BIRTH_WRONG , "O Usuário deve ser maior de 16 anos.");
        fillModalInput(modalContent, addUserButton, MODAL_PHONE_NUMBER_INPUT, phoneNumber, "Por favor, digite o telefone celular do usuário.", null , null);
        
    	
        // Já existe um usuário cadastrado com este número de matrícula. 
        addUserButton.click();
        Assert.assertEquals(waitForElement(INFO_MESSAGE).getText(),"Por favor, selecione um horário para o usuário.", "Validation message mismatch.");
        clickButton(MODAL_CONFIRM_BUTTON);
        selectDropdownOptionOnText(MODAL_PERMITED_TIME_INPUT,NEW_USER_PERMITED_TIME);

        addUserButton.click();
        waitForElement(LOADING_SPINNER);
        waitForElementToDisappear(LOADING_SPINNER);

        Assert.assertEquals(waitForElement(INFO_MESSAGE).getText(), "Usuário Cadastrado com sucesso.", "Failed to save new User.");
        
        driver.findElement(MODAL_CONFIRM_BUTTON).click();
    }

    
    
    private void fillModalInput(WebElement modalContent, WebElement addUserButton, By inputId, String correctValue, String validationMessage, String wrongValue, String expectedErrorValidationMessage) {
    	
        addUserButton.click();
        Assert.assertEquals(waitForElement(INFO_MESSAGE).getText(), validationMessage, "Validation message mismatch.");
        clickButton(MODAL_CONFIRM_BUTTON);
        WebElement input =   modalContent.findElement(inputId);
        if (wrongValue != null) {
        	input.sendKeys(wrongValue);
       	 addUserButton.click();
       	 Assert.assertEquals(waitForElement(INFO_MESSAGE).getText(), expectedErrorValidationMessage, "Validation message mismatch.");
       	 driver.findElement(MODAL_CONFIRM_BUTTON).click();
       	 input.clear();
       }
        
        
        input.sendKeys(correctValue);
    }
    private void navigateToControllerUser() {
        WebElement controllerTimes = driver.findElement(USER_CARD_BUTTON_SELECTOR);
        scrollIntoView(controllerTimes);
        controllerTimes.click();

	    waitForElement(LOADING_SPINNER);
	    waitForElementToDisappear(LOADING_SPINNER);
    }

    private void handleModal(WebElement createTimeButton, By closeButtonLocator, boolean useFooter) {
        createTimeButton.click();
        waitForElement(MODAL_CONTENT);
        clickButton(closeButtonLocator);
        waitForElementToDisappear(MODAL_CONTENT);
    }
    
    private void validateNewUserEntry(List<WebElement> newRows, String expectedName) {
        boolean found = newRows.stream().anyMatch(row -> {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.get(1).getText().equals(expectedName)) {
                return true;
            }
            return false;
        });

        Assert.assertTrue(found, "The new User entry '" + expectedName + "' was not found in the User list.");
    }
}
