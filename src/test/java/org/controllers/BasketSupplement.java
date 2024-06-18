package org.controllers;

import com.sun.javafx.application.PlatformImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.vexillum.ItemPopulation;
import org.vexillum.Operator;

/**
 * A short super that just boots and properly loads the basket screen, gets all item information from {@code ItemPopulation}
 */
public abstract class BasketSupplement extends ItemPopulation {
    protected BasketController controller;
    protected Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/basket_screen.fxml"));
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
}
