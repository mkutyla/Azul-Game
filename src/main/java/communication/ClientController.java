package communication;

import GUIs.*;
import GUIs.Controllers.AuthenticationController;
import GUIs.Controllers.LobbyCreatorController;
import GUIs.Controllers.ProfileController;
import game.Game;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * This class is a controller for the modeled Client - user of Azul.
 */
public class ClientController implements Runnable {

    /**
     * Socket connecting Client with the Server.
     */
    private Socket socket;

    /**
     * Socket's input stream.
     */
    private ObjectInputStream input;
    /**
     * Socket's output stream.
     */
    private ObjectOutputStream output;

    /**
     * Client's model containing their data.
     */
    private UserData model;

    /**
     * Thread running this.
     */
    private Thread connectionThread;

    /**
     * Lobby creator's view controller.
     */
    private LobbyCreatorController lobbyCreatorController;

    /**
     * Lobby creator's view controller.
     */
    private AuthenticationController authenticationController;

    /**
     * Profile's view controller.
     */
    private ProfileController profileController;

    /**
     * Stores id of resumed game in this Client's unfinished games.
     */
    private int resumedGameId;

    /**
     * Indicates whether running game was resumed (loaded) or newly created.
     */
    private boolean isResumed;

    /**
     * Stage on which all windows are displayed on.
     */
    private final AzulStage currentStage;

    /**
     * This method attempts to connect Client to the Server.
     *
     * @param host Server's ip/host
     * @param port Server's port
     * @throws Exception when an error occurs
     */
    public void connect(String host, String port) throws Exception {
        try {
            socket = new Socket(host, Integer.parseInt(port));
        } catch (UnknownHostException e) {
            throw new Exception("Unknown host.");
        } catch (IOException e) {
            throw new Exception("Server unreachable.");
        } catch (NumberFormatException e) {
            throw new Exception("Port value must be a number.");
        }

        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            socket.close();
            throw new Exception("Can not get input/output connection stream.");
        }

