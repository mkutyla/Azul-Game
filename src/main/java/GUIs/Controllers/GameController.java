package GUIs.Controllers;

import GUIs.AzulStage;
import communication.ClientController;
import communication.Header;
import communication.Protocol;
import game.Board;
import game.Game;
import game.Tile;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is javafx controller for Game Application.
 */
public class GameController {

    /**
     * Game objects connected to GUI
     */
    private Game game;

    /**
     * Client's board
     */
    private Board board;

    /**
     * This client's id.
     */
    private int id;

    /**
     * Client using this controller.
     */
    private ClientController client;

    /**
     * All players playing with user (including).
     */
    private ArrayList<String> usernames;

    /**
     * Stage on which controlled Game Application is displayed.
     */
    private AzulStage stage;

    /**
     * ID of a player that is currently being spectated by this.client.
     */
    private int currentId;

    /**
     * The creator of this game.
     */
    private String creator;

    /**
     * Setts initial parameters for this controller.
     *
     * @param client  using this controller
     * @param game    that is being displayed by controlled Game Application
     * @param creator of this game
     * @param id      of this client
     * @param stage   on which Game Application is displayed
     */
    public void setParams(ClientController client, Game game, String creator, int id, AzulStage stage) {
        this.client = client;
        this.game = game;
        this.id = id;
        this.board = game.getPlayer(id);
        this.stage = stage;
        this.currentId = id;
        this.usernames = game.getUsernames();
        this.creator = creator;

        viewSelector.setItems(FXCollections.observableArrayList(usernames));
        viewSelector.setValue(usernames.get(id));
        viewSelector.setEditable(false);
        turnLabel.setText(game.getUsernames().get(game.getCurrentPlayerTurn()));

        init();
    }


    /**
     * Changing view to different users perspective
     *
     * @param username username to view
     */
    private void changeView(String username) {
        for (int i = 0; i < usernames.size(); i++) {
            if (usernames.get(i).equals(username)) {
                this.board = game.getPlayer(i);
                this.currentId = i;
                viewSelector.setValue(usernames.get(i));

                if (i == id) {
                    stage.setTitle(String.format("Azul Game [creator: %s]: user %s", creator, client.getUsername()));
                } else {
                    stage.setTitle(String.format("Azul Game [creator: %s]: user %s, viewing board of %s",
                            creator, client.getUsername(), usernames.get(i)));
                }

                clearQueues();
                clearPattern();
                init();

                return;
            }
        }
    }

    @FXML
    private ComboBox<String> viewSelector;

    /**
     * Points
     */
    @FXML
    private Label points, turnLabel;

    @FXML
    private SplitPane splitpane;

    /**
     * Workshops
     */
    @FXML
    private Button workshop00, workshop01, workshop02, workshop03, workshop10, workshop11, workshop12, workshop13,
            workshop20, workshop21, workshop22, workshop23, workshop30, workshop31, workshop32, workshop33,
            workshop40, workshop41, workshop42, workshop43, workshop50, workshop51, workshop52, workshop53,
            workshop60, workshop61, workshop62, workshop63, workshop70, workshop71, workshop72, workshop73,
            workshop80, workshop81, workshop82, workshop83;
    private ArrayList<Button[]> workshops;
    @FXML
    private Button currentTile;
    //Initially set to null
    private int currentTileWorkshopId = -1;

    /**
     * Pattern queues
     */
    @FXML
    private Button q00, q10, q11, q20, q21, q22, q30, q31, q32, q33, q40, q41, q42, q43, q44;
    private ArrayList<Button[]> queues;

    /**
     * Pattern wall (on the right)
     */
    @FXML
    private Button button00, button01, button02, button03, button04,
            button10, button11, button12, button13, button14,
            button20, button21, button22, button23, button24,
            button30, button31, button32, button33, button34,
            button40, button41, button42, button43, button44;
    private ArrayList<Button[]> pattern;

    /**
     * Middle field
     */
    @FXML
    private Button middleSpecial, middleBlue, middleYellow, middleRed, middleBlack, middleGreen;
    @FXML
    private Label blueLabel, yellowLabel, redLabel, blackLabel, greenLabel;

