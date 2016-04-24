package org.kaikai.kafkaavro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by kaicao on 03/04/16.
 */
// http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-auto-configuration
// @SpringBootApplication is same as @Configuration @EnableAutoConfiguration @ComponentScan
@SpringBootApplication // better to be marked in the root package level so all underlying packages will be scanned automatically
public class ApplicationMain {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(ApplicationMain.class, args);
  }
}
