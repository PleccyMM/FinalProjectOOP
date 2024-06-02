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

public abstract class ItemSupplementTest extends ApplicationTest {
    private static Flag flagHand, flagMedium, flagLarge;

    @Override
    public void start(Stage stage) throws Exception {
        Loader l = new Loader();
        l.showItem(stage, Collections.emptyList(), new Operator(), DatabaseControl.getDeignFromIso("GB"), true, null);
    }

    @BeforeAll
    public static void saveInformation() {
        System.out.println("Saving");
        flagHand = DatabaseControl.createFlag("GB", FLAG_SIZE.HAND);
        flagMedium = DatabaseControl.createFlag("GB", FLAG_SIZE.MEDIUM);
        flagLarge = DatabaseControl.createFlag("GB", FLAG_SIZE.LARGE);
    }

    @BeforeEach
    void setAmount() {
        DatabaseControl.updateAmountAndRestock(173, 0, 10, 7);
        DatabaseControl.updateAmountAndRestock(173, 3, 5, 20);
        DatabaseControl.updateAmountAndRestock(173, 4, 0, 11);
    }


    @AfterAll
    public static void restoreInformation() {
        System.out.println("Restoring");
        DatabaseControl.updateAmountAndRestock(173, 0, flagHand.getTotalAmount(), flagHand.getRestock());
        DatabaseControl.updateAmountAndRestock(173, 3, flagMedium.getTotalAmount(), flagMedium.getRestock());
        DatabaseControl.updateAmountAndRestock(173, 4, flagLarge.getTotalAmount(), flagLarge.getRestock());
    }
}
