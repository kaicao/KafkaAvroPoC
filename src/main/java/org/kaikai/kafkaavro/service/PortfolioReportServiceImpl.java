package org.kaikai.kafkaavro.service;

import org.kaikai.kafkaavro.kafka.KafkaAvroProducer;
import org.kaikai.kafkaavro.model.PortfolioReport;
import org.kaikai.kafkaavro.request.PortfolioReportAddRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by kaicao on 10/04/16.
 */
@Service("impl")
public class PortfolioReportServiceImpl implements PortfolioReportService {

  private static final Logger LOG = LoggerFactory.getLogger(PortfolioReportServiceImpl.class);

  private final KafkaAvroProducer kafkaAvroProducer;

  @Autowired
  public PortfolioReportServiceImpl(KafkaAvroProducer kafkaAvroProducer) {
    this.kafkaAvroProducer = kafkaAvroProducer;
  }

  @Override
  public PortfolioReport getPortfolioReport(UUID id) {
    return PortfolioReport.builder().setId(id).setName("report").build();
  }

  @Override
  public void addPortfolioReport(PortfolioReportAddRequest request) {
    LOG.info(String.format("Create PortfolioReport with id: %s, name: %s", request.getId(), request.getName()));
  }
}
