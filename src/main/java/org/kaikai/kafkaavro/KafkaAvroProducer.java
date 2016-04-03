package org.kaikai.kafkaavro;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by kaicao on 03/04/16.
 */
public class KafkaAvroProducer implements Closeable {

  private static final KafkaAvroProducer INSTANCE = new KafkaAvroProducer();

  private Producer<String, String> producer;
  private String topicName;

  private KafkaAvroProducer() {
  }

  public static KafkaAvroProducer getInstance() {
    return INSTANCE;
  }

  public void start(String topicName) throws Exception {
    this.topicName = topicName;
    Properties props = new Properties();
    // Properties description check org.apache.kafka.clients.producer.ProducerConfig source code
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all"); // producer requires leader to wait for all in-sync replicas acknowledged (highest consistency)
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("lingers.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    producer = new KafkaProducer<>(props);
  }

  public void send(String message) throws Exception {
    producer.send(new ProducerRecord<>(topicName, 0, "key", message));
  }

  @Override
  public void close() throws IOException {
    producer.close();
  }
}
