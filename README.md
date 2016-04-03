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