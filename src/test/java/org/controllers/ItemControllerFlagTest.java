package org.controllers;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.testfx.framework.junit5.*;
import org.testfx.matcher.control.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerFlagTest extends ItemSupplementTest {

    @Test
    @Order(1)
    public void materialPriceTest() {
        Label lblTotalPrice = lookup("#lblTotalPrice").query();
        String oldPrice = lblTotalPrice.getText();

        ToggleSwitch tglMaterial = lookup("#tglMaterial").query();
        clickOn(tglMaterial);

        assertNotEquals(lblTotalPrice.getText(), oldPrice);
    }

    @Test
    @Order(1)
    public void materialDisableTest() {
        clickOn("#tglImportExport");

        verifyThat("#tglMaterial", Node::isDisabled);
    }

    @Test
    @Order(2)
    public void preventExportWithoutHoistTest() {
        clickOn("#boxSize_90x60cm");

        verifyThat("#btnAddToBasket", Node::isDisabled);

        clickOn("#cmbModifications");
        clickOn("Fabric Rings (\u00A30.50)");

        verifyThat("#cmbModifications", ComboBoxMatchers.hasSelectedItem("Fabric Rings (\u00A30.50)"));
    }

    @Test
    @Order(3)
    public void fullFlagCreationTest() throws InterruptedException {
        clickOn("#boxSize_150x90cm");

        clickOn("#cmbModifications");
        clickOn("Wooden Toggles (\u00A35.00)");

        clickOn("#tglMaterial");

        clickOn("#btnAdd");
        clickOn("#btnAdd");

        clickOn("#btnAddToBasket");

        Thread.sleep(500);
    }
}
