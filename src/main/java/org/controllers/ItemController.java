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
    @FXML private BorderPane panMain;
    @FXML private ImageView imgFlag;
    private Design loadedDesign;

    public void load(Stage stage, Operator operator, Design loadedDesign) {
        this.operator = operator;
        this.loadedDesign = loadedDesign;

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
            createSizeSelection(5);
            populateInfo();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createSizeSelection(int amount) throws Exception {
        VBox itemBox = null;
        BorderPane borderPane = null;
        for (Node n : panMain.getChildrenUnmodifiable()) {
            if (Objects.equals(n.getId(), "boxItemStore")) {
                itemBox = (VBox) n;
                break;
            }
        }

        if (itemBox == null) { throw new Exception(); }

        for (int i = 0; i < amount; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_item.fxml"));
            Parent itemView = loader.load();
            VBox box = (VBox) itemView;
            box.setId("light-blue-fill" + i);
            itemBox.getChildren().add(box);
        }
    }

    private void populateInfo() {
        try {
            Image img = new Image("org/Assets/FlagsLarge/" + loadedDesign.getIsoID() + ".png");
            imgFlag.setFitWidth((int) (img.getWidth() * 0.33));
            imgFlag.setFitHeight((int) (img.getHeight() * 0.33));
            imgFlag.setImage(img);
        }
        catch (Exception ignored) { }
    }
}