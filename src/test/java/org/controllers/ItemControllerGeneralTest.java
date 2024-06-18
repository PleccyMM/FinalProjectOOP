package org.controllers;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.testfx.matcher.control.LabeledMatchers;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * General tests for {@code ItemController} functionality
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerGeneralTest extends ItemSupplementTest {
    @AfterAll
    public static void deleteFile() {
        File f = new File("United Kingdom_Flag.txt");
        f.delete();
        database.closeDBSession();
    }

    /**
     * Tests that the size buttons accurately updated information
     */
    @Test
    @Order(1)
    public void changeSizeTest() {
        VBox boxHand = lookup("#boxSize_Hand #boxSelect").query();
        VBox box90 = lookup("#boxSize_90x60cm #boxSelect").query();
        verifyThat(boxHand, Node::isVisible);
        verifyThat(box90, node -> !node.isVisible());

        clickOn("#boxSize_90x60cm");

        verifyThat(boxHand, node -> !node.isVisible());
        verifyThat(box90,Node::isVisible);
    }

    /**
     * Checks that incrementing the amount works
     */
    @Test
    @Order(1)
    public void increaseCountTest() {
        clickOn("#btnAdd");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("2"));
    }

    /**
     * Checks that decreasing the amount works
     */
    @Test
    @Order(2)
    public void decreaseCountTest() {
        clickOn("#btnAdd");
        clickOn("#btnAdd");
        clickOn("#btnAdd");
        clickOn("#btnMinus");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("3"));
    }

    /**
     * Ensures that items being exported are capped at the amount in stock
     */
    @Test
    @Order(3)
    public void attemptOverAddTest() {
        for (int i = 0; i < 12; i++) {
            clickOn("#btnAdd");
        }

        verifyThat("#lblIncrement", LabeledMatchers.hasText("10"));
        verifyThat("#btnAdd", Node::isDisabled);
    }

    /**
     * Ensures negative amounts of items cannot be added, instead at little 1
     */
    @Test
    @Order(3)
    public void attemptUnderAddTest() {
        clickOn("#btnMinus");
        clickOn("#btnMinus");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("1"));
        verifyThat("#btnMinus", Node::isDisabled);
    }

    /**
     * Checks functionality for the amount buttons and label when the item has 0 in stock, ensuring both are disabled
     * and the value is shown correctly
     */
    @Test
    @Order(4)
    public void zeroQuantityTest() {
        clickOn("#boxSize_240x150cm");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("0"));
        verifyThat("#btnAdd", Node::isDisabled);
        verifyThat("#btnMinus", Node::isDisabled);
    }

    /**
     * Ensures that if the amount is set and then switches to a size that has less in stock, the amount is capped
     */
    @Test
    @Order(4)
    public void switchSizeToOverstockTest() {
        for (int i = 0; i < 7; i++) {
            clickOn("#btnAdd");
        }
        verifyThat("#lblIncrement", LabeledMatchers.hasText("8"));

        clickOn("#boxSize_150x90cm");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("5"));
    }

    /**
     * Ensures that going from a stock with no items in stock to one with some properly resets the amount box
     */
    @Test
    @Order(4)
    public void zeroToOneTest() {
        clickOn("#boxSize_240x150cm");
        verifyThat("#lblIncrement", LabeledMatchers.hasText("0"));

        clickOn("#boxSize_150x90cm");
        verifyThat("#lblIncrement", LabeledMatchers.hasText("1"));
    }

    /**
     * Checks that the toggle-switch for importing/exporting works as expected
     */
    @Test
    @Order(5)
    public void setToImportTest() {
        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
        verifyThat(tglImportExport, node -> !node.getToLeft().get());
        clickOn(tglImportExport);

        verifyThat(tglImportExport, node -> node.getToLeft().get());
        verifyThat("#btnAddToBasket", LabeledMatchers.hasText("Add to Import"));
    }

    /**
     * Ensures that imports can go over the amount in stock without being capped
     */
    @Test
    @Order(6)
    public void attemptImportingOverRestockTest() {
        clickOn("#tglImportExport");

        for (int i = 0; i < 12; i++) {
            clickOn("#btnAdd");
        }

        verifyThat("#lblIncrement", LabeledMatchers.hasText("13"));
    }

    /**
     * Checks that if the amount importing is over the held stock reverting back to exporting will cap the limit correctly
     */
    @Test
    @Order(7)
    public void attemptImportingOverRestockAndRevertTest() {
        clickOn("#tglImportExport");

        for (int i = 0; i < 12; i++) {
            clickOn("#btnAdd");
        }

        verifyThat("#lblIncrement", LabeledMatchers.hasText("13"));
        verifyThat("#btnAdd", node -> !node.isDisabled());

        clickOn("#tglImportExport");
        verifyThat("#lblIncrement", LabeledMatchers.hasText("10"));
        verifyThat("#btnAdd", Node::isDisabled);
    }

    /**
     * Ensures that items being exported have a variable price initially
     */
    @Test
    @Order(8)
    public void initialExportPriceTest() {
        Label lblPrice = lookup("#lblPrice").query();
        Label lblTotalPrice = lookup("#lblTotalPrice").query();

        String oldPrice = lblTotalPrice.getText();

        assertEquals(lblPrice.getText(), lblTotalPrice.getText());

        clickOn("#boxSize_90x60cm");
        assertEquals(lblPrice.getText(), lblTotalPrice.getText() + "+");
        assertNotEquals(lblTotalPrice.getText(), oldPrice);
    }

    /**
     * Ensures that the price increases with the amount as expected for items being exported
     */
    @Test
    @Order(9)
    public void exportPriceIncrementTest() {
        Label lblPrice = lookup("#lblPrice").query();
        Label lblTotalPrice = lookup("#lblTotalPrice").query();

        for (int i = 0; i < 4; i++) {
            clickOn("#btnAdd");
        }

        assertEquals(Double.parseDouble(lblPrice.getText().substring(1)) * 5,
                            Double.parseDouble(lblTotalPrice.getText().substring(1)));
    }

    /**
     * Ensures that the price for importing is correct relevant to the amount
     */
    @Test
    @Order(9)
    public void importPriceIncrementTest() {
        clickOn("#tglImportExport");

        Label lblPrice = lookup("#lblPrice").query();
        Label lblTotalPrice = lookup("#lblTotalPrice").query();
        Label lblCostToProduce = lookup("#lblCostToProduce").query();

        for (int i = 0; i < 4; i++) {
            clickOn("#btnAdd");
        }

        assertEquals(Double.parseDouble(lblPrice.getText().substring(1)) * 5,
                            Double.parseDouble((lblTotalPrice.getText().substring(1))));
        assertEquals(lblCostToProduce.getText(), "\u00A30.40 - (\u00A34.00)");
    }

    /**
     * Checks the pop-up appears as expected
     */
    @Test
    @Order(10)
    public void informationPopupTest() {
        clickOn("#btnMore");

        verifyThat("#boxContainment", Node::isVisible);
    }

    /**
     * Checks the buttons for changing the restock limit work as intended
     */
    @Test
    @Order(11)
    public void restockButtonsTest() {
        clickOn("#btnMore");

        clickOn("#btnAddRestock");
        verifyThat("#lblIncrementRestock", LabeledMatchers.hasText("8"));

        clickOn("#btnMinusRestock");
        verifyThat("#lblIncrementRestock", LabeledMatchers.hasText("7"));
    }

    /**
     * Ensures the restock limit cannot be set to 0
     */
    @Test
    @Order(12)
    public void setRestockToZeroTest() {
        clickOn("#btnMore");

        for (int i = 0; i < 10; i++) {
            clickOn("#btnMinusRestock");
        }
        verifyThat("#lblIncrementRestock", LabeledMatchers.hasText("1"));
        verifyThat("#btnMinusRestock", Node::isDisabled);
    }

    /**
     * Ensures the restock limit changes go through and modify other labels
     */
    @Test
    @Order(13)
    public void updateRestockTest() {
        clickOn("#btnMore");

        for (int i = 0; i < 3; i++) {
            clickOn("#btnAddRestock");
        }

        verifyThat("#lblIncrementRestock", LabeledMatchers.hasText("10"));
        clickOn("#btnUpdateStock");

        verifyThat("#lblRestock", LabeledMatchers.hasText("10"));
    }

    /**
     * Checks the file is created in the directory when printing
     */
    @Test
    @Order(14)
    public void printTest() {
        clickOn("#btnMore");

        clickOn("#btnPrint");

        assertTrue(new File("United Kingdom_Flag.txt").exists(), "File was not made");
    }

    /**
     * Ensures warnings for restocking are placed in the correct locations
     */
    @Test
    @Order(15)
    public void verifyRestockWarningTest() {
        Label lblWarningHand = lookup("#boxSize_Hand #lblRestockWarning").query();
        Label lblWarningMedium = lookup("#boxSize_150x90cm #lblRestockWarning").query();

        verifyThat(lblWarningHand, LabeledMatchers.hasText(""));
        verifyThat(lblWarningMedium, LabeledMatchers.hasText("RESTOCK"));
    }

    /**
     * Checks that returning to the stock screen works
     */
    @Test
    @Order(16)
    public void goBackTest() throws InterruptedException {
        clickOn("#btnBack");

        Thread.sleep(500);
        verifyThat("#btnFlag_AC", Node::isVisible);
    }

    /**
     * Full system test involving adding 2 UK flags of different sizes and different import/export properties to basket
     */
    @Test
    @Order(17)
    public void fullSystemTest() throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            clickOn("#btnAdd");
        }

        clickOn("#tglMaterial");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("5"));

        PlatformImpl.runAndWait(() -> clickOn("#btnAddToBasket"));

        Thread.sleep(500);

        clickOn("#enrSearch");
        write("United Kingdom");

        clickOn("#btnSearch");
        clickOn("#btnFlag_GB");

        Thread.sleep(500);

        clickOn("#boxSize_150x90cm");

        clickOn("#tglImportExport");

        for (int i = 0; i < 7; i++) {
            clickOn("#btnAdd");
        }
        clickOn("#btnMinus");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("7"));

        PlatformImpl.runAndWait(() -> {
            clickOn("#btnAddToBasket");
        });

        Thread.sleep(500);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(4, boxScroll.getChildren().size());

        clickOn("#btnCheckout");

        assertEquals(2, boxScroll.getChildren().size());
    }
}
