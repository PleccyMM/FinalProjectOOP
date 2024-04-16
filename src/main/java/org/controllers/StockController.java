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
    @FXML private HBox boxTagOps;
    @FXML private VBox boxTagSelect;
    private List<RadioButton> rdbList;
    ToggleGroup tg;
    private List<Design> allDesigns;

    public void load(Stage stage, List<StockItem> items, Operator operator, String search) {
        this.operator = operator;

        try {
            HBox headerBox = (HBox) panMain.lookup("#boxHeader");
            if (headerBox == null) { throw new Exception(); }

            loadHeader(stage, operator, items, headerBox, search);

            String[] tagBoxes = new String[] {"Type", "Region", "Initial"};
            for (String tagBox : tagBoxes) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("stock_tag.fxml"));
                Parent box = loader.load();

                ((Label) box.lookup("#lblTag")).setText(tagBox);

                try {
                    Image img = new Image("org/Assets/Icons/DownArrow.png");
                    ((ImageView) box.lookup("#imgArrow")).setImage(img);
                }
                catch (Exception ignored) { }

                box.setOnMouseClicked(tagClick);
                boxTagOps.getChildren().add(box);
            }

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

                Design currentDesign = allDesigns.get(i+j);

                Button btnFlag = ((Button) productView.lookup("#btnFlag"));
                btnFlag.setId(btnFlag.getId() + "_" + currentDesign.getIsoID());
                btnFlag.setOnAction(btnFlagHandle);

                Button btnCushion = ((Button) productView.lookup("#btnCushion"));
                btnCushion.setId(btnCushion.getId() + "_" + currentDesign.getIsoID());
                btnCushion.setOnAction(btnCushionHandle);

                try {
                    Image img = new Image("org/Assets/FlagsSmall/" + currentDesign.getIsoID() + ".png");
                    ((ImageView) productView.lookup("#imgDisp")).setImage(img);
                }
                catch (Exception ignored) { }

                ((Label) productView.lookup("#lblStockName")).setText(currentDesign.getName());

                if (currentDesign.getType() == TYPE.NATIONAL.getValue()) {
                    ((Label) productView.lookup("#lblStockPrice")).setText("\u00A31.50-\u00A320.00+");
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
                showItemScreen(event, true);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<ActionEvent> btnCushionHandle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                showItemScreen(event, false);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    private void showItemScreen(ActionEvent event, Boolean isFlag) throws Exception {
        Object source = event.getSource();
        Button b = (Button) source;
        String isoID = b.getId().split("_")[1];
        for (Design d : allDesigns) {
            if (d.getIsoID().equals(isoID)) {
                l.showItem(stage, operator, items, d, isFlag);
                break;
            }
        }
    }

    private void generateRadioButtons(String tagSelect) {
        tg = new ToggleGroup();
        rdbList = new ArrayList<>();

        switch (tagSelect) {
            case "type":
                rdbList.add(createRdb("Domestic", tg));
                rdbList.add(createRdb("International", tg));
                rdbList.add(createRdb("Pride", tg));
                break;
            case "region":
                rdbList.add(createRdb("Africa", tg));
                rdbList.add(createRdb("Asia", tg));
                rdbList.add(createRdb("Europe", tg));
                rdbList.add(createRdb("North America", tg));
                rdbList.add(createRdb("Oceania", tg));
                rdbList.add(createRdb("South America", tg));
                break;
            case "initial":
                rdbList.add(createRdb("A-D", tg));
                rdbList.add(createRdb("E-I", tg));
                rdbList.add(createRdb("J-P", tg));
                rdbList.add(createRdb("Q-Z", tg));
        }
        boxTagSelect.getChildren().addAll(rdbList);
    }

    private RadioButton createRdb(String text, ToggleGroup tg) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setToggleGroup(tg);
        return radioButton;
    }

    EventHandler<MouseEvent> tagClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            try {
                Object source = event.getSource();
                Node box = (Node) source;

                Image downArr = new Image("org/Assets/Icons/DownArrow.png");
                Image upArr = new Image("org/Assets/Icons/UpArrow.png");
                ImageView imgView = ((ImageView) box.lookup("#imgArrow"));

                boxTagSelect.getChildren().clear();
                if (Objects.equals(box.getId(), "up")) {
                    box.setId("down");
                    imgView.setImage(downArr);
                }
                else {
                    for (Node n : boxTagOps.getChildren()) {
                        n.setId("down");
                        ((ImageView) n.lookup("#imgArrow")).setImage(downArr);
                    }

                    box.setId("up");
                    imgView.setImage(upArr);

                    String labelText = ((Label) box.lookup("#lblTag")).getText().toLowerCase();
                    generateRadioButtons(labelText);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}
