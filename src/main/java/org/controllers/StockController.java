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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.vexillum.*;

import javafx.scene.image.ImageView;
import java.util.List;


public class StockController {
    private Operator operator;
    @FXML private VBox boxScroll;
    public void loginOperator(Operator o) {
        operator = o;
    }
    @FXML
    protected void handleBtnTest(ActionEvent event) throws Exception {
        List<Design> allDesigns = DatabaseControl.getAllDesigns();

        for (int i = 0; i < allDesigns.size(); i+=3) {
            HBox box = new HBox();
            box.setSpacing(10);
            box.setAlignment(Pos.CENTER);

            int runAmount = 3;
            if (i + 2 > allDesigns.size()) {
                runAmount = allDesigns.size() - i;
            }

            for (int j = 0; j < runAmount; j++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("stock_item.fxml"));
                Parent productView = loader.load();

                List<Node> listFXML = productView.getChildrenUnmodifiable();
                Label lbl = (Label) listFXML.get(1);

                VBox imgContainer = (VBox) listFXML.get(0);
                List<Node> listVBox = imgContainer.getChildren();
                ImageView img = (ImageView) listVBox.get(0);
                img.setImage(new Image("org/Assets/Flags/" + allDesigns.get(i+j).getIsoID() + ".png"));

                lbl.setText(allDesigns.get(i+j).getName());
                box.getChildren().add(productView);
            }
            boxScroll.getChildren().add(box);
        }

        //boxScroll.getChildren().add(productView);
    }
}
