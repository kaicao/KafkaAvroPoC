package org.kaikai.kafkaavro.kafka;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.kaikai.kafkaavro.avro.model.PortfolioAvro;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by kaicao on 03/04/16.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class KafkaAvroProducer implements Closeable {

  private static final KafkaAvroProducer INSTANCE = new KafkaAvroProducer();

  private Producer<String, byte[]> producer;
  private String topicName;
  private EncoderFactory avroEncoderFactory = EncoderFactory.get();
  private SpecificDatumWriter<PortfolioAvro> portfolioWriter = new SpecificDatumWriter<>(PortfolioAvro.class);

  public KafkaAvroProducer() {
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
    props.put("key.serializer", StringSerializer.class);
    props.put("value.serializer", ByteArraySerializer.class);

    producer = new KafkaProducer<>(props);
  }

  public void send(String message) throws Exception {
    producer.send(new ProducerRecord<>(topicName, 0, "key", message.getBytes()));
  }

  public void send(PortfolioAvro portfolio) {
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      BinaryEncoder encoder = avroEncoderFactory.binaryEncoder(output, null);
      portfolioWriter.write(portfolio, encoder);
      encoder.flush();

      producer.send(new ProducerRecord<>(topicName, output.toByteArray()));
    } catch (IOException e) {
      throw new RuntimeException("Avro serialization failed", e);
    }
  }

  @Override
  public void close() throws IOException {
    producer.close();
  }
}
