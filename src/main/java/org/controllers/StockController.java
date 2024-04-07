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

public class StockController extends ControllerParent {
    @FXML private VBox boxScroll;
    @FXML private ScrollPane scrBackground;
    @FXML private BorderPane panMain;

    public void load(Stage stage, Operator operator, String search) {
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

            loadHeader(stage, headerBox, search);

            loadStock(search);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadStock(String search) throws Exception {
        List<Design> allDesigns;
        if (search.isEmpty()) {
            allDesigns = DatabaseControl.getAllDesigns();
        }
        else {
            allDesigns = DatabaseControl.searchDesigns(search);
        }

        boxScroll.getChildren().add(new HBox());

        int flagsToLoad;
        int maxFlagLoad = allDesigns.size();

        flagsToLoad = Math.min(allDesigns.size(), maxFlagLoad);

        System.out.println("Loaded Designs: " + flagsToLoad);

        for (int i = 0; i < flagsToLoad; i+=3) {
            HBox box = new HBox();
            box.setSpacing(48);
            box.setAlignment(Pos.CENTER);

            int runAmount = 3;
            if (i + 2 >= flagsToLoad) {
                System.out.println("Reducing run because " + i + " + 2 >= " + flagsToLoad);
                runAmount = flagsToLoad - i;
            }

            for (int j = 0; j < runAmount; j++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("stock_item.fxml"));
                Parent productView = loader.load();

                List<Node> listVBox;
                Label lblName = null;
                ImageView imgView = null;

                for (Node n : productView.getChildrenUnmodifiable()) {
                    if (Objects.equals(n.getId(), "lblStockName")) {
                        lblName = (Label) n;
                    }
                    if (Objects.equals(n.getId(), "imageHolder")) {
                        listVBox = ((VBox) n).getChildren();
                        for (Node n2 : listVBox) {
                            if (Objects.equals(n2.getId(), "imgDisp")) {
                                imgView = (ImageView) n2;
                            }
                        }
                    }
                    if (lblName != null && imgView != null) { break; }
                }

                try {
                    Image img = new Image("org/Assets/FlagsSmall/" + allDesigns.get(i+j).getIsoID() + ".png");
                    imgView.setImage(img);
                }
                catch (Exception ignored) { }

                lblName.setText(allDesigns.get(i+j).getName());
                box.getChildren().add(productView);
            }
            boxScroll.getChildren().add(box);
        }

        boxScroll.getChildren().add(new HBox());
    }

    @FXML
    protected void handleBtnTest(ActionEvent event) throws Exception {

    }
}
