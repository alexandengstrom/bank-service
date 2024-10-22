package se.liu.ida.tdp024.account.data.impl.db.util;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import se.liu.ida.tdp024.account.data.api.util.Logger;


public class KafkaLogger implements Logger {
    private Producer<String, String> producer;
    private String topic;

    public KafkaLogger(String topic) {
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers", "kafka:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

            this.producer = new KafkaProducer<>(props);
            this.topic = topic;
        } catch (Exception e) {
            Properties props = new Properties();
            props.put("bootstrap.servers", "http://localhost:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

            this.producer = new KafkaProducer<>(props);
            this.topic = topic;
        }

    }

    @Override
    public void publish(String message) {
        this.producer.send(new ProducerRecord<>(this.topic, message));
    }
}