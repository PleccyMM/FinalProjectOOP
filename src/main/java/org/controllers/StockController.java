package org.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.vexillum.*;
import java.util.List;

public class StockController {
    private Operator operator;

    public void loginOperator(Operator o) {
        operator = o;
    }
    @FXML
    protected void handleBtnTest(ActionEvent event) throws Exception {

    }
}
