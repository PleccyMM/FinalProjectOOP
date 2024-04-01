package org.controllers;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Loader {
    public Loader() {}
    public void showLogin(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login_screen.fxml"));
        Parent root = loader.load();

        LoginController controller = loader.getController();

        Scene scene = new Scene(root, 600, 300);
        stage.setScene(scene);
        stage.show();
    }
}
