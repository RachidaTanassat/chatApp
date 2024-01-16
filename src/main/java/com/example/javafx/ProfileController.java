package com.example.javafx;

import com.example.javafx.dao.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
public class ProfileController {
    @FXML
    private Circle circle;

    @FXML
    private Circle close;

    @FXML
    private TextField email;

    @FXML
    private Button login;

    @FXML
    private Circle min;

    @FXML
    private TextField passwordhid;
    @FXML
    private PasswordField password;
    @FXML
    private TextField newpasswordhid;
    @FXML
    private PasswordField newpassword;
    private File selectedFile;
    public void initData(User user) {
        email.setText(user.getEmail());
        password.setText(user.getPassword());
        passwordhid.setText(user.getPassword());
        newpassword.setText(user.getPassword());
        newpasswordhid.setText(user.getPassword());
        fillCircleWithImage(circle, user.getImageData());
    }
    private void fillCircleWithImage(Circle circle, byte[] imageData) {
        if (imageData != null) {
            // Convertir le tableau d'octets en objet Image
            Image image = new Image(new ByteArrayInputStream(imageData));
            circle.setFill(new ImagePattern(image));
        }
    }
    public void changepasswordfield(MouseEvent mouseEvent) {
        if(password.isVisible()==true){
            password.setVisible(false);
            passwordhid.setVisible(true);
        }
        else{
            password.setVisible(true);
            passwordhid.setVisible(false);
        }
    }
    public void newchangepasswordfield(MouseEvent mouseEvent) {
        if(newpassword.isVisible()==true){
            newpassword.setVisible(false);
            newpasswordhid.setVisible(true);
        }
        else{
            newpassword.setVisible(true);
            newpasswordhid.setVisible(false);
        }
    }

    public void changeProfileImage(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", ".png", ".jpg", ".gif",".jpeg")
        );
        fileChooser.setTitle("Choisir une image de profil");

        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Convertir le fichier image en tableau d'octets (bytes)
            byte[] imageData = convertImageToBytes(selectedFile);
            fillCircleWithImage(circle, imageData);
        }
    }

    private byte[] convertImageToBytes(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(ActionEvent actionEvent) {
        String userEmail = email.getText();
        String newPassword = password.getText();

        // Convertir la nouvelle image en tableau d'octets
        byte[] newImageData = convertImageToBytes(selectedFile);

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"))) {
            MongoDatabase database = mongoClient.getDatabase("projet_java");
            MongoCollection<Document> collection = database.getCollection("users");

            // Construire la requête de mise à jour
            Document query = new Document("email", userEmail);
            Document update = new Document("$set", new Document("password", newPassword)
                    .append("imageData", newImageData));

            // Exécuter la mise à jour
            collection.updateOne(query, update);

            System.out.println("Mise à jour réussie !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}