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
    @FXML private Button btnAdd;
    @FXML private Button btnMinus;
    @FXML private Label lblIncriment;
    @FXML private ComboBox cmbModifications;
    @FXML private ToggleSwitch tglSwitch;

    @FXML private Button btnAddToBasket;

    private Design loadedDesign;
    private Integer loadedPos;
    private Boolean isFlag;
    private StockItem item;
    private String selectedSize;

    public void load(Stage stage, Operator operator, List<StockItem> items, Design loadedDesign, Boolean isFlag, Integer loadedPos) {
        this.loadedDesign = loadedDesign;
        this.loadedPos = loadedPos;
        this.isFlag = isFlag;

        try {
            HBox headerBox = (HBox) panMain.lookup("#boxHeader");
            if (headerBox == null) { throw new Exception(); }

            loadHeader(stage, operator, items, headerBox, new SearchConditions());
            typeSetUp();
            populateInfo();
            listenerToggle();
            if (loadedPos != null) {
                setUpOptions();
                btnAddToBasket.setText("Update Item");
            }
            updateItem();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void typeSetUp() throws Exception {
        if (isFlag) {
            selectedSize = "Hand";
            item = loadedPos != null ? items.get(loadedPos) : DatabaseControl.createFlag(loadedDesign.getIsoID(), FLAG_SIZE.HAND);
        }
        else {
            selectedSize = "45x45cm";
            lblToggleL.setText("With filling");
            lblToggleR.setText("No filling (-\u00A38)");
            cmbModifications.getItems().clear();
            cmbModifications.getItems().addAll("Foam", "Polyester (\u00A31.00)", "Feathers (\u00A33.00)", "Cotton (\u00A34.00)");
            cmbModifications.setPromptText("Cushion Filling");
            item = loadedPos != null ? items.get(loadedPos) : DatabaseControl.createCushion(loadedDesign.getIsoID(), CUSHION_SIZE.SMALL, CUSHION_MATERIAL.EMPTY);
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

    private void setUpOptions() {
        lblIncriment.setText(item.getAmount() + "");

        var s = cmbModifications.getSelectionModel();

        if (item instanceof Flag f) {
            selectedSize = FLAG_SIZE.getString(f.getSize());

            switch (f.getHoist()) {
                case NONE -> s.select(0);
                case FABRIC -> s.select(1);
                case METAL -> s.select(2);
                case WOODEN -> s.select(3);
            }
            if (f.getSize() == FLAG_SIZE.HAND || f.getSize() == FLAG_SIZE.DESK) {
                tglSwitch.setToLeft(f.getMaterial() == FLAG_MATERIAL.PAPER);
            }
            else {
                tglSwitch.setToLeft(f.getMaterial() == FLAG_MATERIAL.POLYESTER);
            }
        }
        else if (item instanceof Cushion c) {
            tglSwitch.setToLeft(!c.isJustCase());

            switch (c.getMaterial()) {
                case FOAM -> s.select(0);
                case POLYESTER -> s.select(1);
                case FEATHERS -> s.select(2);
                case COTTON -> s.select(3);
            }

            selectedSize = CUSHION_SIZE.getString(c.getSize());
        }
    }

    private void updateItem() {
        if (item instanceof Flag f) {
            FLAG_SIZE fs = FLAG_SIZE.fromString(selectedSize);
            f.setSize(fs);
            f.setSizeID(FLAG_SIZE.getSizeId(fs));

            if (f.getSize() == FLAG_SIZE.HAND || f.getSize() == FLAG_SIZE.DESK) {
                lblToggleL.setText("Paper");
                lblToggleR.setText("Polyester (\u00A31)");
            }
            else {
                lblToggleL.setText("Polyester (\u00A31)");
                lblToggleR.setText("Nylon (\u00A33)");
            }

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
        }
        else if (item instanceof Cushion c) {
            boolean justCase = !tglSwitch.getToLeft().get();

            c.setJustCase(justCase);

            CUSHION_SIZE cs = CUSHION_SIZE.fromString(selectedSize);
            c.setSize(cs);
            c.setSizeID(CUSHION_SIZE.getSizeId(cs));

            if (!justCase) {
                cmbModifications.setDisable(false);
                switch (cmbModifications.getSelectionModel().getSelectedIndex()) {
                    case 0 -> c.setMaterial(CUSHION_MATERIAL.FOAM);
                    case 1 -> c.setMaterial(CUSHION_MATERIAL.POLYESTER);
                    case 2 -> c.setMaterial(CUSHION_MATERIAL.FEATHERS);
                    case 3 -> c.setMaterial(CUSHION_MATERIAL.COTTON);
                }
            }
            else {
                cmbModifications.setDisable(true);
                c.setMaterial(CUSHION_MATERIAL.EMPTY);
            }
        }

        DatabaseControl.setStockData(item);

        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        float price = item.calculatePrice();
        String cost = eurFormatter.format(price);

        if (cmbModifications.getSelectionModel().isEmpty() && !(!isFlag && !tglSwitch.getToLeft().get())) {
            lblPrice.setText(cost + "+");
            btnAddToBasket.setDisable(true);
        }
        else {
            lblPrice.setText(cost);
            btnAddToBasket.setDisable(false);
        }

        int amount = Integer.parseInt(lblIncriment.getText());
        item.setAmount(amount);

        btnAdd.setDisable(false);
        btnMinus.setDisable(false);
        if (amount == 1) btnMinus.setDisable(true);
        else if (amount == item.getTotalAmount()) btnAdd.setDisable(true);

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
        if (loadedPos != null) items.set(loadedPos, item);
        else items.add(item);

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