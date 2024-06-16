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
import org.testfx.matcher.control.ComboBoxMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerCushionTest extends ApplicationTest {
    private static StockItem cushionSmall, cushionLarge;
    protected static DatabaseControl database = new DatabaseControl();
    protected ItemController controller;

    @Override
    public void start(Stage stage) throws Exception {
        database.openDBSession();
        setAmount();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_screen.fxml"));
        Parent root = loader.load();

        ItemController controller = loader.getController();
        controller.load(stage, new Operator(), new ArrayList<>(), database.getDeignFromIso("DC"), false, null);

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
        database.closeDBSession();
    }

    @BeforeAll
    public static void saveInformation() {
        database.openDBSession();

        cushionSmall = database.createCushion("DC", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.EMPTY).clone();
        cushionLarge = database.createCushion("DC", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.EMPTY).clone();

        database.closeDBSession();
    }

    public void setAmount() {
        database.updateAmountAndRestock(442, 5, 10, 7);
        database.updateAmountAndRestock(442, 7, 11, 4);
    }

    @AfterAll
    public static void restoreInformation() {
        database.openDBSession();

        database.updateAmountAndRestock(442, 5, cushionSmall.getTotalAmount(), cushionSmall.getRestock());
        database.updateAmountAndRestock(442, 7, cushionSmall.getTotalAmount(), cushionLarge.getRestock());

        database.closeDBSession();
    }

    @AfterEach
    public void closeDatabase() {
        database.closeDBSession();
    }


    @Test
    @Order(1)
    public void setEmptyTest() {
        Label lblPrice = lookup("#lblPrice").query();
        String oldPrice = lblPrice.getText();

        ToggleSwitch tglMaterial = lookup("#tglMaterial").query();
        clickOn(tglMaterial);

        assertEquals(lblPrice.getText() + "+", oldPrice);
        verifyThat("#cmbModifications", Node::isDisabled);
        verifyThat("#btnAddToBasket", node -> !node.isDisabled());
    }

    @Test
    @Order(2)
    public void preventExportingWithoutFillingTest() {
        verifyThat("#btnAddToBasket", Node::isDisabled);
    }

    @Test
    @Order(3)
    public void fullCushionCreationTest() throws InterruptedException {
        clickOn("#boxSize_60x60cm");

        clickOn("#cmbModifications");
        clickOn("Feathers (\u00A311.00)");

        for (int i = 0; i < 4; i++) {
            clickOn("#btnAdd");
        }

        clickOn("#btnAddToBasket");

        Thread.sleep(500);

        verifyThat("#lblName", LabeledMatchers.hasText("Tristan da Cunha Cushion 60x60cm"));
        verifyThat("#lblPriceSingle", LabeledMatchers.hasText("\u00A323.60"));
        verifyThat("#lblIncrement", LabeledMatchers.hasText("5"));
        verifyThat("#lblSubtotal", LabeledMatchers.hasText("\u00A3118.00"));

        verifyThat("#lblExportSales", LabeledMatchers.hasText("\u00A3118.00"));
        verifyThat("#lblExportCosts", LabeledMatchers.hasText("\u00A335.00"));
        verifyThat("#lblExportSubtotal", LabeledMatchers.hasText("\u00A383.00"));
        verifyThat("#lblImportCosts", LabeledMatchers.hasText("\u00A30.00"));
        verifyThat("#lblTotal", LabeledMatchers.hasText("\u00A383.00"));

        clickOn("#btnInformation");

        verifyThat("#lblAdditionalVal", LabeledMatchers.hasText("Feathers (\u00A311.00)"));
    }
}
