package org.controllers;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;
import java.util.*;
import org.vexillum.*;

/**
 * This class manages all switching between new screens, any screen switches must be done through this class
 */
public class Loader {
    public Loader() {}

    public void showLogin(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login_screen.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    public void showStock(Stage stage, List<StockItem> items, Operator operator, SearchConditions searchConditions) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("stock_screen.fxml"));
        Parent root = loader.load();

        StockController controller = loader.getController();
        controller.load(stage, items, operator, searchConditions);

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    public void showItem(Stage stage, List<StockItem> items, Operator operator, Design loadedDesign, Boolean isFlag, Integer loadedPos) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_screen.fxml"));
        Parent root = loader.load();

        ItemController controller = loader.getController();
        controller.load(stage, operator, items, loadedDesign, isFlag, loadedPos);

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    public void showBasket(Stage stage, List<StockItem> items, Operator operator) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("basket_screen.fxml"));
        Parent root = loader.load();

        BasketController controller = loader.getController();
        controller.load(stage, items, operator);

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    public void showAdmin(Stage stage, List<StockItem> items, Operator operator) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_screen.fxml"));
        Parent root = loader.load();

        AdminController controller = loader.getController();
        controller.load(stage, items, operator);

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }
}
