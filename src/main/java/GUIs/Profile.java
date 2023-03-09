package GUIs;

import GUIs.Controllers.ProfileController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is used to create new Profile window.
 */
public class Profile extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
        stage.setScene(new Scene(loader.load(), 600, 400));

        ProfileController controller = loader.getController();
        controller.setStage((AzulStage) stage);
        controller.setStatus();
        controller.setUnfinishedGames();

        ((AzulStage) stage).client.setProfileController(controller);

        stage.setTitle(String.format("Azul: %s's profile", ((AzulStage) stage).client.getUsername()));
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> {
                    ((AzulStage) stage).client.logOut();
                    stage.close();
                }
        );

    }
}
