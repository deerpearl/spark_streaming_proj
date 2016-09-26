package com.deerpearl

import org.apache.spark.SparkConf
import com.deerpearl.common.config.ConfigRegistry._
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SuperTwitterStreamingApp {



  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster(args(0)).setAppName("SuperTwitterStreaming")
      .set("spark.rpc.netty.dispatcher.numThreads","2")
      .set("spark.cassandra.connection.host", "127.0.0.1")
      .set("spark.executorEnv.kafkaBootstrapServers", "127.0.0.1:9092") //kafkaBootstrapServers) //bootstrapServers)
      .set("spark.executorEnv.kafkaProducerKeySerializer", kafkaProducerKeySerializer)
      .set("spark.executorEnv.kafkaProducerValueSerializer", kafkaProducerValueSerializer)

    implicit val ssc = new StreamingContext(conf, Seconds(5))
    val twitterStreamingServices: SuperTwitterStreamingServices = new SuperTwitterStreamingServices {}

    val filters = Set("scala", "play", "akka", "spark" , "47", "global", "consulting")
    val windowSizeSeconds = 30
    val slideDuration = 10

    implicit val dsStream = twitterStreamingServices.createTwitterStream()

    twitterStreamingServices.ingestTweets(topics = filters,
      windowSize = Seconds(windowSizeSeconds),
      slideDuration = Seconds(slideDuration))

    ssc.awaitTermination()

  }
 
}
