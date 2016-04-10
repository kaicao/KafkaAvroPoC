# Download Kafka
http://kafka.apache.org/downloads.html
Currently downloaded kafka 0.9.0.1
Unzip and add unzipped <kafka folder>/bin to the PATH, so kafka commands can be executed from anywhere

# Start server
# cd to the KafkaAvroPoC kafka folder
# 1. start zookeeper
zookeeper-server-start.sh config/zookeeper.properties
# 2. start kafka
kafka-server-start.sh config/server.properties

# Start application
open in IntelliJ and run ApplicationMain

# REST APIs
REST APIs are implemented with Jersey base on JAX-RS specification
API implementations are under org.kaikai.kafkaavro.web.rest folder
Documentation can be accessed by Swagger UI mentioned later

# Swagger UI
Can be accessed by http://localhost:9000/swagger-ui/index.html
Swagger UI provide easy representation of REST API defined by Jersey endpoints together with Swagger documentation
By default Spring Boot will serve static content from a directory called /static,
so the swagger-ui files has been put under resources/static folder, 
and also swagger-ui/index.html has been configured to fetch REST APIs specification from /api/swagger.json
Reference: https://github.com/swagger-api/swagger-ui

# Swagger REST specification
REST endpoints specification can be accessed by /api/swagger.json or /api/swagger.yaml
