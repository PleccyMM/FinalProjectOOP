package org.vexillum;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import org.controllers.Loader;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class Main extends Application
{
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

        //loader.showLogin(stage);
        loader.showStock(stage, new Operator(), new ArrayList<StockItem>(), new SearchConditions());
    }

    public static void main(String[] args) {
        launch(args);
    }
}