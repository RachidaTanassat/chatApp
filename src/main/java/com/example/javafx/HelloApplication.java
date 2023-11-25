package com.example.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //MongoDBConnector mongoDBConnector = new MongoDBConnector("mongodb://localhost:27017", "java_project");

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("App.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        stage.setTitle("N7gramm!");
        stage.setScene(scene);
        stage.show();
        //mongoDBConnector.close();
    }

    public static void main(String[] args) {

        launch();
    }
}