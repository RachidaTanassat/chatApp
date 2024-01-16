module com.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires fontawesomefx;
    requires mongo.java.driver;
    requires jbcrypt;
    requires opentok.server.sdk;
    requires kafka.clients;
    requires java.desktop;
    requires javafx.media;

    opens com.example.javafx to javafx.fxml;
    exports com.example.javafx;
   // exports com.example.javafx.controllers;
   // opens com.example.javafx.controllers to javafx.fxml;
}