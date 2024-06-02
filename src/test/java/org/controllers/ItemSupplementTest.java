package org.controllers;

import javafx.stage.*;
import org.testfx.framework.junit5.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import java.util.*;

public abstract class ItemSupplementTest extends ApplicationTest {
    private static Flag flagHand, flagDesk, flagSmall, flagMedium, flagLarge;

    @Override
    public void start(Stage stage) throws Exception {
        Loader l = new Loader();
        l.showItem(stage, Collections.emptyList(), new Operator(), DatabaseControl.getDeignFromIso("GB"), true, null);
    }

    @BeforeAll
    public static void saveInformation() {
        flagHand = DatabaseControl.createFlag("GB", FLAG_SIZE.HAND);
        flagDesk = DatabaseControl.createFlag("GB", FLAG_SIZE.DESK);
        flagSmall = DatabaseControl.createFlag("GB", FLAG_SIZE.SMALL);
        flagMedium = DatabaseControl.createFlag("GB", FLAG_SIZE.MEDIUM);
        flagLarge = DatabaseControl.createFlag("GB", FLAG_SIZE.LARGE);
    }

    @BeforeEach
    public void setAmount() {
        DatabaseControl.updateAmountAndRestock(173, 0, 10, 7);
        DatabaseControl.updateAmountAndRestock(173, 1, 8, 3);
        DatabaseControl.updateAmountAndRestock(173, 2, 11, 11);
        DatabaseControl.updateAmountAndRestock(173, 3, 5, 20);
        DatabaseControl.updateAmountAndRestock(173, 4, 0, 11);
    }

    @AfterAll
    public static void restoreInformation() {
        DatabaseControl.updateAmountAndRestock(173, 0, flagHand.getTotalAmount(), flagHand.getRestock());
        DatabaseControl.updateAmountAndRestock(173, 1, flagDesk.getTotalAmount(), flagDesk.getRestock());
        DatabaseControl.updateAmountAndRestock(173, 2, flagSmall.getTotalAmount(), flagSmall.getRestock());
        DatabaseControl.updateAmountAndRestock(173, 3, flagMedium.getTotalAmount(), flagMedium.getRestock());
        DatabaseControl.updateAmountAndRestock(173, 4, flagLarge.getTotalAmount(), flagLarge.getRestock());
    }
}
