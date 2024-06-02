package org.controllers;

import com.sun.javafx.application.*;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.testfx.api.*;
import org.testfx.framework.junit5.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import org.testfx.matcher.control.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * These are the test cases for {@code AdminController}, since the original class is fairly streamlined these test cases
 * only concern themselves with full integration tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminControllerTest extends ApplicationTest {
    private static Calendar calendar = Calendar.getInstance();
    private static Date approvalDate, denyDate;

    @Override
    public void start(Stage stage) throws Exception {
        Loader l = new Loader();
        l.showAdmin(stage, Collections.emptyList(), new Operator());
    }

    @BeforeAll
    public static void setUp() {
        approvalDate = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        denyDate = calendar.getTime();

        //denyOperator is used to just remove any operator with this ID, if for some reason they exist
        DatabaseControl.denyOperator(9999);
        DatabaseControl.addRequest(9999, "approvalIntegration", "testPassword", approvalDate);

        DatabaseControl.denyOperator(9998);
        DatabaseControl.addRequest(9998, "denyIntegration", "testPassword", denyDate);
    }

    @AfterAll
    public static void removeAnyLeftovers() {
        DatabaseControl.denyOperator(9999);
        DatabaseControl.denyOperator(9998);
    }

    /**
     * Tests the approval stage, ensuring that the operator awaiting approval is displayed, is properly removed from the
     * screen when accepted and also from the relevant part of the database
     */
    @Test
    @Order(0)
    public void approvalIntegrationTest() {
        verifyThat("#9999", Node::isVisible);
        Button btnAccept = lookup("#9999 #btnAccept").query();
        clickOn(btnAccept);

        //If the relevant ID is missing, the .query command will cause a crash, which then must mean the value is missing
        //meaning that it has been properly removed from the screen
        assertTrue(() -> {
            try {
                lookup("#9999").query();
                return false;
            } catch (Exception e) {
                return true;
            }
        }, "The box containing the awaiting approval should not still exist");

        Integer[] i = {9999};
        List<Operator> list = DatabaseControl.getOperatorsByID(i);
        assertEquals(1, list.size(), "The operator's ID couldn't be found in the database");
        assertTrue(list.get(0).isApproved(), "The operator wasn't approved in the database");

        HashMap<Date, Integer> map = DatabaseControl.getApprovals();
        assertNull(map.get(approvalDate), "The operator shouldn't still exist in the awaiting approvals table");
    }

    @Test
    @Order(1)
    public void denyIntegrationTest() {
        verifyThat("#9998", Node::isVisible);
        Button btnDeny = lookup("#9998 #btnDeny").query();

        clickOn(btnDeny);

        assertTrue(() -> {
            try {
                lookup("#9998").query();
                return false;
            } catch (Exception e) {
                return true;
            }
        }, "The box containing the awaiting approval should not still exist");

        Integer[] i = {9998};
        List<Operator> list = DatabaseControl.getOperatorsByID(i);
        assertEquals(Collections.emptyList(), list, "The operator's ID was still found in the database");

        HashMap<Date, Integer> map = DatabaseControl.getApprovals();
        assertNull(map.get(denyDate), "The operator shouldn't still exist in the awaiting approvals table");
    }
}