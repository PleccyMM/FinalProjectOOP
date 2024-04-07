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

    protected void loadHeader(Stage stage, HBox headerBox, String search) {
        try {
            this.stage = stage;
            HBox searchBox = null;

            for (Node n : headerBox.getChildrenUnmodifiable()) {
                if (Objects.equals(n.getId(), "boxSearch")) {
                    searchBox = (HBox) n;
                }
                else if (Objects.equals(n.getId(), "btnBack")) {
                    ((Button) n).setOnAction(btnBackHandle);
                }
            }

            if (searchBox == null) {
                throw new Exception();
            }

            for (Node n : searchBox.getChildrenUnmodifiable()) {
                if (Objects.equals(n.getId(), "imgSearch")) {
                    ((Button) n).setOnAction(searchHandleBtn);
                }
                else if (Objects.equals(n.getId(), "enrSearch")) {
                    enrSearch = (TextField) n;
                    enrSearch.setText(search);
                    enrSearch.setOnKeyPressed(searchHandleEnr);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }


    EventHandler<ActionEvent> btnBackHandle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                l.showStock(stage, operator, "");
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
                l.showStock(stage, operator, enrSearch.getText());
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
                l.showStock(stage, operator, enrSearch.getText());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}
