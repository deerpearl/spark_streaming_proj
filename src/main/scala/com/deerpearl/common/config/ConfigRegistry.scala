package com.deerpearl.common.config

import com.typesafe.config.{Config, ConfigFactory}
import scala.collection.JavaConverters._
import scala.language.postfixOps

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


  lazy val kafkaConfig = config.getConfig("kafka")

  lazy val kafkaNodesEnvVariables = 1 to 10 map { index =>
    (sys.env.get(s"KAFKA_${index}_PORT_9092_TCP_ADDR"),
      sys.env.get(s"KAFKA_${index}_PORT_9092_TCP_PORT"))
  } toList

  lazy val kafkaNodesValues: List[String] = kafkaNodesEnvVariables flatMap {
    case (Some(h), Some(p)) => Some(s"$h:$p")
    case _ => None
  }

  lazy val kafkaBootstrapServers = mkStringNodes(nodes = kafkaNodesValues,
    propKey = "kafka.hosts",
    cfg = kafkaConfig,
    configurationKeyList = "hosts")
  lazy val kafkaTopics = kafkaConfig.getString("topics").split(",").toSet

  lazy val zookeeperHost = kafkaConfig.getString("zookeeper.host")
  lazy val zookeeperPort = kafkaConfig.getInt("zookeeper.port")

  lazy val kafkaGroupId = kafkaConfig.getString("group.id")
  lazy val kafkaTopicRaw = kafkaConfig.getString("topic.raw")

  lazy val kafkaProducerKeySerializer = kafkaConfig.getString("producer.key.serializer")
  lazy val kafkaProducerValueSerializer = kafkaConfig.getString("producer.value.serializer")

  // Helper methods:

  private[config] def getStringFromEnvOrConfig(configKey: String) =
    sys.props.get(configKey) getOrElse config.getString(configKey)

  private[config] def getIntFromEnvOrConfig(configKey: String) =
    sys.props.get(configKey) map (_.toInt) getOrElse config.getInt(configKey)

  private[config] def mkStringNodes(nodes: List[String], propKey: String, cfg: Config, configurationKeyList: String): String =
    if (nodes.nonEmpty) nodes.mkString(",")
    else sys.props.get(propKey) getOrElse {
      val hostList = cfg.getStringList(configurationKeyList).asScala
      hostList.mkString(",")
    }
}