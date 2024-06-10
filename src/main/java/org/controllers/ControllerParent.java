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
     * This is called whenever the window changes, in practice this is only used by {@code itemController} to prevent
     * changes to the database/{@code items} when leaving during the editing of an order item
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

    /**
     * returns all items
     * @return {@code items} in full
     */
    protected List<StockItem> getItems() {
        return items;
    }
    /**
     * returns only the item at the specified index by reference
     * @param i the given index
     * @return {@code items} at position i, null if {@code i > items.size()}
     */
    protected StockItem getItems(int i) {
        return i > items.size() ? null : items.get(i);
    }
    /**
     * returns the item that matches the provided hash
     * @param hash the given hash to be found
     * @return null if the hash is not found in {@code items}, the item itself by reference otherwise
     */
    protected StockItem findItemHash(int hash) {
        for (StockItem item : items) {
            if (item.hashCode() == hash) return item;
        }
        return null;
    }

    /**
     * Used to set an existing item to a new value, performs a merge if a similar item is already inside the basket and removes
     * if needed, list is sorted after
     * @param i index of the located item currently inside the {@code items} list
     * @param item the item itself, with the changes performed
     */
    protected void setItem(int i, StockItem item) {
        if (!itemMerge(item, i)) items.set(i, item);
        else items.remove(i);
        Collections.sort(items);
    }
    /**
     * Adds the item to the {@code items} list, merging if necessary, list is sorted after
     * @param item the item to be added
     */
    protected void addItem(StockItem item) {
        if (!itemMerge(item, -1)) items.add(item);
        Collections.sort(items);
    }
    /**
     * Helper function for {@code setItem} and {@code addItem}, deals with the merging logic.
     * <p>
     * Items that are being exported {@code (amount > 0)} are compared using the standard overridden {@code equals()} function,
     * whereas items being imported {@code (amount < 0)} are compared using {@code baseEquals()}, this is effectively just the
     * super classes compare function, without the children. This is necessary as an item that was an exported item, but has
     * been since edited to become an imported item, still contains the data relating to any options chosen (wooden toggles, material, etc.)
     * which would prevent the {@code equals()} from declaring them equals, by using the properties relevant to only the super
     * {@code (StockItem)} this problem is averted.
     * @param item the item that is to be merged, if necessary
     * @param indexCheck the index of the given item in {@code items}, this should be set to -1 if the given item is not in {@code items}
     * @return true when an item was merged, false if the item was not merged
     */
    private boolean itemMerge(StockItem item, int indexCheck) {
        for (int index = 0; index < items.size(); index++) {
            StockItem i = items.get(index);

            if (index != indexCheck &&
                    ((item.getAmount() < 0 && i.baseEquals(item)) ||
                            (item.getAmount() > 0 && i.equals(item)))) {
                i.setAmount(i.getAmount() + item.getAmount());
                return true;
            }
        }
        return false;
    }

    /*
    The next 3 functions are just to help with code cleanliness, rather than having to use getItems() all the time
     */
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

    /**
     * Opens the database connection
     */
    protected void openDB() {
        database.openDBSession();
    }
    /**
     * Closes the database connection, but not the session itself
     */
    protected void closeDB() {
        database.closeDBSession();
    }
}
