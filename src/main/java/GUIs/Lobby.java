package GUIs;

import GUIs.Controllers.LobbyController;
import communication.Header;
import communication.Protocol;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * This class is used to create new Lobby window.
 */
public class Lobby extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("lobby.fxml"));
        stage.setScene(new Scene(loader.load(), 300, 150));

        LobbyController controller = loader.getController();
        controller.setClient(((AzulStage) stage).client);
        controller.setTitle(username);

        stage.setTitle(String.format("Azul: %s's lobby", username));
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> {
            ((AzulStage) stage).client.send(new Protocol(Header.LEAVELOBBY));
            ((AzulStage) stage).client.getProfileView();
        });

    }

    /**
     * Username of the player that created this lobby.
     */
    private String username;

    /**
     * Setts initial parameters for this Lobby window.
     *
     * @param username of the player who created this lobby.
     */
    public void addParams(String username) {
        this.username = username;
    }
}
