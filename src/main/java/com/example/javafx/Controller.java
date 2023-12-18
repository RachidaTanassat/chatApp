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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;


public class Controller implements Initializable {
    IMessageService service = new IServiceMessageImpl(new MessageDaoImpl());
    IUserService userService = new IserviceUserImpl(new UserDaoImpl());
    PrintWriter pw;
    @FXML
    private Circle imageUser;
    @FXML
    private  TextField search;
    ObservableList<String> listModal = FXCollections.observableArrayList();
    @FXML
    private ListView<String> listView ;

    @FXML
    private FontAwesomeIcon iconEnvoyer;

    @FXML
    private TextField textfield;
    @FXML
    private VBox leftVBox;
    private User user;




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
                        });

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


  /*  @FXML
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
    }*/

    @FXML
    void DroppedList(MouseEvent event) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Profile");

        menuItem1.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
                VBox modalRoot = loader.load();
                addContactController addContact= loader.getController();
                addContact.setUser(this.user);

                Stage modalStage = new Stage();
                modalStage.initModality(Modality.APPLICATION_MODAL);
                modalStage.setTitle("Profile");
                modalStage.setScene(new Scene(modalRoot));
                modalStage.showAndWait();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });

            MenuItem menuItem2 = new MenuItem("Add contact");

            MenuItem menuItem3 = new MenuItem("Others");

            contextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3);
               menuItem2.setOnAction(e -> {
                   try {
                       FXMLLoader loader = new FXMLLoader(getClass().getResource("addContact.fxml"));
                       VBox modalRoot = loader.load();
                       addContactController addContact= loader.getController();
                       addContact.setUser(this.user);

                       Stage modalStage = new Stage();
                       modalStage.initModality(Modality.APPLICATION_MODAL);
                       modalStage.setTitle("Ajouter un contact");
                       modalStage.setScene(new Scene(modalRoot));
                       modalStage.showAndWait();
                   } catch (IOException e1) {
                       e1.printStackTrace();
                   }

         });

            contextMenu.show(imageUser, event.getScreenX(), event.getScreenY());
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

    public void getMessages(String idReciever, String idSender){
        List<Message> messages = service.getMessageByUserId(idReciever, idSender);
        for(Message msg:messages){
            listModal.add(msg.getContent());
        }
    }

    public void initData(User user) {
        this.user = user;
        fillCircle(user, imageUser);

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

    private HBox createHBox(User userHbox) {
        Circle circle = new Circle(23.0, Color.WHITE);
        circle.setStroke(Color.BLACK);

        fillCircle(userHbox, circle);


        Label nameLabel = new Label(userHbox.getNom());
        Label emailLabel = new Label(userHbox.getEmail());

        VBox innerVBox = new VBox(nameLabel, emailLabel);
        innerVBox.setSpacing(5.0); // Adjust the spacing according to your preference

        HBox hbox = new HBox(circle, innerVBox);
        hbox.setOnMouseClicked( e -> {
            getMessages(user.getUser_id(), userHbox.getUser_id());
            listView.getItems().setAll(listModal);

        });
        hbox.setSpacing(10.0); // Adjust the spacing according to your preference
        hbox.getStyleClass().add("label_style");


        return hbox;
    }


    @FXML
    void Rechercher(KeyEvent event) {
        String query = search.getText().toLowerCase();

        List<User> filteredUsers = userService.searchUserByQuery(query);

    }


    public void fillCircle(User user, Circle circle) {

            byte[] imageData = user.getImageData();
            if (imageData != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
                Image image = new Image(inputStream);
                circle.setFill(new ImagePattern(image));
            } else {
                InputStream defaultImageStream = getClass().getResourceAsStream("/com/example/img/user1.jpeg");
                Image defaultImage = new Image(defaultImageStream);
                circle.setFill(new ImagePattern(defaultImage));
            }
    }



}