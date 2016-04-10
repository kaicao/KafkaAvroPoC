package org.kaikai.kafkaavro.request;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by kaicao on 10/04/16.
 *
 * Request object used to map JSON request object for add new portfolio report
 */
public class PortfolioReportAddRequest implements Serializable {
  private static final long serialVersionUID = 2992560067988452218L;

  private UUID id;
  private String name;

  public UUID getId() {
    return id;
  }

  public PortfolioReportAddRequest setId(UUID id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public PortfolioReportAddRequest setName(String name) {
    this.name = name;
    return this;
  }
}
