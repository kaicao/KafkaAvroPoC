package org.kaikai.kafkaavro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by kaicao on 03/04/16.
 */
public class ApplicationMain {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationMain.class);
  private static final String TOPIC_NAME = "test_topic_1";
  private static final AtomicBoolean keepRunning = new AtomicBoolean(true);

  public static void main(String[] args) throws Exception {
    LOG.info("Start producer and consumer");
    KafkaAvroProducer producer = KafkaAvroProducer.getInstance();
    KafkaAvroConsumer consumer = KafkaAvroConsumer.getInstance();
    producer.start(TOPIC_NAME);
    consumer.start(TOPIC_NAME, consumerRecords -> {
      // Logic to process the batch of consumer records
      consumerRecords.forEach(record -> LOG.info(record.toString()));
    });
    final Thread mainThread = Thread.currentThread();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          LOG.info("Close producer and consumer");
          producer.close();
          consumer.close();
          keepRunning.set(false);
          Thread.sleep(2_000L);
          mainThread.join();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    int count = 0;
    while(keepRunning.get()) {
      // keep running
      if (count < 1000) {
        producer.send("message" + count++);
      }
    }
  }
}
