package org.controllers;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.testfx.framework.junit5.*;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.ComboBoxMatchers;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Tests the functionality of {@code LoginController}
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginControllerTest extends ApplicationTest {
    private static DatabaseControl database = new DatabaseControl();
    
    @Override
    public void start(Stage stage) throws Exception {
        Loader l = new Loader();
        l.showLogin(stage);
    }

    /**
     * Sets up the operators ready for logging in
     */
    @BeforeAll
    public static void addOperator() {
        Calendar calendar = Calendar.getInstance();

        database.openDBSession();

        database.denyOperator(9999);
        database.addRequest(9999, "testApproved", "testPassword1", calendar.getTime());
        database.acceptOperator(9999);
        calendar.add(Calendar.HOUR, 1);
        database.denyOperator(9998);
        database.addRequest(9998, "testNotApproved", "testPassword2", calendar.getTime());

        calendar.add(Calendar.HOUR, 1);
        database.denyOperator(9997);
        database.addRequest(9997, "testAdmin", "testPassword3", calendar.getTime());
        database.acceptOperator(9997);
        database.promoteOperator(9997);

        database.closeDBSession();
    }

    @AfterAll
    public static void removeOperator() {
        database.openDBSession();

        database.denyOperator(10001);
        database.denyOperator(10000);
        database.denyOperator(9999);
        database.denyOperator(9998);
        database.denyOperator(9997);

        database.closeDBSession();
    }

    /**
     * Checks warning for totally incorrect information
     */
    @Test
    @Order(1)
    public void incorrectEverythingTest() {
        clickOn("#enrName");
        write("iDontExist");
        clickOn("#enrPassword");
        write("thisIsWrong");

        clickOn("#btnLogin");

        verifyThat("#lblWarning", LabeledMatchers.hasText("Incorrect username or password"));
    }

    /**
     * Checks warning for empty attempts to login
     */
    @Test
    @Order(1)
    public void emptyEntryTest() {
        clickOn("#btnLogin");

        verifyThat("#lblWarning", LabeledMatchers.hasText("Incorrect username or password"));
    }

    /**
     * Checks warning for valid passwords but an invalid name
     */
    @Test
    @Order(2)
    public void incorrectNameTest() {
        clickOn("#enrName");
        write("iDontExist");
        clickOn("#enrPassword");
        write("testPassword1");

        clickOn("#btnLogin");

        verifyThat("#lblWarning", LabeledMatchers.hasText("Incorrect username or password"));
    }

    /**
     * Checks warning for a valid name but incorrect password
     */
    @Test
    @Order(3)
    public void incorrectPasswordTest() {
        clickOn("#enrName");
        write("testApproved");
        clickOn("#enrPassword");
        write("thisIsWrong");

        clickOn("#btnLogin");

        verifyThat("#lblWarning", LabeledMatchers.hasText("Incorrect username or password"));
    }

    /**
     * Checks warning for a correct set of information on an operator who isn't approved
     */
    @Test
    @Order(4)
    public void loginWhilstUnapprovedTest() {
        clickOn("#enrName");
        write("testNotApproved");
        clickOn("#enrPassword");
        write("testPassword2");

        clickOn("#btnLogin");

        verifyThat("#lblWarning", LabeledMatchers.hasText("Incorrect username or password"));
    }

    /**
     * Ensures that logging in with completely correct information works
     */
    @Test
    @Order(5)
    public void correctEverythingTest() throws InterruptedException {
        clickOn("#enrName");
        write("testApproved");
        clickOn("#enrPassword");
        write("testPassword1");

        clickOn("#btnLogin");

        Thread.sleep(500);

        verifyThat("#btnFlag_AC", Node::isVisible);

        ComboBox<String> cmbProfile = lookup("#cmbProfile").queryComboBox();
        verifyThat(cmbProfile, ComboBoxMatchers.containsItems("Logout"));
        assertFalse(cmbProfile.getItems().contains("Admin Panel"));
    }

    /**
     * Checks that admins have additional options when logged in
     */
    @Test
    @Order(6)
    public void adminLoginTest() throws InterruptedException {
        clickOn("#enrName");
        write("testAdmin");
        clickOn("#enrPassword");
        write("testPassword3");

        clickOn("#btnLogin");

        Thread.sleep(500);

        verifyThat("#cmbProfile", ComboBoxMatchers.containsExactlyItemsInOrder("Admin Panel", "Logout"));
    }

    /**
     * Ensures going to the registration screen updates all needed elements on screen
     */
    @Test
    @Order(7)
    public void transferToRegisterTest() {
        clickOn("#lblCreateAccount");

        verifyThat("#enrPasswordAgain", Node::isVisible);
        verifyThat("#btnLogin", LabeledMatchers.hasText("Make Request"));
        verifyThat("#lblTitle", LabeledMatchers.hasText("New Account"));
        verifyThat("#lblCreateAccount", LabeledMatchers.hasText("Login"));
    }

    /**
     * Ensures that passwords must match to be able to submit a new request
     */
    @Test
    @Order(8)
    public void registerNewUserMismatchedPasswordsTest() {
        clickOn("#lblCreateAccount");

        clickOn("#enrName");
        write("newUser");
        clickOn("#enrPassword");
        write("testPassword3");
        clickOn("#enrPasswordAgain");
        write("thisIsWrong");

        clickOn("#btnLogin");

        verifyThat("#lblWarning", LabeledMatchers.hasText("Passwords must match"));
    }

    /**
     * Ensures that not filling in all the needed information provides a relevant warning
     */
    @Test
    @Order(9)
    public void registerNewUserEmptyTest() {
        clickOn("#lblCreateAccount");

        clickOn("#btnLogin");
        verifyThat("#lblWarning", LabeledMatchers.hasText("All data must be filled"));

        clickOn("#enrName");
        //Just fills in the name part to check that it gives the same error if that part is at least filled
        write("0");
        clickOn("#btnLogin");
        verifyThat("#lblWarning", LabeledMatchers.hasText("All data must be filled"));
    }

    /**
     * Ensures users cannot share the name of already existent users
     */
    @Test
    @Order(10)
    public void registerNewUserSameNameTest() {
        clickOn("#lblCreateAccount");

        clickOn("#enrName");
        write("testApproved");
        clickOn("#enrPassword");
        write("testPassword3");
        clickOn("#enrPasswordAgain");
        write("testPassword3");

        clickOn("#btnLogin");

        verifyThat("#lblWarning", LabeledMatchers.hasText("Username already taken"));
    }

    /**
     * Checks that if all requirements are met registering works as expected
     */
    @Test
    @Order(11)
    public void registerNewUserCorrectTest() {
        clickOn("#lblCreateAccount");

        clickOn("#enrName");
        write("newUser");
        clickOn("#enrPassword");
        write("testPassword3");
        clickOn("#enrPasswordAgain");
        write("testPassword3");

        clickOn("#btnLogin");

        verifyThat("#boxContainment", Node::isVisible);
        database.openDBSession();
        Operator operator = database.getSpecificOperator("newUser").get(0);
        assertFalse(operator.isApproved());
        database.denyOperator(operator.getOperatorID());
        database.closeDBSession();
    }
}
