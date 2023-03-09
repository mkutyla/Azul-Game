package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Main Game Class used for initializing a single game which consists of desired number
 * of players from 2-4, according number of workshops and a Tile pouch
 */
public class Game implements Serializable {
    /**
     * Boards of this game corresponding and representing this Game's players.
     */
    private final Board[] boards;

    /**
     * Pouch to draw the tiles from.
     */
    private final Pouch pouch;

    /**
     * All workshops to get the tiles from.
     */
    public final Workshop[] workshops;

    /**
     * Tiles dropped to the middle.
     */
    private final LinkedList<Tile> middleField;

    /**
     * Flag indicating whether tiles where drawn from the middle field this round.
     */
    private boolean isFirst;

    /**
     * ID of the player that has drawn the tiles for the first time in a round.
     */
    private int playerTookFirst;

    /**
     * ID of the player whose turn this is.
     */
    private int currentPlayerTurn;

    /**
     * String representing a specific time when this game was saved.
     */
    private String whenSaved;

    /**
     * Usernames of all the players playing this ga,e
     */
    private final ArrayList<String> usernames;


    /**
     * Setter for this.whenSaved.
     *
     * @param whenSaved to be assigned
     */
    public void setWhenSaved(String whenSaved) {
        this.whenSaved = whenSaved;
    }

    /**
     * Getter for this.whenSaved.
     *
     * @return this.whenSaved.
     */
    public String getWhenSaved() {
        return whenSaved;
    }

    /**
     * Getter for this.usernames.
     *
     * @return this.usernames.
     */
    public ArrayList<String> getUsernames() {
        return usernames;
    }

    /**
     * Single Workshop which stores randomly selected Tiles from pouch
     */
    public class Workshop implements Serializable {

        /**
         * Tiles stored in this Workshop.
         */
        private final Tile[] tiles = new Tile[4];

        /**
         * Indicating whether this Workshop contains any tiles.
         */
        private boolean isEmpty;

        /**
         * Constructor for this.
         */
        public Workshop() {
            this.isEmpty = true;
        }

        /**
         * Fills workshop with tiles.
         */
        public void fillWithTiles() {
            for (int i = 0; i < 4; i++) {
                tiles[i] = pouch.getTile();
            }
            isEmpty = false;
        }

        /**
         * Picks Tiles of given color from the workshops and adds remaining
         * to the middle field
         *
         * @param color Color
         * @return all picked Tiles with given Color
         */
        public ArrayList<Tile> pickTiles(Tile color) {
            ArrayList<Tile> picked = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (tiles[i] != null && tiles[i].equals(color)) {
                    picked.add(tiles[i]);
                } else {
                    middleField.add(tiles[i]);
                }
                tiles[i] = null;
            }
            isEmpty = true;
            return picked;
        }

        /**
         * Getter for this.tiles.
         *
         * @return this.tiles
         */
        public Tile[] getTiles() {
            return tiles;
        }

