package org.kaikai.kafkaavro.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
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
  private static final int MIN_BATCH_SIZE = 100;

  private Consumer<String, String> consumer;
  private String topicName;
  private AtomicBoolean keepRunning = new AtomicBoolean(true);
  private ExecutorService executorService = Executors.newSingleThreadExecutor();

  private KafkaAvroConsumer() {
  }

  public static KafkaAvroConsumer getInstance() {
    return INSTANCE;
  }

  public void start(String topicName, java.util.function.Consumer<List<ConsumerRecord<String, String>>> processFunction) throws Exception {
    this.topicName = topicName;
    Properties props = new Properties();
    // Properties description check org.apache.kafka.clients.consumer.ConsumerConfig source code
    props.put("bootstrap.servers", "localhost:9092");
    props.put("group.id", "test");
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("session.timeout.ms", "30000");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

    consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList(topicName));
    Runnable runnable = () -> {
      try {
        while (keepRunning.get()) {
          ConsumerRecords<String, String> records = consumer.poll(100L);
          for (TopicPartition partition : records.partitions()) {
            // Consume records in batch per partition
            List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records.records(partition)) {
              buffer.add(record);
            }
            ConsumerRecord lastRecord = buffer.get(buffer.size() - 1);
            if (buffer.size() >= MIN_BATCH_SIZE) {
              // TODO avoid process messages in another thread or using queue to communicate between threads
              processFunction.accept(buffer);
              consumer.commitSync(Collections.singletonMap(
                  partition,
                  new OffsetAndMetadata(lastRecord.offset())
              ));
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
}
