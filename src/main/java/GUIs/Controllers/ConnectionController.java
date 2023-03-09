package GUIs.Controllers;

import GUIs.AzulStage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * This class is javafx controller for Connection Application.
 */
public class ConnectionController {

    @FXML
    private TextField host, port;

    @FXML
    private Label alert;


    /**
     * This method gets data from TextField's in order to connect user to the Server.
     * Connected with connectionView.connectionButton.
     */
    public void connectionButtonOnAction() {
        alert.setVisible(false);

        if (host.getText().isBlank() || port.getText().isBlank()) {
            alert.setVisible(true);
            alert.setText("Please enter host and port!");
            return;
        }

        try {
            ((AzulStage) host.getScene().getWindow()).client.connect(host.getText(), port.getText());
        } catch (Exception e) {
            alert.setVisible(true);
            alert.setText(e.getMessage());
        }
    }
}
