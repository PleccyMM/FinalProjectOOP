package org.controllers;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    protected SearchConditions sc;

    protected abstract void stageChangeHandle();

    protected void loadHeader(Stage stage, Operator operator, List<StockItem> items, HBox headerBox, SearchConditions sc) {
        try {
            this.operator = operator;
            this.items = items;
            this.sc = sc;

            this.stage = stage;
            Button btnBack = ((Button) headerBox.lookup("#btnBack"));
            btnBack.setOnAction(btnBackHandle);

            Button btnSearch = ((Button) headerBox.lookup("#btnSearch"));
            btnSearch.setOnAction(searchHandleBtn);

            Button btnBasket = ((Button) headerBox.lookup("#btnBasket"));
            btnBasket.setOnAction(basketHandleBtn);

            enrSearch = (TextField) headerBox.lookup("#enrSearch");
            enrSearch.setText(sc.getSearch());
            enrSearch.setOnKeyPressed(searchHandleEnr);

            ComboBox cmbProfile = ((ComboBox) headerBox.lookup("#cmbProfile"));
            cmbProfile.setOnAction(cmbProfileChange);
            if (operator.isAdministrator()) {
                cmbProfile.getItems().add("Admin Panel");
            }
            cmbProfile.getItems().add("Logout");
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }

    protected void performSearch() throws Exception {
        stageChangeHandle();
        l.showStock(stage, operator, items, sc);
    }

    EventHandler<ActionEvent> btnBackHandle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                stageChangeHandle();
                l.showStock(stage, operator, items, new SearchConditions());
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
                sc.setSearch(enrSearch.getText());
                performSearch();
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
                sc.setSearch(enrSearch.getText());
                performSearch();
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
                stageChangeHandle();
                l.showBasket(stage, items, operator);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> cmbProfileChange = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                ComboBox c = (ComboBox) source;

                if (c.getValue() == "Admin Panel") {
                    l.showAdmin(stage, items, operator);
                }
                else {
                    l.showLogin(stage);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}
