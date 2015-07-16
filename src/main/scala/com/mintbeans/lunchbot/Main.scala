package com.mintbeans.lunchbot

import java.time.LocalDate

import com.mintbeans.lunchbot.config.ConfigModule
import com.mintbeans.lunchbot.facebook.FacebookModule
import com.mintbeans.lunchbot.slack.SlackModule

import scala.collection.JavaConverters._

object Main extends App with ConfigModule with SlackModule with FacebookModule {
  val facebookPages = config.getConfigList("facebook.pages").asScala

  val since = Some(LocalDate.now.atStartOfDay)
  val message = facebookPages.map({ config =>
    val id = config.getString("id")
    val label = config.getString("label")

    val post = facebook.lastPost(id, since)

    s"\n*** ${label} ***\n${post.message.getOrElse("No text content available.")}\n"
  }).foldLeft("### Dinner options ###\n")((m,c) => m + c)

  //NOTE: You'll need to invite your bot to the target channel (implied by as_user=true, which is required for
  //the bot name/icon to be displayed properly)
  slack.chat.postMessage(config.getString("slack.channel"), message, Map("as_user" -> "true"))
}
