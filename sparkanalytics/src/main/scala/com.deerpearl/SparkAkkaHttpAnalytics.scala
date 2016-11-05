package com.deerpearl

import org.apache.spark.{SparkConf, SparkContext}
import com.datastax.spark.connector._

import com.datastax.driver.core.querybuilder.QueryBuilder._ // this is the only one used for CassandraConnectionUri part


object SparkAkkaHttpAnalytics {


  def gettwitterdata() {

    val uri = CassandraConnectionUri("cassandra://localhost:9042/super_gloo")
    val session = Helper.createSessionAndInitKeyspace(uri)

    val selectStmt = select().column("tweet_text")
      .from("streaming_tweets_by_day")
      .limit(1)

    val resultSet = session.execute(selectStmt)
    val row = resultSet.one()

    print(row.toString)


    val conf = new SparkConf(true)
      .setMaster("local[2]").setAppName("SparkAnalyticsModule")
      .set("spark.cassandra.connection.host", "127.0.0.1")

    val sc = new SparkContext(conf)

    val rdd = sc.cassandraTable("super_gloo", "streaming_tweets_by_day")
    rdd.foreach(println)

  }
 
}
