package com.example.javafx;
import com.example.javafx.dao.MessageDaoImpl;
import com.example.javafx.dao.UserDaoImpl;
import com.example.javafx.dao.entities.Message;
import com.example.javafx.dao.entities.User;
import com.example.javafx.kafka.KafkaAudio;
import com.example.javafx.kafka.KafkaConfig;
import com.example.javafx.kafka.KafkaFile;
import com.example.javafx.service.IMessageService;
import com.example.javafx.service.IServiceMessageImpl;
import com.example.javafx.service.IUserService;
import com.example.javafx.service.IserviceUserImpl;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;


public class Controller implements Initializable {
    IMessageService service = new IServiceMessageImpl(new MessageDaoImpl());
    IUserService userService = new IserviceUserImpl(new UserDaoImpl());
    PrintWriter pw;

    @FXML
    private Label usernamecurrent;
    @FXML
    private Circle imagecurrentcontact;
    @FXML
    private HBox barreuser;
    private String destname;

    @FXML
    private Circle imageUser;
    @FXML
    private FontAwesomeIcon idFichier;
    @FXML
    private HBox hboxButtom;
    @FXML
    private TextField search;
    ObservableList<String> listModal = FXCollections.observableArrayList();
    @FXML
    private ListView<String> listView;

    @FXML
    private FontAwesomeIcon iconEnvoyer;

    @FXML
    private TextField textfield;
    @FXML
    private VBox leftVBox;
    private User user;
    private String dest;

    private String keyRecption ;
    private String keyEnvoie;
    KafkaConfig kafkaConfig;
    KafkaFile kafkaFile;
    KafkaAudio kafkaAudio;


    private final String topic = "chat";

    @Override

        public void initialize(URL url, ResourceBundle resourceBundle) {


        listView.setCellFactory(param -> new MessageListCell());

        // Initialize KafkaConsumer when the application starts
            kafkaConfig = new KafkaConfig();
            kafkaFile = new KafkaFile();
            kafkaAudio = new KafkaAudio();


            new Thread(() -> {
                try {
                    System.out.println("Avant la connexion à Kafka");

                    // Use the provided KafkaConsumer instance
                    kafkaConfig.receiveMessages(topic);

                    System.out.println("Connexion à Kafka réussie.");

                    kafkaConfig.setMessageCallback(message -> {


                        // Vérifiez si la clé du message correspond à la clé souhaitée
                        if (keyRecption != null && keyRecption.equals(parseKeyFromMessage(message))) {
                            System.out.println("my key: " + keyRecption);
                            String value = message.split("Value = ")[1].trim();
                            listModal.add(value);
                            listView.getItems().setAll(listModal);
                        }
                        if (keyEnvoie != null && keyEnvoie.equals(parseKeyFromMessage(message))) {
                            System.out.println("my key: " + keyEnvoie);
                            String value = message.split("Value = ")[1].trim();
                            listModal.add("Moi: " + value);
                            listView.getItems().setAll(listModal);
                        }
                    });

                }catch (Exception e) {
                    System.out.println("Erreur lors de la connexion à Kafka : " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();




    }

    private String parseKeyFromMessage(String message) {
        String[] keyParts = message.split("Key = ");
        if (keyParts.length >= 2) {
            String key = keyParts[1].split(",")[0].trim();
            return key;
        }
        else {
                // Log an error or return a default value, depending on your requirements
                System.err.println("Invalid message format: " + message);
                return "";
            }
        }

















    @FXML
    void DroppedList(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Profile");

        MenuItem menuItem2 = new MenuItem("Add contact");

        MenuItem menuItem3 = new MenuItem("Others");

        contextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3);
        menuItem1.setOnAction(e -> {
            openProfileModal();

        });

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

                if (addContact.idContact!= null){
                    User contact = userService.getUserById(addContact.idContact);
                    if (contact != null) {
                        HBox hbox = createHBox(contact);
                        leftVBox.getChildren().add(hbox);  // Add HBox to VBox

                    } else {
                        System.out.println("User with ID " + addContact.idContact + " not found.");
                    }
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });

        contextMenu.show(imageUser, event.getScreenX(), event.getScreenY());
    }
    private void openProfileModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profil.fxml"));
            AnchorPane modalRoot = loader.load();
            ProfileController profileController = loader.getController();
            profileController.initData(this.user);
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Profile");
            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void ChangeIcon(KeyEvent keyEvent) {
        iconEnvoyer.setGlyphName("SEND");
        if (textfield.getText().isEmpty()) {
            iconEnvoyer.setGlyphName("MICROPHONE");
        }



    }

    public void EnvoyerMsg(MouseEvent event) {
        if ("SEND".equals(iconEnvoyer.getGlyphName())) {
            String message = textfield.getText();
            String key = user.getEmail() + dest;

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    System.out.println("Avant l'envoi du message");
                    kafkaConfig.sendMessage(topic, key, message);
                    System.out.println("Après l'envoi du message");
                    return null;
                }
            };

            task.setOnSucceeded(workerStateEvent -> {
                Platform.runLater(() -> {
                    textfield.clear();
                });
            });

            task.setOnFailed(workerStateEvent -> {
                Throwable exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            });

            new Thread(task).start();
        } else {
            AudioRecorder audioRecorder = new AudioRecorder();
            audioRecorder.startRecording(topic, user.getEmail(), dest);

            iconEnvoyer.setOnMouseClicked(e -> {
                audioRecorder.stopRecording();
                // Perform any UI updates here if needed
            });
        }
    }