        /**
         * Getter for this.isEmpty.
         *
         * @return this.isEmpty
         */
        public boolean isEmpty() {
            return isEmpty;
        }
    }

    /**
     * Fills all workshops with tiles.
     */
    public void fillWorkshops() {
        for (Workshop workshop : workshops) {
            workshop.fillWithTiles();
        }
    }

    /**
     * Used for collecting Tiles from the middle of the board.
     *
     * @param boardIndex of a boardIndex that collects the Tiles
     * @param color      Color of picked Tiles
     * @return Picked Tiles
     */
    public ArrayList<Tile> pickTilesFromMiddle(int boardIndex, Tile color) {
        //adding minus floor points for the first boardIndex that collects from the middle in a round
        if (isFirst) {
            isFirst = false;
            playerTookFirst = boardIndex;
            boards[boardIndex].addTileToFloor();
        }
        ArrayList<Tile> picked = new ArrayList<>();
        for (int i = 0; i < middleField.size(); i++) {
            if (middleField.get(i).equals(color)) {
                picked.add(middleField.get(i));
                middleField.remove(i);
                i -= 1;
            }
        }
        return picked;
    }

    /**
     * Ends the round.
     */
    public void endRound() {
        getTilesFromRows();
        emptyFloors();
        if (isEndOfGame()) {
            endGame();
            return;
        }
        isFirst = true;
        //This player will start the next round
        currentPlayerTurn = playerTookFirst;
        playerTookFirst = -1;

        fillWorkshops();
    }

    /**
     * Empties all boards floor.
     */
    private void emptyFloors() {
        for (Board board : boards) {
            board.calculateFloorScore();
        }
    }

    /**
     * Checks whether the game has ended i.e. if any of the players completed a row.
     *
     * @return true, if the game has ended, false otherwise
     */
    public boolean isEndOfGame() {
        for (Board player : boards) {
            if (player.isAnyRowCompleted()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Collects Tiles from all completed rows and places them in correct board spots.
     */
    public void getTilesFromRows() {
        for (Board board : boards) {
            board.checkQueues();
        }
    }

    /**
     * Returns queue tiles to the Tile pouch
     *
     * @param amount amount of Tiles to return
     * @param tile   type of Tiles (all the Tiles from a single queue are the same type)
     */
    public void addToPouch(int amount, Tile tile) {
        for (int i = 0; i < amount; i++) {
            pouch.returnTile(tile);
        }
    }

    /**
     * Checks if round has ended i.e. both workshops and middle field are empty
     *
     * @return True/False
     */
    public boolean isRoundEnd() {
        if (!middleField.isEmpty()) {
            return false;
        }
        for (Workshop w : workshops) {
            if (!w.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * End the game, calculates final scores of all players
     */
    public void endGame() {
        for (Board player : boards) {
            player.calculateFinalScore();
        }
    }


    /**
     * Game object constructor initializes all player boards and proper number
     * of workshops as well as pouch
     *
     * @param players   number of players
     * @param usernames usernames of players
     */
    public Game(int players, ArrayList<String> usernames) {
        this.usernames = usernames;
        this.boards = new Board[players];

        for (int i = 0; i < players; i++) {
            boards[i] = new Board(this);
        }

        this.pouch = new Pouch();

        this.workshops = new Workshop[players * 2 + 1];
        for (int i = 0; i < players * 2 + 1; i++) {
            workshops[i] = new Workshop();
        }

        this.middleField = new LinkedList<>();
        this.isFirst = true;
        this.playerTookFirst = -1;
        this.currentPlayerTurn = new Random().nextInt(playerCount());

        fillWorkshops();
    }

    /**
     * Initiates next turn
     */
    public void nextTurn() {
        if (isRoundEnd()) {
            endRound();
            return;
        }
        currentPlayerTurn = ++currentPlayerTurn % playerCount();
    }

    /**
     * Getter for this.currentPlayerTurn.
     *
     * @return this.currentPlayerTurn
     */
    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    /**
     * Gets the number of players playing this Game.
     *
     * @return number of players playing this Game (2, 3 or 4)
     */
    public int playerCount() {
        return boards.length;
    }

    /**
     * Getter for this.middleField.
     *
     * @return this.middleField
     */
    public LinkedList<Tile> getMiddleField() {
        return middleField;
    }

    /**
     * Gets board (player) matching passed id.
     *
     * @return matching board (player)
     */
    public Board getPlayer(int id) {
        return boards[id];
    }

    /**
     * Getter for this.isFirst.
     *
     * @return this.isFirst
     */
    public boolean isFirst() {
        return isFirst;
    }

    /**
     * Finds and returns board id of the winner
     *
     * @return int id of board of the winner
     */
    public int getWinner() {
        int highestScore = 0;
        int winner = 0;
        for (int i = 0; i < playerCount(); i++) {
            if (boards[i].getScore() > highestScore) {
                highestScore = boards[i].getScore();
                winner = i;
            }
        }
        return winner;
    }

    /**
     * Getter for this.playerTookFirst.
     *
     * @return this.playerTookFirst.
     */
    public int getPlayerTookFirst() {
        return playerTookFirst;
    }
}
