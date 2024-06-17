package org.controllers;

import org.vexillum.*;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.util.*;

/**
 * Controller used for the approval and denial of applicants, contains relatively little logic
 */
public class AdminController extends ControllerParent {
    @FXML private BorderPane panMain;
    @FXML private VBox boxScroll;

    @Override
    protected void stageChangeHandle() { }

    public void load(Stage stage, List<StockItem> items, Operator operator) throws Exception {
        HBox headerBox = (HBox) panMain.lookup("#boxHeader");
        if (headerBox == null) { throw new Exception(); }

        loadHeader(stage, operator, items, headerBox, new SearchConditions());

        openDB();
        addOperatorItem(getDatabase().getApprovals());
        closeDB();
    }

    /**
     * Method used to populate the screen with awaiting approvals in order of their application time, the longest ago being first
     * @param operatorMap a hashmap with {@code Date} as a key and {@code Integer} as values, should be taken from {@code DatabaseControl.getApprovals()}
     * @throws Exception loading the fxml file may fail
     */
    private void addOperatorItem(HashMap<Date, Integer> operatorMap) throws Exception {
        boxScroll.getChildren().clear();

        //Attempts to condense this into a list conversion to array has proven a bit weird, so the for loop is here and hacky
        //might be worth looking into again
        Integer[] ids = new Integer[operatorMap.size()];
        int i = 0;
        for(var m : operatorMap.entrySet()) {
            ids[i++] = m.getValue();
        }

        //Sorting dates is important, as they must be listed from the earliest applicant to the last
        List<Date> dates = new ArrayList<>(operatorMap.keySet());
        Collections.sort(dates);

        //Only operators actively awaiting approval are fetched, speeding up the later linear search
        List<Operator> operators = getDatabase().getOperatorsByIDAwaitingApproval(ids);

        for(Date date : dates) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/admin_item.fxml"));
            Parent itemView = loader.load();
            HBox box = (HBox) itemView;

            int id = operatorMap.get(date);
            Operator op = new Operator();
            //This is a linear search, which is fine for such a short list, plus the operators are removed as it goes on
            for (Operator o : operators) {
                if (o.getOperatorID() == id) {
                    op = o;
                    operators.remove(o);
                    break;
                }
            }

            //operatorID() is the primary key, so it's guaranteed unique and safe to use as an ID
            box.setId(op.getOperatorID() + "");

            Label name = (Label) box.lookup("#lblUsername");
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

    /**
     * Handles accepted logic, though most of it is within the {@code DatabaseControl} class
     */
    EventHandler<ActionEvent> btnAcceptClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Object source = event.getSource();
            Node n = (Node) source;
            Node box = n.getParent().getParent();

            int id = Integer.parseInt(box.getId());

            openDB();
            getDatabase().acceptOperator(id);
            try {
                addOperatorItem(getDatabase().getApprovals());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                closeDB();
            }
        }
    };

    /**
     * Handles denial logic, though most of it is within the {@code DatabaseControl} class
     */
    EventHandler<ActionEvent> btnDenyClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Object source = event.getSource();
            Node n = (Node) source;
            Node box = n.getParent().getParent();

            int id = Integer.parseInt(box.getId());
            openDB();
            getDatabase().denyOperator(id);
            try {
                addOperatorItem(getDatabase().getApprovals());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                closeDB();
            }
        }
    };

}
