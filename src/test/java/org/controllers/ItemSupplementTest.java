package org.controllers;

import javafx.stage.*;
import org.testfx.framework.junit5.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.util.*;

public abstract class ItemSupplementTest extends ApplicationTest {
    private static Flag flagHand, flagDesk, flagSmall, flagMedium, flagLarge;
    protected static DatabaseControl database = new DatabaseControl();

    @Override
    public void start(Stage stage) throws Exception {
        Loader l = new Loader();
        database.openDBSession();
        System.out.println("Loading the thing");
        l.showItem(stage, new ArrayList<>(), new Operator(), database.getDeignFromIso("GB"), true, null);
        database.closeDBSession();
    }

    @BeforeAll
    public static void saveInformation() {
        database.openDBSession();
        
        flagHand = database.createFlag("GB", FLAG_SIZE.HAND);
        flagDesk = database.createFlag("GB", FLAG_SIZE.DESK);
        flagSmall = database.createFlag("GB", FLAG_SIZE.SMALL);
        flagMedium = database.createFlag("GB", FLAG_SIZE.MEDIUM);
        flagLarge = database.createFlag("GB", FLAG_SIZE.LARGE);

        database.closeDBSession();
    }

    @BeforeEach
    public void setAmount() {
        database.openDBSession();

        database.updateAmountAndRestock(173, 0, 10, 7);
        database.updateAmountAndRestock(173, 1, 8, 3);
        database.updateAmountAndRestock(173, 2, 11, 11);
        database.updateAmountAndRestock(173, 3, 5, 20);
        database.updateAmountAndRestock(173, 4, 0, 11);

        database.closeDBSession();
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
}
