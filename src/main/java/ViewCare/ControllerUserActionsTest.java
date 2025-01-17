package ViewCare;

import org.testng.annotations.Listeners;

import base.BaseTest;
import base.CustomTestListener;

@Listeners(CustomTestListener.class)
public class ControllerUserActionsTest extends BaseTest {
	
	
    private static final String USER_CARD_SELECTOR = "#detalhamento_controlador .cardSimtroProject:nth-of-type(3) .simtTitle";

}
