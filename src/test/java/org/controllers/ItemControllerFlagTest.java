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
public class ItemControllerFlagTest extends ApplicationTest {
    private ItemController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_screen.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.load(stage, new Operator(), Collections.emptyList(), DatabaseControl.getDeignFromIso("GB"), true, null);

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
        ToggleSwitch tglImportExport = lookup("#tglImportExport").query();
        clickOn(tglImportExport);

        verifyThat("#tglMaterial", Node::isDisabled);
    }

    @Test
    @Order(2)
    public void preventExportWithoutHoistTest() {
        Node box = lookup("#boxSize_90x60cm").query();
        clickOn(box);

        verifyThat("#btnAddToBasket", Node::isDisabled);

        ComboBox<?> cmbModifications = lookup("#cmbModifications").query();
        clickOn(cmbModifications);
        clickOn("Fabric Rings (\u00A30.50)");

        verifyThat("#cmbModifications", ComboBoxMatchers.hasSelectedItem("Fabric Rings (\u00A30.50)"));
    }
}
