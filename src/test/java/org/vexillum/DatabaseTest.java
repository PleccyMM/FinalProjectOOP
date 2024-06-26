package org.vexillum;

import org.testfx.framework.junit5.ApplicationTest;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests some of the functionality of the database that isn't tested properly in the controller tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseTest extends ApplicationTest {
    private final DatabaseControl databaseControl = new DatabaseControl();

    @BeforeEach
    public void openDatabase() {
        databaseControl.openDBSession();
    }

    @AfterEach
    public void closeDatabase() {
        databaseControl.closeDBSession();
    }

    @Test
    @Order(0)
    public void databaseOpenTest() {
        assertTrue(databaseControl.isOpen());
    }

    @Test
    @Order(1)
    public void databaseCloseTest() {
        databaseControl.closeDBSession();
        assertFalse(databaseControl.isOpen());
    }

    /**
     * Checks adding new operators adds them to both tables correctly
     */
    @Test
    @Order(3)
    public void operatorAdditionTest() {
        databaseControl.addRequest(9999, "TestAddition", "testpassword", new Date());

        HashMap<Date, Integer> approvals = databaseControl.getApprovals();
        List<Operator> operators = databaseControl.getSpecificOperator("TestAddition");

        assertTrue(approvals.containsValue(9999));
        Operator expected = new Operator(9999,"TestAddition", "testpassword");
        assertFalse(operators.isEmpty());
        assertEquals(expected, operators.get(0));
    }

    /**
     * Checks approving deletes users as expected
     */
    @Test
    @Order(4)
    public void operatorApprovalTest() {
        databaseControl.acceptOperator(9999);

        HashMap<Date, Integer> approvals = databaseControl.getApprovals();
        List<Operator> operators = databaseControl.getSpecificOperator("TestAddition");

        assertFalse(approvals.containsValue(9999));
        Operator expected = new Operator(9999,"TestAddition", "testpassword");
        expected.setApproved(true);
        assertFalse(operators.isEmpty());
        assertEquals(expected, operators.get(0));
    }

    /**
     * Checks denying deletes users as expected
     */
    @Test
    @Order(5)
    public void operatorDenyTest() {
        databaseControl.addRequest(9998, "TestAdditionDeny", "testpassword", new Date());

        databaseControl.denyOperator(9998);

        HashMap<Date, Integer> approvals = databaseControl.getApprovals();
        List<Operator> operators = databaseControl.getSpecificOperator("TestAdditionDeny");

        assertFalse(approvals.containsValue(9998));
        assertTrue(operators.isEmpty());

        databaseControl.denyOperator(9999);
    }

    /**
     * Checks that all designs are fetch-able with a blank search
     */
    @Test
    @Order(6)
    public void getDesignsTest() {
        List<Design> designs = databaseControl.searchDesigns(new SearchConditions());

        assertEquals(328, designs.size());
    }
}
