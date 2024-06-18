package org.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.*;
import org.testfx.framework.junit5.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.util.*;

/**
 * Super class used to populate the item screen and start it up
 */
public abstract class ItemSupplementTest extends ApplicationTest {
    private static StockItem flagHand, flagDesk, flagSmall, flagMedium, flagLarge;
    protected static DatabaseControl database = new DatabaseControl();
    protected ItemController controller;

    @Override
    public void start(Stage stage) throws Exception {
        database.openDBSession();
        setAmount();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/item_screen.fxml"));
        Parent root = loader.load();

        ItemController controller = loader.getController();
        controller.load(stage, new Operator(), new ArrayList<>(), database.getDeignFromIso("GB"), true, null);

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
        database.closeDBSession();
    }

    @BeforeAll
    public static void saveInformation() {
        database.openDBSession();
        
        flagHand = database.createFlag("GB", FLAG_SIZE.HAND).clone();
        flagDesk = database.createFlag("GB", FLAG_SIZE.DESK).clone();
        flagSmall = database.createFlag("GB", FLAG_SIZE.SMALL).clone();
        flagMedium = database.createFlag("GB", FLAG_SIZE.MEDIUM).clone();
        flagLarge = database.createFlag("GB", FLAG_SIZE.LARGE).clone();

        database.closeDBSession();
    }

    public void setAmount() {
        database.updateAmountAndRestock(173, 0, 10, 7);
        database.updateAmountAndRestock(173, 1, 8, 3);
        database.updateAmountAndRestock(173, 2, 11, 11);
        database.updateAmountAndRestock(173, 3, 5, 20);
        database.updateAmountAndRestock(173, 4, 0, 11);
    }

    @AfterAll
    public static void restoreInformation() {
        database.openDBSession();

        database.updateAmountAndRestock(173, 0, flagHand.getTotalAmount(), flagHand.getRestock());
        database.updateAmountAndRestock(173, 1, flagDesk.getTotalAmount(), flagDesk.getRestock());
        database.updateAmountAndRestock(173, 2, flagSmall.getTotalAmount(), flagSmall.getRestock());
        database.updateAmountAndRestock(173, 3, flagMedium.getTotalAmount(), flagMedium.getRestock());
        database.updateAmountAndRestock(173, 4, flagLarge.getTotalAmount(), flagLarge.getRestock());

        database.closeDBSession();
    }

    @AfterEach
    public void closeDatabase() {
        database.closeDBSession();
    }

    @Test
    @Order(0)
    public void dummyTest() { }
}
