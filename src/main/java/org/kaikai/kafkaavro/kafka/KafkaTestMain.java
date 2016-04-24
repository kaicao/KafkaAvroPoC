package org.kaikai.kafkaavro.kafka;

import org.kaikai.kafkaavro.avro.model.SecurityAvro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.kaikai.kafkaavro.avro.model.PortfolioAvro;

/**
 * Created by kaicao on 24/04/16.
 */
public class KafkaTestMain {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaTestMain.class);
  private static final String TOPIC_NAME = "test_topic_portfolio";
  private static final AtomicBoolean keepRunning = new AtomicBoolean(true);

  public static void main(String[] args) throws Exception {
    testKafka();
  }

  private static void testKafka() throws Exception {
    LOG.info("Start producer and consumer");
    KafkaAvroProducer producer = KafkaAvroProducer.getInstance();
    KafkaAvroConsumer consumer = KafkaAvroConsumer.getInstance();
    producer.start(TOPIC_NAME);
    consumer.start(TOPIC_NAME, consumerRecord -> {
      // Logic to process the batch of consumer records
      LOG.info(consumerRecord.toString());
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
      if (count < 100) {
        producer.send(createPortfolio(count ++));
      }
    }
  }

  private static PortfolioAvro createPortfolio(int count) {
    Map<CharSequence, SecurityAvro> securities = new HashMap<>();
    securities.put("sec1", SecurityAvro.newBuilder().setId("sec1").setName("Nokia").build());
    securities.put("sec2", SecurityAvro.newBuilder().setId("sec2").setName("GOOGLE").build());
    return PortfolioAvro.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Portfolio_" + count)
        .setDescription("Description " +count)
        .setSecurities(securities)
        .build();
  }
}
