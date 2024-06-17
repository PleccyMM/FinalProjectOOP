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

/**
 * By far the largest controller, used to display individual items prior to them being added to the export/import, also
 * when they are being edited. There is a lot of moving parts here as it has the most amount of unique information
 * being displayed to the user and basically no procedural fxml loads
 */
public class ItemController extends ControllerParent {
    //All used fx:ids defined item_screen.fxml
    @FXML private StackPane panStacker;
    @FXML private BorderPane panMain;
    @FXML private ImageView imgFlag;
    @FXML private VBox imageHolder;

    @FXML private BorderPane panImg;
    @FXML private HBox boxVerticalContainer;
    @FXML private Label lblVerticalSize;
    @FXML private VBox boxVerticalSize;
    @FXML private VBox boxHorizontalContainer;
    @FXML private Label lblHorizontalSize;
    @FXML private VBox boxHorizontalSize;
    @FXML private HBox boxVerticalMatch;
    @FXML private VBox boxHorizontalMatch;

    @FXML private Label lblDesignName;
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
    @FXML private Label lblIncrement;
    @FXML private ComboBox cmbModifications;
    @FXML private ToggleSwitch tglMaterial;
    @FXML private ToggleSwitch tglImportExport;

    @FXML private Button btnAddToBasket;

    //The few additional attributes, mainly relating to design, the important one is item which is changed throughout this
    //entire class to become the new item to be added to basket
    private Design loadedDesign;
    private Integer loadedPos;
    private Boolean isFlag;
    private StockItem item;
    private String selectedSize;
    //boxSelected is just a partially transparent box used to highlight the currently selected item, it always references
    //a usually non-visible box with the same ID found inside item_item.fxml
    private VBox boxSelected;
    //btnBasketPrefix is either "Add" or "Update", depending on if the window was accessed from the editing screen or main screen
    private String btnBasketPrefix;

    /**
     * This is the only implemented use of {@code stageChangeHandle()}, its role here is to ensure that if the user is
     * editing an existing item but leaves early the database keeps the {@code totalAmount} that was present going in. The
     * reason this is an abstract in the parent is because all screen leaving that doesn't terminate the program from this page happens
     * within the header, which is controlled by {@code ControllerParent} and thus must be known within that class
     * <p>
     * Example scenario: the user edits an item being exported, the amount of that item is thus added back into the database
     * ready for the editing, the user changes the values a bit and leaves. The {@code items} list is not updated but the database
     * has been restocked, effectively upping the amount in stock unnaturally. By reverting to how it was when they began editing
     * the issue is negated
     */
    @Override
    protected void stageChangeHandle() {
        if (loadedPos == null) return;

        item = getItems(loadedPos);
        openDB();
        getDatabase().updateAmountAndRestock(item.getStockID(), item.getSizeID(), item.getTotalAmount(), item.getRestock());
        closeDB();
    }

    public void load(Stage stage, Operator operator, List<StockItem> items, Design loadedDesign, Boolean isFlag, Integer loadedPos) {
        this.loadedDesign = loadedDesign;
        this.loadedPos = loadedPos;
        this.isFlag = isFlag;

        try {
            openDB();

            HBox headerBox = (HBox) panMain.lookup("#boxHeader");
            if (headerBox == null) { throw new Exception(); }
            loadHeader(stage, operator, items, headerBox, new SearchConditions());

            //Due to the large amount of set up logic, the creation of the page is divided across several methods
            typeSetUp();
            listenerToggleMat();
            listenerToggleImport();

            if (loadedPos != null) {
                if (item.isExport()) {
                    int newAmount = item.getTotalAmount() + item.getAmount();
                    item.setTotalAmount(newAmount);
                    getDatabase().updateAmountAndRestock(item.getStockID(), item.getSizeID(), newAmount, item.getRestock());
                }

                setUpOptions();
                btnBasketPrefix = "Update";
                openDB();
            }
            else {
                btnBasketPrefix = "Add to";
            }
            populateInfo();
            updateItem();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeDB();
        }
    }

