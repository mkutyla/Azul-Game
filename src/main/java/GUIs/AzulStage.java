package GUIs;

import communication.ClientController;
import javafx.stage.Stage;

/**
 * Simple extension of Stage class to pass around this AzulStage's Client.
 */
public class AzulStage extends Stage {
    /**
     * Client whose stage this is.
     */
    public ClientController client;

    /**
     * Constructor for this class.
     *
     * @param client who calls this method
     */
    public AzulStage(ClientController client) {
        super();
        this.client = client;
    }


}
