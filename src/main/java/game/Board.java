package game;


import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class represents a Board of Azul.
 */
public class Board implements Serializable {
    /**
     * Game which this board is part of
     */
    private final Game game;

    /**
     * Score on this board.
     */
    private int score = 0;

    /**
     * Pattern of the pattern board.
     */
    private final Tile[][] pattern = new Tile[5][5];

    /**
     * All tiles placed by the user.
     */
    private final boolean[][] placedTiles = new boolean[5][5];

    /**
     * Amount of points lost due to tiles falling to the floor.
     */
    private int floor = 0;

    /**
     * Containing tiles put into each tile queue.
     */
    private final TileQueue[] tileQueues = new TileQueue[5];


    /**
     * Tile Queue on the left of each board is a place where players place Tiles.
     * When the Queue is full one tile from the queue is placed in the corresponding
     * place on the main board while the rest return to the pouch.
     */
    public class TileQueue implements Serializable {
        private Tile color;
        private final int size;
        private int counter = 0;

        public TileQueue(int size) {
            this.size = size;
        }

        /**
         * Adding a tile to the queue
         *
         * @param tile Tile to add
         */
        public void add(Tile tile) {
            //if the queue is empty, set its color
            if (counter == 0) {
                color = tile;
            }
            if (!tile.equals(color)) {
                throw new IllegalArgumentException("Wrong color");
            }
            if (isFull()) {
                throw new FullTileQueueException("This queue is already full");
            }
            counter += 1;
        }

        /**
         * Used for clearing the queue from all tiles
         */
        public void clear() {
            color = null;
            counter = 0;
        }

        /**
         * Getter for this.counter.
         *
         * @return this.counter
         */
        public int getCounter() {
            return counter;
        }

        /**
         * Check whether this TileQueue is full.
         *
         * @return true, if this queue reached its capacity, false otherwise
         */
        public boolean isFull() {
            return counter == size;
        }

        /**
         * Getter for this.size.
         *
         * @return this.size.
         */
        public int getSize() {
            return size;
        }

        /**
         * Gets Color of this queue.
         *
         * @return Color of this queue.
         */
        public Color getColor() {
            if (this.color == null) return null;
            return this.color.getColor();
        }
    }

    /**
     * Thrown whenever an extra tile is tried to be appended into a full tile queue.
     */
    public class FullTileQueueException extends IllegalArgumentException implements Serializable {

        /**
         * Constructor for this.
         *
         * @param msg message to assign to this exception
         */
        public FullTileQueueException(String msg) {
            super(msg);
        }
    }

    /**
     * Main board constructor connects the constructed board to appropriate game.
     * Creates all needed object for the game such as:
     * - 5 Queues with different size
     * - Pattern of the main board
     *
     * @param game Object of game class to which we need to connect the game
     */
    public Board(Game game) {
        this.game = game;

        for (int i = 0; i < 5; i++) {
            tileQueues[i] = new TileQueue(i + 1);
        }

        //placing the pattern
        ArrayList<Tile> order = new ArrayList<>(List.of(Tile.BLUE, Tile.YELLOW, Tile.RED, Tile.BLACK, Tile.GREEN));
        for (Tile[] row : pattern) {
            for (int i = 0; i < 5; i++) {
                row[i] = order.get(i);
            }
            Collections.rotate(order, 1);
        }
    }

    /**
     * Placing a tile in corresponding pattern place
     *
     * @param queue queue to place a tile to
     */
    private void placeTile(TileQueue queue) {
        for (int i = 0; i < 5; i++) {
            if (pattern[queue.size - 1][i].equals(queue.color)) {
                this.placedTiles[queue.size - 1][i] = true;
                addScore(queue.size - 1, i);
                break;
            }
        }
    }

    /**
     * Checks whether any of the queues is full and places tiles from
     * the full queues on the main board
     */
    public void checkQueues() {
        for (TileQueue queue : tileQueues) {
            if (queue.isFull()) {
                placeTile(queue);
                game.addToPouch(queue.size, queue.color);
                queue.clear();
            }
        }
    }

    /**
     * Checks whether user can put tile of selected color in row queueId.
     *
     * @param queueId queue's id
     * @param tile    tile to insert
     * @return true, if user can put it, false otherwise
     */
    public boolean canAdd(int queueId, Tile tile) {
        for (int i = 0; i < 5; i++) {
            if (pattern[queueId][i].equals(tile)) {
                return !placedTiles[queueId][i];
            }
        }
        return true;
    }

