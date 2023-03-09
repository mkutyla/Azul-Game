package GUIs.Controllers;

import GUIs.AzulStage;
import communication.Header;
import communication.Protocol;
import game.Game;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;

/**
 * This class is javafx controller for Profile Application.
 */
public class ProfileController {

    @FXML
    private Label status, unfinishedLabel, error;

    @FXML
    private TextField code;

    @FXML
    private Button remove;

    @FXML
    private VBox unfinishedGames;

    /**
     * Stage on which Profile Application is displayed.
     */
    private AzulStage stage;

    /**
     * Game that was selected from the scroll pane.
     */
    private Game selectedGame = null;

    /**
     * HBox corresponding to selectedGame.
     */
    private HBox selectedHBox = null;

    /**
     * Number of client's unfinished games.
     */
    private int unfinishedGamesNumber = 0;

    /**
     * Setter for this.stage.
     *
     * @param stage to be assigned
     */
    public void setStage(AzulStage stage) {
        this.stage = stage;
    }

    /**
     * Sets a new label on id:status component.
     */
    public void setStatus() {
        status.setText(String.format("Hello, %s! \uD83D\uDE00", stage.client.getUsername()));
    }

    /**
     * Called whenever id:host component is interacted with.
     */
    public void hostOnAction() {
        stage.client.send(new Protocol(Header.CREATEGAME));
    }

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
     * Called whenever id:join component is interacted with.
     */
    public void joinOnAction() {
        if (code.getText().isBlank()) {
            error.setText("Please enter a valid code!");
            error.setVisible(true);
            return;
        }

        stage.client.setResumed(false);
        Protocol request = new Protocol(Header.JOINGAME);
        request.put("code", code.getText());
        stage.client.send(request);
    }

    /**
     * Called whenever id:resume component is interacted with.
     */
    public void resumeOnAction() {
        if (selectedGame == null) {
            errorSetText("You haven't chosen a game to resume!", true);
            return;
        }

        stage.client.setResumedGameId(selectedGame);
        stage.client.setResumed(true);
        Protocol request = new Protocol(Header.CREATEGAME);
        request.put("game", selectedGame);
        stage.client.send(request);
    }

    /**
     * Adds a new HBox corresponding to an unfinished game.
     *
     * @param gameText to display, corresponding to passed game
     * @param game     game to choose
     */
    private void addUnfinishedGames(String gameText, Game game) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.setPrefWidth(unfinishedGames.getPrefWidth());
        hbox.setStyle("-fx-background-color: #F9D89A");

        Text text = new Text(gameText);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setPadding(new Insets(5, 5, 5, 5));
        textFlow.setStyle("-fx-background-color: #F9D89A");

        hbox.getChildren().add(textFlow);

        hbox.setOnMouseClicked(mouseEvent -> {
            // set every node's color to default
            for (Node n : unfinishedGames.getChildren()) {
                n.setStyle("-fx-background-color: #F9D89A");
                for (Node c : ((HBox) n).getChildren()) {
                    c.setStyle("-fx-background-color: #F9D89A");
                }
            }
            selectedGame = game;
            selectedHBox = hbox;
            remove.setVisible(true);
            // highlight selected one
            hbox.setStyle("-fx-background-color: #F6B63E");
            textFlow.setStyle("-fx-background-color: #F6B63E");
        });
        unfinishedGames.getChildren().add(hbox);
    }

    /**
     * Called whenever id:remove component is interacted with.
     */
    public void removeOnAction() {
        stage.client.removeGame(selectedGame);
        unfinishedGames.getChildren().remove(selectedHBox);

        selectedHBox = null;
        selectedGame = null;

        unfinishedLabel.setText(String.format("Unfinished games (%d):", --unfinishedGamesNumber));

        remove.setVisible(false);
    }

    /**
     * Reads all of this client's unfinished games and processes them to put into a VBox.
     */
    public void setUnfinishedGames() {
        ArrayList<String> unfinishedGamesList = new ArrayList<>();

        for (Game game : stage.client.getUnfinishedGames()) {

            StringBuilder sb = new StringBuilder();
            for (String username : game.getUsernames()) {
                sb.append(username).append(", ");
            }
            sb.deleteCharAt(sb.length() - 2); // trimming last ", "

            unfinishedGamesList.add(String.format("[%s] %s", game.getWhenSaved(), sb));
        }

        unfinishedLabel.setText(String.format("Unfinished games (%d):", unfinishedGamesList.size()));
        unfinishedGamesNumber = unfinishedGamesList.size();

        for (int i = 0; i < unfinishedGamesList.size(); i++) {
            addUnfinishedGames(unfinishedGamesList.get(i), stage.client.getUnfinishedGames().get(i));
        }
    }


}
