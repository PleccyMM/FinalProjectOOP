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

public abstract class ControllerParent {
    private TextField enrSearch;
    protected Stage stage;
    protected final Loader l = new Loader();
    protected Operator operator;
    protected List<StockItem> items;

    protected void loadHeader(Stage stage, Operator operator, List<StockItem> items, HBox headerBox, String search) {
        try {
            this.operator = operator;
            this.items = items;

            this.stage = stage;
            Button btnBack = ((Button) headerBox.lookup("#btnBack"));
            btnBack.setOnAction(btnBackHandle);

            Button btnSearch = ((Button) headerBox.lookup("#btnSearch"));
            btnSearch.setOnAction(searchHandleBtn);

            Button btnBasket = ((Button) headerBox.lookup("#btnBasket"));
            btnBasket.setOnAction(basketHandleBtn);

            enrSearch = (TextField) headerBox.lookup("#enrSearch");
            enrSearch.setText(search);
            enrSearch.setOnKeyPressed(searchHandleEnr);
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }


    EventHandler<ActionEvent> btnBackHandle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                l.showStock(stage, operator, items, "");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<ActionEvent> searchHandleBtn = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                l.showStock(stage, operator, items, enrSearch.getText());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<KeyEvent> searchHandleEnr = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() != KeyCode.ENTER) {
                return;
            }
            try {
                l.showStock(stage, operator, items, enrSearch.getText());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> basketHandleBtn = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                l.showBasket(stage, items, operator);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}
