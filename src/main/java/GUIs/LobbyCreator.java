package GUIs;

import GUIs.Controllers.LobbyCreatorController;
import communication.ClientController;
import communication.Header;
import communication.Protocol;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * This class is used to create new LobbyCreator window.
 */
public class LobbyCreator extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("lobbyCreator.fxml"));
        stage.setScene(new Scene(loader.load(), 600, 400));

        LobbyCreatorController controller = loader.getController();
        controller.setClient(((AzulStage) stage).client);
        ((AzulStage) stage).client.setLobbyCreatorController(controller);
        controller.setTitle(username, code);


        stage.setTitle(String.format("Azul: %s's lobby", ((AzulStage) stage).client.getUsername()));
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> {
            ClientController client = ((AzulStage) stage).client;
            client.send(new Protocol(Header.LEAVELOBBY));
            client.getProfileView();
        });
    }

    /**
     * Username of the player who created this lobby.
     */
    private String username;

    /**
     * Code of this lobby, used to connect to it.
     */
    private String code;

    /**
     * Sets initial parameters for this LobbyCreator window.
     *
     * @param username of the player who created this lobby
     * @param code     of this lobby
     */
    public void addParams(String username, String code) {
        this.username = username;
        this.code = code;
    }
}
