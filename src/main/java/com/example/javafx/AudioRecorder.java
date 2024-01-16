package com.example.javafx;

import com.example.javafx.kafka.KafkaAudio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class AudioRecorder {
    private KafkaAudio kafkaAudio;
    private TargetDataLine targetDataLine;
    private boolean isRecording = false;

    public AudioRecorder() {
        kafkaAudio = new KafkaAudio();
    }

    public void startRecording(String kafkaTopic, String sender, String receiver) {
        try {
            // Create the "audio" directory in the working directory
            File audioFolder = new File("audio");
            if (!audioFolder.exists()) {
                audioFolder.mkdir();
            }

            // Specify a commonly supported audio format
            AudioFormat audioFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100, 16, 1, 2, 44100, false);

            // Get the target data line from the default audio device
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

            // Open the target data line and start capturing audio
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // Set isRecording to true
            isRecording = true;

            System.out.println("Recording...");

            // Start a new thread for capturing and saving audio
            Thread captureThread = new Thread(() -> {
                try {
                    // Create an audio input stream from the target data line
                    AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);

                    // Specify the output file for saving the recorded audio
                    File outputFile = new File("audio/output.wav");

                    // Write the recorded audio to the output file
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);

                    // Read the recorded audio file into bytes
                    byte[] fileBytes = Files.readAllBytes(outputFile.toPath());

                    // Convert the bytes to Base64 for Kafka transmission
                    String fileAudio = "Audio:" + Base64.getEncoder().encodeToString(fileBytes);

                    // Send the recorded audio to Kafka
                    boolean sentSuccessfully = kafkaAudio.sendAudio(kafkaTopic, sender, receiver, fileAudio.getBytes());

                    if (sentSuccessfully) {
                        System.out.println("Recorded audio sent to Kafka successfully.");
                    } else {
                        System.err.println("Failed to send recorded audio to Kafka.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            captureThread.start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
        }

        // Set isRecording to false
        isRecording = false;

        System.out.println("Recording stopped.");
    }

    public boolean isRecording() {
        return isRecording;
    }
}
