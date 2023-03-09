package game;

import java.io.Serializable;
import java.util.Collections;
import java.util.Stack;

/**
 * This class represents an Azul Pouch of Tiles.
 */
public class Pouch implements Serializable {
    /**
     * Tiles stored in this pouch.
     */
    private final Stack<Tile> tiles = new Stack<>();

    /**
     * Tiles that are to return to this pouch.
     */
    private final Stack<Tile> returnedTiles = new Stack<>();

    /**
     * Constructor for this.
     */
    public Pouch() {
        for (int i = 0; i < 20; i++) {
            tiles.push(Tile.BLACK);
            tiles.push(Tile.RED);
            tiles.push(Tile.YELLOW);
            tiles.push(Tile.GREEN);
            tiles.push(Tile.BLUE);
        }
    }

    /**
     * Used for drawing a single tile from a shuffled bag
     *
     * @return a single tile
     */
    public Tile getTile() {
        Collections.shuffle(tiles);
        if (tiles.isEmpty()) {
            refill();
        }
        //If tile is still empty after refill
        if (tiles.isEmpty()) {
            return null;
        }
        return tiles.pop();
    }

    /**
     * Refills this pouch with all returned tiles.
     */
    private void refill() {
        while (!returnedTiles.isEmpty()) {
            tiles.push(returnedTiles.pop());
        }
    }


    /**
     * Adds a new returned tile to this.returnedTiles.
     *
     * @param tile to add
     */
    public void returnTile(Tile tile) {
        this.returnedTiles.push(tile);
    }

}
