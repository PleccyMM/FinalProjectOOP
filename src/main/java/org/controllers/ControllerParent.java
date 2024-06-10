package org.controllers;

import org.vexillum.*;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.util.*;

/**
 * Parent used for all screens in the system, with exception to the login screen
 * <p>
 * Responsible for ensuring that the header is drawn to screen and control regarding the header, as well other essential
 * data storage
 */
public abstract class ControllerParent {
    private TextField enrSearch;
    protected Stage stage;
    protected final Loader l = new Loader();
    protected Operator operator;
    private List<StockItem> items;
    protected SearchConditions sc;
    private final DatabaseControl database = new DatabaseControl();

    /**
     * This is called whenever the window changes
     */
    protected abstract void stageChangeHandle();

    /**
     * Method used to attach events to many of the nodes in the searchbar
     *
     * @param stage should be the same stage as the window being loaded
     * @param operator the current logged in operator
     * @param items all items that are currently set for importing and exporting
     * @param headerBox the primary HBox that the header.fxml file specifies
     * @param sc the search conditions, needed to fill the search bar's textbox
     */
    protected void loadHeader(Stage stage, Operator operator, List<StockItem> items, HBox headerBox, SearchConditions sc) {
        this.operator = operator;
        this.items = items;
        this.sc = sc;

        this.stage = stage;
        Button btnBack = ((Button) headerBox.lookup("#btnBack"));
        btnBack.setOnAction(btnBackHandle);

        Button btnSearch = ((Button) headerBox.lookup("#btnSearch"));
        btnSearch.setOnAction(searchHandleBtn);

        Button btnBasket = ((Button) headerBox.lookup("#btnBasket"));
        btnBasket.setOnAction(basketHandleBtn);

        enrSearch = (TextField) headerBox.lookup("#enrSearch");
        enrSearch.setText(sc.getSearch());
        enrSearch.setOnKeyPressed(searchHandleEnr);

        //If the user is an operator an additional item needs to be added to the dropdown button
        ComboBox cmbProfile = ((ComboBox) headerBox.lookup("#cmbProfile"));
        cmbProfile.setOnAction(cmbProfileChange);
        if (operator.isAdministrator()) {
            cmbProfile.getItems().add("Admin Panel");
        }
        cmbProfile.getItems().add("Logout");
    }

    /**
     * Method called whenever a new search is commenced
     *
     * @throws Exception loading fxml could fail
     */
    protected void performSearch() throws Exception {
        stageChangeHandle();
        l.showStock(stage, items, operator, sc);
    }

    EventHandler<ActionEvent> btnBackHandle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                stageChangeHandle();
                l.showStock(stage, items, operator, new SearchConditions());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<ActionEvent> searchHandleBtn = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                sc.setSearch(enrSearch.getText());
                performSearch();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<KeyEvent> searchHandleEnr = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() != KeyCode.ENTER) {
                return;
            }
            try {
                sc.setSearch(enrSearch.getText());
                performSearch();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> basketHandleBtn = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                stageChangeHandle();
                l.showBasket(stage, items, operator);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> cmbProfileChange = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                ComboBox c = (ComboBox) source;

                if (c.getValue() == "Admin Panel") {
                    l.showAdmin(stage, items, operator);
                }
                else {
                    l.showLogin(stage);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    protected List<StockItem> getItems() {
        return items;
    }
    protected StockItem getItems(int i) {
        return items.get(i);
    }
    protected void setItem(int i, StockItem item) {
        for (StockItem itemChk : items) {
            if (itemChk.baseEquals(item)) {
                itemMerge(item);
                items.remove(i);
                Collections.sort(items);
                return;
            }
        }
        items.set(i, item);
        Collections.sort(items);
    }
    protected void addItem(StockItem item) {
        if (!itemMerge(item)) items.add(item);
        Collections.sort(items);
    }
    private boolean itemMerge(StockItem item) {
        for (StockItem i : items) {
            if ((item.getAmount() < 0 && i.baseEquals(item)) ||
                (item.getAmount() > 0 && i.equals(item))) {
                i.setAmount(i.getAmount() + item.getAmount());
                return true;
            }
        }
        return false;
    }
    protected void itemsClear() {
        items.clear();
    }
    protected void removeItem(StockItem item) {
        items.remove(item);
    }
    protected int itemsSize() {
        return items.size();
    }

    protected DatabaseControl getDatabase() {
        return database;
    }
    protected void openDB() {
        database.openDBSession();
    }
    protected void closeDB() {
        System.out.println("Call to close with made");
        database.closeDBSession();
    }
}
