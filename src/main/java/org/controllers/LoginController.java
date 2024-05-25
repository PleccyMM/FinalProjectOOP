package org.controllers;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import org.vexillum.*;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class LoginController {
    @FXML StackPane panStacker;

    @FXML private TextField enrName;
    @FXML private PasswordField enrPassword;
    @FXML private PasswordField enrPasswordAgain;
    @FXML private Text lblWarning;

    @FXML private Label lblTitle;
    @FXML private Label lblCreateAccount;
    @FXML private Button btnLogin;

    @FXML
    private void handleBtnLogin(ActionEvent event) throws Exception {
        attemptLogin();
    }
    @FXML
    private void textHandleEnr(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER) {
            return;
        }
        try {
            attemptLogin();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void attemptLogin() throws Exception {
        if (!btnLogin.getText().equals("Login")) {
            newAccount();
            return;
        }

        resetLogin();
        List<Operator> operatorCatch = DatabaseControl.getSpecificOperator(enrName.getText());

        //Being good and not nesting ifs
        if (operatorCatch.isEmpty()) {
            System.out.println("FAILED TO FIND NAME");
            failure("Incorrect username or password");
            return;
        }
        if (operatorCatch.size() > 1) {}

        for (Operator o : operatorCatch) {
            if (o.attemptLogin(enrPassword.getText())) {
                System.out.println("SUCCESS, LOGGED IN");
                Stage stage = (Stage) enrName.getScene().getWindow();
                Loader loader = new Loader();
                loader.showStock(stage, o, new ArrayList<>(), new SearchConditions());
                return;
            }
        }

        failure("Incorrect username or password");
        System.out.println("WRONG PASSWORD");
    }

    private void newAccount() throws Exception {
        resetLogin();
        if (!Objects.equals(enrPassword.getText(), enrPasswordAgain.getText())) {
            failure("Passwords must match");
            return;
        }
        if (enrPassword.getText().isEmpty() || enrName.getText().isEmpty()) {
            failure("All data must be filled");
            return;
        }

        int highestVal = 0;
        for (Integer i : DatabaseControl.getExistentIDs()) {
            if (i > highestVal) highestVal = i;
        }
        DatabaseControl.addRequest(highestVal + 1, enrName.getText(), enrPassword.getText(), new Date());
        clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("login_popup.fxml"));
        Parent itemView = loader.load();

        VBox box = (VBox) itemView;

        box.setOnMouseClicked(hidePopupClick);

        box.lookup("#boxContainment").setOnMouseClicked(ignoreHideClick);
        box.lookup("#btnBlue").setOnMouseClicked(hidePopupClick);

        panStacker.getChildren().add(box);
    }

    @FXML
    private void lblCreateAccountClick(MouseEvent event) throws Exception {
        clear();
        resetLogin();

        if (Objects.equals(lblCreateAccount.getText(), "Create Account")) {
            enrPasswordAgain.setVisible(true);
            lblCreateAccount.setText("Login");
            btnLogin.setText("Make Request");
            lblTitle.setText("New Account");
            lblTitle.setStyle("-fx-font-size: 38;");
        }
        else {
            enrPasswordAgain.setVisible(false);
            lblCreateAccount.setText("Create Account");
            btnLogin.setText("Login");
            lblTitle.setText("Login");
            lblTitle.setStyle("-fx-font-size: 48;");
        }
    }

    EventHandler<MouseEvent> ignoreHideClick = MouseEvent::consume;
    EventHandler<MouseEvent> hidePopupClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            try {
                hidePopup();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    private void hidePopup() {
        Node n = panStacker.lookup("#boxDarkening");
        panStacker.getChildren().remove(n);
    }

    private void resetLogin() {
        lblWarning.setText("");
        enrName.setStyle("-fx-border-color: #FFFFFF ;");
        enrPassword.setStyle("-fx-border-color: #FFFFFF ;");
        enrPasswordAgain.setStyle("-fx-border-color: #FFFFFF ;");
    }
    private void clear() {
        enrName.setText("");
        enrPassword.setText("");
        enrPasswordAgain.setText("");
    }
    private void failure(String text) {
        lblWarning.setText(text);
        enrName.setStyle("-fx-border-color: #FF0000 ;");
        enrPassword.setStyle("-fx-border-color: #FF0000 ;");
        enrPasswordAgain.setStyle("-fx-border-color: #FF0000 ;");
    }
}
