package org.controllers;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.testfx.matcher.control.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Tests the functionality of {@code BasketController} and {@code ItemController} through ensuring the edit button works
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasketControllerEditTest extends BasketSupplement {
    /**
     * Ensures that all information is correctly displayed when going to edit an item being imported
     */
    @Test
    @Order(1)
    public void editImportTest() {
        int index = controller.getItems(0).hashCode();
        Button btnEdit = lookup("#" + index + " #btnEdit").query();

        clickOn(btnEdit);

        verifyThat("#lblDesignName", LabeledMatchers.hasText("Eritrea"));
        verifyThat("#lblTags", LabeledMatchers.hasText("Africa\nInternational"));

        VBox boxSelect = lookup("#boxSize_60x60cm #boxSelect").query();
        verifyThat(boxSelect, Node::isVisible);

        verifyThat("#tglMaterial", Node::isDisabled);
        verifyThat("#cmbModifications", Node::isDisabled);

        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
        assertTrue(tglImportExport.getToLeft().get());

        verifyThat("#lblIncrement", LabeledMatchers.hasText("3"));
        verifyThat("#lblTotalPrice", LabeledMatchers.hasText("\u00A321.00"));
        verifyThat("#lblPrice", LabeledMatchers.hasText("\u00A37.00"));

        verifyThat("#lblCurrentStock", LabeledMatchers.hasText("5"));
        verifyThat("#lblRestock", LabeledMatchers.hasText("7"));
        verifyThat("#lblCostToProduce", LabeledMatchers.hasText("\u00A37.00 - (\u00A335.00)"));
    }

    /**
     * Ensures that all information is correctly displayed when going to edit an item being exported
     */
    @Test
    @Order(2)
    public void editExportTest() {
        scrollToExport(4, controller.getItems());

        int index = controller.getItems(4).hashCode();
        Button btnEdit = lookup("#" + index + " #btnEdit").query();
        clickOn(btnEdit);

        verifyThat("#lblDesignName", LabeledMatchers.hasText("Devon"));
        verifyThat("#lblTags", LabeledMatchers.hasText("Europe\nNational"));

        VBox boxSelect = lookup("#boxSize_90x60cm #boxSelect").query();
        verifyThat(boxSelect, Node::isVisible);

        ToggleSwitch tglMaterial = lookup("#tglMaterial").query();
        verifyThat(tglMaterial, node -> !node.isDisabled());
        assertFalse(tglMaterial.getToLeft().get());

        ComboBox<String> comboBox = lookup("#cmbModifications").query();
        String selectedItem = comboBox.getSelectionModel().getSelectedItem();
        assertEquals(selectedItem, "Wooden Toggles (\u00A35.00)");

        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
        assertFalse(tglImportExport.getToLeft().get());

        verifyThat("#lblIncrement", LabeledMatchers.hasText("8"));
        verifyThat("#lblTotalPrice", LabeledMatchers.hasText("\u00A3107.20"));
        verifyThat("#lblPrice", LabeledMatchers.hasText("\u00A313.40"));

        //The amount already in basket should have been added to the previous amount
        verifyThat("#lblCurrentStock", LabeledMatchers.hasText("20"));
        verifyThat("#lblRestock", LabeledMatchers.hasText("4"));
        verifyThat("#lblCostToProduce", LabeledMatchers.hasText("\u00A31.00 - (\u00A320.00)"));
    }
}
