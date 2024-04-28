package org.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.vexillum.Operator;
import org.vexillum.SearchConditions;
import org.vexillum.StockItem;

import java.util.List;

public class AdminController extends ControllerParent {
    @FXML
    private BorderPane panMain;

    @Override
    protected void stageChangeHandle() { }

    public void load(Stage stage, List<StockItem> items, Operator operator) throws Exception {
        HBox headerBox = (HBox) panMain.lookup("#boxHeader");
        if (headerBox == null) { throw new Exception(); }

        loadHeader(stage, operator, items, headerBox, new SearchConditions());
    }
}
