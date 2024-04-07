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

public class ItemController extends ControllerParent {
    @FXML BorderPane panMain;

    @FXML
    public void initialize() throws Exception {
        VBox itemBox = null;
        BorderPane borderPane = null;
        for (Node n : panMain.getChildrenUnmodifiable()) {
            if (Objects.equals(n.getId(), "boxItemStore")) {
                itemBox = (VBox) n;
                break;
            }
        }

        if (itemBox == null) { throw new Exception(); }

        for (int i = 0; i < 5; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_item.fxml"));
            Parent itemView = loader.load();
            VBox box = (VBox) itemView;
            box.setId("light-blue-fill" + i);
            itemBox.getChildren().add(box);
        }
    }

    public void load(Stage stage, Operator operator) {
        this.operator = operator;

        try {
            HBox headerBox = null;

            for (Node n : panMain.getChildrenUnmodifiable()) {
                if (Objects.equals(n.getId(), "boxHeader")) {
                    headerBox = (HBox) n;
                    break;
                }
            }

            if (headerBox == null) { throw new Exception(); }

            loadHeader(stage, headerBox, "");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
