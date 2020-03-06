package pokal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pokal.persistence.SaveFileHandler;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        SaveFileHandler.init();
        final Parent root = FXMLLoader.load(getClass().getResource("ZugListe.fxml"));
        final Scene scene = new Scene(root, 600, 400, Color.web("#5CDB95"));
        primaryStage.setTitle("Pokalschuss");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/trophy.png")));
        primaryStage.show();
    }
}
