package game;

import java.awt.*;
import java.io.Serializable;

/**
 * This enum represents every coloured Azul Tile.
 */
public enum Tile implements Serializable {
    BLACK,
    RED,
    YELLOW,
    GREEN,
    BLUE;

    /**
     * Returns the Tile corresponding to passed color.
     *
     * @param color color to match a Tile.
     * @return matching Tile
     */
    public static Tile valueOf(javafx.scene.paint.Color color) {
        switch (color.toString()) {
            case "0x000000ff" -> {
                return Tile.BLACK;
            }
            case "0xff0000ff" -> {
                return Tile.RED;
            }
            case "0xffd700ff" -> {
                return Tile.YELLOW;
            }
            case "0x008000ff" -> {
                return Tile.GREEN;
            }
            case "0x0000ffff" -> {
                return Tile.BLUE;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Returns Color of this Tile.
     *
     * @return color of this Tile.
     */
    public Color getColor() {
        switch (this) {
            case BLACK -> {
                return Color.decode("#000000");
            }
            case RED -> {
                return Color.decode("#FF0000");
            }
            case YELLOW -> {
                return Color.decode("#FFD700");
            }
            case GREEN -> {
                return Color.decode("#008000");
            }
            case BLUE -> {
                return Color.decode("#0000FF");
            }
            default -> {
                return null;
            }
        }
    }

}