    /**
     * Floor
     */
    @FXML
    private Button floor0, floor1, floor2, floor3, floor4, floor5, floor6;
    private ArrayList<Button> floor;
    @FXML
    private Button specialFloorTile;

    /**
     * Initial method used to set up all components of Game Application.
     */
    public void init() {
        for (Node node : splitpane.lookupAll(".split-pane-divider")) {
            node.setVisible(false);
        }

        viewSelector.valueProperty().addListener((observableValue, s, t1) -> changeView(t1));

        //Array containing queues buttons
        this.queues = new ArrayList<>() {
            {
                add(new Button[]{q00});
                add(new Button[]{q10, q11});
                add(new Button[]{q20, q21, q22});
                add(new Button[]{q30, q31, q32, q33});
                add(new Button[]{q40, q41, q42, q43, q44});
            }
        };
        setQueueAction();

        //Array containing workshops
        this.workshops = new ArrayList<>() {
            {
                add(new Button[]{workshop00, workshop01, workshop02, workshop03});
                add(new Button[]{workshop10, workshop11, workshop12, workshop13});
                add(new Button[]{workshop20, workshop21, workshop22, workshop23});
                add(new Button[]{workshop30, workshop31, workshop32, workshop33});
                add(new Button[]{workshop40, workshop41, workshop42, workshop43});
                add(new Button[]{workshop50, workshop51, workshop52, workshop53});
                add(new Button[]{workshop60, workshop61, workshop62, workshop63});
                add(new Button[]{workshop70, workshop71, workshop72, workshop73});
                add(new Button[]{workshop80, workshop81, workshop82, workshop83});
            }
        };

        //Array containing pattern wall
        this.pattern = new ArrayList<>() {
            {
                add(new Button[]{button00, button01, button02, button03, button04});
                add(new Button[]{button10, button11, button12, button13, button14});
                add(new Button[]{button20, button21, button22, button23, button24});
                add(new Button[]{button30, button31, button32, button33, button34});
                add(new Button[]{button40, button41, button42, button43, button44});
            }
        };

        //Array containing floor
        this.floor = new ArrayList<>() {
            {
                add(floor0);
                add(floor1);
                add(floor2);
                add(floor3);
                add(floor4);
                add(floor5);
                add(floor6);
            }
        };
        for (Button floorButton : floor) {
            floorButton.setOnAction(a -> addToFloor());
        }

        setWorkshopAction();
        updateWorkshops();
        updateFloor();
        updateMiddle();
        updateQueues();
        updatePattern();
        updatePoints();
    }


