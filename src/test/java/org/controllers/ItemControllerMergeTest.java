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
public class ItemControllerMergeTest extends ItemSupplementTest {
    @Order(1)
    @Test
    public void basicImportMergeTest() {
        clickOn("#tglImportExport");
        for (int i = 0; i < 4; i++) {
            clickOn("#btnAdd");
        }
        clickOn("#btnAddToBasket");

        clickOn("#enrSearch");
        write("United Kingdom");
        clickOn("#btnSearch");
        clickOn("#btnFlag_GB");

        clickOn("#tglImportExport");
        for (int i = 0; i < 2; i++) {
            clickOn("#btnAdd");
        }
        clickOn("#btnAddToBasket");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("8"));
    }

    @Order(2)
    @Test
    public void basicExportMergeTest() {
        for (int i = 0; i < 2; i++) {
            clickOn("#btnAdd");
        }
        clickOn("#btnAddToBasket");

        clickOn("#enrSearch");
        write("United Kingdom");
        clickOn("#btnSearch");
        clickOn("#btnFlag_GB");

        clickOn("#btnAdd");

        clickOn("#btnAddToBasket");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("5"));
    }

    @Order(3)
    @Test
    public void exportMergeExclusionTest() {
        clickOn("#boxSize_150x90cm");
        clickOn("#cmbModifications");
        clickOn("Fabric Rings (\u00A30.50)");
        clickOn("#btnAddToBasket");

        clickOn("#enrSearch");
        write("United Kingdom");
        clickOn("#btnSearch");
        clickOn("#btnFlag_GB");

        clickOn("#boxSize_150x90cm");
        clickOn("#cmbModifications");
        clickOn("None (\u00A30.00)");
        clickOn("#tglMaterial");
        clickOn("#btnAddToBasket");

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(4, boxScroll.getChildren().size());
    }

    @Order(4)
    @Test
    public void editExportMergeTest() {
        exportMergeExclusionTest();

        Flag flag = new Flag();
        flag.setStockID(173);
        flag.setName("United Kingdom");
        flag.setSizeID(3);
        flag.setAmount(1);
        flag.setMaterial(FLAG_MATERIAL.NYLON);
        flag.setHoist(FLAG_HOIST.NONE);

        Button btnEdit = lookup("#" + flag.hashCode() + " #btnEdit").query();
        clickOn(btnEdit);

        clickOn("#cmbModifications");
        clickOn("Fabric Rings (\u00A30.50)");
        clickOn("#tglMaterial");
        clickOn("#btnAddToBasket");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("2"));
        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(3, boxScroll.getChildren().size());
    }

    @Order(5)
    @Test
    public void editImportMergeTest() {
        clickOn("#boxSize_90x60cm");
        clickOn("#tglImportExport");
        for (int i = 0; i < 3; i++) {
            clickOn("#btnAdd");
        }
        clickOn("#btnAddToBasket");

        clickOn("#enrSearch");
        write("United Kingdom");
        clickOn("#btnSearch");
        clickOn("#btnFlag_GB");

        clickOn("#tglImportExport");
        for (int i = 0; i < 2; i++) {
            clickOn("#btnAdd");
        }
        clickOn("#btnAddToBasket");

        Flag flag = new Flag();
        flag.setStockID(173);
        flag.setName("United Kingdom");
        flag.setSizeID(2);
        flag.setAmount(-1);
        flag.setMaterial(FLAG_MATERIAL.POLYESTER);
        flag.setHoist(FLAG_HOIST.NONE);

        Button btnEdit = lookup("#" + flag.hashCode() + " #btnEdit").query();
        clickOn(btnEdit);

        clickOn("#boxSize_Hand");
        clickOn("#btnAddToBasket");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("7"));
        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(3, boxScroll.getChildren().size());
    }

    private void initalSwitch() {
        clickOn("#boxSize_90x60cm");
        for (int i = 0; i < 2; i++) {
            clickOn("#btnAdd");
        }
        clickOn("#cmbModifications");
        clickOn("Metal Rings (\u00A32.50)");
        clickOn("#btnAddToBasket");

        clickOn("#enrSearch");
        write("United Kingdom");
        clickOn("#btnSearch");
        clickOn("#btnFlag_GB");

        clickOn("#boxSize_Desk");
        clickOn("#tglImportExport");
        clickOn("#btnAdd");
        clickOn("#btnAddToBasket");
    }

    @Order(6)
    @Test
    public void switchToExportTest() {
        initalSwitch();

        Flag flag = new Flag();
        flag.setStockID(173);
        flag.setName("United Kingdom");
        flag.setSizeID(1);
        flag.setAmount(-1);
        flag.setMaterial(FLAG_MATERIAL.PAPER);
        flag.setHoist(FLAG_HOIST.NONE);

        Button btnEdit = lookup("#" + flag.hashCode() + " #btnEdit").query();
        clickOn(btnEdit);

        clickOn("#boxSize_90x60cm");
        clickOn("#tglImportExport");
        clickOn("#cmbModifications");
        clickOn("Metal Rings (\u00A32.50)");
        clickOn("#btnAddToBasket");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("5"));
        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(3, boxScroll.getChildren().size());
    }

    @Order(7)
    @Test
    public void switchToImportTest() {
        initalSwitch();

        Flag flag = new Flag();
        flag.setStockID(173);
        flag.setName("United Kingdom");
        flag.setSizeID(2);
        flag.setAmount(1);
        flag.setMaterial(FLAG_MATERIAL.POLYESTER);
        flag.setHoist(FLAG_HOIST.METAL);

        Button btnEdit = lookup("#" + flag.hashCode() + " #btnEdit").query();
        clickOn(btnEdit);

        clickOn("#boxSize_Desk");
        clickOn("#tglImportExport");
        clickOn("#btnAddToBasket");

        verifyThat("#lblIncrement", LabeledMatchers.hasText("5"));
        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(3, boxScroll.getChildren().size());
    }
}
