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
public class BasketControllerTest extends ApplicationTest {
    private BasketController controller;
    private Stage stage;
    private final List<StockItem> items = new ArrayList<>();

    private static final List<StockItem> stockItems = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("basket_screen.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.load(stage, items, new Operator());

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    public static void saveStockInformation() {
        stockItems.add(DatabaseControl.createFlag("GI", FLAG_SIZE.SMALL));
        stockItems.add(DatabaseControl.createFlag("PR-TG", FLAG_SIZE.LARGE));
        stockItems.add(DatabaseControl.createFlag("CY-DE", FLAG_SIZE.SMALL));
        stockItems.add(DatabaseControl.createFlag("MU", FLAG_SIZE.DESK));
        stockItems.add(DatabaseControl.createFlag("PN", FLAG_SIZE.MEDIUM));

        stockItems.add(DatabaseControl.createCushion("TS", CUSHION_SIZE.LONG, CUSHION_MATERIAL.FEATHERS));
        stockItems.add(DatabaseControl.createCushion("ER", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.EMPTY));
        stockItems.add(DatabaseControl.createCushion("CY-MO", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.COTTON));
        stockItems.add(DatabaseControl.createCushion("MO", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.EMPTY));
    }

    @AfterAll
    public static void restoreStockInformation() {
        for (StockItem item : stockItems) {
            DatabaseControl.updateAmountAndRestock(item.getStockID(), item.getSizeID(), item.getTotalAmount(), item.getRestock());
        }
    }

    @BeforeEach
    public void setUpItems() {
        DatabaseControl.updateAmountAndRestock(stockItems.get(0).getStockID(), stockItems.get(0).getSizeID(), 11, 4);
        Flag flagSmallGibraltar = (Flag) stockItems.get(0).clone();
        flagSmallGibraltar.setAmount(3);
        flagSmallGibraltar.setHoist(FLAG_HOIST.NONE);
        flagSmallGibraltar.setMaterial(FLAG_MATERIAL.POLYESTER);

        DatabaseControl.updateAmountAndRestock(stockItems.get(1).getStockID(), stockItems.get(1).getSizeID(), 11, 7);
        Flag flagLargeTrans = (Flag) stockItems.get(1).clone();
        flagLargeTrans.setAmount(-7);

        Flag flagSmallDevon = (Flag) stockItems.get(2).clone();
        flagSmallDevon.setAmount(8);
        flagSmallDevon.setHoist(FLAG_HOIST.WOODEN);
        flagSmallDevon.setMaterial(FLAG_MATERIAL.NYLON);

        Flag flagDeskMauritius = (Flag) stockItems.get(3).clone();
        flagDeskMauritius.setAmount(2);
        flagDeskMauritius.setMaterial(FLAG_MATERIAL.PAPER);

        Flag flagMediumPitcairn = (Flag) stockItems.get(4).clone();
        flagMediumPitcairn.setAmount(-5);

        Cushion cushionLongTransnistria = (Cushion) stockItems.get(5).clone();
        cushionLongTransnistria.setAmount(1);

        Cushion cushionLargeEritrea = (Cushion) stockItems.get(6).clone();
        cushionLargeEritrea.setAmount(-3);

        Cushion cushionLargeMonmouthshire = (Cushion) stockItems.get(7).clone();
        cushionLargeMonmouthshire.setAmount(5);

        Cushion cushionSmallMacao = (Cushion) stockItems.get(8).clone();
        cushionSmallMacao.setAmount(-7);

        items.clear();
        items.add(flagSmallGibraltar);
        items.add(flagLargeTrans);
        items.add(flagSmallDevon);
        items.add(flagDeskMauritius);
        items.add(flagMediumPitcairn);
        items.add(cushionLongTransnistria);
        items.add(cushionLargeEritrea);
        items.add(cushionLargeMonmouthshire);
        items.add(cushionSmallMacao);

        PlatformImpl.runAndWait(() -> controller.load(stage, items, new Operator()));
    }

    @Test
    @Order(0)
    public void testSuite() throws InterruptedException {
        //Thread.sleep(100000);
    }

    @Test
    @Order(1)
    public void displayTest() {
        VBox boxScroll = lookup("#boxScroll").query();

        assertEquals(boxScroll.getChildren().size(), items.size() + 2);
    }

    @Test
    @Order(2)
    public void increaseDecreaseAmountImport() {
        Button btnAdd = lookup("#1 #btnAdd").query();
        Button btnMinus = lookup("#1 #btnMinus").query();
        Label lblIncrement = lookup("#1 #lblIncrement").query();

        verifyThat(lblIncrement, LabeledMatchers.hasText("7"));

        clickOn(btnAdd);
        verifyThat(lblIncrement, LabeledMatchers.hasText("8"));

        clickOn(btnMinus);
        verifyThat(lblIncrement, LabeledMatchers.hasText("7"));
    }

    @Test
    @Order(3)
    public void increaseAmountAboveStockImport() {
        Button btnAdd = lookup("#1 #btnAdd").query();
        Label lblIncrement = lookup("#1 #lblIncrement").query();

        verifyThat(lblIncrement, LabeledMatchers.hasText("7"));

        for (int i = 0; i < 7; i++) {
            clickOn(btnAdd);
        }
        verifyThat(lblIncrement, LabeledMatchers.hasText("14"));
    }
}
