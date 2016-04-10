package org.kaikai.kafkaavro.service;

import org.kaikai.kafkaavro.model.PortfolioReport;
import org.kaikai.kafkaavro.request.PortfolioReportAddRequest;

import java.util.UUID;

/**
 * Created by kaicao on 10/04/16.
 */
public interface PortfolioReportService {

  PortfolioReport getPortfolioReport(UUID id);
  void addPortfolioReport(PortfolioReportAddRequest request);
}
