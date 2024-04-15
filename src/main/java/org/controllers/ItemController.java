package org.controllers;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.vexillum.*;

import java.text.NumberFormat;
import java.util.*;

public class ItemController extends ControllerParent {
    @FXML private BorderPane panMain;
    @FXML private ImageView imgFlag;

    @FXML private Label lblName;
    @FXML private Label lblTags;

    @FXML private Label lblCurrentStock;
    @FXML private ImageView imgSeverity;
    @FXML private Label lblRestock;

    @FXML private Label lblToggleL;
    @FXML private Label lblToggleR;
    @FXML private Label lblTotalPrice;
    @FXML private Label lblPrice;
    @FXML private Label lblIncriment;
    @FXML private ComboBox cmbModifications;
    @FXML private ToggleSwitch tglSwitch;

    private Design loadedDesign;
    private Boolean isFlag;
    private StockItem item;
    private String selectedSize;

    public void load(Stage stage, Operator operator, List<StockItem> items, Design loadedDesign, Boolean isFlag) {
        this.operator = operator;
        this.loadedDesign = loadedDesign;
        this.isFlag = isFlag;

        try {
            HBox headerBox = (HBox) panMain.lookup("#boxHeader");
            if (headerBox == null) { throw new Exception(); }

            loadHeader(stage, operator, items, headerBox, "");
            typeSetUp();
            populateInfo();
            listenerToggle();
            updateItem();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void typeSetUp() throws Exception {
        if (isFlag) {
            selectedSize = "Hand";
            System.out.println("ISO ID: " + loadedDesign.getIsoID());
            item = DatabaseControl.createFlag(loadedDesign.getIsoID(), FLAG_SIZE.HAND);
        }
        else {
            selectedSize = "45x45cm";
            lblToggleL.setText("With filling");
            lblToggleR.setText("No filling");
            cmbModifications.getItems().clear();
            cmbModifications.getItems().addAll("Foam", "Polyester (\u00A31.00)", "Feathers (\u00A33.00)", "Cotton (\u00A34.00)");
            cmbModifications.setPromptText("Cushion Filling");
            item = DatabaseControl.createCushion(loadedDesign.getIsoID(), CUSHION_SIZE.SMALL);
        }
        createSizeSelection();
    }

    private void createSizeSelection() throws Exception {
        VBox itemBox = (VBox) panMain.lookup("#boxItemStore");
        if (itemBox == null) { throw new Exception(); }

        String[] sizeVals;
        if (isFlag) {
            sizeVals = new String[]{"Hand", "Desk", "90x60cm", "150x90cm", "240x150cm"};
        }
        else {
            sizeVals = new String[]{"45x45cm", "55x55cm", "60x60cm", "50x30cm"};
        }

        for (String sizeVal : sizeVals) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_item.fxml"));
            Parent itemView = loader.load();

            VBox box = (VBox) itemView;
            box.setOnMouseClicked(boxClick);

            try {
                Image img = new Image("org/Assets/FlagsSmall/" + loadedDesign.getIsoID() + ".png");
                ImageView imgView = (ImageView) box.lookup("#imgDesign");

                imgView.setFitWidth((int) (img.getWidth() * 0.5));
                imgView.setFitHeight((int) (img.getHeight() * 0.5));
                imgView.setImage(img);
            }
            catch (Exception ignored) { }

            ((Label) box.lookup("#lblSize")).setText(sizeVal);
            box.setId("boxSize_" + sizeVal);
            itemBox.getChildren().add(box);
        }
    }

    private void updateItem() {
        if (item instanceof Flag f) {
            if (tglSwitch.getToLeft().get()) {
                f.setMaterial(FLAG_MATERIAL.getType(lblToggleL.getText().split(" ")[0]));
            }
            else {
                f.setMaterial(FLAG_MATERIAL.getType(lblToggleR.getText().split(" ")[0]));
            }

            switch (cmbModifications.getSelectionModel().getSelectedIndex()) {
                case 0 -> f.setHoist(FLAG_HOIST.NONE);
                case 1 -> f.setHoist(FLAG_HOIST.FABRIC);
                case 2 -> f.setHoist(FLAG_HOIST.METAL);
                case 3 -> f.setHoist(FLAG_HOIST.WOODEN);
            }

            FLAG_SIZE fs = FLAG_SIZE.fromString(selectedSize);
            f.setSize(fs);
            f.setSizeID(FLAG_SIZE.getSizeId(fs));
        }
        else if (item instanceof Cushion c) {
            c.setJustCase(!tglSwitch.getToLeft().get());
            CUSHION_SIZE cs = CUSHION_SIZE.fromString(selectedSize);
            c.setSize(cs);
            c.setSizeID(CUSHION_SIZE.getSizeId(cs));
        }

        DatabaseControl.setStockData(item);

        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        float price = item.calculatePrice();
        String cost = eurFormatter.format(price);
        if (cmbModifications.getSelectionModel().isEmpty()) {
            lblPrice.setText(cost + "+");
        }
        else {
            lblPrice.setText(cost);
        }

        item.setAmount(Integer.parseInt(lblIncriment.getText()));

        String totalCost = eurFormatter.format(price * item.getAmount());
        lblTotalPrice.setText(totalCost);
    }

    private void listenerToggle() {
        tglSwitch.getToLeft().addListener((observable, oldValue, newValue) -> {
            updateItem();
        });
    }

    EventHandler<MouseEvent> boxClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            try {
                Object source = event.getSource();
                VBox box = (VBox) source;
                selectedSize = box.getId().split("_")[1];
                updateItem();
                populateInfo();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    @FXML
    protected void cmbModificationsChange(ActionEvent event) throws Exception {
        updateItem();
    }

    @FXML
    protected void btnMinusClick(ActionEvent event) throws Exception {
        int val = Integer.parseInt(lblIncriment.getText());
        if (val > 1) val -= 1;

        lblIncriment.setText(String.valueOf(val));
        updateItem();
    }

    @FXML
    protected void btnAddClick(ActionEvent event) throws Exception {
        int val = Integer.parseInt(lblIncriment.getText());
        if (val < item.getTotalAmount()) val += 1;

        lblIncriment.setText(String.valueOf(val));
        updateItem();
    }

    @FXML
    protected void btnEditClick(ActionEvent event) throws Exception {

    }

    @FXML
    protected void btnAddToBasketClick(ActionEvent event) throws Exception {
        Loader l = new Loader();
        items.add(item);
        l.showBasket(stage, items, operator);
    }

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

            Integer regionID = loadedDesign.getRegion();
            String regionName = regionID == null ? "" : DatabaseControl.getRegionName(regionID) + "\n";

            Integer typeID = loadedDesign.getType();
            String typeName = typeID == null ? "" : DatabaseControl.getTypeName(typeID);

            int totalAmount = item.getTotalAmount();
            int restock = item.getRestock();

            lblCurrentStock.setText(totalAmount + "");
            lblRestock.setText(restock + "");

            try {
                String severityImg;
                if (totalAmount <= restock) { severityImg = "IndicatorBad"; }
                else if (totalAmount * 0.5 <= restock) { severityImg = "IndicatorMid"; }
                else { severityImg =  "IndicatorGood"; }

                Image img = new Image("org/Assets/Icons/" + severityImg + ".png");
                imgSeverity.setFitWidth(12);
                imgSeverity.setFitHeight(12);
                imgSeverity.setImage(img);
            }
            catch (Exception ignored) {}

            lblTags.setText(regionName + typeName);
        }
    }
}