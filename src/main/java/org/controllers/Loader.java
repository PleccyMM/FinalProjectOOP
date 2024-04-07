package org.controllers;

import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.vexillum.*;
import java.util.*;

public class Loader {
    public Loader() {}

    public void showLogin(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login_screen.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    public void showStock(Stage stage, Operator operator, String search) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("stock_screen.fxml"));
        Parent root = loader.load();

        StockController controller = loader.getController();
        controller.load(stage, operator, search);

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    public void showItem(Stage stage, Operator operator, Design loadedDesign) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_screen.fxml"));
        Parent root = loader.load();

        ItemController controller = loader.getController();
        controller.load(stage, operator, loadedDesign);

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }
}
