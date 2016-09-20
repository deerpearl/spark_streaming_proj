package com.deerpearl

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SuperTwitterStreamingApp {



  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster(args(0)).setAppName("SuperTwitterStreaming")
      .set("spark.rpc.netty.dispatcher.numThreads","2")
      .set("spark.cassandra.connection.host", "127.0.0.1")
    implicit val ssc = new StreamingContext(conf, Seconds(5))
    val twitterStreamingServices: SuperTwitterStreamingServices = new SuperTwitterStreamingServices {}


    //borrowed from deerpearl
/*
    val stream = ssc.receiverStream(new SuperTwitterReceiver())
    stream.print() //Note: it seems like this is not the output to console
    if (args.length > 1 ){
      stream.saveAsTextFiles(args(1))
    }
    ssc.start()
    ssc.awaitTermination()
*/

    // try 'spark on code' here

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
