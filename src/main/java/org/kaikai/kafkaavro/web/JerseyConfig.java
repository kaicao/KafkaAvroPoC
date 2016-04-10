package org.kaikai.kafkaavro.web;

import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * Created by kaicao on 10/04/16.
 * JerseyConfig bean as setup for Jersey REST APIs and filters,
 * check Jersey document https://jersey.java.net/documentation/latest/user-guide.html
 */
@Component
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

  public JerseyConfig() {
    // Scan APIs under package recursively
    packages(true, "org.kaikai.kafkaavro.web.rest");
    // Register Swagger endpoint for /swagger.json and /swagger.yaml to get REST specification
    register(ApiListingResource.class);
    register(SwaggerSerializers.class);
  }
}