    @FXML
    void ChoixFile(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        // Document item with an icon
        MenuItem documentItem = new MenuItem("Document");


        documentItem.setOnAction(this::EnvoyerFichier);

        // Image item with an icon
        MenuItem imageItem = new MenuItem("Image");

        imageItem.setOnAction(this::EnvoyerImage);

        contextMenu.getItems().addAll(documentItem, imageItem);

        // Show the context menu just above the icon
        contextMenu.show(idFichier, event.getScreenX(), event.getScreenY() - contextMenu.getHeight());
    }


    public void EnvoyerImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.jpeg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Read the file as a byte array
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        try {
                            String fileMessage = "Image:" + Base64.getEncoder().encodeToString(fileBytes);
                            // Send the file message via Kafka
                            boolean success = kafkaFile.sendImage(topic, user.getEmail(), dest,  fileMessage.getBytes());
                            if (success) {
                                System.out.println("Image sent successfully");
                            } else {
                                System.out.println("Failed to send the Image");
                            }

                        } catch (Exception e) {
                            // Handle exceptions, show an alert, etc.
                            e.printStackTrace();
                        }
                        return null;
                    }
                };

                task.setOnFailed(workerStateEvent -> {
                    Throwable exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                });

                new Thread(task).start();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'image.", ButtonType.OK);
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }



    public void EnvoyerFichier(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers", "*.pdf", "*.docx", "*.pptx", "*.csv"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Read the file as a byte array
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        try {
                            // Extract the file extension
                            String fileName = selectedFile.getName();

                            String fileMessage = "File:" + Base64.getEncoder().encodeToString(fileBytes);
                            System.out.println(fileName);

                            // Send the file message via Kafka along with the file extension
                            boolean success = kafkaFile.sendFile(topic, user.getEmail(), dest, (fileMessage + ":" + fileName).getBytes());
                            if (success) {
                                // File sent successfully, update UI or perform other actions
                                System.out.println("File sent successfully");
                            } else {
                                // Handle the case where the file sending failed
                                System.out.println("Failed to send the file");
                            }

                        } catch (Exception e) {
                            // Handle exceptions, show an alert, etc.
                            e.printStackTrace();
                        }
                        return null;
                    }
                };

                task.setOnFailed(workerStateEvent -> {
                    Throwable exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                });

                new Thread(task).start();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement du fichier.", ButtonType.OK);
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }



    public void getMessages(String idReciever, String idSender){
        listModal.clear();
        listView.getItems().clear();
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
            dest = userHbox.getEmail();
            destname=userHbox.getNom();
            hboxButtom.setVisible(true);
            getMessages(user.getUser_id(), userHbox.getUser_id());
            listView.getItems().setAll(listModal);
            keyRecption = dest + user.getEmail();
            keyEnvoie = user.getEmail() + dest;
            usernamecurrent.setText(destname);
            fillCircleWithImage(imagecurrentcontact,userHbox.getImageData());
            barreuser.setVisible(true);

        });
        hbox.setSpacing(10.0); // Adjust the spacing according to your preference
        hbox.getStyleClass().add("label_style");


        return hbox;
    }
    private void fillCircleWithImage(Circle circle, byte[] imageData) {
        if (imageData != null) {
            // Convertir le tableau d'octets en objet Image
            Image image = new Image(new ByteArrayInputStream(imageData));
            circle.setFill(new ImagePattern(image));
        }
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