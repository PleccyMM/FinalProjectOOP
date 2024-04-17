package org.controllers;

import org.vexillum.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.nio.charset.*;
import java.util.*;

public class StockController extends ControllerParent {
    @FXML private VBox boxScroll;
    @FXML private ScrollPane scrBackground;
    @FXML private BorderPane panMain;
    @FXML private HBox boxTagOps;
    @FXML private Region rgnButtonPush;
    @FXML private VBox boxTagSelect;

    private List<RadioButton> rdbList;
    private ToggleGroup tg;
    private List<Design> allDesigns;

    public void load(Stage stage, List<StockItem> items, Operator operator, SearchConditions searchConditions) {
        this.operator = operator;
        this.sc = searchConditions;

        try {
            HBox headerBox = (HBox) panMain.lookup("#boxHeader");
            if (headerBox == null) { throw new Exception(); }

            loadHeader(stage, operator, items, headerBox, sc.getSearch());

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

            loadStock();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadStock() throws Exception {
        allDesigns = DatabaseControl.searchDesigns(sc);

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
                l.showItem(stage, operator, items, d, isFlag, null);
                break;
            }
        }
    }

    private void generateRadioButtons(String tagSelect) {
        tg = new ToggleGroup();
        rdbList = new ArrayList<>();

        switch (tagSelect) {
            case "type":
                rdbList.add(createRdb("Any", tg, rdbType));
                rdbList.add(createRdb("National", tg, rdbType));
                rdbList.add(createRdb("International", tg, rdbType));
                rdbList.add(createRdb("Pride", tg, rdbType));

                Integer type = sc.getType();
                if (type == null) rdbList.get(0).setSelected(true);
                else {
                    Integer i = DatabaseControl.getTypeId(rdbList.get(type + 1).getText());
                    rdbList.get(i + 1).setSelected(true);
                }
                break;
            case "region":
                rdbList.add(createRdb("Any", tg, rdbRegion));
                rdbList.add(createRdb("Africa", tg, rdbRegion));
                rdbList.add(createRdb("Asia", tg, rdbRegion));
                rdbList.add(createRdb("Europe", tg, rdbRegion));
                rdbList.add(createRdb("North America", tg, rdbRegion));
                rdbList.add(createRdb("Oceania", tg, rdbRegion));
                rdbList.add(createRdb("South America", tg, rdbRegion));

                Integer region = sc.getRegion();
                if (region == null) rdbList.get(0).setSelected(true);
                else {
                    Integer i = DatabaseControl.getRegionId(rdbList.get(region + 1).getText());
                    rdbList.get(i + 1).setSelected(true);
                }
                break;
            case "initial":
                rdbList.add(createRdb("All", tg, rdbInitial));
                rdbList.add(createRdb("A-F", tg, rdbInitial));
                rdbList.add(createRdb("G-L", tg, rdbInitial));
                rdbList.add(createRdb("M-S", tg, rdbInitial));
                rdbList.add(createRdb("T-Z", tg, rdbInitial));

                String lastInitial = sc.getStartLetters()[1];
                if (lastInitial == null) rdbList.get(0).setSelected(true);
                else {
                    byte[] b = lastInitial.getBytes(StandardCharsets.UTF_8);
                    int i = (b[0] - 65) / (6 + b[0] / 77);
                    rdbList.get(i + 1).setSelected(true);
                }
        }
        boxTagSelect.getChildren().addAll(rdbList);
    }

    private RadioButton createRdb(String text, ToggleGroup tg, EventHandler<ActionEvent> handler) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setToggleGroup(tg);
        radioButton.setOnAction(handler);
        return radioButton;
    }

    EventHandler<ActionEvent> rdbType = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                RadioButton r = (RadioButton) source;

                sc.setType(DatabaseControl.getTypeId(r.getText()));
                performSearch();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<ActionEvent> rdbRegion = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                RadioButton r = (RadioButton) source;

                sc.setRegion(DatabaseControl.getRegionId(r.getText()));
                performSearch();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> rdbInitial = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                RadioButton r = (RadioButton) source;

                String[] initials = r.getText().split("-");
                if (initials.length == 1) {
                    sc.setStartLetters(new String[2]);
                }
                else {
                    sc.setStartLetters(initials);
                }
                performSearch();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

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
                    int index = 0;
                    for (int i = 0; i < boxTagOps.getChildren().size(); i++) {
                        Node n = boxTagOps.getChildren().get(i);

                        if (n == box) {
                            index = i;
                            box.setId("up");
                            imgView.setImage(upArr);
                        }
                        else {
                            n.setId("down");
                            ((ImageView) n.lookup("#imgArrow")).setImage(downArr);
                        }
                    }

                    rgnButtonPush.setMinWidth((((HBox) box).getWidth() + boxTagOps.getSpacing()) * (index));

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
