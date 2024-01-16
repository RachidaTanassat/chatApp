package com.example.javafx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class MessageListCell extends ListCell<String> {

    private HBox receivedMessage;
    private HBox sentMessage;

    private Label receivedLabel;
    private Label sentLabel;

    private ImageView receivedImageView;
    private ImageView sentImageView;



    public MessageListCell() {
        receivedLabel = new Label();
        receivedMessage = new HBox(receivedLabel);

        receivedMessage.setAlignment(Pos.CENTER_LEFT);
        receivedLabel.setPadding(new Insets(4, 8, 4, 8));

        sentLabel = new Label();
        sentMessage = new HBox(sentLabel);
        sentMessage.setAlignment(Pos.CENTER_RIGHT);
        sentLabel.setPadding(new Insets(8));

        // Add styling for sent messages
        sentLabel.setStyle("-fx-background-color: #008AD8;-fx-background-radius: 11px;");
        sentLabel.setTextFill(Color.WHITE);
        sentLabel.setFont(new Font("Segoe UI", 20));

        // Add styling for received messages
        receivedLabel.setStyle("-fx-background-color: #D9D9D9;-fx-background-radius: 11px;");
        receivedLabel.setFont(new Font("Segoe UI", 20));

        setAlignment(Pos.CENTER);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            if (item.startsWith("Moi: ")) {
                if (item.startsWith("Moi: File:")) {
                    String fileContent = item.substring("Moi: File:".length());
                    createFileBox(fileContent, true);
                } else if (item.startsWith("Moi: Image:")) {
                    String base64Image = item.substring("Moi: Image:".length());
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

                    sentImageView = new ImageView(new Image(inputStream));
                    sentImageView.setFitWidth(100);
                    sentImageView.setFitHeight(100);

                    sentMessage.getChildren().setAll(sentImageView);
                    setGraphic(sentMessage);
                } else if (item.startsWith("Moi: Audio:")) {
                    // Handle Audio item similar to the FXML structure
                   // setGraphic(createAudioBox(item.substring("Moi: Audio:".length()), true));

                } else {
                    sentLabel.setText(item.substring(5)); // Skip "Moi: " to display only the message
                    sentMessage.setMinWidth(computeTextWidth(sentLabel.getText()));
                    setGraphic(sentMessage);
                }
            } else {
                if (item.startsWith("File:")) {
                    String fileContent = item.substring("File:".length());
                    createFileBox(fileContent, false);

                } else if (item.startsWith("Image:")) {
                    String base64Image = item.substring("Image:".length());
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                    Image image = new Image(inputStream);
                    receivedImageView = new ImageView(image);
                    receivedImageView.setFitWidth(100);
                    receivedImageView.setFitHeight(100);
                    receivedMessage.getChildren().setAll(receivedImageView);

                    setGraphic(receivedMessage);
                } else if (item.startsWith("Audio:")) {
                    // Handle Audio item similar to the FXML structure
                   // setGraphic(createAudioBox(item.substring("Audio:".length()), false));
                } else {
                    receivedLabel.setText(item);
                    receivedMessage.setMinWidth(computeTextWidth(receivedLabel.getText()));
                    setGraphic(receivedMessage);
                }
            }
        }
    }

   /* private HBox createAudioBox(String audioText, boolean sent) {
        HBox audioBox = new HBox();

        // Add styling for the audio box
        if (sent) {
            audioBox.setStyle("-fx-background-color: #008AD8;-fx-background-radius: 11px;");
        } else {
            audioBox.setStyle("-fx-background-color: #D9D9D9;-fx-background-radius: 11px;");
        }

        // Add button for playing audio
        Button playButton = new Button();
        playButton.setStyle("-fx-background-color: transparent;");
        playButton.setGraphic(new FontAwesomeIcon());

        // Decode Base64-encoded audio data
        String base64Audio = audioText.trim();
        byte[] audioBytes = Base64.getDecoder().decode(base64Audio);
        try {
            // Create a temporary file for the audio
            File tempFile = File.createTempFile("tempAudio", ".wav");
            tempFile.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(audioBytes);
            }

            Media media = new Media(tempFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            // Set the action for the play button
            playButton.setOnAction(event -> {
                mediaPlayer.seek(Duration.ZERO); // Rewind to the beginning
                mediaPlayer.play(); // Start playback
            });
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception (display error message, log, etc.)
        }

        // Add label for audio text
        Label audioLabel = new Label("Audio Message");
        audioLabel.setPrefHeight(13.0);
        audioLabel.setPrefWidth(87.0);
        audioLabel.setTextFill(Color.web("#8d8c8c"));
        audioLabel.setFont(new Font("Segoe UI", 10.0));

        // Add elements to the audio box
        audioBox.getChildren().addAll(playButton, audioLabel);

        // Set padding for the audio box
        audioBox.setPadding(new Insets(2, 2, 2, 11));

        return audioBox;
    }*/

    private double computeTextWidth(String text) {
        return new Text(text).getLayoutBounds().getWidth();
    }






    private void createFileBox(String fileContent, boolean sent) {
        String[] contentAndExtension = splitFileContent(fileContent);
        String content = contentAndExtension[0];
        String fileName = contentAndExtension[1];


        int lastDotIndex = fileName.lastIndexOf('.');
        String fileExtension = lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";

        // Créez une ImageView avec l'icône correspondante à l'extension du fichier
        ImageView imageView = new ImageView(getImageForFileType(fileExtension));
        imageView.setFitHeight(35.0);
        imageView.setFitWidth(40.0);
        imageView.setPreserveRatio(true);

        // Ajoutez le nom du fichier dans une étiquette
        Label fileNameLabel = new Label(fileName);
        fileNameLabel.setPrefHeight(55.0);
        fileNameLabel.setPrefWidth(151.0);

        // Créez une HBox pour les éléments de l'affichage du fichier
        HBox fileDisplayBox = new HBox(imageView, fileNameLabel);
        fileDisplayBox.setPrefHeight(47.0);
        fileDisplayBox.setPrefWidth(207.0);
        fileDisplayBox.setSpacing(10);
        fileDisplayBox.setAlignment(Pos.CENTER);
        fileDisplayBox.setStyle("-fx-border-radius: 4");


        // Ajoutez des boutons pour ouvrir et télécharger
        Button openButton = new Button("Ouvrir");
        Button downloadButton = new Button("Télécharger");

        // Créez une HBox pour les boutons
        HBox buttonsHBox = new HBox(openButton, downloadButton);
        buttonsHBox.setPrefHeight(31.0);
        buttonsHBox.setPrefWidth(207.0);
        buttonsHBox.setSpacing(6);





        // Ajoutez une classe CSS personnalisée pour le style du bouton Télécharger
        downloadButton.getStyleClass().add("download-button");

        // Ajoutez des styles CSS pour le fichier et les boutons
        fileDisplayBox.getStyleClass().add(sent ? "sent-file-box" : "received-file-box");
        buttonsHBox.setAlignment(Pos.CENTER);


        VBox fileBox = new VBox(fileDisplayBox, buttonsHBox);


        if (sent) {

            openButton.setStyle("-fx-background-color: #55b9f2;");
            downloadButton.setStyle("-fx-background-color: #55b9f2;");
            openButton.setTextFill(Color.WHITE);
            downloadButton.setTextFill(Color.WHITE);
            fileNameLabel.setTextFill(Color.WHITE);
            fileDisplayBox.setStyle("-fx-border-color: transparent transparent #008AD8 transparent;");



            fileBox.setStyle("-fx-background-color: #55b9f2;-fx-background-radius: 11px;");
           sentMessage.getChildren().setAll(fileBox);
            setGraphic(sentMessage);
        } else {

            openButton.setStyle("-fx-background-color: #D9D9D9;");
            downloadButton.setStyle("-fx-background-color: #D9D9D9;");

            fileBox.setStyle("-fx-background-color: #D9D9D9;-fx-background-radius: 11px;");
            sentMessage.getChildren().setAll(fileBox);
            fileDisplayBox.setStyle("-fx-border-color: transparent transparent #b5b1b1 transparent;");

            receivedMessage.getChildren().setAll(fileBox);
            setGraphic(receivedMessage);
        }



        openButton.setOnAction(event -> openFileInBrowser(content, fileExtension));

        // Add actions for the download button
        downloadButton.setOnAction(event -> {
            // Show a file chooser dialog and let the user choose the download location
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");

            // Set the initial file name with the correct file extension
            if (!fileExtension.isEmpty()) {
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("."+fileExtension, "*."+fileExtension);
                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.setSelectedExtensionFilter(extFilter);
            }

            File selectedFile = fileChooser.showSaveDialog(null);

            if (selectedFile != null) {
                try {
                    // Decode Base64 content and save it to the chosen location
                    byte[] fileBytes = Base64.getDecoder().decode(content);
                    try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                        fos.write(fileBytes);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception (display an error message, log, etc.)
                }
            }
        });

    }



    private Image getImageForFileType(String fileExtension) {
        String imagePath = null;
        switch (fileExtension.toLowerCase()) {
            case "pdf":
                imagePath = "/com/example/img/pdf_icon.png";
                break;
            case "doc":
            case "docx":
                imagePath = "/com/example/img/word_icon.png";
                break;
            case "ppt":
                imagePath = "/com/example/img/ppt_icon.png";
                break;

            default:imagePath = "/com/example/img/pdf_icon.png";
        }

        assert imagePath != null;
        return new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm());

    }


    private void openFileInBrowser(String fileContent, String fileExtension) {
        // Assuming fileContent is the Base64-encoded file content
        byte[] fileBytes = Base64.getDecoder().decode(fileContent);

        try {

            File tempFile = File.createTempFile("tempFile", "." + fileExtension);
            tempFile.deleteOnExit();

            // Write the decoded bytes to the temporary file
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(fileBytes);
            }

            // Open the file using the default application
            Desktop.getDesktop().open(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    private String[] splitFileContent(String fileContent) {
        // Split fileContent into content and extension
        int colonIndex = fileContent.indexOf(':');
        if (colonIndex != -1 && colonIndex + 1 < fileContent.length()) {
            String content = fileContent.substring(0, colonIndex);
            String fileNameWithExtension = fileContent.substring(colonIndex + 1);

            // Remove any invalid characters in the file name
            fileNameWithExtension = fileNameWithExtension.replaceAll("[^a-zA-Z0-9.]+", "");

            return new String[]{content, fileNameWithExtension};
        }
        return new String[]{"", ""}; // Return empty strings if no valid content and extension found
    }




}
