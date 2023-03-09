package communication;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Properties;

/**
 * This class is the main Server used to run Azul gameplay.
 */
public class Server implements Runnable {

    /**
     * Used to connect with the clients.
     */
    private ServerSocket serverSocket;

    /**
     * All connected clients
     */
    private final ArrayList<Service> clients = new ArrayList<>();

    /**
     * All stored users.
     */
    private final HashMap<String, UserData> users;

    /**
     * Games recognizable by their codes (key).
     */
    private final HashMap<String, GameHandler> games = new HashMap<>();

    /**
     * Properties of this server.
     */
    private final Properties props;

    /**
     * Server thread.
     */
    private Thread serverThread;

    /**
     * Constructor for this.
     *
     * @param p server's properties
     */
    public Server(Properties p) {
        props = p;

        int port = Integer.parseInt(props.getProperty("port"));
        users = readUsers();

        try {
            serverSocket = new ServerSocket(port);
            System.out.printf("Server started at host %s on port %s\n", InetAddress.getLocalHost(), port);
        } catch (IOException e) {
            System.err.println("Error starting Server.");
            return;
        }

        (serverThread = new Thread(this)).start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                System.out.println("Shutting down ...");
                send(new Protocol(Header.LOGOUT));
                Thread.sleep(500); // wait for all clients to send their data
                writeUsers(); // save the data
                storeProperties(); // write it to properties

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
                System.exit(-1);
            }
        }));
    }

    /**
     * This method sends passed Protocol to all connected clients.
     *
     * @param msg to send
     */
    synchronized void send(Protocol msg) {
        for (Service s : clients) {
            s.send(msg);
        }
    }

    /**
     * This method reads users database from a property file
     *
     * @return read database
     */
    private HashMap<String, UserData> readUsers() {
        HashMap<String, UserData> readUsers = new HashMap<>();

        if (!props.containsKey("users")) return readUsers;

        byte[] data = Base64.getDecoder().decode(props.getProperty("users"));
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data))) {
            readUsers = (HashMap<String, UserData>) ois.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException ioException) {
            ioException.printStackTrace();
        }
        return readUsers;
    }

    /**
     * This method writes users database to a property file.
     */
    private void writeUsers() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream os = new ObjectOutputStream(bos)) {
            os.writeObject(users);
            props.setProperty("users", new String(Base64.getEncoder().encode(bos.toByteArray())));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * This method stores properties to a file matching passed file name
     */
    private void storeProperties() {
        try {
            props.store(new FileOutputStream("Server.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method adds passed user credentials to this Server's users database.
     *
     * @param username     registered user's username
     * @param passwordHash registered user's password hash
     * @return true, if user was successfully added, false if user with passed username is already registered
     */
    public boolean addUser(String username, String passwordHash) {
        if (users.containsKey(username)) return false;
        users.put(username, new UserData(username, passwordHash));
        return true;
    }

    /**
     * This method checks whether passed credentials match any user stored in this Server's database.
     *
     * @param username     a username to verify
     * @param passwordHash a passwordHash to match passed user's passwordHash
     * @return true if passed credentials match this Server's user, false otherwise
     */
    public boolean matchesCredentials(String username, String passwordHash) {
        if (!users.containsKey(username)) return false;
        return users.get(username).getPasswordHash().equals(passwordHash);
    }

    @Override
    public void run() {
        while (serverThread == Thread.currentThread()) {
            try {
                Socket clientSocket = serverSocket.accept();
                createAndStartClientService(clientSocket);
            } catch (IOException e) {
                System.err.println("Error accepting connection. Client will not be served...");
            }
        }
    }

    /**
     * This method creates a client handler.
     *
     * @param clientSocket to handle
     * @throws IOException when I/O error occurs
     */
    synchronized void createAndStartClientService(Socket clientSocket) throws IOException {
        Service clientService = new Service(clientSocket, this, nextID());
        clientService.init();
        new Thread(clientService).start();
        clients.add(clientService);
        System.out.println("Client added. Number of clients: " + clients.size());
    }


    /**
     * This method closes and removes passed Service
     *
     * @param clientService to remove
     */
    synchronized void removeClientService(Service clientService) {
        clients.remove(clientService);
        clientService.close();
        System.out.println("Client removed. Number of clients: " + clients.size());
    }

    /**
     * ID of latest client.
     */
    private int $lastID = -1;

    /**
     * Gets next client's ID.
     *
     * @return next client's ID.
     */
    synchronized int nextID() {
        return ++$lastID;
    }

    /**
     * Checks whether user with passed username is connected to this Server.
     *
     * @param username to look for in connected users
     * @return true, if user is connected, false otherwise
     */
    public boolean isConnected(String username) {
        for (Service s : clients) {
            if (s.getUsername() != null && s.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns user's data.
     *
     * @param username user's username
     * @return user's data or null, if username doesn't match any user
     */
    public UserData getUserData(String username) {
        if (!users.containsKey(username)) return null;
        return users.get(username);
    }

    /**
     * This method updates passed user's data.
     *
     * @param username user's username
     * @param data     user's updated data
     */
    synchronized public void updateUser(String username, UserData data) {
        users.put(username, data);
    }


    /**
     * Checks whether passed game code matches any running GameHandler.
     *
     * @param code game code
     * @return true, if code matches any game, false otherwise
     */
    public boolean validateCode(String code) {
        return this.games.containsKey(code);
    }

    /**
     * Returns GameHandler matching passed code.
     *
     * @param code game code
     * @return game matching passed code
     */
    public GameHandler getGame(String code) {
        return this.games.get(code);
    }

    /**
     * Adds passed GameHandler to this server's games and generates its session code.
     *
     * @param game to add
     * @return this game's code, used to connect to it by the users
     */
    public String addGame(GameHandler game) {
        final String AB = "0123456789ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        final SecureRandom rnd = new SecureRandom();
        final int LEN = 5;
        StringBuilder sb = new StringBuilder(LEN);
        do {
            for (int i = 0; i < LEN; i++)
                sb.append(AB.charAt(rnd.nextInt(AB.length())));
        } while (games.containsKey(sb.toString()));

        this.games.put(sb.toString(), game);
        return sb.toString();
    }

    /**
     * Removes game matching passed game code from this.games.
     *
     * @param code game code of the game to remove
     */
    public void removeGame(String code) {
        this.games.remove(code);
    }


    /**
     * Main method to start this Server.
     *
     * @param args are not used
     */
    public static void main(String[] args) {
        Properties props = new Properties();
        String pName = "Server.properties";

        try {
            props.load(new FileInputStream(pName));
        } catch (Exception e) {
            props.put("port", "40000");
        }

        try {
            props.store(new FileOutputStream(pName), null);
        } catch (Exception ignore) {
        }

        new Server(props);
    }
}
