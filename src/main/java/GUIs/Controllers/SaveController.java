package GUIs.Controllers;

import communication.ClientController;
import game.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
/**
 * This class is javafx controller for Profile Application.
 */
public class SaveController {

    /**
     * Client using this controller.
     */
    private ClientController client;

    /**
     * Game to save.
     */
    private Game game;

    @FXML
    private Label whoLeft;

    /**
     * Setts initial fields.
     *
     * @param client   using this controller
     * @param game     to save
     * @param username of a player that left the game
     */
    public void setParams(ClientController client, Game game, String username) {
        this.client = client;
        this.game = game;
        whoLeft.setText(username);
    }

    /**
     * Called whenever id:save component is interacted with.
     */
    public void saveOnAction() {
        client.closeSave(game, true);
    }

    /**
     * Called whenever id:leave component is interacted with.
     */
    public void leaveOnAction() {
        client.closeSave(game, false);
    }
}
