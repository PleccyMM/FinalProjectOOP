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
import java.text.NumberFormat;
import java.util.*;

public class BasketController extends ControllerParent {
    @FXML private BorderPane panMain;
    @FXML private VBox boxScroll;

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

            ((Label) box.lookup("#lblIncriment")).setText(i.getAmount() + "");
            ((Button) box.lookup("#btnMinus")).setOnAction(btnMinusClick);
            ((Button) box.lookup("#btnAdd")).setOnAction(btnAddClick);

            ((Button) box.lookup("#btnEdit")).setOnAction(btnEditClick);

            box.setId(index + "");
            boxScroll.getChildren().add(box);
            index++;
        }
    }

    private void setCosts(Node b, StockItem i) {
        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        float price = i.calculatePrice();
        String cost = eurFormatter.format(price);
        String subtotal = eurFormatter.format(price * i.getAmount());

        ((Label) b.lookup("#lblPriceSingle")).setText(cost);
        ((Label) b.lookup("#lblSubtotal")).setText(subtotal);
    }

    EventHandler<ActionEvent> btnMinusClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                Node n = (Node) source;
                Node box = n.getParent().getParent();
                Label l = (Label) box.lookup("#lblIncriment");

                int val = Integer.parseInt(l.getText()) - 1;
                int index = Integer.parseInt(box.getId());

                StockItem i = items.get(index);
                DatabaseControl.updateAmountAndRestock(i.getStockID(), i.getSizeID(), i.getTotalAmount() + 1, i.getRestock());
                i.setAmount(val);
                i.setTotalAmount(i.getTotalAmount() + 1);

                if (val == 0) {
                    items.remove(i);
                    boxScroll.getChildren().remove(box);
                    return;
                }

                l.setText(val + "");
                i.setAmount(val);

                box.lookup("#btnAdd").setDisable(false);
                setCosts(box, i);
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
                Label l = (Label) box.lookup("#lblIncriment");

                int val = Integer.parseInt(l.getText()) + 1;
                l.setText(val + "");

                int index = Integer.parseInt(box.getId());

                StockItem i = items.get(index);
                DatabaseControl.updateAmountAndRestock(i.getStockID(), i.getSizeID(), i.getTotalAmount() - 1, i.getRestock());
                i.setAmount(val);
                i.setTotalAmount(i.getTotalAmount() - 1);

                if (i.getTotalAmount() == 0) {
                    n.setDisable(true);
                }

                setCosts(box, i);
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

                System.out.println("EDITING ITEM");
                l.showItem(stage, operator, items, d, isFlag, i);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}
