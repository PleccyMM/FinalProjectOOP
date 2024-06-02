package org.controllers;

import com.sun.javafx.application.PlatformImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.testfx.matcher.control.LabeledMatchers;
import org.vexillum.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasketControllerTest extends ItemPopulation {
    private BasketController controller;
    private Stage stage;


    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("basket_screen.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.load(stage, testItems, new Operator());

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUpItems() throws InterruptedException {
        super.setUpItems();
        PlatformImpl.runAndWait(() -> controller.load(stage, testItems, new Operator()));
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

        assertEquals(boxScroll.getChildren().size(), testItems.size() + 2);
    }

    @Test
    @Order(2)
    public void increaseDecreaseAmountImport() {
        int index = testItems.get(0).hashCode();
        Button btnAdd = lookup("#" + index + " #btnAdd").query();
        Button btnMinus = lookup("#" + index + " #btnMinus").query();
        Label lblIncrement = lookup("#" + index + " #lblIncrement").query();

        verifyThat(lblIncrement, LabeledMatchers.hasText("3"));

        clickOn(btnAdd);
        verifyThat(lblIncrement, LabeledMatchers.hasText("4"));

        clickOn(btnMinus);
        verifyThat(lblIncrement, LabeledMatchers.hasText("3"));
    }

    @Test
    @Order(3)
    public void increaseAmountAboveStockImport() {
        int index = testItems.get(0).hashCode();
        Button btnAdd = lookup("#" + index + " #btnAdd").query();
        Label lblIncrement = lookup("#" + index + " #lblIncrement").query();

        verifyThat(lblIncrement, LabeledMatchers.hasText("3"));

        for (int i = 0; i < 7; i++) {
            clickOn(btnAdd);
        }
        verifyThat(lblIncrement, LabeledMatchers.hasText("10"));
    }
}
