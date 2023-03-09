package GUIs;

import GUIs.Controllers.DisconnectionController;
import communication.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * This class is used to create new Disconnection window.
 */
public class Disconnection extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("disconnection.fxml"));
        stage.setScene(new Scene(loader.load(), 300, 150));

        DisconnectionController controller = loader.getController();
        controller.setParams(client, whoLeft);

        stage.setTitle("Azul game: disconnected");
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> client.getProfileView());
    }


    /**
     * Client using this Disconnection window.
     */
    private ClientController client;

    /**
     * Username of a player that left the game.
     */
    private String whoLeft;

    /**
     * Setts initial parameters for this Disconnection window.
     *
     * @param client  using this window
     * @param whoLeft username of a player that left the game
     */
    public void addParams(ClientController client, String whoLeft) {
        this.client = client;
        this.whoLeft = whoLeft;
    }
}
