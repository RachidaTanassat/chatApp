package com.example.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("signIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 310,465);
        stage.setTitle("N7gramme!");
        // stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {

        launch();


    }
}