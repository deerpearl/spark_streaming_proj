package com.deerpearl

import org.apache.spark.{SparkConf, SparkContext}
import com.datastax.spark.connector._
import org.apache.spark.rdd.RDD
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import com.datastax.driver.core.querybuilder.QueryBuilder._
import org.apache.spark.sql.Row // this is the only one used for CassandraConnectionUri part


object SparkAkkaHttpAnalytics {


  def gettwitterdata():String = {

    val uri = CassandraConnectionUri("cassandra://localhost:9042/super_gloo")
    val session = Helper.createSessionAndInitKeyspace(uri)

    val selectStmt = select("sentiment", "user_location") //.column("sentiment")
      .from("streaming_tweets_by_day")
    //      .limit(1)

    val resultSet = session.execute(selectStmt)
    val rs = resultSet.all()

    /*
    while (rs.iterator().hasNext()) {
      val row = rs.iterator().next()
      json = json + "{sentiment: " + row.getObject(0) +
      "localtion: " + row.getObject(1) + "}"
    }
*/

    //-- 2) second way to get cassandra data
    val conf = new SparkConf(true)
      .setMaster("local[2]").setAppName("SparkAnalyticsModule")
      .set("spark.cassandra.connection.host", "127.0.0.1")

    val sc = new SparkContext(conf)

    val rdd = sc.cassandraTable("super_gloo", "streaming_tweets_by_day")
      .select("sentiment", "user_location")
      .keyBy[(String, String)]("sentiment", "user_location")

    val json = "results" -> rdd.collect().toList.map{
      case (sen, loc) =>
          ("sentiment", sen.toString()) ~
          ("location",  loc.toString())

    }

    compact(render(json))
  }
 
}
