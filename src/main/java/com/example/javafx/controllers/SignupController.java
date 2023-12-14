package com.example.javafx.controllers;

import com.example.javafx.dao.UserDaoImpl;
import com.example.javafx.dao.entities.User;
import com.example.javafx.service.IUserService;
import com.example.javafx.service.IserviceUserImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.*;

public class SignupController implements Initializable {
    IUserService userService = new IserviceUserImpl(new UserDaoImpl());

    @FXML
    private TextField email;

    @FXML
    private TextField password;

    @FXML
    private TextField userName;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
@FXML
    void signUp(ActionEvent event) {
        String userEmail = email.getText();

        if (userService.emailExists(userEmail)) {

            showAlert("Error", "Email already exists. Please use a different email.", AlertType.ERROR);

        } else {
            User user = new User();
            user.setNom(userName.getText());
            user.setEmail(userEmail);
            user.setPassword(password.getText());

            userService.addUser(user);

        }
    }

    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
