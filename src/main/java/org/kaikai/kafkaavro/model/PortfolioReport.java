package org.kaikai.kafkaavro.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by kaicao on 10/04/16.
 *
 * Immutable Value Object to represent Portfolio Report
 * usually created base on Entity object from Storage, and immutable to make sure no change to the record
 */
public class PortfolioReport implements Serializable {
  private static final long serialVersionUID = 3975550975814618048L;

  private final UUID id;
  private final String name;

  public PortfolioReport(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID id;
    private String name;

    public PortfolioReport build() {
      return new PortfolioReport(id, name);
    }

    public Builder setId(UUID id) {
      this.id = id;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
