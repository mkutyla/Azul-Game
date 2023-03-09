package GUIs.Controllers;

import communication.ClientController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * This class is javafx controller for Disconnect Application.
 */
public class DisconnectionController {

    /**
     * Client using this controller.
     */
    private ClientController client;

    @FXML
    private Label whoLeft;


    /**
     * Sets initial parameters for this controller.
     *
     * @param client   using this controller
     * @param username of user that left the game
     */
    public void setParams(ClientController client, String username) {
        this.client = client;
        whoLeft.setText(username);
    }

    /**
     * Called whenever id:ok component is interacted with.
     */
    public void okOnAction() {
        client.getProfileView();
    }

}
