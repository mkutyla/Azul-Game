package GUIs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is used to create new Azul window.
 */
public class Azul extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("opener.fxml"));
        stage.setScene(new Scene(loader.load(), 560, 400));
        stage.setTitle("Azul: game type chooser");
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> Thread.currentThread().interrupt());
    }

    /**
     * Main method to start Azul Application.
     *
     * @param args usage specified by Application class
     */
    public static void main(String[] args) {
        launch(args);
    }
}

