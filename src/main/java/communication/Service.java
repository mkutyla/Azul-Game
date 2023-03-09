package communication;

import game.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class is used to handle Server's client.
 */
public class Service implements Runnable {

    /**
     * ID of client handled by this Service.
     */
    private int id;

    /**
     * Server running this Service.
     */
    private final Server server;

    /**
     * Used to send data between client and this Service.
     */
    private Socket clientSocket;

    /**
     * Game to which this client is currently connected.
     */
    private GameHandler game;

    /**
     * Username of this
     */
    private String username;

    /**
     * Stream used to receive data from client.
     */
    private ObjectInputStream input;

    /**
     * Stream used to send data to client.
     */
    private ObjectOutputStream output;

    /**
     * Constructor for this.
     *
     * @param clientSocket socket to handle
     * @param server       that called this Service
     */
    public Service(Socket clientSocket, Server server, int id) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.username = null;
        this.id = id;
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
     * Initializes streams.
     *
     * @throws IOException when an I/O error occurs
     */
    public void init() throws IOException {
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        input = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * Closes this Service.
     */
    public void close() {
        if (clientSocket == null) return;
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing client (" + id + "), " + e);
        } finally {
            output = null;
            input = null;
            clientSocket = null;
        }
    }

    @Override
    public void run() {
        while (true) {
            Protocol command = receive();
            System.out.printf("[%d] Received %s%n", id, command);
            Protocol answer;
            switch (command.header) {
                case CONNECT -> {
                    answer = new Protocol(Header.CONNECTED);
                    answer.put("id", (id = server.nextID()));
                    send(answer);
                }
                case REGISTER -> {
                    String username = (String) command.data.get("username");
                    String passwordHash = (String) command.data.get("passwordHash");
                    if (server.addUser(username, passwordHash)) {
                        answer = new Protocol(Header.REGISTERED);
                        answer.put("userdata", server.getUserData(username));
                    } else {
                        answer = new Protocol(Header.FAIL);
                        answer.put("source", Header.REGISTER);
                        answer.put("dialog", "This username is already taken!");
                    }

                    send(answer);
                }
                case LOGIN -> {
                    String user = (String) command.data.get("username");
                    String passwordHash = (String) command.data.get("passwordHash");

                    if (!server.matchesCredentials(user, passwordHash)) {
                        answer = new Protocol(Header.FAIL);
                        answer.put("source", Header.LOGIN);
                        answer.put("dialog", "Invalid credentials!");
                    } else if (server.isConnected(user)) {
                        answer = new Protocol(Header.FAIL);
                        answer.put("source", Header.LOGIN);
                        answer.put("dialog", "Already logged in!");
                    } else {
                        this.username = user;
                        answer = new Protocol(Header.LOGGEDIN);
                        answer.put("userdata", server.getUserData(user));
                    }

                    send(answer);
                }
                case LOGGEDOUT -> {
                    UserData userData = (UserData) command.data.get("userdata");
                    server.updateUser(this.username, userData);
                    server.removeClientService(this);
                    return;
                }
                case JOINGAME -> {
                    String gameCode = (String) command.data.get("code");
                    if (!server.validateCode(gameCode)) {
                        answer = new Protocol(Header.FAIL);
                        answer.put("source", Header.JOINGAME);
                        answer.put("dialog", "Invalid game code!");
                    } else {
                        this.game = server.getGame(gameCode);
                        game.addPlayer(this);
                        break;
                    }
                    send(answer);
                }
                case LEAVEGAME -> {
                    if (game != null && game.hasStarted()) {
                        game.left(this);
                    }
                }
                case LEAVELOBBY -> {
                    if (game == null || game.hasStarted()) break;
                    game.leftLobby(this);
                }
                case CREATEGAME -> {
                    if (command.data.containsKey("game")) {
                        game = new GameHandler(server, this, (Game) command.data.get("game"));
                    } else {
                        game = new GameHandler(server, this);
                    }
                    answer = new Protocol(Header.CREATEDGAME);
                    answer.put("code", game.getGameCode());
                    send(answer);
                }
                case STARTGAME -> game.start();
                case UPDATEGAME -> game.updateGame((Game) command.data.get("game"));
                case STOP -> {
                    server.removeClientService(this);
                    return;
                }
                case NULLCOMMAND -> {
                    return;
                }
            }

        }
    }

    /**
     * Used to send commands to the client.
     *
     * @param command to send
     */
    protected void send(Protocol command) {
        if (output != null) {
            System.out.printf("[%d] Sending %s%n", id, command);
            try {
                output.writeObject(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Used to receive commands from the client.
     *
     * @return read command or new Protocol(Header.NULLCOMMAND) if an error occurred
     */
    private Protocol receive() {
        try {
            return (Protocol) input.readObject();
        } catch (IOException | ClassNotFoundException ioe) {
            System.err.println("Error reading client (" + id + "), " + ioe);
            return new Protocol(Header.NULLCOMMAND);
        }
    }

    /**
     * Setter for this.game.
     *
     * @param gameHandler to be assigned
     */
    public void setGame(GameHandler gameHandler) {
        this.game = gameHandler;
    }
}
