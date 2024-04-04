package org.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.vexillum.*;

import javafx.scene.image.ImageView;

import java.util.List;


public class StockController {
    private Operator operator;
    @FXML private VBox boxScroll;
    @FXML private ScrollPane scrBackground;
    public void loginOperator(Operator o) {
        operator = o;
    }

    public void loadStock() throws Exception {
        List<Design> allDesigns = DatabaseControl.getAllDesigns();

        boxScroll.getChildren().add(new HBox());

        int flagsToLoad = allDesigns.size();
        System.out.println("HERE " + allDesigns.size());
        for (int i = 0; i < flagsToLoad; i+=3) {
            HBox box = new HBox();
            box.setSpacing(48);
            box.setAlignment(Pos.CENTER);

            int runAmount = 3;
            if (i + 2 > flagsToLoad) {
                runAmount = flagsToLoad - i;
            }

            for (int j = 0; j < runAmount; j++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("stock_item.fxml"));
                Parent productView = loader.load();

                List<Node> listFXML = productView.getChildrenUnmodifiable();
                Label lbl = (Label) listFXML.get(1);

                VBox imgContainer = (VBox) listFXML.get(0);
                List<Node> listVBox = imgContainer.getChildren();
                ImageView imgView = (ImageView) listVBox.get(0);
                try {
                    Image img = new Image("org/Assets/FlagsSmall/" + allDesigns.get(i+j).getIsoID() + ".png");
                    imgView.setImage(img);
                    /*imgView.setFitWidth((int) (img.getWidth() / 10));
                    imgView.setFitHeight((int) (img.getHeight() / 10));*/
                }
                catch (Exception ignored) { }

                lbl.setText(allDesigns.get(i+j).getName());
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
