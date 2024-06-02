package org.controllers;

import com.sun.javafx.application.PlatformImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.testfx.framework.junit5.*;
import org.testfx.matcher.control.LabeledMatchers;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerGeneralTest extends ItemSupplementTest {
    @AfterAll
    public static void deleteFile() {
        File f = new File("United Kingdom_0.txt");
        f.delete();
        database.closeDBSession();
    }

    @Test
    @Order(0)
    public void dummyTest() {

    }


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

    @Test
    @Order(1)
    public void increaseCountTest() {
        clickOn("#btnAdd");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("2"));
    }

    @Test
    @Order(2)
    public void decreaseCountTest() {
        clickOn("#btnAdd");
        clickOn("#btnAdd");
        clickOn("#btnAdd");
        clickOn("#btnMinus");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("3"));
    }

    @Test
    @Order(3)
    public void attemptOverAddTest() {
        for (int i = 0; i < 12; i++) {
            clickOn("#btnAdd");
        }

        verifyThat("#lblIncrement", LabeledMatchers.hasText("10"));
        verifyThat("#btnAdd", Node::isDisabled);
    }

    @Test
    @Order(3)
    public void attemptUnderAddTest() {
        clickOn("#btnMinus");
        clickOn("#btnMinus");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("1"));
        verifyThat("#btnMinus", Node::isDisabled);
    }

    @Test
    @Order(4)
    public void zeroQuantityTest() {
        clickOn("#boxSize_240x150cm");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("0"));
        verifyThat("#btnAdd", Node::isDisabled);
        verifyThat("#btnMinus", Node::isDisabled);
    }

    @Test
    @Order(5)
    public void setToImportTest() {
        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
        verifyThat(tglImportExport, node -> !node.getToLeft().get());
        clickOn(tglImportExport);

        verifyThat(tglImportExport, node -> node.getToLeft().get());
        verifyThat("#btnAddToBasket", LabeledMatchers.hasText("Add to Import"));
    }

    @Test
    @Order(6)
    public void attemptImportingOverRestockTest() {
        clickOn("#tglImportExport");

        for (int i = 0; i < 12; i++) {
            clickOn("#btnAdd");
        }

        verifyThat("#lblIncrement", LabeledMatchers.hasText("13"));
    }

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
        assertEquals(lblCostToProduce.getText(), lblPrice.getText());
    }

    @Test
    @Order(10)
    public void informationPopupTest() {
        clickOn("#btnMore");

        verifyThat("#boxContainment", Node::isVisible);
    }

    @Test
    @Order(11)
    public void restockButtonsTest() {
        clickOn("#btnMore");

        clickOn("#btnAddRestock");
        verifyThat("#lblIncrementRestock", LabeledMatchers.hasText("8"));

        clickOn("#btnMinusRestock");
        verifyThat("#lblIncrementRestock", LabeledMatchers.hasText("7"));
    }

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

    @Test
    @Order(15)
    public void printTest() {
        clickOn("#btnMore");

        clickOn("#btnPrint");

        assertTrue(new File("United Kingdom_0.txt").exists(), "File was not made");
    }

    @Test
    @Order(16)
    public void goBackTest() throws InterruptedException {
        clickOn("#btnBack");

        Thread.sleep(500);
        verifyThat("#btnFlag_AC", Node::isVisible);
    }

    @Test
    @Order(17)
    public void fullIntegrationTest() throws InterruptedException {
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
