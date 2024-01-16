package com.example.javafx.kafka;

import javafx.application.Platform;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DeleteRecordsOptions;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaConfig {

    private Properties producerProperties;
    private Properties consumerProperties;
    private Producer<String, String> producer;
    private Consumer<String, String> consumer;
    private java.util.function.Consumer<String> messageCallback;

    public KafkaConfig() {
        // Initialize and configure Kafka producer properties
        producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "broker-2-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-3-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-5-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-4-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-0-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-1-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093");
        producerProperties.put("security.protocol", "SASL_SSL");
        producerProperties.put("sasl.mechanism", "PLAIN");
        producerProperties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"token\" password=\"0WXauOLhNE_0YJyDC8YvOAiM-kEsnfdula_9Y1Tagvh8\";");
        producerProperties.put("ssl.protocol", "TLSv1.2");
        producerProperties.put("ssl.enabled.protocols", "TLSv1.2");
        producerProperties.put("ssl.endpoint.identification.algorithm", "HTTPS");
        producerProperties.put("key.serializer", StringSerializer.class.getName());
        producerProperties.put("value.serializer", StringSerializer.class.getName());



        // Initialize and configure Kafka consumer properties
        consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers", "broker-2-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-3-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-5-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-4-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-0-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093,broker-1-95jpsxj0s9lj40r2.kafka.svc08.us-south.eventstreams.cloud.ibm.com:9093");
        consumerProperties.put("security.protocol", "SASL_SSL");
        consumerProperties.put("sasl.mechanism", "PLAIN");
        consumerProperties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"token\" password=\"0WXauOLhNE_0YJyDC8YvOAiM-kEsnfdula_9Y1Tagvh8\";");
        consumerProperties.put("ssl.protocol", "TLSv1.2");
        consumerProperties.put("ssl.enabled.protocols", "TLSv1.2");
        consumerProperties.put("ssl.endpoint.identification.algorithm", "HTTPS");
        consumerProperties.put("group.id", "group-id-1");
        consumerProperties.put("key.deserializer", StringDeserializer.class.getName());
        consumerProperties.put("value.deserializer", StringDeserializer.class.getName());

        // Initialize KafkaProducer and KafkaConsumer
        producer = new KafkaProducer<>(producerProperties);
        consumer = new KafkaConsumer<>(consumerProperties);
    }

    public void sendMessage(String topic, String key, String value) {
        // Create a ProducerRecord and send the message
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        producer.send(record);
    }

    public void setMessageCallback(java.util.function.Consumer<String> callback) {
        this.messageCallback = callback;
    }



    public void receiveMessages(String topic) {


        // Assign partitions directly
        List<TopicPartition> partitions = Arrays.asList(new TopicPartition(topic, 0), new TopicPartition(topic, 1));
        consumer.assign(partitions);

        new Thread(() -> {
            try {
                consumer.seekToBeginning(partitions);

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    if (!records.isEmpty()) {
                        System.out.println("New records received");
                    }
                    records.forEach(record -> {
                        String senderReceiver = record.key();
                        String message = record.value();



                            String formattedMessage = String.format("Received message: Key = %s, Value = %s%n", record.key(), record.value());

                            Platform.runLater(() -> {
                                if (messageCallback != null) {
                                    // Pass the message to the callback
                                    messageCallback.accept(formattedMessage);
                                }
                            });

                    });
                }
            } catch (Exception e) {
                // Handle exceptions, if needed
                e.printStackTrace();
            }
        }).start();

        System.out.println("Message receiving loop started");
    }






    public void deleteAllMessages(String topic, int partition) {
        // Create a map of partitions and their corresponding offsets to delete
        TopicPartition topicPartition = new TopicPartition(topic, partition);
        RecordsToDelete records = RecordsToDelete.beforeOffset(Long.MAX_VALUE);

        // Use the AdminClient to delete records
        try (AdminClient adminClient = AdminClient.create(consumerProperties)) {
            adminClient.deleteRecords(Collections.singletonMap(topicPartition, records), new DeleteRecordsOptions()).all().get();
            System.out.println("All messages are deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    public void closeProducerAndConsumer() {
        // Close the KafkaProducer and KafkaConsumer when done
        producer.close();
        consumer.close();
    }
}
