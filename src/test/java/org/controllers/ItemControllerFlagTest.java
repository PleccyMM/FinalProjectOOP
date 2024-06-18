package org.controllers;

import javafx.scene.*;
import javafx.scene.control.*;
import org.testfx.matcher.control.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Tests some functionality of {@code ItemController} specific to flags
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerFlagTest extends ItemSupplementTest {

    /**
     * Checks that the price changes upon changing the material selected
     */
    @Test
    @Order(1)
    public void materialPriceTest() {
        Label lblTotalPrice = lookup("#lblTotalPrice").query();
        String oldPrice = lblTotalPrice.getText();

        ToggleSwitch tglMaterial = lookup("#tglMaterial").query();
        clickOn(tglMaterial);

        assertNotEquals(lblTotalPrice.getText(), oldPrice);
    }

    /**
     * Ensures that material selection is disabled when importing
     */
    @Test
    @Order(1)
    public void materialDisableTest() {
        clickOn("#tglImportExport");

        verifyThat("#tglMaterial", Node::isDisabled);
    }

    /**
     * Ensures larger flags cannot be exported without a hoist selected
     */
    @Test
    @Order(2)
    public void preventExportWithoutHoistTest() {
        clickOn("#boxSize_90x60cm");

        verifyThat("#btnAddToBasket", Node::isDisabled);

        clickOn("#cmbModifications");
        clickOn("Fabric Rings (\u00A30.50)");

        verifyThat("#cmbModifications", ComboBoxMatchers.hasSelectedItem("Fabric Rings (\u00A30.50)"));
    }

    /**
     * Tests the creation of a flag in full
     */
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

        verifyThat("#lblName", LabeledMatchers.hasText("United Kingdom Flag 150x90cm"));
        verifyThat("#lblPriceSingle", LabeledMatchers.hasText("\u00A315.20"));
        verifyThat("#lblIncrement", LabeledMatchers.hasText("3"));
        verifyThat("#lblSubtotal", LabeledMatchers.hasText("\u00A345.60"));

        verifyThat("#lblExportSales", LabeledMatchers.hasText("\u00A345.60"));
        verifyThat("#lblExportCosts", LabeledMatchers.hasText("\u00A34.50"));
        verifyThat("#lblExportSubtotal", LabeledMatchers.hasText("\u00A341.10"));
        verifyThat("#lblImportCosts", LabeledMatchers.hasText("\u00A30.00"));
        verifyThat("#lblTotal", LabeledMatchers.hasText("\u00A341.10"));

        clickOn("#btnInformation");

        verifyThat("#lblAdditionalVal", LabeledMatchers.hasText("Wooden Toggles (\u00A35.00)"));
        verifyThat("#lblMaterialVal", LabeledMatchers.hasText("Nylon"));
    }
}
