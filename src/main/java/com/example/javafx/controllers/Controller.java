package com.example.javafx.controllers;
import com.example.javafx.dao.MessageDaoImpl;
import com.example.javafx.dao.entities.Message;
import com.example.javafx.service.IMessageService;
import com.example.javafx.service.IServiceMessageImpl;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    IMessageService service = new IServiceMessageImpl(new MessageDaoImpl());
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




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        Image img = new Image(Objects.requireNonNull(getClass().getResource("/com/example/img/user.jpeg")).toExternalForm());
        circle.setFill(new ImagePattern(img));
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


   public void ChangeIcon(KeyEvent keyEvent) {
        iconEnvoyer.setGlyphName("SEND");


    }


    public void EnvoyerMsg(ActionEvent event) {
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
}