package org.vexillum;

import javafx.application.Application;
import javafx.stage.Stage;
import org.controllers.Loader;

/**
 * The main boot location of the program
 */
public class Main extends Application {
    private final Loader loader = new Loader();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Vexillum Management");

        //DatabaseControl.AddDesigns();
        //DatabaseControl.AddStockItem();

        //DatabaseControl.AddFlags();
        //DatabaseControl.AddTags();

        //DatabaseControl.AddCushions();
        //DatabaseControl.SetAmounts();

        loader.showLogin(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}