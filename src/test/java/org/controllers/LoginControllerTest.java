package org.controllers;

import com.sun.javafx.application.PlatformImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import org.testfx.framework.junit5.*;
import org.testfx.matcher.control.LabeledMatchers;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginControllerTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        Loader l = new Loader();
        l.showLogin(stage);
    }

    @BeforeAll
    public static void addOperator() {
        Calendar calendar = Calendar.getInstance();
        DatabaseControl.denyOperator(9999);
        DatabaseControl.addRequest(9999, "testApproved", "testPassword1", calendar.getTime());
        DatabaseControl.acceptOperator(9999);
        calendar.add(Calendar.HOUR, 1);
        DatabaseControl.denyOperator(9998);
        DatabaseControl.addRequest(9998, "testNotApproved", "testPassword2", calendar.getTime());
    }

    @AfterAll
    public static void removeOperator() {
        DatabaseControl.denyOperator(10001);
        DatabaseControl.denyOperator(10000);
        DatabaseControl.denyOperator(9999);
        DatabaseControl.denyOperator(9998);
    }

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

    @Test
    @Order(1)
    public void emptyEntryTest() {
        clickOn("#btnLogin");

        verifyThat("#lblWarning", LabeledMatchers.hasText("Incorrect username or password"));
    }

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

    @Test
    @Order(5)
    public void correctEverythingTest() throws InterruptedException {
        clickOn("#enrName");
        write("testApproved");
        clickOn("#enrPassword");
        write("testPassword1");

        clickOn("#btnLogin");

        Thread.sleep(3000);

        verifyThat("#btnFlag_AC", Node::isVisible);
    }

    @Test
    @Order(6)
    public void transferToRegisterTest() {
        clickOn("#lblCreateAccount");

        verifyThat("#enrPasswordAgain", Node::isVisible);
        verifyThat("#btnLogin", LabeledMatchers.hasText("Make Request"));
        verifyThat("#lblTitle", LabeledMatchers.hasText("New Account"));
        verifyThat("#lblCreateAccount", LabeledMatchers.hasText("Login"));
    }

    @Test
    @Order(7)
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

    @Test
    @Order(7)
    public void registerNewUserEmptyTest() {
        clickOn("#lblCreateAccount");

        clickOn("#btnLogin");
        verifyThat("#lblWarning", LabeledMatchers.hasText("All data must be filled"));

        clickOn("#enrName");
        write("0");
        clickOn("#btnLogin");
        verifyThat("#lblWarning", LabeledMatchers.hasText("All data must be filled"));
    }

    @Test
    @Order(8)
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

    @Test
    @Order(9)
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
        Operator operator = DatabaseControl.getSpecificOperator("newUser").get(0);
        assertFalse(operator.isApproved());
        DatabaseControl.denyOperator(operator.getOperatorID());
    }
}
