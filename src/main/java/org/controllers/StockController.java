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
    private List<Design> allDesigns;

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

                Label lblName = null;
                Label lblStockPrice = null;
                ImageView imgView = null;
                Design currentDesign = allDesigns.get(i+j);

                for (Node n : productView.getChildrenUnmodifiable()) {
                    if (Objects.equals(n.getId(), "lblStockName")) {
                        lblName = (Label) n;
                    }
                    else if (Objects.equals(n.getId(), "lblStockPrice")) {
                        lblStockPrice = (Label) n;
                    }
                    else if (Objects.equals(n.getId(), "imageHolder")) {
                        List<Node> listVBox;
                        listVBox = ((VBox) n).getChildren();
                        for (Node n2 : listVBox) {
                            if (Objects.equals(n2.getId(), "imgDisp")) {
                                imgView = (ImageView) n2;
                            }
                        }
                    }
                    else if (Objects.equals(n.getId(), "boxButons")) {
                        List<Node> listHBox;
                        listHBox = ((HBox) n).getChildren();
                        for (Node n2 : listHBox) {
                            if (Objects.equals(n2.getId(), "btnFlag")) {
                                Button btnFlag = (Button) n2;
                                btnFlag.setId(btnFlag.getId() + "_" + currentDesign.getIsoID());
                                btnFlag.setOnAction(btnFlagHandle);
                            }
                            if (Objects.equals(n2.getId(), "btnCushion")) {
                                Button btnCushion = (Button) n2;
                            }
                        }
                    }
                }

                try {
                    Image img = new Image("org/Assets/FlagsSmall/" + currentDesign.getIsoID() + ".png");
                    imgView.setImage(img);
                }
                catch (Exception ignored) { }

                lblName.setText(currentDesign.getName());

                if (currentDesign.getType() == TYPE.NATIONAL.getValue()) {
                    lblStockPrice.setText("\u00A31.50-\u00A322.00");
                }

                box.getChildren().add(productView);
            }
            boxScroll.getChildren().add(box);
        }

        boxScroll.getChildren().add(new HBox());
    }

    EventHandler<ActionEvent> btnFlagHandle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                Button b = (Button) source;
                String isoID = b.getId().split("_")[1];
                for (Design d : allDesigns) {
                    if (d.getIsoID().equals(isoID)) {
                        l.showItem(stage, operator, d);
                        break;
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    @FXML
    protected void handleBtnTest(ActionEvent event) throws Exception {

    }
}