    /**
     * Used for setting Queues field action
     */
    private void setQueueAction() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < i + 1; j++) {
                int finalI = i;
                queues.get(i)[j].setOnAction(a -> addToQueue(finalI));
            }
        }
    }

    /**
     * Used for setting workshop action
     */
    private void setWorkshopAction() {
        for (int i = 0; i < game.workshops.length; i++) {
            for (int j = 0; j < 4; j++) {
                int workshopId = i;
                int tileId = j;
                workshops.get(i)[j].setOnAction(a -> pickTileFromWorkshop(workshopId, tileId));
            }
        }
    }

    /**
     * Incrementing Label value by 1
     *
     * @param label Label which text to increment by 1
     */
    public void addToLabel(Label label) {
        int points = Integer.parseInt(label.getText());
        label.setText(String.valueOf(++points));
    }

    /**
     * Selecting middle Blue tile
     */
    public void pickMiddleBlue() {
        try {
            isMyTurn();

            if (blueLabel.getText().equals("0")) {
                return;
            }
            String style = middleBlue.getStyle();
            currentTile.setStyle(style);
            currentTileWorkshopId = -2;

            resetBorders();
            middleBlue.getParent().setStyle("-fx-border-width: 2.0; -fx-border-color: red");
        } catch (NotYourTurnException e) {
            msgPopUp(e.getMessage());
        }
    }

    /**
     * Selecting middle Yellow tile
     */
    public void pickMiddleYellow() {
        try {
            isMyTurn();

            if (yellowLabel.getText().equals("0")) {
                return;
            }
            String style = middleYellow.getStyle();
            currentTile.setStyle(style);
            currentTileWorkshopId = -2;

            resetBorders();
            middleYellow.getParent().setStyle("-fx-border-width: 2.0; -fx-border-color: red");
        } catch (NotYourTurnException e) {
            msgPopUp(e.getMessage());
        }
    }

    /**
     * Selecting middle Red tile
     */
    public void pickMiddleRed() {
        try {
            isMyTurn();

            if (redLabel.getText().equals("0")) {
                return;
            }
            String style = middleRed.getStyle();
            currentTile.setStyle(style);
            currentTileWorkshopId = -2;

            resetBorders();
            middleRed.getParent().setStyle("-fx-border-width: 2.0; -fx-border-color: red");
        } catch (NotYourTurnException e) {
            msgPopUp(e.getMessage());
        }
    }

    /**
     * Selecting middle Black tile
     */
    public void pickMiddleBlack() {
        try {
            isMyTurn();

            if (blackLabel.getText().equals("0")) {
                return;
            }
            String style = middleBlack.getStyle();
            currentTile.setStyle(style);
            currentTileWorkshopId = -2;

            resetBorders();
            middleBlack.getParent().setStyle("-fx-border-width: 2.0; -fx-border-color: red");
        } catch (NotYourTurnException e) {
            msgPopUp(e.getMessage());
        }
    }

    /**
     * Selecting middle Green tile
     */
    public void pickMiddleGreen() {
        try {
            isMyTurn();

            if (greenLabel.getText().equals("0")) {
                return;
            }
            String style = middleGreen.getStyle();
            currentTile.setStyle(style);
            currentTileWorkshopId = -2;

            resetBorders();
            middleGreen.getParent().setStyle("-fx-border-width: 2.0; -fx-border-color: red");
        } catch (NotYourTurnException e) {
            msgPopUp(e.getMessage());
        }
    }

    /**
     * Selecting Tiles from the workshops and adding them to current
     *
     * @param workshopId id of workshop
     * @param tileId     id of tile within the workshop
     */
    public void pickTileFromWorkshop(int workshopId, int tileId) {
        try {
            isMyTurn();

            String style = workshops.get(workshopId)[tileId].getStyle();
            this.currentTile.setStyle(style);
            this.currentTileWorkshopId = workshopId;

            resetBorders();
            workshops.get(workshopId)[tileId].getParent().setStyle("-fx-border-width: 2.0; -fx-border-color: red");
        } catch (NotYourTurnException e) {
            msgPopUp(e.getMessage());
        }
    }

    /**
     * Backend adding to queues
     *
     * @param queueId id of queue to add
     */
    public void addToQueue(int queueId) {
        Color color = (javafx.scene.paint.Color) currentTile.getBackground().getFills().get(0).getFill();

        //if nothing is picked
        if (currentTileWorkshopId == -1) {
            msgPopUp("Tile not selected");
            return;
        }

        // if queue (or board) is already full or already has this tile's color
        if (board.getTileQueues()[queueId].isFull() ||
                !board.canAdd(queueId, Tile.valueOf(color))) {
            msgPopUp("Queue is full or the Tile is already placed on the wall");
            return;
        }

        // check whether the row is empty and can add this tile's color
        if (board.getTileQueues()[queueId].getColor() != null &&
                !board.canAddFilledQueue(board.getTileQueues()[queueId].getColor(), color)) {
            msgPopUp("Cannot add this color to this Queue");
            return;
        }

        ArrayList<Tile> pickedTiles;

        //If picked from the middle
        if (currentTileWorkshopId == -2) {
            pickedTiles = game.pickTilesFromMiddle(id, Tile.valueOf(color));
            //If picked from the workshop
        } else {
            pickedTiles = game.workshops[currentTileWorkshopId].pickTiles(Tile.valueOf(color));
        }

        try {
            board.addMulTilesToQueue(queueId, pickedTiles);
        } catch (IllegalArgumentException e) {
            msgPopUp(e.getMessage());
            return;
        }

        currentTileWorkshopId = -1;
        currentTile.setStyle("-fx-border-color: transparent");

        //Updating all GUI fields after adding tile
        updateQueues();
        updateMiddle();
        updateFloor();
        updateWorkshops();

        //Giving turn to next player
        this.game.nextTurn();

        Protocol request = new Protocol(Header.UPDATEGAME);
        request.put("game", game);
        client.send(request);
    }

    /**
     * Adds fallen tiles to the floor.
     */
    private void addToFloor() {
        Color color = (javafx.scene.paint.Color) currentTile.getBackground().getFills().get(0).getFill();

        //if nothing is picked
        if (currentTileWorkshopId == -1) {
            msgPopUp("Tile not selected");
            return;
        }

        ArrayList<Tile> pickedTiles;

        //If picked from the middle
        if (currentTileWorkshopId == -2) {
            pickedTiles = game.pickTilesFromMiddle(id, Tile.valueOf(color));
            //If picked from the workshop
        } else {
            pickedTiles = game.workshops[currentTileWorkshopId].pickTiles(Tile.valueOf(color));
        }

        for (Tile ignored : pickedTiles) {
            board.addTileToFloor();
        }

        currentTileWorkshopId = -1;
        currentTile.setStyle("-fx-border-color: transparent");

        //Updating all GUI fields after adding tile
        updateWorkshops();
        updateQueues();
        updateMiddle();
        updateFloor();

        //Giving turn to next player
        this.game.nextTurn();

        Protocol request = new Protocol(Header.UPDATEGAME);
        request.put("game", game);
        client.send(request);
    }

    /**
     * Method checks if it is this players turn
     *
     * @throws NotYourTurnException throws exception when it is not this players turn
     */
    public void isMyTurn() throws NotYourTurnException {
        if (game.getCurrentPlayerTurn() != id || currentId != id) {
            throw new NotYourTurnException("It is not your turn currently");
        }
    }

    /**
     * Single message pop-up
     *
     * @param msg message the pop-up will contain
     */
    public void msgPopUp(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Illegal move");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    /**
     * Used for ending round, displaying pop-up and returning to Client View
     */
    public void endGame() {
        updatePattern();
        updatePoints();

        if (id == game.getWinner()) {
            File imageFile = new File("src/main/resources/GUIs/winner.gif");
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);

            BorderPane pane = new BorderPane();
            pane.setCenter(imageView);
            Scene scene = new Scene(pane);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle("You WIN!" + " Points: " + board.getScore());
            stage.setResizable(false);
            stage.setOnCloseRequest(
                    e -> {
                        ClientController client = (this.stage).client;
                        client.getProfileView();
                        stage.close();
                    }
            );
            stage.show();
        } else {
            File imageFile = new File("src/main/resources/GUIs/looser.gif");
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);

            BorderPane pane = new BorderPane();
            pane.setCenter(imageView);
            Scene scene = new Scene(pane);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle("You LOSE!" + " Points: " + board.getScore());
            stage.setResizable(false);
            stage.setOnCloseRequest(
                    e -> {
                        ClientController client = (this.stage).client;
                        client.getProfileView();
                        stage.close();
                    }
            );
            stage.show();
        }
    }

    /**
     * Updating GUI queues
     */
    public void updateQueues() {
        Board.TileQueue[] queues = board.getTileQueues();

        for (int queueId = 0; queueId < 5; queueId++) {
            //If queue is not empty
            if (queues[queueId].getCounter() != 0) {
                for (int tile = queueId; tile > queueId - queues[queueId].getCounter(); tile--) {
                    if (queues[queueId].getColor() != null) {
                        String style = "-fx-background-color: " + "#" + Integer.toHexString(
                                queues[queueId].getColor().getRGB()).substring(2);
                        this.queues.get(queueId)[tile].setStyle(style);
                    }
                }
            } else {
                //If queue is empty reset color to default
                for (int tile = 0; tile < queueId + 1; tile++) {
                    String style = "-fx-border-color: transparent";
                    this.queues.get(queueId)[tile].setStyle(style);
                }
            }
        }
    }

    /**
     * Synchronizing GUI workshops with underlying Game workshops
     */
    public void updateWorkshops() {
        //if the game has ended
        if (game.isEndOfGame()) {
            endGame();
            return;
        }
        int workshopIndex = 0;
        for (Game.Workshop workshop : game.workshops) {
            int tileIndex = 0;
            for (Tile tile : workshop.getTiles()) {
                if (tile == null) {
                    this.workshops.get(workshopIndex)[tileIndex].setStyle("-fx-background-color: transparent");

                } else {
                    String style = "-fx-background-color: " + "#" + Integer.toHexString(tile.getColor().getRGB()).substring(2);
                    this.workshops.get(workshopIndex)[tileIndex].setStyle(style);
                }
                tileIndex += 1;
            }
            workshopIndex += 1;
        }
    }

    /**
     * Clears queues.
     */
    private void clearQueues() {
        for (int queueId = 0; queueId < 5; queueId++) {
            for (int tile = 0; tile < queueId + 1; tile++) {
                String style = "-fx-border-color: transparent";
                this.queues.get(queueId)[tile].setStyle(style);
            }
        }
    }

    /**
     * Clears displayed pattern.
     */
    private void clearPattern() {
        ArrayList<String> order = new ArrayList<>(List.of("#c9daf8", "#fff2cc", "#e6b8af", "#999999", "#b6d7a8"));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String style = "-fx-background-color: " + order.get(j);
                pattern.get(i)[j].setStyle(style);
            }
            Collections.rotate(order, 1);
        }

    }

    /**
     * Used for updating pattern wall
     */
    public void updatePattern() {
        boolean[][] placed = board.getPlacedTiles();
        ArrayList<Tile> order = new ArrayList<>(List.of(Tile.BLUE, Tile.YELLOW, Tile.RED, Tile.BLACK, Tile.GREEN));

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Tile color = order.get(j);
                if (placed[i][j]) {
                    String style = "-fx-background-color: " + "#" + Integer.toHexString(color.getColor().getRGB()).substring(2);
                    pattern.get(i)[j].setStyle(style);
                }
            }
            Collections.rotate(order, 1);
        }
    }


    /**
     * Updates middle field counters in GUI
     */
    public void updateMiddle() {
        LinkedList<Tile> middle = game.getMiddleField();
        resetLabel(blackLabel);
        resetLabel(redLabel);
        resetLabel(yellowLabel);
        resetLabel(greenLabel);
        resetLabel(blueLabel);

        for (Tile tile : middle) {
            switch (tile) {
                case BLACK -> addToLabel(blackLabel);
                case RED -> addToLabel(redLabel);
                case YELLOW -> addToLabel(yellowLabel);
                case GREEN -> addToLabel(greenLabel);
                case BLUE -> addToLabel(blueLabel);
                default -> {
                }
            }
        }

        if (game.isFirst()) {
            middleSpecial.setText("1");
        } else {
            middleSpecial.setText("");
        }
    }

    /**
     * Updating floor fields
     */
    public void updateFloor() {
        resetFloor();
        for (int i = 0; i < board.getFloor(); i++) {
            floor.get(i).setText("X");
        }
        specialFloorTile.setVisible(id == game.getPlayerTookFirst());
    }

    /**
     * Updating points
     */
    public void updatePoints() {
        resetLabel(points);
        for (int i = 0; i < board.getScore(); i++) {
            addToLabel(points);
        }
    }

    /**
     * Resetting floor fields
     */
    private void resetFloor() {
        for (Button button : floor) {
            button.setText("");
        }
    }

    /**
     * Resetting border fields
     */
    private void resetBorders() {
        for (Button[] workshop : workshops) {
            workshop[0].getParent().setStyle("-fx-border-color: transparent");
        }
        middleBlue.getParent().setStyle("-fx-border-color: transparent");
    }

    /**
     * Resetting value label to 0
     *
     * @param label specified label to set to 0
     */
    private void resetLabel(Label label) {
        label.setText("0");
    }
}