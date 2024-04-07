package org.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vexillum.*;
import java.util.List;

public class LoginController {

    @FXML private TextField enrName;
    @FXML private PasswordField enrPassword;
    @FXML private Text lblWarning;

    @FXML
    protected void handleBtnLogin(ActionEvent event) throws Exception {
        resetLogin();
        List<Operator> operatorCatch = DatabaseControl.getSpecificOperator(enrName.getText());

        //Being good and not nesting ifs
        if (operatorCatch.isEmpty()) {
            System.out.println("FAILED TO FIND NAME");
            failedLogin();
            return;
        }

        for (Operator o : operatorCatch) {
            if (o.attemptLogin(enrPassword.getText())) {
                System.out.println("SUCCESS, LOGGED IN");
                Stage stage = (Stage) enrName.getScene().getWindow();
                Loader loader = new Loader();
                loader.showStock(stage, o, "");
                return;
            }
        }

        failedLogin();
        System.out.println("WRONG PASSWORD");
    }

    private void resetLogin() {
        lblWarning.setText("");
        enrName.setStyle("-fx-border-color: #FFFFFF ;");
        enrPassword.setStyle("-fx-border-color: #FFFFFF ;");
    }
    private void failedLogin() {
        lblWarning.setText("Incorrect username or password");
        enrName.setStyle("-fx-border-color: #FF0000 ;");
        enrPassword.setStyle("-fx-border-color: #FF0000 ;");
    }
}
