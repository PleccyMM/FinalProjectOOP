package org.controllers;

import org.vexillum.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

/**
 * This class manages all switching between new screens, any screen switches must be done through this class
 */
public class BasketController extends ControllerParent {
    @FXML private StackPane panStacker;
    @FXML private BorderPane panMain;
    @FXML private VBox boxScroll;
    @FXML private Label lblExportSales;
    @FXML private Label lblExportCosts;
    @FXML private Label lblExportSubtotal;
    @FXML private Label lblImportCosts;
    @FXML private Label lblTotal;

    //These hashmaps are used to keep the items briefly seperated so that drawing them to the screen is easier, they in
    //no way replace or supersede the base items list in any way, nor do they store any unique or separate data
    //these can probably be replaced with just ArrayLists due to changes with the internal parts of StockItem
    private HashMap<Integer, StockItem> importItems = new HashMap<>();
    private HashMap<Integer, StockItem> exportItems = new HashMap<>();

    @Override
    protected void stageChangeHandle() {}

    public void load(Stage stage, List<StockItem> items, Operator operator) {
        try {
            HBox headerBox = (HBox) panMain.lookup("#boxHeader");
            if (headerBox == null) { throw new Exception(); }

            loadHeader(stage, operator, items, headerBox, new SearchConditions());
            createItems();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs the logic to set up the screen to add items, but doesn't actually add the items themselves, that is instead
     * delegated to {@code addItem}
     * @throws Exception
     */
    private void createItems() throws Exception {
        int index = 0;
        //For loop separates the imports and exports into separate Hashmaps ready for drawing individually
        for (StockItem i : getItems()) {
            if (i.isImport()) importItems.put(index, i);
            else exportItems.put(index, i);
            index++;
        }

        //basket_divider.fxml is basically just an empty file that is used to put he big EXPORT & IMPORT labels, keeping it there
        //just removes the need for a function in here and keeps consistency with no javaFX elements being defined in runtime
        FXMLLoader loader = new FXMLLoader(getClass().getResource("basket_divider.fxml"));
        Parent itemView = loader.load();
        HBox box = (HBox) itemView;
        ((Label) box.lookup("#lblImportExport")).setText("Imports");
        boxScroll.getChildren().clear();
        boxScroll.getChildren().add(box);
        //Imported items are added before any export
        addItem(importItems);

        loader = new FXMLLoader(getClass().getResource("basket_divider.fxml"));
        itemView = loader.load();
        box = (HBox) itemView;
        ((Label) box.lookup("#lblImportExport")).setText("Exports");
        boxScroll.getChildren().add(box);
        //Export added last
        addItem(exportItems);

        openDB();
        calculateTotalCost();
        closeDB();
    }

    /**
     * Used to actually draw the items to screen, this does it one at a time and doesn't take from {@code list} but instead
     * a partial hashmap constructed from {@code list}
     * @param itemsMap either the imports or exports currently held in {@code items}
     * @throws IOException loading the fxml and images may fail
     */
    private void addItem(HashMap<Integer, StockItem> itemsMap) throws IOException {
        for(var item : itemsMap.entrySet()) {
            StockItem i = item.getValue();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("basket_item.fxml"));
            Parent itemView = loader.load();
            HBox box = (HBox) itemView;

            String designPath = "org/Assets/FlagsSmall/" + i.getIsoID() + ".png";
            Image design = new Image(designPath);

            //If the image isn't a flag then the design must be masked and overlayed on a cushion, equally if it is a long cushion there's a separate mask that must be invoked
            Image img = !(i instanceof Cushion c) ? design :
                    c.getSize() != CUSHION_SIZE.LONG ?
                            Masker.standardCushion(true, designPath) : Masker.longCushion(true, designPath);
            ((ImageView) box.lookup("#imgDesign")).setImage(img);

            VBox imageHolder = (VBox) box.lookup("#imageHolder");
            //This check to see if they equal is basically another quick check to see if it's a cushion or not, if it's not
            //a cushion the border can be drawn, otherwise a rectangular border doesn't work on the non-quadrilateral cushion shape
            if (!img.equals(design)) imageHolder.setStyle("-fx-border-width: 2; -fx-border-color: #FFFFFF");
            else imageHolder.setStyle("-fx-border-width: 2; -fx-border-color: #000000");

            String name = i.getName();

            if (i instanceof Flag f) {
                name += " Flag ";
                name += FLAG_SIZE.getString(f.getSize());
            }
            else if (i instanceof Cushion c) {
                name += " Cushion ";
                name += CUSHION_SIZE.getString(c.getSize());
            }

            ((Label) box.lookup("#lblName")).setText(name);

            setCosts(box, i);

            ((Button) box.lookup("#btnInformation")).setOnAction(btnInfoClick);
            ((Label) box.lookup("#lblIncrement")).setText(i.getPrintAmount() + "");
            ((Button) box.lookup("#btnMinus")).setOnAction(btnMinusClick);
            ((Button) box.lookup("#btnAdd")).setOnAction(btnAddClick);

            ((Button) box.lookup("#btnEdit")).setOnAction(btnEditClick);

            if (i.isImport()) {
                box.getChildren().remove(box.lookup("#btnInformation"));
            }

            //hashCodes are always unique, see StockItem.equals(), StockItem.hashCode(), and ControllerParent.itemMerge() for more information
            box.setId(i.hashCode() + "");
            boxScroll.getChildren().add(box);
        }
    }

    /**
     * Sets the cost of an individual item, not all of them
     * @param b the item's HBox container
     * @param i the item itself in {@code items}
     */
    private void setCosts(Node b, StockItem i) {
        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        double price = i.calculatePrice();
        String cost = eurFormatter.format(price);
        String subtotal = eurFormatter.format(price * i.getPrintAmount());

        ((Label) b.lookup("#lblPriceSingle")).setText(cost);
        ((Label) b.lookup("#lblSubtotal")).setText(subtotal);
    }

    /**
     * Sets the total costs of all items
     */
    private void calculateTotalCost() {
        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);

        double exportSales = 0;
        double exportCosts = 0;
        double importCosts = 0;
        //Import and export being separate is once again useful for separating their costs
        for (var item : exportItems.entrySet()) {
            StockItem i = item.getValue();
            exportSales += i.calculatePrice() * i.getAmount();
            exportCosts += getDatabase().getPrice(i.getSizeID()) * i.getAmount();
        }
        for (var item : importItems.entrySet()) {
            StockItem i = item.getValue();
            importCosts += i.calculatePrice() * i.getPrintAmount();
        }

        lblExportSales.setText(eurFormatter.format(exportSales));
        lblExportCosts.setText(eurFormatter.format(exportCosts));
        lblExportSubtotal.setText(eurFormatter.format(exportSales - exportCosts));
        lblImportCosts.setText(eurFormatter.format(importCosts));
        lblTotal.setText(eurFormatter.format(exportSales - exportCosts - importCosts));
    }

