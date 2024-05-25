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

public class AdminController extends ControllerParent {
    @FXML private BorderPane panMain;
    @FXML private VBox boxScroll;

    @Override
    protected void stageChangeHandle() { }

    public void load(Stage stage, List<StockItem> items, Operator operator) throws Exception {
        HBox headerBox = (HBox) panMain.lookup("#boxHeader");
        if (headerBox == null) { throw new Exception(); }

        loadHeader(stage, operator, items, headerBox, new SearchConditions());
        addOperatorItem();
    }

    private void addOperatorItem() throws Exception {
        boxScroll.getChildren().clear();

        HashMap<Date, Integer> map = DatabaseControl.getApprovals();

        Integer[] ids = new Integer[map.size()];
        int i = 0;
        for(var m : map.entrySet()) {
            ids[i++] = m.getValue();
        }

        List<Operator> operators = DatabaseControl.getOperatorsByID(ids);

        for(var m : map.entrySet()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_item.fxml"));
            Parent itemView = loader.load();
            HBox box = (HBox) itemView;

            Date date = m.getKey();
            int id = m.getValue();
            Operator op = new Operator();
            for (Operator o : operators) {
                if (o.getOperatorID() == id) {
                    op = o;
                    break;
                }
            }

            box.setId(op.getOperatorID() + "");

            Label name = (Label) box.lookup("#lblName");
            name.setText(op.getName());

            Label time = (Label) box.lookup("#lblApplicationTime");
            time.setText(date.toString());

            Button accept = (Button) box.lookup("#btnAccept");
            accept.setOnAction(btnAcceptClick);

            Button deny = (Button) box.lookup("#btnDeny");
            deny.setOnAction(btnDenyClick);

            boxScroll.getChildren().add(box);
        }
    }

    EventHandler<ActionEvent> btnAcceptClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Object source = event.getSource();
            Node n = (Node) source;
            Node box = n.getParent();

            int id = Integer.parseInt(box.getId());
            DatabaseControl.acceptOperator(id);
            try {
                addOperatorItem();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> btnDenyClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Object source = event.getSource();
            Node n = (Node) source;
            Node box = n.getParent();

            int id = Integer.parseInt(box.getId());
            DatabaseControl.denyOperator(id);
            try {
                addOperatorItem();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

}
