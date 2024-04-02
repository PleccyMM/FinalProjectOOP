package org.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.vexillum.*;
import java.util.List;

public class LoginController {

    @FXML private TextField userNameEntry;
    @FXML private PasswordField passwordEntry;
    @FXML
    protected void handleBtnLogin(ActionEvent event) throws Exception {
        List<Operator> operatorCatch = DatabaseControl.getSpecificOperator(userNameEntry.getText());

        if (operatorCatch.isEmpty()) {
            System.out.println("FAILED TO FIND NAME");
            return;
        }

        for (Operator o : operatorCatch) {
            if (o.AttemptLogin(passwordEntry.getText())) {
                System.out.println("SUCCESS, LOGGED IN");
                return;
            }
        }

        System.out.println("WRONG PASSWORD");
    }
}
