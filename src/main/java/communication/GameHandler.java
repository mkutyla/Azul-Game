package communication;

import game.Game;

import java.util.ArrayList;

/**
 * This class is used to handle game that is being played by Clients.
 */
public class GameHandler {
    /**
     * This game's code, used to connect to this game.
     */
    private final String gameCode;
    /**
     * Server that's hosting this game.
     */
    private final Server server;
    /**
     * Contains all connected players.
     */
    private final ArrayList<Service> players;
    /**
     * True, if this game is running, false otherwise.
     */
    private boolean hasStarted;
    /**
     * Game that is being played by the players.
     */
    private Game game;

    /**
     * Getter for hasStarted
     *
     * @return hasStarted
     */
    public boolean hasStarted() {
        return hasStarted;
    }

    /**
     * Constructor for this method.
     *
     * @param server  on which this is running
     * @param creator player that created this game
     */
    public GameHandler(Server server, Service creator) {
        this.server = server;
        this.players = new ArrayList<>();
        this.players.add(creator);
        this.gameCode = server.addGame(this);

    }

    /**
     * Constructor for this method.
     *
     * @param server  on which this is running
     * @param creator player that created this game
     */
    public GameHandler(Server server, Service creator, Game game) {
        this.server = server;
        this.players = new ArrayList<>();
        this.players.add(creator);
        this.gameCode = server.addGame(this);
        this.game = game;

    }

    /**
     * Returns this.gameCode.
     *
     * @return this.gameCode
     */
    public String getGameCode() {
        return this.gameCode;
    }

    /**
     * Attempts to add passed player to the lobby. Sends a reply to the requester.
     *
     * @param player who wants to join this lobby
     */
    synchronized public void addPlayer(Service player) {
        Protocol reply;
        if (players.contains(player)) {
            reply = new Protocol(Header.FAIL);
            reply.put("source", Header.JOINGAME);
            reply.put("dialog", "You are already in this lobby!");
        } else if (hasStarted) {
            reply = new Protocol(Header.FAIL);
            reply.put("source", Header.JOINGAME);
            reply.put("dialog", "This game has already started!");
        } else if (isFull()) {
            reply = new Protocol(Header.FAIL);
            reply.put("source", Header.JOINGAME);
            reply.put("dialog", "This lobby is full!");
        } else if (game != null && !game.getUsernames().contains(player.getUsername())) {
            reply = new Protocol(Header.FAIL);
            reply.put("source", Header.JOINGAME);
            reply.put("dialog", "This game is being resumed!");
        } else {
            players.add(player);
            player.setGame(this);
            reply = new Protocol(Header.JOINEDGAME);
            reply.put("creator", players.get(0).getUsername());
            Protocol replyCreator = new Protocol(Header.PLAYERJOINED);
            replyCreator.put("username", player.getUsername());
            players.get(0).send(replyCreator);
        }

        player.send(reply);
    }

    /**
     * Attempts to start this lobby.
     */
    public void start() {
        if (game != null && game.playerCount() != players.size()) {
            StringBuilder sb = new StringBuilder();
            boolean found;
            for (String username : game.getUsernames()) {
                found = false;
                for (Service player : players) {
                    if (player.getUsername().equals(username)) {
                        found = true;
                        break;
                    }
                }
                if (!found) sb.append(username).append(",\n");
            }
            sb.deleteCharAt(sb.length() - 2); // trimming last ",\n"
            Protocol fail = new Protocol(Header.FAIL);
            fail.put("source", Header.STARTGAME);
            fail.put("dialog", String.format("[Resumed game] Missing player(s):\n%s", sb));
            players.get(0).send(fail);
            return;
        }

        this.hasStarted = true;
        ArrayList<String> usernames = new ArrayList<>();
        for (Service s : players) {
            usernames.add(s.getUsername());
        }

        if (game == null) {
            game = new Game(players.size(), usernames);
        }

        Protocol reply = new Protocol(Header.STARTEDGAME);
        reply.put("creator", players.get(0).getUsername());
        reply.put("game", game);
        for (int i = 0; i < players.size(); i++) {
            reply.put("id", i);
            players.get(i).send(reply);
        }
    }

    /**
     * This method updates this.game.
     *
     * @param updated updated game
     */
    public void updateGame(Game updated) {
        this.game = updated;
        sendUpdated();
    }

    /**
     * Sends updated game to all players.
     */
    public void sendUpdated() {
        Protocol update = new Protocol(Header.GAMEUPDATED);
        update.data.put("game", this.game);
        for (int i = 0; i < players.size(); i++) {
            update.put("id", i);
            players.get(i).send(update);
        }
    }

    /**
     * Returns whether this lobby reached in max capacity (4 players)
     *
     * @return true, if lobby has 4 players, false otherwise
     */
    public boolean isFull() {
        return players.size() == 4;
    }

    /**
     * Reacts to client's disconnection from the game.
     *
     * @param service Client that left the game
     */
    synchronized public void left(Service service) {
        Protocol forCreator = new Protocol(Header.LEAVEGAME);
        forCreator.put("game", game);
        forCreator.put("username", service.getUsername());
        Service creator = players.get(0);
        creator.send(forCreator);
        creator.setGame(null);

        Protocol forUsers = new Protocol(Header.LEAVEGAME);
        forUsers.put("username", service.getUsername());
        for (int i = 1; i < players.size(); i++) {
            Service player = players.get(i);
            player.setGame(null);
            if (player.getUsername().equals(service.getUsername())) continue; // skip the one that left
            player.send(forUsers); // send to others
        }

        // removing this game from Server
        server.removeGame(gameCode);
    }

    /**
     * Removes this game and all of its clients.
     *
     * @param creator of this lobby
     */
    public void killLobby(String creator) {
        Protocol reply = new Protocol(Header.LEAVELOBBY);
        reply.put("username", creator);
        reply.put("creator", true);
        for (Service s : players) {
            s.setGame(null);
            s.send(reply);
        }
        server.removeGame(gameCode);
    }

    /**
     * Performs necessary operations to remove passed service from this GameHandler.
     *
     * @param service that left the lobby, to remove
     */
    public void leftLobby(Service service) {
        if (players.get(0).getUsername().equals(service.getUsername())) { // creator left
            killLobby(service.getUsername());
        } else {
            players.remove(service);
            service.setGame(null);

            Protocol forCreator = new Protocol(Header.LEAVELOBBY);
            forCreator.put("username", service.getUsername());
            players.get(0).send(forCreator);
        }

    }
}
