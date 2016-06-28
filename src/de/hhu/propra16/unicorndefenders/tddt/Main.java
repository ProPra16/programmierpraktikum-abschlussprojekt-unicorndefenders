package de.hhu.propra16.unicorndefenders.tddt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("DAMN! It's TDD, Bitch :D");
        primaryStage.setScene(new Scene(root, 1250, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
