package GUIs;

import GUIs.Controllers.AuthenticationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is used to create new Authentication window.
 */
public class Authentication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        AuthenticationController controller = loader.getController();
        ((AzulStage) stage).client.setAuthenticationController(controller);

        Scene scene = new Scene(root, 560, 400);
        stage.setScene(scene);
        stage.setTitle("Azul: authentication");
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> ((AzulStage) stage).client.disconnect());
    }
}
