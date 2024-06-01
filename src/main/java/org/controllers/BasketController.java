package org.controllers;

import org.vexillum.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javax.swing.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;

public class BasketController extends ControllerParent {
    @FXML private BorderPane panMain;
    @FXML private VBox boxScroll;
    @FXML private Label lblExportSales;
    @FXML private Label lblExportCosts;
    @FXML private Label lblExportSubtotal;
    @FXML private Label lblImportCosts;
    @FXML private Label lblTotal;

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

    private void createItems() throws Exception {
        int index = 0;

        for (StockItem i : items) {
            if (i.getAmount() < 0) importItems.put(index, i);
            else exportItems.put(index, i);
            index++;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("basket_divider.fxml"));
        Parent itemView = loader.load();
        HBox box = (HBox) itemView;
        ((Label) box.lookup("#lblImportExport")).setText("Imports");
        boxScroll.getChildren().add(box);
        addItem(importItems);

        loader = new FXMLLoader(getClass().getResource("basket_divider.fxml"));
        itemView = loader.load();
        box = (HBox) itemView;
        ((Label) box.lookup("#lblImportExport")).setText("Exports");
        boxScroll.getChildren().add(box);
        addItem(exportItems);

        calculateTotalCost();
    }

    private void addItem(HashMap<Integer, StockItem> itemsMap) throws IOException {
        for(var item : itemsMap.entrySet()) {
            Integer index = item.getKey();
            StockItem i = item.getValue();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("basket_item.fxml"));
            Parent itemView = loader.load();
            HBox box = (HBox) itemView;

            Image img = new Image("org/Assets/FlagsSmall/" + i.getIsoID() + ".png");
            ((ImageView) box.lookup("#imgDesign")).setImage(img);
            String name = "";
            name += DatabaseControl.getIsoName(i.getIsoID());

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

            ((Label) box.lookup("#lblIncrement")).setText(i.getPrintAmount() + "");
            ((Button) box.lookup("#btnMinus")).setOnAction(btnMinusClick);
            ((Button) box.lookup("#btnAdd")).setOnAction(btnAddClick);

            ((Button) box.lookup("#btnEdit")).setOnAction(btnEditClick);

            if (i.getAmount() < 0) {
                box.getChildren().remove(box.lookup("#btnInformation"));
            }

            box.setId(index + "");
            boxScroll.getChildren().add(box);
        }
    }

    private void setCosts(Node b, StockItem i) {
        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        double price = i.calculatePrice();
        String cost = eurFormatter.format(price);
        String subtotal = eurFormatter.format(price * i.getPrintAmount());

        ((Label) b.lookup("#lblPriceSingle")).setText(cost);
        ((Label) b.lookup("#lblSubtotal")).setText(subtotal);
    }

    private void calculateTotalCost() {
        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);

        double exportSales = 0;
        double exportCosts = 0;
        double importCosts = 0;
        for (var item : exportItems.entrySet()) {
            StockItem i = item.getValue();
            exportSales += i.calculatePrice() * i.getAmount();
            exportCosts += DatabaseControl.getPrice(i.getSizeID()) * i.getAmount();
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

    @FXML
    protected void btnCheckoutClick(ActionEvent event) throws Exception {
        for(var item : importItems.entrySet()) {
            StockItem i = item.getValue();
            DatabaseControl.updateAmountAndRestock(i.getStockID(), i.getSizeID(), i.getTotalAmount() + i.getPrintAmount(), i.getRestock());
        }
        for (int i = 0; i < items.size(); i++) {
            Node b = boxScroll.lookup("#" + i);
            boxScroll.getChildren().remove(b);
        }

        items.clear();
        importItems.clear();
        exportItems.clear();
        calculateTotalCost();
    }

    EventHandler<ActionEvent> btnMinusClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                Node n = (Node) source;
                Node box = n.getParent().getParent();
                Label l = (Label) box.lookup("#lblIncrement");

                int val = Integer.parseInt(l.getText()) - 1;
                int index = Integer.parseInt(box.getId());

                StockItem i = items.get(index);
                DatabaseControl.updateAmountAndRestock(i.getStockID(), i.getSizeID(), i.getTotalAmount() + 1, i.getRestock());

                if (i.getAmount() < 0) i.setAmount(val * -1);
                else {
                    i.setAmount(val * i.getAmount());
                    i.setTotalAmount(i.getTotalAmount() + 1);
                }

                if (val == 0) {
                    items.remove(i);
                    boxScroll.getChildren().remove(box);
                    calculateTotalCost();
                    return;
                }

                l.setText(val + "");

                box.lookup("#btnAdd").setDisable(false);
                setCosts(box, i);
                calculateTotalCost();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
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

                int val = Integer.parseInt(l.getText()) + 1;
                l.setText(val + "");

                int index = Integer.parseInt(box.getId());

                StockItem i = items.get(index);
                DatabaseControl.updateAmountAndRestock(i.getStockID(), i.getSizeID(), i.getTotalAmount() - 1, i.getRestock());

                if (i.getAmount() < 0) i.setAmount(val * -1);
                else {
                    i.setAmount(i.getAmount() + 1);
                    i.setTotalAmount(i.getTotalAmount() - 1);
                }

                if (i.getAmount() > 0 && i.getTotalAmount() == 0) {
                    n.setDisable(true);
                }

                setCosts(box, i);
                calculateTotalCost();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> btnEditClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();

                HBox b = (HBox) ((Node) source).getParent();
                int i = Integer.parseInt(b.getId());

                boolean isFlag = items.get(i) instanceof Flag;
                Design d = DatabaseControl.getDeignFromIso(items.get(i).getIsoID());

                l.showItem(stage, items, operator, d, isFlag, i);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}
