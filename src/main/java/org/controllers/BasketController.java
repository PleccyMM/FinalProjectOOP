package org.controllers;

import javafx.beans.property.SimpleBooleanProperty;
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
import org.vexillum.*;

import javax.swing.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class BasketController extends ControllerParent {
    private Operator operator;
    @FXML private BorderPane panMain;
    @FXML private VBox boxScroll;

    public void load(Stage stage, List<StockItem> items, Operator operator) {
        this.operator = operator;

        try {
            HBox headerBox = (HBox) panMain.lookup("#boxHeader");
            if (headerBox == null) { throw new Exception(); }

            loadHeader(stage, operator, items, headerBox, "");

            /*Flag f1 = new Flag(5, "CH", FLAG_MATERIAL.POLYESTER, FLAG_HOIST.METAL, FLAG_SIZE.LARGE);
            Flag f2 = new Flag(110, "CZ", FLAG_MATERIAL.PAPER, FLAG_HOIST.NONE, FLAG_SIZE.DESK);
            Flag f3 = new Flag(289, "SY", FLAG_MATERIAL.NYLON, FLAG_HOIST.WOODEN, FLAG_SIZE.SMALL);
            Flag f4 = new Flag(289, "SY", FLAG_MATERIAL.NYLON, FLAG_HOIST.FABRIC, FLAG_SIZE.LARGE);
            items.add(f1);
            items.add(f2);
            items.add(f3);
            items.add(f4);*/

            createItems();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createItems() throws Exception {
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

            NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
            float price = i.calculatePrice();
            String cost = eurFormatter.format(price);
            String subtotal = eurFormatter.format(price * i.getAmount());

            ((Label) box.lookup("#lblPriceSingle")).setText(cost);
            ((Label) box.lookup("#lblSubtotal")).setText(subtotal);

            boxScroll.getChildren().add(box);
        }
    }
}
