package org.kaikai.kafkaavro.kafka;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.kaikai.kafkaavro.avro.model.PortfolioAvro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by kaicao on 03/04/16.
 */
public class KafkaAvroConsumer implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaAvroConsumer.class);
  private static final KafkaAvroConsumer INSTANCE = new KafkaAvroConsumer();
  private static final int MIN_BATCH_SIZE = 10;

  private Consumer<String, byte[]> consumer;
  private String topicName;
  private DecoderFactory avroDecoderFactory = DecoderFactory.get();
  private SpecificDatumReader<PortfolioAvro> portfolioReader = new SpecificDatumReader<>(PortfolioAvro.class);

  private AtomicBoolean keepRunning = new AtomicBoolean(true);
  private ExecutorService executorService = Executors.newSingleThreadExecutor();

  private KafkaAvroConsumer() {
  }

  public static KafkaAvroConsumer getInstance() {
    return INSTANCE;
  }

  public void start(String topicName, java.util.function.Consumer<ConsumerRecord<String, PortfolioAvro>> processFunction) throws Exception {
    this.topicName = topicName;
    Properties props = new Properties();
    // Properties description check org.apache.kafka.clients.consumer.ConsumerConfig source code
    props.put("bootstrap.servers", "localhost:9092");
    props.put("group.id", "test");
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("session.timeout.ms", "30000");
    props.put("key.deserializer", StringDeserializer.class);
    props.put("value.deserializer", ByteArrayDeserializer.class);

    consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList(topicName));
    Runnable runnable = () -> {
      try {
        while (keepRunning.get()) {
          ConsumerRecords<String, byte[]> records = consumer.poll(1000L);
          for (TopicPartition partition : records.partitions()) {
            // Consume records in batch per partition
            List<ConsumerRecord<String, PortfolioAvro>> buffer = new ArrayList<>();
            for (ConsumerRecord<String, byte[]> record : records.records(partition)) {
              ConsumerRecord portfolioRecord = new ConsumerRecord<>(
                  record.topic(),
                  record.partition(),
                  record.offset(),
                  record.key(),
                  readPortfolio(record.value())
              );
              buffer.add(portfolioRecord);
              processFunction.accept(portfolioRecord);
            }
              // TODO avoid process messages in another thread or using queue to communicate between threads


            ConsumerRecord lastRecord = buffer.get(buffer.size() - 1);
              consumer.commitSync(Collections.singletonMap(
                  partition,
                  new OffsetAndMetadata(lastRecord.offset())
              ));

            if (buffer.size() >= MIN_BATCH_SIZE) {
              LOG.info(String.format("Topic %s partition %d commit offset %d",
                  partition.topic(), partition.partition(), lastRecord.offset()));
            }
          }
        }
      } finally {
        try {
          close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    executorService.execute(runnable);
  }

  @Override
  public void close() throws IOException {
    keepRunning.set(false);
    consumer.close();
    executorService.shutdownNow();
    try {
      executorService.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private PortfolioAvro readPortfolio(byte[] bytes) {
    BinaryDecoder decoder = avroDecoderFactory.binaryDecoder(bytes, null);
    try {
      return portfolioReader.read(null, decoder);
    } catch (IOException e) {
      throw new RuntimeException("Arvo deserialization failed", e);
    }
  }
}
