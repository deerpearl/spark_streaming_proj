package com.deerpearl

//Note: this is class is not used

import org.apache.spark.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import org.jfarcand.wcs.{TextListener, WebSocket}
import twitter4j.TwitterStreamFactory


class SuperTwitterReceiver() extends Receiver[String](StorageLevel.MEMORY_ONLY) with Runnable with Logging {
 

  @transient
  private var thread: Thread = _
 
  override def onStart(): Unit = {
     thread = new Thread(this)
     thread.start()
  }
 
  override def onStop(): Unit = {
     thread.interrupt()
  }
 
  override def run(): Unit = {
     receive()
   }

  private def receive(): Unit = {

    val twitterStream = new TwitterStreamFactory(SuperUtil.config).getInstance
    twitterStream.addListener(SuperUtil.simpleStatusListener)
    twitterStream.sample //Note: this is where the output to console
    Thread.sleep(2000)

  }
 

 
}