    /**
     * Checkout clears all the items in the basket currently and performs the needed database updates for imports, as exports
     * are already removed from the database to stop over-adding
     */
    @FXML
    protected void btnCheckoutClick(ActionEvent event) throws Exception {
        openDB();
        for(StockItem i : getItems()) {
            if (i.isImport()) {
                getDatabase().updateAmountAndRestock(i.getStockID(), i.getSizeID(), i.getTotalAmount() + i.getPrintAmount(), i.getRestock());
            }
            Node b = boxScroll.lookup("#" + i.hashCode());
            boxScroll.getChildren().remove(b);
        }

        itemsClear();
        importItems.clear();
        exportItems.clear();
        calculateTotalCost();
        closeDB();
    }

    /**
     * Locates the index of an item from a hash code, this is different from {@code findHash()} which returns a {@code StockItem}
     * @param hash the hash code that is being searched for
     * @return the index of the item if found, otherwise -1
     */
    private int locateIndex(int hash) {
        for (int i = 0; i < itemsSize(); i++) {
            if (getItems(i).hashCode() == hash) {
                return i;
            }
        }

        System.out.println("Failure to find " + hash + ":\n");
        for (StockItem item : getItems()) {
            System.out.println(item.hashCode());
        }

        return -1;
    }

    /**
     * Creates a pop-up box showing additional information about items
     */
    EventHandler<ActionEvent> btnInfoClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                Node n = (Node) source;
                Node itemBox = n.getParent();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("basket_popup.fxml"));
                Parent itemView = loader.load();

                VBox box = (VBox) itemView;

                box.setOnMouseClicked(hidePopupClick);

                box.lookup("#boxContainment").setOnMouseClicked(ignoreHideClick);
                box.lookup("#btnBlue").setOnMouseClicked(hidePopupClick);

                StockItem item = findItemHash(Integer.parseInt(itemBox.getId()));
                if (item instanceof Cushion c) {
                    Label lblAddD = (Label) box.lookup("#lblAdditionalDesc");
                    lblAddD.setText("Filling:");
                    Label lblAddV = (Label) box.lookup("#lblAdditionalVal");
                    lblAddV.setText(c.getMaterial().toString());

                    //For the cushion only the one part matters, so the second half of the box can be hidden
                    Label lblMatD = (Label) box.lookup("#lblMaterialDesc");
                    Label lblMatV = (Label) box.lookup("#lblMaterialVal");
                    lblMatD.setDisable(true);
                    lblMatV.setDisable(true);
                }
                else if (item instanceof Flag f) {
                    Label lblAddD = (Label) box.lookup("#lblAdditionalDesc");
                    lblAddD.setText("Hoist:");
                    Label lblAddV = (Label) box.lookup("#lblAdditionalVal");
                    lblAddV.setText(!f.isSmall() ? f.getHoist().toString() : "N/A");

                    Label lblMatD = (Label) box.lookup("#lblMaterialDesc");
                    lblMatD.setDisable(false);
                    lblMatD.setText("Material:");
                    Label lblMatV = (Label) box.lookup("#lblMaterialVal");
                    lblMatV.setDisable(false);
                    lblMatV.setText(f.getMaterial().toString());
                }

                //panStacker as this needs to be an overlay
                panStacker.getChildren().add(box);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * Removes the parent of the entire pop-up, removing it entirely
     */
    private void hidePopup() {
        Node n = panStacker.lookup("#boxDarkening");
        panStacker.getChildren().remove(n);
    }

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

    /*
    The following 2 handlers are a bit messy and similar in many ways, though btnMinusClick has some additional quirks which are mentioned
     */

    EventHandler<ActionEvent> btnMinusClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                Node n = (Node) source;
                Node box = n.getParent().getParent();
                Label l = (Label) box.lookup("#lblIncrement");


                //Updates the value ready for display and setting
                int newVal = Integer.parseInt(l.getText()) - 1;
                int hashVal = Integer.parseInt(box.getId());
                int index = locateIndex(hashVal);
                if (index < 0) {
                    System.out.println("Error at minus click");
                    return;
                }


                StockItem i = getItems(index);
                openDB();

                //Logic for removing items if they are set to 0, initially just wipes it from internal and UI, then gets all new costs
                if (newVal == 0) {

                    if (i.isImport()) importItems.remove(Collections.binarySearch(getItems(), i));
                    else exportItems.remove(Collections.binarySearch(getItems(), i));
                    removeItem(i);

                    boxScroll.getChildren().remove(box);
                    calculateTotalCost();

                    closeDB();
                    return;
                }

                if (i.isImport()) i.setAmount(newVal * -1);
                else {

                    //Only updates the database if it's being exported, as the amount in storage needs to be propped back up
                    getDatabase().updateAmountAndRestock(i.getStockID(), i.getSizeID(), i.getTotalAmount() + 1, i.getRestock());
                    i.setAmount(newVal);
                    i.setTotalAmount(i.getTotalAmount() + 1);
                }

                l.setText(newVal + "");

                //Just make sure adding is possible again, as it must be if it was already this high
                box.lookup("#btnAdd").setDisable(false);
                setCosts(box, i);
                calculateTotalCost();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                closeDB();
            }
        }
    };
    EventHandler<ActionEvent> btnAddClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                Node n = (Node) source;
                Node box = n.getParent().getParent();
                Label l = (Label) box.lookup("#lblIncrement");

                int newVal = Integer.parseInt(l.getText()) + 1;
                l.setText(newVal + "");

                int hashVal = Integer.parseInt(box.getId());
                int index = locateIndex(hashVal);
                if (index < 0) {
                    return;
                }

                StockItem i = getItems(index);
                openDB();

                if (i.isImport()) i.setAmount(newVal * -1);
                else {
                    getDatabase().updateAmountAndRestock(i.getStockID(), i.getSizeID(), i.getTotalAmount() - 1, i.getRestock());
                    i.setAmount(newVal);
                    i.setTotalAmount(i.getTotalAmount() - 1);
                }

                //Only caps the add button at total stock if the item is being exported, otherwise it's fine
                if (i.isExport() && i.getTotalAmount() == 0) {
                    n.setDisable(true);
                }

                setCosts(box, i);
                calculateTotalCost();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally {
                closeDB();
            }
        }
    };

    EventHandler<ActionEvent> btnEditClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();

                HBox box = (HBox) ((Node) source).getParent();

                int hashVal = Integer.parseInt(box.getId());
                int index = locateIndex(hashVal);
                if (index < 0) {
                    return;
                }

                //Prep for loading the ItemController
                boolean isFlag = getItems(index) instanceof Flag;
                openDB();
                Design d = getDatabase().getDeignFromIso(getItems(index).getIsoID());
                closeDB();

                //Loads the ItemController, importantly providing it with an index for the item's location
                l.showItem(stage, getItems(), operator, d, isFlag, index);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}
