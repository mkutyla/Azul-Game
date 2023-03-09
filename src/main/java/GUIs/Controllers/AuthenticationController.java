package GUIs.Controllers;

import GUIs.AzulStage;
import com.google.common.hash.Hashing;
import communication.Header;
import communication.Protocol;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

import java.nio.charset.StandardCharsets;

/**
 * This class is javafx controller for Authentication Application.
 */
public class AuthenticationController {

    @FXML
    private TextField username, password;

    @FXML
    private Label alert;

    /**
     * Sets alert message on this.alert.
     *
     * @param msg     to print
     * @param isError indicating whether message is an error
     */
    public void alertSetText(String msg, boolean isError) {
        if (isError) {
            alert.setTextFill(Paint.valueOf("#FF0000"));
        } else {
            alert.setTextFill(Paint.valueOf("#000000"));
        }
        alert.setText(msg);
        alert.setVisible(true);
    }

    /**
     * This method gets data from TextField's in order to login user into the
     * Server.
     * Connected with authenticationView.loginButton.
     */
    public void loginButtonOnAction() {

        if (username.getText().isBlank() || password.getText().isBlank()) {
            alertSetText("Please enter username and password!", true);
            return;
        }

        final String passwordHash = Hashing.sha256().hashString(password.getText(), StandardCharsets.UTF_8).toString();

        Protocol request = new Protocol(Header.LOGIN);
        request.put("username", username.getText());
        request.put("passwordHash", passwordHash);
        ((AzulStage) username.getScene().getWindow()).client.send(request);
    }


    /**
     * This method gets data from TextField's in order to register user
     * to the Server.
     * Connected with authenticationView.registerButton.
     */
    public void registerButtonOnAction() {

        if (username.getText().isBlank() || password.getText().isBlank()) {
            alertSetText("Please enter login and password!", true);
            return;
        }

        if (username.getText().length() > 20) {
            alertSetText("Username cannot be longer than 20 characters!", true);
            return;
        }

        if (!username.getText().matches("^[a-zA-Z0-9]*$")) {
            alertSetText("Username must contain only alphanumeric chars!", true);
            return;
        }


        final String passwordHash = Hashing.sha256().hashString(password.getText(), StandardCharsets.UTF_8).toString();

        Protocol request = new Protocol(Header.REGISTER);
        request.put("username", username.getText());
        request.put("passwordHash", passwordHash);
        ((AzulStage) username.getScene().getWindow()).client.send(request);
    }

}
