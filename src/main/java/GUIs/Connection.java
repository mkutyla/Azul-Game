package GUIs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is used to create new Connection window.
 */
public class Connection extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("connection.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 560, 400);
        stage.setScene(scene);
        stage.setTitle("Azul: connection");
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> stage.close());
    }
}
