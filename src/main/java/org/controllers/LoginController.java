package org.controllers;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.util.*;
import org.vexillum.*;

/**
 * Controller used for both logging in and creating new accounts, first screen seen upon boot up of the system.
 * The only controller that does not inherit from ControllerParent, as it has no search bar.
 * <p>
 * Main design is found in {@code login_screen.fxml}, pop-up for confirmation when creating a new account is found in
 * {@code login_popup.fxml}
 */
public class LoginController {
    //Since a popup can be present, the entire fxml file has a StackPane as the parent so the popup and tint can be
    //displayed over everything else
    @FXML StackPane panStacker;

    @FXML private TextField enrName;
    @FXML private PasswordField enrPassword;
    @FXML private PasswordField enrPasswordAgain;
    @FXML private Label lblWarning;

    @FXML private Label lblTitle;
    @FXML private Label lblCreateAccount;
    @FXML private Button btnLogin;

    private final DatabaseControl database = new DatabaseControl();

    @FXML
    private void handleBtnLogin(ActionEvent event) throws Exception {
        attemptLogin();
    }

    /**
     * Used to allow login to be conducted by just pressing enter on any of the text entries, rather than being forced
     * to click the button
     */
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

    /**
     * Method used for attempting login AND redirecting, redirecting is handled here to prevent repeated logic in both
     * the {@code handleBtnLogin} and {@code textHandleEnr} events
     * @throws Exception loading fxml could fail
     */
    private void attemptLogin() throws Exception {
        database.openDBSession();
        if (!btnLogin.getText().equals("Login")) {
            newAccount();
            return;
        }

        //Resets any warnings as they aren't needed anymore
        resetLogin();
        //We don't send the password and username, to get an exact match, to emulate a better security protocol
        List<Operator> operatorCatch = database.getSpecificOperator(enrName.getText());
        database.closeDBSession();

        if (operatorCatch.isEmpty()) {
            failure("Incorrect username or password");
            return;
        }

        for (Operator o : operatorCatch) {
            if (o.attemptLogin(enrPassword.getText())) {
                Stage stage = (Stage) enrName.getScene().getWindow();
                Loader loader = new Loader();
                loader.showStock(stage, new ArrayList<>(), o, new SearchConditions());
                return;
            }
        }

        failure("Incorrect username or password");
    }

    /**
     * Responsible for adding a new account by sending off the relative data to the database, whilst also performing
     * error trapping, creating a new account doesn't allow immediate login as it must first be approved
     * @throws Exception loading fxml could fail
     */
    private void newAccount() throws Exception {
        resetLogin();
        if (!Objects.equals(enrPassword.getText(), enrPasswordAgain.getText())) {
            failure("Passwords must match");
            return;
        }
        //Checking both enrPassword and enrPasswordAgain is unnecessary, since we already check to see if they're equal,
        //only checking one is needed
        if (enrPassword.getText().isEmpty() || enrName.getText().isEmpty()) {
            failure("All data must be filled");
            return;
        }

        if (!database.getSpecificOperator(enrName.getText()).isEmpty()) {
            failure("Username already taken");
            return;
        }

        //highestVal is used to figure out and assign the new operator's ID to the next natural one, this can probably
        //be improved using the auto increment feature in MySQL, need to be investigated
        int highestVal = 0;
        for (Integer i : database.getExistentIDs()) {
            if (i > highestVal) highestVal = i;
        }
        database.addRequest(highestVal + 1, enrName.getText(), enrPassword.getText(), new Date());
        database.closeDBSession();
        clear();

        //This entire part is just attaching logic to the popup, this popup is just for confirmation
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login_popup.fxml"));
        Parent itemView = loader.load();

        VBox box = (VBox) itemView;

        box.setOnMouseClicked(hidePopupClick);

        //boxContainment is the partially transparent background, so users can more naturally click off the popup a
        //onClickEvent is attached to it
        box.lookup("#boxContainment").setOnMouseClicked(ignoreHideClick);
        box.lookup("#btnBlue").setOnMouseClicked(hidePopupClick);

        panStacker.getChildren().add(box);
    }

    /**
     * Used for changing the state of the screen between the login screen and the register screen, this handles the
     * transformation both ways
     */
    @FXML
    private void lblCreateAccountClick(MouseEvent event) {
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

    private void hidePopup() {
        Node n = panStacker.lookup("#boxDarkening");
        panStacker.getChildren().remove(n);
    }

    /**
     * Cleans the warnings up
     */
    private void resetLogin() {
        lblWarning.setText("");
        enrName.setStyle("-fx-border-color: #FFFFFF ;");
        enrPassword.setStyle("-fx-border-color: #FFFFFF ;");
        enrPasswordAgain.setStyle("-fx-border-color: #FFFFFF ;");
    }
    /**
     * Cleans the entry boxes
     */
    private void clear() {
        enrName.setText("");
        enrPassword.setText("");
        enrPasswordAgain.setText("");
    }
    /**
     * Used to print warnings to the screen
     * @param text string for the warning to be written
     */
    private void failure(String text) {
        lblWarning.setText(text);
        enrName.setStyle("-fx-border-color: #FF0000 ;");
        enrPassword.setStyle("-fx-border-color: #FF0000 ;");
        enrPasswordAgain.setStyle("-fx-border-color: #FF0000 ;");
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
}
