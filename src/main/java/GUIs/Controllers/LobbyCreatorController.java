package GUIs.Controllers;

import communication.ClientController;
import communication.Header;
import communication.Protocol;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * This class is javafx controller for LobbyCreator Application.
 */
public class LobbyCreatorController {


    /**
     * Client using this controller.
     */
    private ClientController client;

    @FXML
    private Label title, playersLabel, error;

    @FXML
    private VBox playersJoined;

    /**
     * Number of all connected players (including client using this controller).
     */
    private int numberOfPlayers = 1;

    /**
     * Sets an alert message on id:error component.
     *
     * @param msg     to display
     * @param isError indicating whether passed message should be displayed as an error
     */
    public void errorSetText(String msg, boolean isError) {
        if (isError) {
            error.setTextFill(Paint.valueOf("#FF0000"));
        } else {
            error.setTextFill(Paint.valueOf("#000000"));
        }
        error.setText(msg);
        error.setVisible(true);
    }

    /**
     * Setter for this.client.
     *
     * @param client to assign
     */
    public void setClient(ClientController client) {
        this.client = client;
        playersLabel.setText("Players (1)");
    }

    /**
     * Called whenever id:start component is interacted with.
     */
    public void startOnAction() {
        if (numberOfPlayers < 2) {
            errorSetText("Not enough players!", true);
            return;
        }

        Protocol request = new Protocol(Header.STARTGAME);
        client.send(request);
    }

    /**
     * Adds passed player to the HBox and then into a VBox displaying all connected players.
     *
     * @param username of user that joined this lobby
     */
    public void addPlayer(String username) {
        playersLabel.setText(String.format("Players (%d)", ++numberOfPlayers));

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.setPrefWidth(playersJoined.getPrefWidth());
        hbox.setStyle("-fx-background-color: #F9D89A");

        Text text = new Text(username);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setPadding(new Insets(5, 5, 5, 5));
        textFlow.setStyle("-fx-background-color: #F9D89A");

        hbox.getChildren().add(textFlow);
        playersJoined.getChildren().add(hbox);

    }

    /**
     * Called whenever a player disconnects from this lobby to remove them from it.
     *
     * @param username of player that disconnected.
     */
    synchronized public void removePlayer(String username) {
        for (Node n : playersJoined.getChildren()) {
            HBox hbox = (HBox) n;
            TextFlow textFlow = (TextFlow) hbox.getChildren().get(0);
            Text text = (Text) textFlow.getChildren().get(0);
            if (text.getText().equals(username)) {
                playersLabel.setText(String.format("Players (%d)", --numberOfPlayers));
                playersJoined.getChildren().remove(n);
                break;
            }

        }
    }

    /**
     * Sets a new text on id:title component.
     *
     * @param username whose lobby this is
     * @param code     that can be used to connect to this lobby
     */
    public void setTitle(String username, String code) {
        title.setText(String.format("%s's lobby: %s", username, code));
    }


}
