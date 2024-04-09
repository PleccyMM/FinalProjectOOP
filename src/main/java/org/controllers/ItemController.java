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
    @FXML private Label lblName;
    private Design loadedDesign;
    private Boolean isFlag;
    private StockItem item;

    public void load(Stage stage, Operator operator, Design loadedDesign, Boolean isFlag) {
        this.operator = operator;
        this.loadedDesign = loadedDesign;
        this.isFlag = isFlag;

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
            typeSetUp();
            populateInfo();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void typeSetUp() throws Exception {
        if (isFlag) {
            createSizeSelection(5);
        }
        else {
            createSizeSelection(4);
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
        int imageViewLoc = -1;

        for (int i = 0; i < amount; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_item.fxml"));
            Parent itemView = loader.load();
            VBox box = (VBox) itemView;
            box.setOnMouseClicked(boxClick);

            if (imageViewLoc == -1) {
                List<Node> nodes = box.getChildrenUnmodifiable();
                for (int j = 0; j < nodes.size(); j++) {
                    if (Objects.equals(nodes.get(j).getId(), "imgDesign")) {
                        imageViewLoc = j;
                    }
                }
            }

            try {
                Image img = new Image("org/Assets/FlagsSmall/" + loadedDesign.getIsoID() + ".png");
                ImageView imgView = (ImageView) box.getChildren().get(imageViewLoc);
                imgView.setFitWidth((int) (img.getWidth() * 0.5));
                imgView.setFitHeight((int) (img.getHeight() * 0.5));
                imgView.setImage(img);
            }
            catch (Exception ignored) { }

            box.setId("boxSize" + i);
            itemBox.getChildren().add(box);
        }
    }

    EventHandler<MouseEvent> boxClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            try {
                System.out.println("CLICKED");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    private void populateInfo() {
        try {
            Image img = new Image("org/Assets/FlagsLarge/" + loadedDesign.getIsoID() + ".png");
            imgFlag.setFitWidth((int) (img.getWidth() * 0.25));
            imgFlag.setFitHeight((int) (img.getHeight() * 0.25));
            imgFlag.setImage(img);
        }
        catch (Exception ignored) { }
        finally {
            lblName.setText(loadedDesign.getName());
        }
    }
}