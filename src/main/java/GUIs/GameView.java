package GUIs;

import GUIs.Controllers.GameController;
import communication.Header;
import communication.Protocol;
import game.Game;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is used to create new GameView window.
 */
public class GameView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        stage.setScene(new Scene(loader.load(), 1100, 640));

        ((GameController) loader.getController()).setParams(
                ((AzulStage) stage).client,
                game,
                creator,
                id,
                (AzulStage) stage
        );

        stage.setTitle(String.format("Azul Game [creator: %s]: user %s", game.getUsernames().get(0), ((AzulStage) stage).client.getUsername()));
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> {
                    ((AzulStage) stage).client.send(new Protocol(Header.LEAVEGAME));
                    ((AzulStage) stage).client.getProfileView();
                }
        );

    }

    /**
     * Game to be displayed.
     */
    private Game game;

    /**
     * ID of client running this window.
     */
    private int id;

    /**
     * Creator of this.game.
     */
    private String creator;

    /**
     * Setts initial parameters for this GameView window.
     *
     * @param game    to be displayed
     * @param creator of game to be displayed
     * @param id      of the user using this window
     */
    public void addParams(Game game, String creator, int id) {
        this.game = game;
        this.creator = creator;
        this.id = id;
    }
}
