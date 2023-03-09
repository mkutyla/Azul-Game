package GUIs.Controllers;

/**
 * Thrown whenever a player attempts to make a move when it's not their turn.
 */
public class NotYourTurnException extends Exception {
    /**
     * Constructor for this.
     *
     * @param msg message to assign to this exception
     */
    public NotYourTurnException(String msg) {
        super(msg);
    }
}
