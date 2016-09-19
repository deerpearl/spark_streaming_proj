package com.deerpearl.common.config

import com.typesafe.config.{Config, ConfigFactory}
/**
  * Created by deerpearl on 9/11/16.
  */
case class TwitterAuth(consumerKey: String,
                       consumerSecret: String,
                       accessToken: String,
                       accessTokenSecret: String)

object ConfigRegistry {
  val config = ConfigFactory.load()


  lazy val twitterConfig = config.getConfig("twitter")
  lazy val twitterCredentials = twitterConfig.getConfig("credentials")

  lazy val consumerKey = twitterCredentials.getString("consumerKey")
  lazy val consumerSecret = twitterCredentials.getString("consumerSecret")
  lazy val accessToken = twitterCredentials.getString("accessToken")
  lazy val accessTokenSecret = twitterCredentials.getString("accessTokenSecret")

  lazy val twitterAuth = TwitterAuth(
    consumerKey,
    consumerSecret,
    accessToken,
    accessTokenSecret)

  lazy val sparkCassandraKeyspace: String = config.getString("spark.cassandra.keyspace")

  lazy val sparkOnConfig = config.getConfig("spark-on")
/*
  lazy val sparkOnFilters = sparkOnConfig.getStringList("filters").asScala.toSet
  lazy val windowSizeSeconds = sparkOnConfig.getLong("windowSizeSeconds")
  lazy val slideDuration = sparkOnConfig.getLong("slideDuration")
  lazy val cassandraCQLPath = sparkOnConfig.getString("cassandraCQLPath")
  lazy val sparkOnJars = sparkOnConfig.getStringList("spark.jars").asScala.toList
  */
  lazy val dateFormat = sparkOnConfig.getString("dateFormat")
  lazy val dateFormatSplitter = sparkOnConfig.getString("dateFormatSplitter")
}
