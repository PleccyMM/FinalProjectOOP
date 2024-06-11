package org.controllers;

import com.sun.javafx.application.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.testfx.matcher.control.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasketControllerTest extends BasketSupplement {
    @Test
    @Order(1)
    public void displayTest() {
        VBox boxScroll = lookup("#boxScroll").query();

        assertEquals(boxScroll.getChildren().size(), controller.getItems().size() + 2);
    }

    @Test
    @Order(2)
    public void increaseDecreaseAmountImportTest() {
        int index = controller.getItems(0).hashCode();
        Button btnAdd = lookup("#" + index + " #btnAdd").query();
        Button btnMinus = lookup("#" + index + " #btnMinus").query();
        Label lblIncrement = lookup("#" + index + " #lblIncrement").query();
        Label lblSubtotal = lookup("#" + index + " #lblSubtotal").query();

        verifyThat(lblIncrement, LabeledMatchers.hasText("3"));
        verifyThat(lblSubtotal, LabeledMatchers.hasText("\u00A321.00"));

        clickOn(btnAdd);
        verifyThat(lblIncrement, LabeledMatchers.hasText("4"));
        verifyThat(lblSubtotal, LabeledMatchers.hasText("\u00A328.00"));

        clickOn(btnMinus);
        verifyThat(lblIncrement, LabeledMatchers.hasText("3"));
        verifyThat(lblSubtotal, LabeledMatchers.hasText("\u00A321.00"));

        clickOn(btnMinus);
        verifyThat(lblIncrement, LabeledMatchers.hasText("2"));
        verifyThat(lblSubtotal, LabeledMatchers.hasText("\u00A314.00"));
    }

    @Test
    @Order(3)
    public void increaseAmountAboveStockImportTest() {
        int index = controller.getItems(0).hashCode();
        Button btnAdd = lookup("#" + index + " #btnAdd").query();
        Label lblIncrement = lookup("#" + index + " #lblIncrement").query();

        verifyThat(lblIncrement, LabeledMatchers.hasText("3"));

        for (int i = 0; i < 7; i++) {
            clickOn(btnAdd);
        }
        verifyThat(lblIncrement, LabeledMatchers.hasText("10"));
    }

    @Test
    @Order(4)
    public void removeTopImportTest() {
        int index = controller.getItems(0).hashCode();
        Button btnMinus = lookup("#" + index + " #btnMinus").query();
        Label lblIncrement = lookup("#" + index + " #lblIncrement").query();

        verifyThat(lblIncrement, LabeledMatchers.hasText("3"));

        for (int i = 0; i < 3; i++) {
            clickOn(btnMinus);
        }

        boolean foundHash = false;
        for (StockItem item : controller.getItems()) {
            if (index == item.hashCode()) {
                foundHash = true;
                break;
            }
        }
        assertFalse(foundHash);

        try {
            lookup("#" + index).query();
            fail("The box was still interface-able, meaning it was not removed");
        } catch (Exception ignored) { }
    }

    @Test
    @Order(5)
    public void removeImportThenAroundItBelowTest() {
        int[] index = new int[3];
        Button[] btnMinus = new Button[3];
        Label[] lblIncrement = new Label[3];
        for (int i = 0; i < index.length; i++) {
            index[i] = controller.getItems(i).hashCode();
            btnMinus[i] = lookup("#" + index[i] + " #btnMinus").query();
            lblIncrement[i] = lookup("#" + index[i] + " #lblIncrement").query();
        }

        verifyThat(lblIncrement[1], LabeledMatchers.hasText("7"));
        for (int i = 0; i < 7; i++) {
            clickOn(btnMinus[1]);
        }

        verifyThat(lblIncrement[2], LabeledMatchers.hasText("5"));
        for (int i = 0; i < 5; i++) {
            clickOn(btnMinus[2]);
        }

        verifyThat(lblIncrement[0], LabeledMatchers.hasText("3"));
        for (int i = 0; i < 3; i++) {
            clickOn(btnMinus[0]);
        }

        boolean foundHash = false;
        for (StockItem item : controller.getItems()) {
            if (List.of(index).contains(item.hashCode())) {
                foundHash = true;
                break;
            }
        }
        assertFalse(foundHash);

        for (int i = 0; i < index.length; i++) {
            try {
                lookup("#" + index[i]).query();
                fail("The box was still interface-able, meaning it was not removed");
            } catch (Exception ignored) {}
        }
    }

    @Test
    @Order(6)
    public void increaseDecreaseAmountExportTest() {
        scrollToExport(4, controller.getItems());

        int index = controller.getItems(4).hashCode();
        Button btnAdd = lookup("#" + index + " #btnAdd").query();
        Button btnMinus = lookup("#" + index + " #btnMinus").query();
        Label lblIncrement = lookup("#" + index + " #lblIncrement").query();
        Label lblSubtotal = lookup("#" + index + " #lblSubtotal").query();

        verifyThat(lblIncrement, LabeledMatchers.hasText("8"));
        verifyThat(lblSubtotal, LabeledMatchers.hasText("\u00A3107.20"));

        clickOn(btnAdd);
        verifyThat(lblIncrement, LabeledMatchers.hasText("9"));
        verifyThat(lblSubtotal, LabeledMatchers.hasText("\u00A3120.60"));

        clickOn(btnMinus);
        verifyThat(lblIncrement, LabeledMatchers.hasText("8"));
        verifyThat(lblSubtotal, LabeledMatchers.hasText("\u00A3107.20"));

        clickOn(btnMinus);
        verifyThat(lblIncrement, LabeledMatchers.hasText("7"));
        verifyThat(lblSubtotal, LabeledMatchers.hasText("\u00A393.80"));
    }

    @Test
    @Order(7)
    public void removeTopExportTest() {
        scrollToExport(4, controller.getItems());

        int index = controller.getItems(4).hashCode();
        Button btnMinus = lookup("#" + index + " #btnMinus").query();
        Label lblIncrement = lookup("#" + index + " #lblIncrement").query();

        verifyThat(lblIncrement, LabeledMatchers.hasText("8"));

        for (int i = 0; i < 8; i++) {
            clickOn(btnMinus);
        }

        boolean foundHash = false;
        for (StockItem item : controller.getItems()) {
            if (index == item.hashCode()) {
                foundHash = true;
                break;
            }
        }
        assertFalse(foundHash);

        try {
            lookup("#" + index).query();
            fail("The box was still interface-able, meaning it was not removed");
        } catch (Exception ignored) { }
    }

    @Test
    @Order(7)
    public void attemptToOverExportTest() {
        scrollToExport(4, controller.getItems());

        int index = controller.getItems(4).hashCode();
        Button btnAdd = lookup("#" + index + " #btnAdd").query();
        Label lblIncrement = lookup("#" + index + " #lblIncrement").query();

        for (int i = 0; i < 15; i++) {
            clickOn(btnAdd);
        }

        verifyThat(lblIncrement, LabeledMatchers.hasText("20"));
    }

    @Test
    @Order(8)
    public void infoTest() {
        scrollToExport(4, controller.getItems());

        int index = controller.getItems(4).hashCode();
        Button btnInformation = lookup("#" + index + " #btnInformation").query();
        clickOn(btnInformation);

        verifyThat("#lblAdditionalVal", LabeledMatchers.hasText("Wooden Toggles (\u00A35.00)"));
        verifyThat("#lblMaterialVal", LabeledMatchers.hasText("Nylon"));
    }

    @Test
    @Order(20)
    public void checkoutTest() {
        clickOn("#btnCheckout");

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(2, boxScroll.getChildren().size());

        verifyThat("#lblTotal", LabeledMatchers.hasText("\u00A30.00"));
    }
}
