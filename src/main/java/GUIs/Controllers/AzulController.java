package GUIs.Controllers;

import communication.ClientController;

/**
 * This class is javafx controller for Azul Application.
 */
public class AzulController {

    /**
     * This method creates a new Client.
     */
    public void playOnAction() {
        new ClientController();
    }

}
