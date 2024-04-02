package org.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.vexillum.*;
import java.util.List;

public class LoginController {

    @FXML private TextField userNameEntry;
    @FXML private PasswordField passwordEntry;
    @FXML private Text lblWarning;
    @FXML
    protected void handleBtnLogin(ActionEvent event) throws Exception {
        resetLogin();
        List<Operator> operatorCatch = DatabaseControl.getSpecificOperator(userNameEntry.getText());

        //Being good and not nesting ifs
        if (operatorCatch.isEmpty()) {
            System.out.println("FAILED TO FIND NAME");
            failedLogin();
            return;
        }

        for (Operator o : operatorCatch) {
            if (o.attemptLogin(passwordEntry.getText())) {
                System.out.println("SUCCESS, LOGGED IN");
                return;
            }
        }

        failedLogin();
        System.out.println("WRONG PASSWORD");
    }

    private void resetLogin() {
        lblWarning.setText("");
        userNameEntry.setStyle("-fx-border-color: #FFFFFF ;");
        passwordEntry.setStyle("-fx-border-color: #FFFFFF ;");
    }
    private void failedLogin() {
        lblWarning.setText("Incorrect username or password");
        userNameEntry.setStyle("-fx-border-color: #FF0000 ;");
        passwordEntry.setStyle("-fx-border-color: #FF0000 ;");
    }
}
