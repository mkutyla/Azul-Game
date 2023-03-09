package communication;

import game.Game;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class models user's data stored by Server class.
 */
public class UserData implements Serializable {

    /**
     * Client's username.
     */
    private final String username;

    /**
     * SHA-256 hash of Client's password.
     */
    private final String passwordHash;

    /**
     * Storing Client's unfinished games.
     */
    private final ArrayList<Game> unfinishedGames;

    /**
     * Constructor for this class.
     *
     * @param username     user's username
     * @param passwordHash sha256 hash of user's password
     */
    public UserData(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.unfinishedGames = new ArrayList<>();
    }

    /**
     * Getter for this.passwordHash.
     *
     * @return this.passwordHash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Getter for this.username.
     *
     * @return this.username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for this.unfinishedGames.
     *
     * @return this.unfinishedGames
     */
    public ArrayList<Game> getUnfinishedGames() {
        return unfinishedGames;
    }

    /**
     * Adds a new Game to the list of unfinished games.
     *
     * @param game to add
     */
    public void addUnfinishedGame(Game game) {
        unfinishedGames.add(game);
    }

    /**
     * Replaces a Game in the list of unfinished games at specific index id.
     *
     * @param id   index of the Game to overwrite
     * @param game to add
     */
    public void addUnfinishedGame(int id, Game game) {
        unfinishedGames.set(id, game);
    }

    /**
     * Removes unfinished game stored at passed index in this.unfinishedGames.
     *
     * @param id index of the Game to remove
     */
    public void removeUnfinishedGame(int id) {
        unfinishedGames.remove(id);
    }
}
