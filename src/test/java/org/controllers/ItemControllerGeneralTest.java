package org.controllers;

import com.sun.javafx.application.PlatformImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.testfx.framework.junit5.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerGeneralTest extends ApplicationTest {
    private ItemController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_screen.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        List<StockItem> list = new ArrayList<>();
        controller.load(stage, new Operator(), list, DatabaseControl.getDeignFromIso("GB"), true, null);

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setAmount() {
        DatabaseControl.updateAmountAndRestock(173, 0, 10, 7);
        DatabaseControl.updateAmountAndRestock(173, 3, 5, 20);
        DatabaseControl.updateAmountAndRestock(173, 4, 0, 11);
    }

//    @Test
//    @Order(0)
//    public void changeSizeTest() {
//        VBox boxHand = lookup("#boxSize_Hand #boxSelect").query();
//        VBox box90 = lookup("#boxSize_90x60cm #boxSelect").query();
//        verifyThat(boxHand, Node::isVisible);
//        verifyThat(box90, node -> !node.isVisible());
//
//        Node box = lookup("#boxSize_90x60cm").query();
//        clickOn(box);
//
//        verifyThat(boxHand, node -> !node.isVisible());
//        verifyThat(box90,Node::isVisible);
//    }
//
//    @Test
//    @Order(0)
//    public void increaseCountTest() {
//        Button btnAdd = lookup("#btnAdd").query();
//        clickOn(btnAdd);
//
//        Label lblIncrement = lookup("#lblIncrement").query();
//        assertEquals("2", lblIncrement.getText());
//    }
//
//    @Test
//    @Order(2)
//    public void decreaseCountTest() {
//        Button btnAdd = lookup("#btnAdd").query();
//        clickOn(btnAdd);
//        clickOn(btnAdd);
//        clickOn(btnAdd);
//        Button btnMinus = lookup("#btnMinus").query();
//        clickOn(btnMinus);
//
//        Label lblIncrement = lookup("#lblIncrement").query();
//        assertEquals("3", lblIncrement.getText());
//    }
//
//    @Test
//    @Order(3)
//    public void attemptOverAddTest() {
//        Button btnAdd = lookup("#btnAdd").query();
//        for (int i = 0; i < 12; i++) {
//            clickOn(btnAdd);
//        }
//
//        Label lblIncrement = lookup("#lblIncrement").query();
//        assertEquals("10", lblIncrement.getText());
//        verifyThat(btnAdd, Node::isDisabled);
//    }
//
//    @Test
//    @Order(3)
//    public void attemptUnderAddTest() {
//        Button btnMinus = lookup("#btnMinus").query();
//        clickOn(btnMinus);
//        clickOn(btnMinus);
//
//        Label lblIncrement = lookup("#lblIncrement").query();
//        assertEquals("1", lblIncrement.getText());
//        verifyThat(btnMinus, Node::isDisabled);
//    }
//
//    @Test
//    @Order(4)
//    public void zeroQuantityTest() {
//        Node box = lookup("#boxSize_240x150cm").query();
//        clickOn(box);
//
//        Label lblIncrement = lookup("#lblIncrement").query();
//        assertEquals("0", lblIncrement.getText());
//
//        verifyThat("#btnAdd", Node::isDisabled);
//        verifyThat("#btnMinus", Node::isDisabled);
//    }
//
//    @Test
//    @Order(5)
//    public void setToImportTest() {
//        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
//        verifyThat(tglImportExport, node -> !node.getToLeft().get());
//        clickOn(tglImportExport);
//
//        verifyThat(tglImportExport, node -> node.getToLeft().get());
//        Button btnAddToBasket = lookup("#btnAddToBasket").query();
//        assertEquals("Add to Import", btnAddToBasket.getText());
//    }
//
//    @Test
//    @Order(6)
//    public void attemptImportingOverRestockTest() {
//        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
//        clickOn(tglImportExport);
//
//        Button btnAdd = lookup("#btnAdd").query();
//        for (int i = 0; i < 12; i++) {
//            clickOn(btnAdd);
//        }
//
//        Label lblIncrement = lookup("#lblIncrement").query();
//        assertEquals("13", lblIncrement.getText());
//    }
//
//    @Test
//    @Order(7)
//    public void attemptImportingOverRestockAndRevertTest() {
//        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
//        clickOn(tglImportExport);
//
//        Button btnAdd = lookup("#btnAdd").query();
//        for (int i = 0; i < 12; i++) {
//            clickOn(btnAdd);
//        }
//
//        Label lblIncrement = lookup("#lblIncrement").query();
//        assertEquals("13", lblIncrement.getText());
//        verifyThat(btnAdd, node -> !node.isDisabled());
//
//        clickOn(tglImportExport);
//        assertEquals("10", lblIncrement.getText());
//        verifyThat(btnAdd, Node::isDisabled);
//    }
//
//    @Test
//    @Order(8)
//    public void initialExportPriceTest() {
//        Label lblPrice = lookup("#lblPrice").query();
//        Label lblTotalPrice = lookup("#lblTotalPrice").query();
//
//        String oldPrice = lblTotalPrice.getText();
//
//        assertEquals(lblPrice.getText(), lblTotalPrice.getText());
//
//        Node box = lookup("#boxSize_90x60cm").query();
//        clickOn(box);
//        assertEquals(lblPrice.getText(), lblTotalPrice.getText() + "+");
//        assertNotEquals(lblTotalPrice.getText(), oldPrice);
//    }
//
//    @Test
//    @Order(9)
//    public void exportPriceIncrementTest() {
//        Label lblPrice = lookup("#lblPrice").query();
//        Label lblTotalPrice = lookup("#lblTotalPrice").query();
//
//        Button btnAdd = lookup("#btnAdd").query();
//        for (int i = 0; i < 4; i++) {
//            clickOn(btnAdd);
//        }
//
//        assertEquals(Double.parseDouble(lblPrice.getText().substring(1)) * 5, Double.parseDouble(lblTotalPrice.getText().substring(1)));
//    }
//
//    @Test
//    @Order(9)
//    public void importPriceIncrementTest() {
//        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
//        clickOn(tglImportExport);
//
//        Label lblPrice = lookup("#lblPrice").query();
//        Label lblTotalPrice = lookup("#lblTotalPrice").query();
//        Label lblCostToProduce = lookup("#lblCostToProduce").query();
//
//        Button btnAdd = lookup("#btnAdd").query();
//        for (int i = 0; i < 4; i++) {
//            clickOn(btnAdd);
//        }
//
//        assertEquals(Double.parseDouble(lblPrice.getText().substring(1)) * 5, Double.parseDouble((lblTotalPrice.getText().substring(1))));
//        assertEquals(lblCostToProduce.getText(), lblPrice.getText());
//    }
//
//    @Test
//    @Order(10)
//    public void goBackTest() throws InterruptedException {
//        Button btnBack = lookup("#btnBack").query();
//        clickOn(btnBack);
//
//        Thread.sleep(3000);
//        verifyThat("#btnFlag_AC", Node::isVisible);
//    }

    @Test
    @Order(11)
    public void fullIntegrationTest() throws InterruptedException {
        Button btnAdd = lookup("#btnAdd").query();
        for (int i = 0; i < 4; i++) {
            clickOn(btnAdd);
        }

        ToggleSwitch tglMaterial = lookup("#tglMaterial").query();
        clickOn(tglMaterial);

        Label lblIncrement = lookup("#lblIncrement").query();
        assertEquals("5", lblIncrement.getText());

        PlatformImpl.runAndWait(() -> {
                    Button btnAddToBasket = lookup("#btnAddToBasket").query();
                    clickOn(btnAddToBasket);
        });

        Thread.sleep(1000);

        TextField searchBar = lookup("#enrSearch").query();
        clickOn(searchBar);
        write("United Kingdom");

        Button btnSearch = lookup("#btnSearch").query();
        clickOn(btnSearch);
        Button btnFlag_GB = lookup("#btnFlag_GB").query();
        clickOn(btnFlag_GB);

        Thread.sleep(1000);

        Node box = lookup("#boxSize_150x90cm").query();
        clickOn(box);

        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
        clickOn(tglImportExport);

        btnAdd = lookup("#btnAdd").query();
        for (int i = 0; i < 7; i++) {
            clickOn(btnAdd);
        }
        Button btnMinus = lookup("#btnMinus").query();
        clickOn(btnMinus);

        lblIncrement = lookup("#lblIncrement").query();
        assertEquals("7", lblIncrement.getText());

        PlatformImpl.runAndWait(() -> {
            Button btnAddToBasket = lookup("#btnAddToBasket").query();
            clickOn(btnAddToBasket);
        });

        Thread.sleep(1000);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(4, boxScroll.getChildren().size());

        Button btnCheckout = lookup("#btnCheckout").query();
        clickOn(btnCheckout);

        assertEquals(2, boxScroll.getChildren().size());
    }
}
