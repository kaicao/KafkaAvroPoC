package org.kaikai.kafkaavro.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Created by kaicao on 17/04/16.
 */
public class SparkTestMain {

  public static void main(String[] args) {
    SparkConf conf = new SparkConf()
        .setMaster("local") // runs Spark on one thread on the local machine, without connecting to a cluster
        .setAppName("KafkaAvro");
    JavaSparkContext sc = new JavaSparkContext(conf);

    wordCount(sc, "README.md", "spark_results");
    sc.stop();
  }

  public static void wordCount(JavaSparkContext sc, String filePath, String savePath) {
    // Load our input data
    JavaRDD<String> input = sc.textFile(filePath);
    // Split up into words
    JavaRDD<String> words = input.flatMap(
        s -> Arrays.asList(s.split(" "))
    );
    // Transform into pairs and count
    JavaPairRDD<String, Integer> counts = words
        .mapToPair(s -> new Tuple2<>(s, 1))
        .reduceByKey((v1, v2) -> v1 + v2);
    // Save the word count back out to a text file
    counts.saveAsTextFile(savePath);
  }

}
