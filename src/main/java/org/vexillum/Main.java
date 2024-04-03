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

public class Main extends Application
{
    private Loader loader = new Loader();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Vexillum Management");
        //loader.showLogin(stage);
        loader.showLogin(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}