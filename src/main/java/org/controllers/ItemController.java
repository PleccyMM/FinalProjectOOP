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

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class ItemController extends ControllerParent {
    @FXML private StackPane panStacker;
    @FXML private BorderPane panMain;
    @FXML private ImageView imgFlag;

    @FXML private BorderPane panImg;
    @FXML private HBox boxVerticalContainer;
    @FXML private Label lblVerticalSize;
    @FXML private VBox boxVerticalSize;
    @FXML private VBox boxHorizontalContainer;
    @FXML private Label lblHorizontalSize;
    @FXML private VBox boxHorizontalSize;
    @FXML private HBox boxVerticalMatch;
    @FXML private VBox boxHorizontalMatch;

    @FXML private Label lblName;
    @FXML private Label lblTags;

    @FXML private Label lblCurrentStock;
    @FXML private ImageView imgSeverity;
    @FXML private Label lblRestock;
    @FXML private Label lblCostToProduce;

    @FXML private Label lblToggleL;
    @FXML private Label lblToggleR;
    @FXML private Label lblTotalPrice;
    @FXML private Label lblPrice;
    @FXML private Button btnAdd;
    @FXML private Button btnMinus;
    @FXML private Label lblIncriment;
    @FXML private ComboBox cmbModifications;
    @FXML private ToggleSwitch tglMaterial;
    @FXML private ToggleSwitch tglImportExport;

    @FXML private Button btnAddToBasket;

    private Design loadedDesign;
    private Integer loadedPos;
    private Boolean isFlag;
    private StockItem item;
    private String selectedSize;
    private VBox boxSelected;

    private String btnBasketPrefix;

    @Override
    protected void stageChangeHandle() {
        if (loadedPos == null) return;

        item = items.get(loadedPos);
        DatabaseControl.updateAmountAndRestock(item.getStockID(), item.getSizeID(), item.getTotalAmount(), item.getRestock());
    }

    public void load(Stage stage, Operator operator, List<StockItem> items, Design loadedDesign, Boolean isFlag, Integer loadedPos) {
        this.loadedDesign = loadedDesign;
        this.loadedPos = loadedPos;
        this.isFlag = isFlag;

        try {
            HBox headerBox = (HBox) panMain.lookup("#boxHeader");
            if (headerBox == null) { throw new Exception(); }

            loadHeader(stage, operator, items, headerBox, new SearchConditions());
            typeSetUp();
            listenerToggleMat();
            listenerToggleImport();
            if (loadedPos != null) {
                int newAmount = item.getTotalAmount() + item.getAmount();
                item.setTotalAmount(newAmount);
                DatabaseControl.updateAmountAndRestock(item.getStockID(), item.getSizeID(), newAmount, item.getRestock());

                setUpOptions();
                btnBasketPrefix = "Update";
            }
            else {
                btnBasketPrefix = "Add to";
            }
            populateInfo();
            updateItem();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void typeSetUp() throws Exception {
        if (isFlag) {
            selectedSize = "Hand";
            item = loadedPos != null ? items.get(loadedPos).clone() : DatabaseControl.createFlag(loadedDesign.getIsoID(), FLAG_SIZE.HAND);
        }
        else {
            selectedSize = "45x45cm";
            lblToggleL.setText("With filling (\u00A38+)");
            lblToggleR.setText("No filling");
            cmbModifications.getItems().clear();
            cmbModifications.getItems().addAll("Foam (\u00A38.00)", "Polyester (\u00A39.00)", "Feathers (\u00A311.00)", "Cotton (\u00A312.00)");
            cmbModifications.setPromptText("Cushion Filling");
            item = loadedPos != null ? items.get(loadedPos).clone() : DatabaseControl.createCushion(loadedDesign.getIsoID(), CUSHION_SIZE.SMALL, CUSHION_MATERIAL.EMPTY);
        }
        createSizeSelection();
    }

    private void createSizeSelection() throws Exception {
        VBox itemBox = (VBox) panMain.lookup("#boxItemStore");
        if (itemBox == null) { throw new Exception(); }

        String[] sizeVals;
        if (isFlag) {
            sizeVals = new String[5];
            for (FLAG_SIZE size : FLAG_SIZE.values()) {
                sizeVals[size.ordinal()] = FLAG_SIZE.getString(size);
            }
        }
        else {
            sizeVals = new String[4];
            for (CUSHION_SIZE size : CUSHION_SIZE.values()) {
                sizeVals[size.ordinal()] = CUSHION_SIZE.getString(size);
            }
        }

        boolean firstRun = true;
        boolean[] needsRestocking = DatabaseControl.restockList(item.getStockID());
        int index = 0;
        for (String sizeVal : sizeVals) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_item.fxml"));
            Parent itemView = loader.load();

            StackPane box = (StackPane) itemView;
            box.setId("boxSize_" + sizeVal);
            box.setOnMouseClicked(boxClick);

            if (firstRun) {
                firstRun = false;
                boxSelected = (VBox) box.lookup("#boxSelect");
                boxSelected.setStyle("-fx-background-color: #00000099;");
            }

            try {
                Image img = new Image("org/Assets/FlagsSmall/" + loadedDesign.getIsoID() + ".png");
                ImageView imgView = (ImageView) box.lookup("#imgDesign");

                imgView.setFitWidth((int) (img.getWidth() * 0.5));
                imgView.setFitHeight((int) (img.getHeight() * 0.5));
                imgView.setImage(img);
            }
            catch (Exception ignored) { }

            ((Label) box.lookup("#lblSize")).setText(sizeVal);

            if (needsRestocking[index]) {
                ((Label) box.lookup("#lblWarning")).setText("RESTOCK");
                box.lookup("#boxWarning").setStyle("-fx-background-color: #FF0000;");
            }

            itemBox.getChildren().add(box);
            index++;
        }
    }

    private void setUpOptions() {
        boxSelected.setStyle("-fx-background-color: transparent");

        lblIncriment.setText(item.getPrintAmount() + "");
        var s = cmbModifications.getSelectionModel();

        if (item instanceof Flag f) {
            selectedSize = FLAG_SIZE.getString(f.getSize());
            Node n = panMain.lookup("#boxSize_" + FLAG_SIZE.getString(f.getSize()));
            boxSelected = (VBox) n.lookup("#boxSelect");

            switch (f.getHoist()) {
                case NONE -> s.select(0);
                case FABRIC -> s.select(1);
                case METAL -> s.select(2);
                case WOODEN -> s.select(3);
            }
            if (f.getSize() == FLAG_SIZE.HAND || f.getSize() == FLAG_SIZE.DESK) {
                tglMaterial.setToLeft(f.getMaterial() == FLAG_MATERIAL.PAPER);
            }
            else {
                tglMaterial.setToLeft(f.getMaterial() == FLAG_MATERIAL.POLYESTER);
            }
        }
        else if (item instanceof Cushion c) {
            selectedSize = CUSHION_SIZE.getString(c.getSize());
            Node n = panMain.lookup("#boxSize_" + CUSHION_SIZE.getString(c.getSize()));
            boxSelected = (VBox) n.lookup("#boxSelect");

            tglMaterial.setToLeft(!c.isJustCase());

            switch (c.getMaterial()) {
                case FOAM -> s.select(0);
                case POLYESTER -> s.select(1);
                case FEATHERS -> s.select(2);
                case COTTON -> s.select(3);
            }
        }

        tglImportExport.setToLeft(item.getAmount() < 0);
        boxSelected.setStyle("-fx-background-color: #9D9D9D88");
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

            if (tglMaterial.getToLeft().get()) {
                f.setMaterial(FLAG_MATERIAL.getType(lblToggleL.getText().split(" ")[0]));
            }
            else {
                f.setMaterial(FLAG_MATERIAL.getType(lblToggleR.getText().split(" ")[0]));
            }

            if (f.getSize() != FLAG_SIZE.HAND && f.getSize() != FLAG_SIZE.DESK) {
                cmbModifications.setDisable(false);
                switch (cmbModifications.getSelectionModel().getSelectedIndex()) {
                    case 0 -> f.setHoist(FLAG_HOIST.NONE);
                    case 1 -> f.setHoist(FLAG_HOIST.FABRIC);
                    case 2 -> f.setHoist(FLAG_HOIST.METAL);
                    case 3 -> f.setHoist(FLAG_HOIST.WOODEN);
                }
            }
            else {
                cmbModifications.setDisable(true);
                f.setHoist(FLAG_HOIST.NONE);
            }
        }
        else if (item instanceof Cushion c) {
            CUSHION_SIZE cs = CUSHION_SIZE.fromString(selectedSize);
            c.setSize(cs);
            c.setSizeID(CUSHION_SIZE.getSizeId(cs));

            boolean justCase = !tglMaterial.getToLeft().get();
            c.setJustCase(justCase);

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

        if (!cmbModifications.getSelectionModel().isEmpty() || (!isFlag && !tglMaterial.getToLeft().get()) || (item instanceof Flag f && (f.getSize() == FLAG_SIZE.DESK || f.getSize() == FLAG_SIZE.HAND))) {
            lblPrice.setText(cost);
            btnAddToBasket.setDisable(false);
        }
        else {
            lblPrice.setText(cost + "+");
            btnAddToBasket.setDisable(true);
        }

        int amount = Integer.parseInt(lblIncriment.getText());
        int newAmount = Math.min(amount, item.getTotalAmount());

        if (tglImportExport.getToLeft().get()) item.setAmount(newAmount * -1);
        else item.setAmount(newAmount);

        if (amount != newAmount) lblIncriment.setText(newAmount + "");

        btnAdd.setDisable(false);
        btnMinus.setDisable(false);
        if (newAmount == 1) btnMinus.setDisable(true);
        else if (newAmount == item.getTotalAmount()) btnAdd.setDisable(true);

        if (tglImportExport.getToLeft().get()) btnAddToBasket.setText(btnBasketPrefix + " Import");
        else btnAddToBasket.setText(btnBasketPrefix + " Export");

        String totalCost = eurFormatter.format(price * newAmount);
        lblTotalPrice.setText(totalCost);
    }

    private void listenerToggleMat() {
        tglMaterial.getToLeft().addListener((observable, oldValue, newValue) -> {
            updateItem();
        });
    }
    private void listenerToggleImport() {
        tglImportExport.setToLeft(false);
        tglImportExport.getToLeft().addListener((observable, oldValue, newValue) -> {
            updateItem();
        });
    }

    EventHandler<MouseEvent> boxClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            try {
                Object source = event.getSource();
                StackPane box = (StackPane) source;

                boxSelected.setStyle("-fx-background-color: transparent");
                boxSelected = (VBox) box.lookup("#boxSelect");
                boxSelected.setStyle("-fx-background-color: #00000099");

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
    protected void btnMoreClick(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_popup.fxml"));
        Parent itemView = loader.load();

        VBox box = (VBox) itemView;

        box.setOnMouseClicked(hidePopupClick);

        box.lookup("#boxContainment").setOnMouseClicked(ignoreHideClick);

        ((Label) box.lookup("#lblIncrimentRestock")).setText(item.getRestock() + "");
        Button b = (Button) box.lookup("#btnMinusRestock");
        b.setOnAction(btnMinusRestockClick);
        ((Button) box.lookup("#btnAddRestock")).setOnAction(btnAddRestockClick);
        if (item.getRestock() == 1) b.setDisable(true);

        ((Button) box.lookup("#btnBlue")).setOnAction(btnUpdateStockClick);
        ((Button) box.lookup("#btnPrint")).setOnAction(btnPrintClick);

        panStacker.getChildren().add(box);
    }

    EventHandler<ActionEvent> btnMinusRestockClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();

                Node b = (Node) source;
                Label l = (Label) b.getParent().lookup("#lblIncrimentRestock");

                int newRestock = Integer.parseInt(l.getText()) - 1;

                l.setText(newRestock + "");
                if (newRestock == 1) b.setDisable(true);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<ActionEvent> btnAddRestockClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();

                Node b = (Node) source;
                Label l = (Label) b.getParent().lookup("#lblIncrimentRestock");

                int newRestock = Integer.parseInt(l.getText()) + 1;

                l.setText(newRestock + "");
                b.getParent().lookup("#btnMinusRestock").setDisable(false);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> btnPrintClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            String msg = "";
            String type;

            String[] sizeVals;
            if (isFlag) {
                sizeVals = new String[5];
                for (FLAG_SIZE size : FLAG_SIZE.values()) {
                    sizeVals[size.ordinal()] = FLAG_SIZE.getString(size);
                }
                type = "Flag";
            }
            else {
                sizeVals = new String[4];
                for (CUSHION_SIZE size : CUSHION_SIZE.values()) {
                    sizeVals[size.ordinal()] = CUSHION_SIZE.getString(size);
                }
                type = "Cushion";
            }

            for (String s : sizeVals) {
                if (Objects.equals(s, selectedSize)) {
                    msg = "Selected size for print: " + s + "\nInformation about this size: " + item.toString() + "\n\n" + msg;
                }
                else {
                    StockItem i = item.clone();
                    if (i instanceof Flag f) {
                        f.setSize(FLAG_SIZE.fromString(s));
                        f.setSizeID(FLAG_SIZE.getSizeId(f.getSize()));
                    }
                    else if (i instanceof Cushion c) {
                        c.setSize(CUSHION_SIZE.fromString(s));
                        c.setSizeID(CUSHION_SIZE.getSizeId(c.getSize()));
                    }
                    DatabaseControl.setStockData(i);
                    msg += "Information about size " + s + ": " + i.toString() + "\n";
                }
            }

            msg = "Information regarding " + loadedDesign.getName() + " " + type + " with ISO ID: " + item.getIsoID() + " and stock ID: " + item.getStockID() + "\n\n" + msg;

            try {
                File f = new File(loadedDesign.getName() + "_" + item.getSizeID() + ".txt");
                f.createNewFile();
                FileWriter fw = new FileWriter(loadedDesign.getName() + "_" + item.getSizeID() + ".txt");
                fw.write(msg);
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> btnUpdateStockClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                Node b = (Node) source;
                int amount = item.getTotalAmount();
                int restock = Integer.parseInt(((Label) b.getParent().getParent().lookup("#lblIncrimentRestock")).getText());

                if (restock < amount) {
                    Node box = new VBox();
                    if (item instanceof Flag f) box = panMain.lookup("#boxSize_" + FLAG_SIZE.getString(f.getSize()));
                    else if (item instanceof Cushion c) box = panMain.lookup("#boxSize_" + CUSHION_SIZE.getString(c.getSize()));

                    ((Label) box.lookup("#lblWarning")).setText("");
                    box.lookup("#boxWarning").setStyle("-fx-background-color: transparent;");

                }

                DatabaseControl.updateAmountAndRestock(item.getStockID(), item.getSizeID(), amount, restock);

                item.setTotalAmount(amount);
                item.setRestock(restock);
                lblAmountAndRestockUpdate(amount, restock);
                hidePopup();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<MouseEvent> ignoreHideClick = MouseEvent::consume;
    EventHandler<MouseEvent> hidePopupClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            try {
                hidePopup();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    private void hidePopup() {
        Node n = panStacker.lookup("#boxDarkening");
        panStacker.getChildren().remove(n);
    }

    @FXML
    protected void btnAddToBasketClick(ActionEvent event) throws Exception {
        Loader l = new Loader();
        if (loadedPos != null) items.set(loadedPos, item);
        else items.add(item);

        int newAmount = item.getTotalAmount() - item.getAmount();
        item.setTotalAmount(newAmount);
        DatabaseControl.updateAmountAndRestock(item.getStockID(), item.getSizeID(), newAmount, item.getRestock());

        l.showBasket(stage, items, operator);
    }

    private void populateInfo() {
        try {
            Image img = new Image("org/Assets/FlagsLarge/" + loadedDesign.getIsoID() + ".png");
            imgFlag.setFitWidth((int) (img.getWidth() * 0.25));
            imgFlag.setFitHeight((int) (img.getHeight() * 0.25));
            imgFlag.setImage(img);

            if (!(item instanceof Flag f) || (f.getSize() != FLAG_SIZE.HAND && f.getSize() != FLAG_SIZE.DESK)) {
                boxVerticalSize.setMaxHeight(imgFlag.getFitHeight());
                boxHorizontalSize.setMaxWidth(imgFlag.getFitWidth());

                String[] sizes = selectedSize.split("x");
                lblVerticalSize.setText(sizes[1]);
                lblHorizontalSize.setText(sizes[0] + "cm");
            }
            else {
                lblVerticalSize.setText("");
                lblHorizontalSize.setText("");
                boxVerticalSize.setMaxHeight(0);
                boxHorizontalSize.setMaxWidth(0);
            }

            double v1 = panImg.getWidth() == 0 ? panImg.getMinWidth() : panImg.getWidth();
            double h1 = panImg.getHeight() == 0 ? panImg.getMinHeight() : panImg.getHeight();

            double v = (v1 - imgFlag.getFitWidth() - 4) / 2;
            double h = (h1 - imgFlag.getFitHeight() - 4) / 2;

            boxVerticalMatch.setMinWidth(v);
            boxVerticalContainer.setMinWidth(v);
            boxHorizontalMatch.setMinHeight(h);
            boxHorizontalContainer.setMinHeight(h);
        }
        catch (Exception ignored) { }

        lblName.setText(loadedDesign.getName());

        Integer regionID = loadedDesign.getRegion();
        String regionName = regionID == null ? "" : DatabaseControl.getRegionName(regionID) + "\n";

        Integer typeID = loadedDesign.getType();
        String typeName = typeID == null ? "" : DatabaseControl.getTypeName(typeID);

        int totalAmount = item.getTotalAmount();
        int restock = item.getRestock();
        lblAmountAndRestockUpdate(totalAmount, restock);

        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        String cost = eurFormatter.format(DatabaseControl.getPrice(item.getSizeID()));
        lblCostToProduce.setText(cost);

        lblTags.setText(regionName + typeName);

        updateItem();
    }

    private void lblAmountAndRestockUpdate(int totalAmount, int restock) {
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
    }
}