package GUIs;

import GUIs.Controllers.SaveController;
import communication.ClientController;
import game.Game;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * This class is used to create new Save window.
 */
public class Save extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("save.fxml"));
        stage.setScene(new Scene(loader.load(), 300, 150));

        SaveController controller = loader.getController();
        controller.setParams(client, game, whoLeft);

        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(event -> client.closeSave(game, false));
    }

    /**
     * Client using this Save window.
     */
    private ClientController client;

    /**
     * Username of the player who left the game.
     */
    private String whoLeft;

    /**
     * Game that was just stopped.
     */
    private Game game;

    /**
     * Sets initial parameters for this Save window.
     *
     * @param client using this Save window
     * @param game   that was just stopped
     * @param whoLeft username of the player who left the game
     */
    public void addParams(ClientController client, Game game, String whoLeft) {
        this.client = client;
        this.game = game;
        this.whoLeft = whoLeft;
    }
}
