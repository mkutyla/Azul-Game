package GUIs.Controllers;

import communication.ClientController;
import communication.Header;
import communication.Protocol;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * This class is javafx controller for Lobby Application.
 */
public class LobbyController {

    /**
     * Client using this controller.
     */
    private ClientController client;

    @FXML
    private Label title;

    /**
     * Called whenever Button id:leave component is interacted with.
     */
    public void leaveOnAction() {
        client.send(new Protocol(Header.LEAVELOBBY));
        client.getProfileView();
    }

    /**
     * Setter for this.client.
     *
     * @param client to be assigned
     */
    public void setClient(ClientController client) {
        this.client = client;
    }

    /**
     * Set's new label for id:title component.
     *
     * @param username of player that created this lobby
     */
    public void setTitle(String username) {
        title.setText(String.format("%s's lobby", username));
    }
}