    /**
     * This is the initial piece of logic in setting up the page, the fxml file mostly defaults to being set up with options
     * relating to flags and not cushions, so most of the logic here is changing the data out if the item is a cushion.
     * Also defaults the size to the smallest and creates/loads the item initially, depending on if its being edited or not
     * @throws Exception invokes {@code createSizeSelection} which can crash due to loading fxml
     */
    private void typeSetUp() throws Exception {
        if (isFlag) {
            selectedSize = "Hand";
            item = loadedPos != null ? getItems(loadedPos).clone() : getDatabase().createFlag(loadedDesign.getIsoID(), FLAG_SIZE.HAND);
        }
        else {
            selectedSize = "45x45cm";
            lblToggleL.setText("With filling (\u00A38+)");
            lblToggleR.setText("No filling");
            cmbModifications.getItems().clear();
            cmbModifications.getItems().addAll("Foam (\u00A38.00)", "Polyester (\u00A39.00)", "Feathers (\u00A311.00)", "Cotton (\u00A312.00)");
            cmbModifications.setPromptText("Cushion Filling");
            item = loadedPos != null ? getItems(loadedPos).clone() : getDatabase().createCushion(loadedDesign.getIsoID(), CUSHION_SIZE.SMALL, CUSHION_MATERIAL.EMPTY);
        }
        createSizeSelection();
    }

