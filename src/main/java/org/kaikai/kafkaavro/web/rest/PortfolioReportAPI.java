package org.kaikai.kafkaavro.web.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.kaikai.kafkaavro.model.PortfolioReport;
import org.kaikai.kafkaavro.request.PortfolioReportAddRequest;
import org.kaikai.kafkaavro.service.PortfolioReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

/**
 * Created by kaicao on 10/04/16.
 */

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Instance per request thread
@Api("Portfolio Report")
@Path("/portfolio/v1/report")
public class PortfolioReportAPI {

  private PortfolioReportService portfolioReportService;

  /**
   * e.g. GET /api/portfolio/v1/report/a8ecaee0-fefe-11e5-a357-0002a5d5c51b
   * @param id  id of report
   * @return  json formatted PortfolioReport if found
   * @throws Exception
   */
  @Path("{id}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(
      value = "Get PortfolioReport by ID",
      response = PortfolioReport.class
  )
  public PortfolioReport getReport(
      @ApiParam("ID of PortfolioReport") @PathParam("id") UUID id
  ) throws Exception {
    return portfolioReportService.getPortfolioReport(id);
  }

  /**
   * e.g. POST
   * http://localhost:9000/api/portfolio/v1/report/a8ecaee0-fefe-11e5-a357-0002a5d5c51b
   * {
   *  "id" : "a8ecaee0-fefe-11e5-a357-0002a5d5c51b",
   *  "name" : "report2"
   * }
   * @param id   id of PortfolioReport to be created
   * @param request Request object to create PortfolioReport
   * @return  Response with status code
   * @throws Exception
   */
  @Path("{id}")
  @POST
  @ApiOperation(
      value = "Add PortfolioReport",
      response = PortfolioReport.class
  )
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addReport(
      @ApiParam("ID of PortfolioReport") @PathParam("id") UUID id,
      @ApiParam("Request for adding PortfolioReport") PortfolioReportAddRequest request
  ) throws Exception {
    portfolioReportService.addPortfolioReport(request.setId(id));
    return Response.created(URI.create(id.toString())).build();
  }

  @Autowired
  @Qualifier("impl")
  public void setPortfolioReportService(PortfolioReportService portfolioReportService) {
    this.portfolioReportService = portfolioReportService;
  }
}