    /**
     * This method checks whether specific color matches a different javafx paint color.
     *
     * @param color  first color
     * @param color2 javafx color
     * @return true, if colors match, false otherwise
     */
    public boolean canAddFilledQueue(Color color, javafx.scene.paint.Color color2) {
        String colorString = color2.toString();
        // parsing color2 into html color format
        colorString = colorString.substring(2, colorString.length() - 2);
        return Integer.toHexString(color.getRGB()).substring(2).equals(colorString);
    }

    /**
     * Trying to add a tile to queue
     *
     * @param queueId id of a queue
     * @param tile    Tile to add
     */
    public void addTileToQueue(int queueId, Tile tile) {
        //checks whether the tile is already placed on the main board
        for (int i = 0; i < 5; i++) {
            if (tile.equals(pattern[queueId][i])) {
                if (placedTiles[queueId][i]) {
                    throw new IllegalArgumentException("The tile is already placed on the board");
                }
            }
        }
        tileQueues[queueId].add(tile);
    }

    /**
     * Used for adding multiple tiles
     * @param queueId id of Queue
     * @param tiles ArrayList of tiles to add
     */
    public void addMulTilesToQueue(int queueId, ArrayList<Tile> tiles) {
        for (Tile tile : tiles) {
            try {
                addTileToQueue(queueId, tile);
            } catch (FullTileQueueException e) {
                addTileToFloor();
            }
        }
    }

    /**
     * Place a tile on the floor field
     */
    public void addTileToFloor() {
        floor += 1;
    }

    /**
     * Calculating floor score on the end of the round
     */
    public void calculateFloorScore() {
        if (floor < 3) {
            score -= floor;
        } else if (floor < 6) {
            score -= 2 + (floor - 2) * 2;
        } else {
            score -= 8 + (floor - 5) * 3;
        }
        if (score < 0) {
            score = 0;
        }
        this.floor = 0;
    }

    /**
     * Adding a score for one placed tile with given coordinates
     *
     * @param row    index of row the tile was placed in
     * @param column index of column the tile was placed in
     */
    public void addScore(int row, int column) {
        //TODO: make this shorter and more readable
        int counter = 0;
        boolean isRowTouching = false;
        boolean isColumnTouching = false;
        //checking row
        for (int i = column + 1; i < 5; i++) {
            if (placedTiles[row][i]) {
                counter++;
                isRowTouching = true;
            } else {
                break;
            }
        }
        for (int i = column - 1; i > -1; i--) {
            if (placedTiles[row][i]) {
                counter++;
                isRowTouching = true;
            } else {
                break;
            }
        }
        //checking columns
        for (int i = row + 1; i < 5; i++) {
            if (placedTiles[i][column]) {
                counter++;
                isColumnTouching = true;
            } else {
                break;
            }
        }
        for (int i = row - 1; i > -1; i--) {
            if (placedTiles[i][column]) {
                counter++;
                isColumnTouching = true;
            } else {
                break;
            }
        }
        score += (isColumnTouching && isRowTouching) ? counter + 2 : counter + 1;
    }

    /**
     * Calculating a final score on the end of the game
     */
    public void calculateFinalScore() {
        //check for complete rows
        for (boolean[] row : placedTiles) {
            if (IntStream.range(0, row.length).allMatch(i -> row[i])) {
                score += 2;
            }
        }

        //check complete columns
        for (int column = 0; column < 5; column++) {
            boolean isComplete = true;
            for (int row = 0; row < 5; row++) {
                if (!placedTiles[row][column]) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) {
                score += 7;
            }
        }

        //check for same color
        for (int column = 0; column < 5; column++) {
            boolean isComplete = true;
            for (int row = 0; row < 5; row++) {
                if (!placedTiles[row][(column + row) % 5]) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) {
                score += 10;
            }
        }
    }

    /**
     * Checks if the game has ended i.e. if any of the rows is completed
     *
     * @return boolean value: True if the row
     */
    public boolean isAnyRowCompleted() {
        for (boolean[] row : placedTiles) {
            if (IntStream.range(0, row.length).allMatch(i -> row[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Getter for this.score.
     *
     * @return this.score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Getter for this.tileQueues.
     *
     * @return this.tileQueues.
     */
    public TileQueue[] getTileQueues() {
        return tileQueues;
    }

    /**
     * Getter for this.floor.
     *
     * @return this.floor
     */
    public int getFloor() {
        return floor;
    }

    /**
     * Getter for this.placedTiles.
     *
     * @return this.placedTiles.
     */
    public boolean[][] getPlacedTiles() {
        return placedTiles;
    }
}