    /**
     * Creates the size selections on the left hand side of the screen
     * @throws Exception loading fxml and images may fail
     */
    private void createSizeSelection() throws Exception {
        VBox itemBox = (VBox) panMain.lookup("#boxItemStore");
        if (itemBox == null) { throw new Exception(); }

        //Flags have 1 more size than cushions, the nested for loops populate sizeVals with the String versions of the sizes,
        //so they can be used in the labels
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

        //firstRun is used just to make sure the highlighted size is correctly placed when first booting the screen
        boolean firstRun = true;
        boolean[] needsRestocking = getDatabase().restockList(item.getStockID());
        int index = 0;
        for (String sizeVal : sizeVals) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/item_item.fxml"));
            Parent itemView = loader.load();

            StackPane box = (StackPane) itemView;
            box.setId("boxSize_" + sizeVal);
            box.setOnMouseClicked(boxClick);

            if (firstRun) {
                firstRun = false;
                boxSelected = (VBox) box.lookup("#boxSelect");
                boxSelected.setVisible(true);
            }

            try {
                Image img = new Image(this.getClass().getResourceAsStream("/Assets/FlagsSmall/" + loadedDesign.getIsoID() + ".png"));
                ImageView imgView = (ImageView) box.lookup("#imgDesign");

                imgView.setFitWidth((int) (img.getWidth() * 0.5));
                imgView.setFitHeight((int) (img.getHeight() * 0.5));
                imgView.setImage(img);
            }
            catch (Exception ignored) { }

            ((Label) box.lookup("#lblSize")).setText(sizeVal);

            //Creates the relevant warning if the total amount has fallen beneath the restock limit
            if (needsRestocking[index]) {
                ((Label) box.lookup("#lblRestockWarning")).setText("RESTOCK");
                box.lookup("#boxWarning").setStyle("-fx-background-color: #FF0000;");
            }

            itemBox.getChildren().add(box);
            index++;
        }
    }

    /**
     * Used for the initial construction of the page when an item is being edited, and thus has some options that need
     * to be set, instead of the usual defaults
     */
    private void setUpOptions() {
        boxSelected.setVisible(false);

        lblIncrement.setText(item.getPrintAmount() + "");
        var s = cmbModifications.getSelectionModel();

        if (item instanceof Flag f) {
            selectedSize = FLAG_SIZE.getString(f.getSize());
            Node n = panMain.lookup("#boxSize_" + FLAG_SIZE.getString(f.getSize()));
            boxSelected = (VBox) n.lookup("#boxSelect");

            tglImportExport.setToLeft(item.isImport());

            switch (f.getHoist()) {
                case NONE -> s.select(0);
                case FABRIC -> s.select(1);
                case METAL -> s.select(2);
                case WOODEN -> s.select(3);
            }
            //Since the materials are different for the two smallest, a conditional is necessary here
            if (f.isSmall()) {
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

            tglImportExport.setToLeft(item.isImport());

            tglMaterial.setToLeft(!c.isJustCase());

            switch (c.getMaterial()) {
                case FOAM -> s.select(0);
                case POLYESTER -> s.select(1);
                case FEATHERS -> s.select(2);
                case COTTON -> s.select(3);
            }
        }

        boxSelected.setVisible(true);
    }

    /**
     * Stupidly long when compared to how the others are divided, maybe look to split up?
     * <p>
     * This method is responsible for updating the item itself depending on the selected attributes, along with changing some of the information
     * on screen as well
     */
    private void updateItem() {
        //This initial section deals with the unique parts of the two items, before joining again to do common stuff
        if (item instanceof Flag f) {
            FLAG_SIZE fs = FLAG_SIZE.fromString(selectedSize);
            f.setSize(fs);
            f.setSizeID(FLAG_SIZE.getSizeId(fs));

            if (f.isSmall()) {
                lblToggleL.setText("Paper");
                lblToggleR.setText("Polyester (\u00A31)");
            }
            else {
                lblToggleL.setText("Polyester (\u00A31)");
                lblToggleR.setText("Nylon (\u00A33)");
            }

            //By removing the pricing info off the end, the string can be sent into the enum to get a value
            if (tglMaterial.getToLeft().get()) {
                f.setMaterial(FLAG_MATERIAL.getType(lblToggleL.getText().split(" ")[0]));
            }
            else {
                f.setMaterial(FLAG_MATERIAL.getType(lblToggleR.getText().split(" ")[0]));
            }

            if (!f.isSmall()) {
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

        getDatabase().setStockData(item);

        //This conditional operator is used to reset the text in lblIncrement back to 1, after having been on an item with
        //noting left in stock
        int amount = Objects.equals(lblIncrement.getText(), "0") ? 1 : Integer.parseInt(lblIncrement.getText());
        //This conditional operator prevents switching from an item that has more stock and then back here, or importing over
        //total stock then setting back to export. Of course, this is not needed if the item is being imported, so that's
        //why the first check is performed
        int newAmount = tglImportExport.getToLeft().get() ? amount : Math.min(amount, item.getTotalAmount());

        //amount < 0 is import, > 0 is export
        if (tglImportExport.getToLeft().get()) item.setAmount(newAmount * -1);
        else item.setAmount(newAmount);

        lblIncrement.setText(newAmount + "");

        //Logic for capping, if the value is too low or if matching the total stock whilst exporting
        btnAdd.setDisable(false);
        btnMinus.setDisable(false);
        if (newAmount <= 1) btnMinus.setDisable(true);
        if (newAmount >= item.getTotalAmount() && item.getAmount() >= 0) btnAdd.setDisable(true);

        if (tglImportExport.getToLeft().get()) btnAddToBasket.setText(btnBasketPrefix + " Import");
        else btnAddToBasket.setText(btnBasketPrefix + " Export");

        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        double price = item.calculatePrice();
        String cost = eurFormatter.format(price);

        tglMaterial.setDisable(false);
        //So if the item is missing essential information a full price cannot be given and it cannot be added to basket
        //This occurs only with exports and only could occur if cmbModifications hasn't been filled - though if it's a cushion and
        //tglMaterial is set to the right (is just a case) then cmbModifications doesn't matter. Equally small flag sizes cannot
        //have anything selected in cmbModifications, so they should also be ignored there
        if (item.isImport() || !cmbModifications.getSelectionModel().isEmpty() ||
                (!isFlag && !tglMaterial.getToLeft().get()) ||
                    (item instanceof Flag f && (f.isSmall()))) {
            lblPrice.setText(cost);
            btnAddToBasket.setDisable(false);
        }
        else {
            lblPrice.setText(cost + "+");
            btnAddToBasket.setDisable(true);
        }

        //Ultimately the cleanest place for this IF
        if (item.isImport()) {
            cmbModifications.setDisable(true);
            tglMaterial.setDisable(true);
        }

        //Used to ensure that options are completely disabled when the stock is empty, checking for imports is not needed
        //as logic above that sets the button back to 1 when it sees it is at 0 ensures this never happens
        if (item.getTotalAmount() <= 0 && item.getAmount() >= 0) btnAddToBasket.setDisable(true);

        String totalCost = eurFormatter.format(price * newAmount);
        lblTotalPrice.setText(totalCost);
    }

    private void listenerToggleMat() {
        tglMaterial.getToLeft().addListener((observable, oldValue, newValue) -> {
            openDB();
            updateItem();
            closeDB();
        });
    }
    private void listenerToggleImport() {
        //tglImportExport should be set to the right by default, doing it here stops the listener invoking too early
        tglImportExport.setToLeft(false);
        tglImportExport.getToLeft().addListener((observable, oldValue, newValue) -> {
            openDB();
            updateItem();
            closeDB();
        });
    }

    /**
     * Deals with invoking the correct parts to change size, also updates visual display for size selection
     */
    EventHandler<MouseEvent> boxClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            try {
                Object source = event.getSource();
                StackPane box = (StackPane) source;

                boxSelected.setVisible(false);
                boxSelected = (VBox) box.lookup("#boxSelect");
                boxSelected.setVisible(true);

                //Each box has the sizeID as the second half of their ID
                selectedSize = box.getId().split("_")[1];
                openDB();
                updateItem();
                populateInfo();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                closeDB();
            }
        }
    };

    @FXML
    protected void cmbModificationsChange(ActionEvent event) {
        openDB();
        updateItem();
        closeDB();
    }

    @FXML
    protected void btnMinusClick(ActionEvent event) {
        int val = Integer.parseInt(lblIncrement.getText());
        val -= 1;

        lblIncrement.setText(String.valueOf(val));

        openDB();
        updateItem();
        closeDB();
    }

    @FXML
    protected void btnAddClick(ActionEvent event) {
        int val = Integer.parseInt(lblIncrement.getText());
        val += 1;

        lblIncrement.setText(String.valueOf(val));

        openDB();
        updateItem();
        closeDB();
    }

    @FXML
    protected void btnAddToBasketClick(ActionEvent event) throws Exception {
        Loader l = new Loader();
        if (loadedPos != null) setItem(loadedPos, item);
        else addItem(item);

        if (item.isExport()) {
            int newAmount = item.getTotalAmount() - item.getAmount();
            item.setTotalAmount(newAmount);

            openDB();
            getDatabase().updateAmountAndRestock(item.getStockID(), item.getSizeID(), newAmount, item.getRestock());
            closeDB();
        }
        l.showBasket(stage, getItems(), operator);
    }

    /**
     * Method used to deal with all the necessary logic when changing sizes, since the display image and information
     * requires updating
     */
    private void populateInfo() {
        try {
            //Cushions have different designs created procedurally when needed, so some additional logic is needed
            String designPath = this.getClass().getResource("/Assets/FlagsLarge/" + loadedDesign.getIsoID() + ".png").toString();
            Image design = new Image(designPath);
            Image img = !(item instanceof Cushion c) ? design :
                    c.getSize() != CUSHION_SIZE.LONG ?
                            Masker.standardCushion(false, designPath, this.getClass()) : Masker.longCushion(false, designPath, this.getClass());

            //Since the images are so large they need shrinking a bit, 25% seemed good whilst retaining quality
            imgFlag.setFitWidth((int) (img.getWidth() * 0.25));
            imgFlag.setFitHeight((int) (img.getHeight() * 0.25));
            imgFlag.setImage(img);

            if (!img.equals(design)) imageHolder.setStyle("-fx-border-width: 2; -fx-border-color: #EEEEEE");
            else imageHolder.setStyle("-fx-border-width: 2; -fx-border-color: #000000");

            //Deals with creating the measurement lines, only the smaller flag sizes don't need them
            if (!(item instanceof Flag f) || (!f.isSmall())) {
                boxVerticalSize.setMaxHeight(imgFlag.getFitHeight());
                boxHorizontalSize.setMaxWidth(imgFlag.getFitWidth());

                String[] sizes = selectedSize.split("x");
                lblVerticalSize.setText(sizes[1]);
                lblHorizontalSize.setText(sizes[0] + "cm");
            }
            else {
                lblVerticalSize.setText("");
                lblHorizontalSize.setText("");
                //Setting them to 0 effectively hides them but prevents messing up the entire organisation
                boxVerticalSize.setMaxHeight(0);
                boxHorizontalSize.setMaxWidth(0);
            }

            //To ensure that the image appears centred a borderpane is used to store the image itself, since the centre
            //of a borderpane expands to fill anything not taken by the other children.
            //By filling the top, bottom, left, and right so they are touching the image on all sides the lines can be
            //drawn either side without the flag being de-centred. The logic below does this with respect for the size of
            //the screen at the given time.
            //Since the left and bottom are filled by the previously seen parts, lbl/boxVertical/HorizontalSize, they have already
            //got the width/height that must be matched. By setting this as dictated by the centre point of the image they can
            //offset the pushing produced by the previous ones.

            double v1 = panImg.getWidth() == 0 ? panImg.getMinWidth() : panImg.getWidth();
            double h1 = panImg.getHeight() == 0 ? panImg.getMinHeight() : panImg.getHeight();

            //-4 to account for border
            double v = (v1 - imgFlag.getFitWidth() - 4) / 2;
            double h = (h1 - imgFlag.getFitHeight() - 4) / 2;

            boxVerticalMatch.setMinWidth(v);
            boxVerticalContainer.setMinWidth(v);
            boxHorizontalMatch.setMinHeight(h);
            boxHorizontalContainer.setMinHeight(h);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        lblDesignName.setText(loadedDesign.getName());

        Integer regionID = loadedDesign.getRegion();
        String regionName = regionID == null ? "" : getDatabase().getRegionName(regionID) + "\n";

        Integer typeID = loadedDesign.getType();
        String typeName = typeID == null ? "" : getDatabase().getTypeName(typeID);

        int totalAmount = item.getTotalAmount();
        int restock = item.getRestock();
        lblAmountAndRestockUpdate(totalAmount, restock);

        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        Double cost = getDatabase().getPrice(item.getSizeID());
        lblCostToProduce.setText(eurFormatter.format(cost) + " - (" + eurFormatter.format(cost * totalAmount) + ")");

        lblTags.setText(regionName + typeName);

        updateItem();
    }

    /**
     * Method to update the image at the bottom of the screen displaying the current stock amount
     * @param totalAmount how much stock in total is in the system
     * @param restock what is the limit for requiring a restock
     */
    private void lblAmountAndRestockUpdate(int totalAmount, int restock) {
        lblCurrentStock.setText(totalAmount + "");
        lblRestock.setText(restock + "");
        try {
            String severityImg;
            //Red for equal to or less, Amber for half or less, green for half or above
            if (totalAmount <= restock) { severityImg = "IndicatorBad"; }
            else if (totalAmount * 0.5 <= restock) { severityImg = "IndicatorMid"; }
            else { severityImg =  "IndicatorGood"; }

            Image img = new Image(this.getClass().getResourceAsStream("/Assets/Icons/" + severityImg + ".png"));
            imgSeverity.setFitWidth(12);
            imgSeverity.setFitHeight(12);
            imgSeverity.setImage(img);
        }
        catch (Exception ignored) {}
    }

    /*
    Everything below this point just deals with the popup, and its related events
     */

    /**
     * Handles creating the popup when more information is requested
     */
    @FXML
    protected void btnMoreClick(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/item_popup.fxml"));
        Parent itemView = loader.load();

        VBox box = (VBox) itemView;

        box.setOnMouseClicked(hidePopupClick);

        box.lookup("#boxContainment").setOnMouseClicked(ignoreHideClick);

        ((Label) box.lookup("#lblIncrementRestock")).setText(item.getRestock() + "");
        Button b = (Button) box.lookup("#btnMinusRestock");
        b.setOnAction(btnMinusRestockClick);
        ((Button) box.lookup("#btnAddRestock")).setOnAction(btnAddRestockClick);
        if (item.getRestock() == 1) b.setDisable(true);

        ((Button) box.lookup("#btnUpdateStock")).setOnAction(btnUpdateStockClick);
        ((Button) box.lookup("#btnPrint")).setOnAction(btnPrintClick);

        panStacker.getChildren().add(box);
    }

    /*
    These click event handlers are used for the popup box, not the main amount modifier
     */

    EventHandler<ActionEvent> btnMinusRestockClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();

                Node b = (Node) source;
                Label l = (Label) b.getParent().lookup("#lblIncrementRestock");

                int newRestock = Integer.parseInt(l.getText()) - 1;

                l.setText(newRestock + "");
                //Ensures that the restock limit cannot go beneath 1
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
                Label l = (Label) b.getParent().lookup("#lblIncrementRestock");

                int newRestock = Integer.parseInt(l.getText()) + 1;

                l.setText(newRestock + "");
                b.getParent().lookup("#btnMinusRestock").setDisable(false);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * Print information button, fairly simple just long due to all the text, creates a file with information on every
     * size for either the flag or cushion, depending on what is selected
     */
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

            NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);

            double totalValue = 0;
            double totalSell = 0;

            openDB();
            for (String s : sizeVals) {
                StockItem i = item.clone();
                if (i instanceof Flag f) {
                    f.setSize(FLAG_SIZE.fromString(s));
                    f.setSizeID(FLAG_SIZE.getSizeId(f.getSize()));
                }
                else if (i instanceof Cushion c) {
                    c.setSize(CUSHION_SIZE.fromString(s));
                    c.setSizeID(CUSHION_SIZE.getSizeId(c.getSize()));
                }
                getDatabase().setStockData(i);

                double thisValue = i.getCostToProduce() * i.getTotalAmount();
                double thisSell = i.calculatePrice() * i.getTotalAmount();
                totalValue += thisValue;
                totalSell += thisSell;

                msg += "Information about size " + s + ": " + i + "Cost of held stock: " + eurFormatter.format(thisValue)
                        + "\nValue of held stock: " + eurFormatter.format(thisSell) + "\n\n";
            }
            closeDB();

            //Once again msg is appended on the end so important information is put first
            msg = "Information regarding " + loadedDesign.getName() + " " + type + " with ISO ID: " + item.getIsoID() +
                    " and stock ID: " + item.getStockID() + "\n" + "Total cost of held stock: " + eurFormatter.format(totalValue) +
                    "\nTotal value of held stock: " + eurFormatter.format(totalSell) + "\n\n" + msg;

            String fileType = item instanceof Flag f ? "Flag" : "Cushion";
            try {
                File f = new File(loadedDesign.getName() + "_" + fileType + ".txt");
                f.createNewFile();
                FileWriter fw = new FileWriter(loadedDesign.getName() + "_" + fileType + ".txt");
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
                int restock = Integer.parseInt(((Label) b.getParent().getParent().lookup("#lblIncrementRestock")).getText());

                //Updates warning sign if needed
                if (restock < amount) {
                    Node box = new VBox();
                    if (item instanceof Flag f) box = panMain.lookup("#boxSize_" + FLAG_SIZE.getString(f.getSize()));
                    else if (item instanceof Cushion c) box = panMain.lookup("#boxSize_" + CUSHION_SIZE.getString(c.getSize()));

                    ((Label) box.lookup("#lblRestockWarning")).setText("");
                    box.lookup("#boxWarning").setStyle("-fx-background-color: transparent;");

                }

                openDB();
                getDatabase().updateAmountAndRestock(item.getStockID(), item.getSizeID(), amount, restock);
                closeDB();

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
}