package com.mintbeans.lunchbot

import com.mintbeans.lunchbot.config.ConfigModule
import com.mintbeans.lunchbot.facebook.FacebookModule

import scala.collection.JavaConverters._

object Main extends App with ConfigModule with FacebookModule {
  val facebookPages = config.getConfigList("facebook.pages").asScala

  facebookPages.foreach { config =>
    val id = config.getString("id")
    val label = config.getString("label")

    val post = facebook.lastPost(id)

    println(s"### ${label} ###")
    println(s"${post.message.getOrElse("No text content available.")}\n")
  }

}
