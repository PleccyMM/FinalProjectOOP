package org.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vexillum.*;

import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;


public class StockController extends ControllerParent {
    private Operator operator;
    @FXML private VBox boxScroll;
    TextField enrSearch;
    @FXML private ScrollPane scrBackground;
    @FXML private BorderPane panMain;

    public void load(String search) {
        try {
            HBox searchBox = null;
            HBox headerBox = null;

            for (Node n : panMain.getChildrenUnmodifiable()) {
                if (Objects.equals(n.getId(), "boxHeader")) {
                    headerBox = (HBox) n;
                    break;
                }
            }

            if (headerBox == null) { throw new Exception(); }

            for (Node n : headerBox.getChildrenUnmodifiable()) {
                if (Objects.equals(n.getId(), "boxSearch")) {
                    searchBox = (HBox) n;
                    break;
                }
            }

            if (searchBox == null) { throw new Exception(); }

            for (Node n : searchBox.getChildrenUnmodifiable()) {
                if (Objects.equals(n.getId(), "imgSearch")) {
                    System.out.println("Found imgSearch");
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
            throw new RuntimeException(e);
        }
    }

    EventHandler<ActionEvent> searchHandleBtn = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Loader l = new Loader();
                l.showStock(((Stage) boxScroll.getScene().getWindow()), operator, enrSearch.getText());
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
                Loader l = new Loader();
                l.showStock(((Stage) boxScroll.getScene().getWindow()), operator, enrSearch.getText());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    public void loginOperator(Operator o) {
        operator = o;
    }

    public void loadStock(String search) throws Exception {
        List<Design> allDesigns;
        if (search.isEmpty()) {
            allDesigns = DatabaseControl.getAllDesigns();
        }
        else {
            allDesigns = DatabaseControl.searchDesigns(search);
        }

        boxScroll.getChildren().add(new HBox());

        int flagsToLoad = allDesigns.size();
        System.out.println("Loaded Designs: " + allDesigns.size());

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
