package com.example.javafx;
import com.example.javafx.dao.MessageDaoImpl;
import com.example.javafx.dao.UserDaoImpl;
import com.example.javafx.dao.entities.Message;
import com.example.javafx.dao.entities.User;
import com.example.javafx.service.IMessageService;
import com.example.javafx.service.IServiceMessageImpl;
import com.example.javafx.service.IUserService;
import com.example.javafx.service.IserviceUserImpl;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    IMessageService service = new IServiceMessageImpl(new MessageDaoImpl());
    IUserService userService = new IserviceUserImpl(new UserDaoImpl());
    PrintWriter pw;
    @FXML
    private Circle circle;
    ObservableList<String> listModal = FXCollections.observableArrayList();
    @FXML
    private ListView<String> listView ;

    @FXML
    private FontAwesomeIcon iconEnvoyer;

    @FXML
    private TextField textfield;
    @FXML
    private VBox leftVBox;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        try {
            Socket socket = new Socket("localhost", 1234);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);


            pw = new PrintWriter(socket.getOutputStream(), true);
            new Thread( ()->{
                try {
                    while (true) {
                        String response = bufferedReader.readLine();
                        Platform.runLater(() -> {
                            listModal.add(response);
                            listView.getItems().setAll(listModal);
                        });

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getMessages();
    }


    @FXML
    void changeImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.jpeg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {

                Image newImage = new Image(selectedFile.toURI().toString());
                circle.setFill(new ImagePattern(newImage));
            } catch (Exception e) {
                // Gérer les erreurs lors du chargement de l'image
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'image.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }


   public void ChangeIcon(KeyEvent keyEvent) {
        iconEnvoyer.setGlyphName("SEND");


    }


    public void EnvoyerMsg(MouseEvent  event) {
        String message = textfield.getText();
        pw.println(message);
        textfield.clear();

        /*Message msg=new Message();
        msg.setContent(message);
        msg.setSender("rachida");
        msg.setReceiver("Aafaf");

        service.addMessage(msg);*/
    }

    public void getMessages(){
        List<Message> messages = service.getAllMessages();
        for(Message msg:messages){
            listModal.add(msg.getContent());
        }
    }

    public void initData(User user) {
        byte[] imageData = user.getImageData(); // Assurez-vous que getImageData() renvoie les données binaires de l'image

        if (imageData != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
            Image image = new Image(inputStream);
            circle.setFill(new ImagePattern(image));
        } else {
            Image img = new Image(Objects.requireNonNull(getClass().getResource("/com/example/img/user1.jpeg")).toExternalForm());
            circle.setFill(new ImagePattern(img));
        }

        List<User> users = new ArrayList<>();
        String contact_ids[] = user.getContacts();

        if (contact_ids != null) {
            for (String id : contact_ids) {
                User contact = userService.getUserById(id);
                if (contact != null) {
                    users.add(contact);
                } else {
                    System.out.println("User with ID " + id + " not found.");
                }
            }

            for (User contact : users) {
                HBox hbox = createHBox(contact);
                leftVBox.getChildren().add(hbox);  // Add HBox to VBox
            }
        }

        System.out.println("Exiting initData");
    }

    private HBox createHBox(User user) {
        Circle circle = new Circle(23.0, Color.WHITE);
        circle.setStroke(Color.BLACK);

        VBox innerVBox = new VBox(new Label(user.getNom()), new Label(user.getEmail()));

        HBox hbox = new HBox(circle, innerVBox);
        hbox.setSpacing(6.0);
        hbox.getStyleClass().add("label_style");

        return hbox;
    }
}