        authenticationView();
        (connectionThread = new Thread(this)).start();
    }

    /**
     * This method displays the Connection window to the Client.
     */
    private void connectionView() {
        try {
            new Connection().start(currentStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method displays the Authentication window to the Client.
     */
    private void authenticationView() {
        try {
            new Authentication().start(currentStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method displays the Profile window to the Client.
     */
    private void profileView() {
        try {
            Platform.runLater(
                    () -> {
                        try {
                            new Profile().start(currentStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for this class.
     */
    public ClientController() {
        this.currentStage = new AzulStage(this);
        connectionView();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Protocol command = receive();
                if (!handleCommand(command) || socket.isClosed()) {
                    close();
                    break;
                }
            } catch (IOException ignore) {
                return;
            }
        }
    }

    /**
     * This method handles received command.
     *
     * @param command to handle
     * @return false, if the Client disconnected from the Server, true otherwise
     */
    private boolean handleCommand(Protocol command) {
        if (command == null) return false;
        Header header = command.header;
        switch (header) {
            case REGISTERED -> Platform.runLater(
                    () -> {
                        try {
                            authenticationController.alertSetText("Successfully registered!", false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
            case LOGGEDIN -> {
                this.model = (UserData) command.data.get("userdata");
                profileView();
            }
            case CREATEDGAME -> creatorView(getUsername(), (String) command.data.get("code"));
            case STARTEDGAME, GAMEUPDATED -> gameView((Game) command.data.get("game"), (String) command.data.get("creator"), (int) command.data.get("id"));
            case JOINEDGAME -> lobbyView((String) command.data.get("creator"));
            case LEAVEGAME -> {
                String whoLeft = (String) command.data.get("username");
                if (whoLeft.equals(getUsername())) whoLeft = "You";

                if (command.data.containsKey("game")) { // this client is a creator's view
                    saveGameView((Game) command.data.get("game"), whoLeft);
                } else { // player's view pop-up
                    disconnectionView(whoLeft);
                }
            }
            case LEAVELOBBY -> {
                String user = (String) command.data.get("username");

                if (command.data.containsKey("creator")) { // creator killed the lobby
                    if (getUsername().equals(user)) { // this is the creator
                        profileView();
                    } else {    // creator left, default client received this
                        disconnectionView(user);
                    }
                } else if (user.equals(getUsername())) { // this client left
                    profileView();
                } else {    // this client is the creator and someone else left
                    Platform.runLater(
                            () -> {
                                try {
                                    lobbyCreatorController.removePlayer(user);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                }
            }
            case PLAYERJOINED -> {
                String username = (String) command.data.get("username");
                try {
                    Platform.runLater(
                            () -> {
                                try {
                                    lobbyCreatorController.addPlayer(username);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            case FAIL -> {
                Header failHeader = (Header) command.data.get("source");
                switch (failHeader) {
                    case LOGIN, REGISTER -> Platform.runLater(
                            () -> {
                                try {
                                    authenticationController.alertSetText(
                                            (String) command.data.get("dialog"),
                                            true
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                    case JOINGAME, CREATEGAME -> Platform.runLater(
                            () -> {
                                try {
                                    profileController.errorSetText(
                                            (String) command.data.get("dialog"),
                                            true
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                    case STARTGAME -> Platform.runLater(
                            () -> {
                                try {
                                    lobbyCreatorController.errorSetText(
                                            (String) command.data.get("dialog"),
                                            true
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                    default -> {
                    }
                }
            }
            case LOGOUT -> {
                logOut();
                return false; // stop the communication
            }
        }
        return true;
    }

    /**
     * Prompts new Disconnection window.
     *
     * @param whoLeft username of the player that left the game
     */
    private void disconnectionView(String whoLeft) {
        try {
            Platform.runLater(
                    () -> {
                        try {
                            Disconnection popUp = new Disconnection();
                            popUp.addParams(this, whoLeft);
                            popUp.start(currentStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called at the closure of Save window to save the game, if Client chosen to do so.
     *
     * @param game      to save
     * @param wantsSave indicating whether Client want to save passed game
     */
    public void closeSave(Game game, boolean wantsSave) {
        game.setWhenSaved(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(LocalDateTime.now()));

        if (isResumed) {
            if (wantsSave) {
                model.addUnfinishedGame(resumedGameId, game);
            } else {
                model.removeUnfinishedGame(resumedGameId);
            }
        } else if (wantsSave) {
            model.addUnfinishedGame(game);
        }
        profileView();
    }

    /**
     * Prompts new SaveView window.
     *
     * @param game    to be possibly saved
     * @param whoLeft username of the player that left the Game
     */
    private void saveGameView(Game game, String whoLeft) {
        try {
            Platform.runLater(
                    () -> {
                        try {
                            Save saveView = new Save();
                            saveView.addParams(this, game, whoLeft);
                            saveView.start(currentStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prompts new GameView window.
     *
     * @param game    to display
     * @param creator of passed game
     * @param id      of this Client in passed game
     */
    private void gameView(Game game, String creator, int id) {
        try {
            Platform.runLater(
                    () -> {
                        try {
                            GameView gameView = new GameView();
                            gameView.addParams(game, creator, id);
                            gameView.start(currentStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prompts new LobbyCreator window.
     *
     * @param username username of this LobbyCreator creator
     * @param code     used to connect to the created lobby
     */
    private void creatorView(String username, String code) {
        try {
            Platform.runLater(
                    () -> {
                        try {
                            LobbyCreator lobby = new LobbyCreator();
                            lobby.addParams(username, code);
                            lobby.start(currentStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prompts new Lobby window.
     *
     * @param creator username of the creator of lobby that this Client connected to
     */
    private void lobbyView(String creator) {
        try {
            Platform.runLater(
                    () -> {
                        try {
                            Lobby lobby = new Lobby();
                            lobby.addParams(creator);
                            lobby.start(currentStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sends a command to the Server.
     *
     * @param command to send
     */
    public void send(Protocol command) {
        if (output != null) {
            try {
                output.writeObject(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method receives the data sent for the Client.
     *
     * @return received data or null, if error occurred
     * @throws IOException when I/O error occurs
     */
    public Protocol receive() throws IOException {
        try {
            return (Protocol) input.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method closes socket and it's streams.
     *
     * @throws IOException when I/O error occurs
     */
    private void close() throws IOException {
        output.close();
        input.close();
        socket.close();
    }

    /**
     * This method sends necessary data to the Server so
     * that Client can log out correctly
     */
    public void logOut() {
        Protocol updatedData = new Protocol(Header.LOGGEDOUT);
        if (model != null) {
            updatedData.put("userdata", model);
        }
        send(updatedData);
        if (currentStage.isShowing()) {
            Platform.runLater(
                    currentStage::close
            );
        }
        connectionThread.interrupt();
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method disconnects user from the Server.
     */
    public void disconnect() {
        send(new Protocol(Header.STOP));
        connectionThread.interrupt();
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns this user's username.
     *
     * @return user's username
     */
    public String getUsername() {
        return model.getUsername();
    }

    /**
     * Getter for this Client's unfinished games.
     *
     * @return this Client's unfinished games
     */
    public ArrayList<Game> getUnfinishedGames() {
        return model.getUnfinishedGames();
    }


    /**
     * Setter for this.lobbyCreatorController.
     *
     * @param lobbyCreatorController to assign
     */
    public void setLobbyCreatorController(LobbyCreatorController lobbyCreatorController) {
        this.lobbyCreatorController = lobbyCreatorController;
    }

    /**
     * Setter for this.authenticationController.
     *
     * @param controller to assign
     */
    public void setAuthenticationController(AuthenticationController controller) {
        this.authenticationController = controller;
    }

    /**
     * Setter for this.profileController.
     *
     * @param controller to assign
     */
    public void setProfileController(ProfileController controller) {
        this.profileController = controller;
    }

    /**
     * Prompts profileView().
     */
    public void getProfileView() {
        profileView();
    }

    /**
     * Sets this.resumedGameId.
     *
     * @param game its id in array of this Client's unfinished games gets assigned to this.resumedGameId
     */
    public void setResumedGameId(Game game) {
        this.resumedGameId = getUnfinishedGames().indexOf(game);
    }

    /**
     * Setter for isResumed.
     *
     * @param isResumed to assign to this.isResumed
     */
    public void setResumed(boolean isResumed) {
        this.isResumed = isResumed;
    }

    /**
     * Removes passed game from the list of unfinished games.
     *
     * @param selectedGame game to remove
     */
    public void removeGame(Game selectedGame) {
        model.removeUnfinishedGame(getUnfinishedGames().indexOf(selectedGame));
    }
}
