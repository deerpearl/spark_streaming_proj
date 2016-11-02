package com.deerpearl

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

//import org.apache.spark.sql.DataFrame
//import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.spark.connector._
//import com.datastax.spark.connector.cql._

//import org.apache.spark.sql.cassandra.CassandraSQLContext

import com.datastax.driver.core.querybuilder.QueryBuilder._ // this is the only one used for CassandraConnectionUri part


object SparkAnalyticsApp {


  def main(args: Array[String]) {

    val uri = CassandraConnectionUri("cassandra://localhost:9042/super_gloo")
    val session = Helper.createSessionAndInitKeyspace(uri)

//    session.execute("CREATE TABLE IF NOT EXISTS things (id int, name text, PRIMARY KEY (id))")
//    session.execute("INSERT INTO things (id, name) VALUES (1, 'foo');")

    val selectStmt = select().column("tweet_text")
      .from("streaming_tweets_by_day")
//      .where(QueryBuilder.eq("id", 1))
      .limit(1)

    val resultSet = session.execute(selectStmt)
    val row = resultSet.one()

    print(row.toString)


    val conf = new SparkConf(true)
      .setMaster(args(0)).setAppName("SparkAnalyticsModule")
  //    .set("spark.rpc.netty.dispatcher.numThreads","2")
      .set("spark.cassandra.connection.host", "127.0.0.1")

    val sc = new SparkContext(conf)

    val rdd = sc.cassandraTable("super_gloo", "streaming_tweets_by_day")
    rdd.foreach(println)

  }
 
}
