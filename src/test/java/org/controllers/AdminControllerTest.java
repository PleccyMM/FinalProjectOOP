package org.controllers;

import javafx.geometry.Bounds;
import javafx.geometry.VerticalDirection;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.testfx.framework.junit5.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;

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
    private static DatabaseControl database = new DatabaseControl();

    @Override
    public void start(Stage stage) throws Exception {
        Loader l = new Loader();
        l.showAdmin(stage, Collections.emptyList(), new Operator());
    }

    @BeforeAll
    public static void setUp() {
        database.openDBSession();
        approvalDate = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        denyDate = calendar.getTime();

        //denyOperator is used to just remove any operator with this ID, if for some reason they exist
        database.denyOperator(9999);
        database.addRequest(9999, "approvalIntegration", "testPassword", approvalDate);

        database.denyOperator(9998);
        database.addRequest(9998, "denyIntegration", "testPassword", denyDate);
        database.closeDBSession();
    }

    @BeforeEach
    public void openDatabase() {
        database.openDBSession();
    }

    @AfterAll
    public static void removeAnyLeftovers() {
        database.openDBSession();
        database.denyOperator(9999);
        database.denyOperator(9998);
        database.denyOperator(9997);
        database.closeDBSession();
    }

    @AfterEach
    public void closeDatabase() {
        database.closeDBSession();
    }

    /**
     * Gets an item and scrolls to it on the screen
     * @param toFind the node you want visible, should be a button for this page
     */
    protected void scrollToItem(String toFind) {
        ScrollPane scrBackground = lookup("#scrMain").query();
        Bounds scrollBound = scrBackground.localToScene(scrBackground.getBoundsInLocal());

        Node buttonToFind = lookup(toFind).query();
        Bounds boxBound;

        clickOn(scrBackground);
        do {
            scroll(1, VerticalDirection.DOWN);

            if (scrBackground.getVvalue() == scrBackground.getVmax()) {
                break;
            }

            boxBound = buttonToFind.localToScene(buttonToFind.getBoundsInLocal());
        } while (!scrollBound.intersects(boxBound));

        scroll(1, VerticalDirection.DOWN);
    }

    /**
     * Tests the approval stage, ensuring that the operator awaiting approval is displayed, is properly removed from the
     * screen when accepted and also from the relevant part of the database
     */
    @Test
    @Order(0)
    public void approvalIntegrationTest() throws InterruptedException {
        scrollToItem("#9999 #btnAccept");
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
        List<Operator> list = database.getOperatorsByID(i);
        assertEquals(1, list.size(), "The operator's ID couldn't be found in the database");
        assertTrue(list.get(0).isApproved(), "The operator wasn't approved in the database");

        HashMap<Date, Integer> map = database.getApprovals();
        assertNull(map.get(approvalDate), "The operator shouldn't still exist in the awaiting approvals table");
    }

    /**
     * Tests the denial stage, ensuring that the operator awaiting approval is displayed, is properly removed from the
     * screen when denied and also from the relevant part of the database
     */
    @Test
    @Order(1)
    public void denyIntegrationTest() {
        scrollToItem("#9998 #btnDeny");
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
        List<Operator> list = database.getOperatorsByID(i);
        assertEquals(Collections.emptyList(), list, "The operator's ID was still found in the database");

        HashMap<Date, Integer> map = database.getApprovals();
        assertNull(map.get(denyDate), "The operator shouldn't still exist in the awaiting approvals table");
    }
}