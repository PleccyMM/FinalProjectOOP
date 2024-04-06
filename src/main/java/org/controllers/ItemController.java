package org.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.vexillum.*;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Objects;

public class ItemController {
    @FXML BorderPane paneMain;

    @FXML
    public void initialize() throws Exception {
        List<Node> listFXML = paneMain.getChildrenUnmodifiable();
        VBox itemBox = null;
        BorderPane borderPane = null;
        for (Node n : listFXML) {
            if (Objects.equals(n.getId(), "boxItemStore")) {
                itemBox = (VBox) n;
                break;
            }
            else {
                System.out.println(n.toString());
            }
        }

        if (itemBox == null) {
            System.out.println("Failed to find");
            return;
        }

        for (int i = 0; i < 5; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_item.fxml"));
            Parent itemView = loader.load();
            VBox box = (VBox) itemView;
            box.setId("light-blue-fill" + i);
            itemBox.getChildren().add(box);
        }
    }
}
