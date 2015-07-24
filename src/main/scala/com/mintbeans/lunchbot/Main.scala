package com.mintbeans.lunchbot

import akka.actor.{Props, ActorSystem}
import com.mintbeans.lunchbot.actors.Announcer
import com.mintbeans.lunchbot.actors.Announcer.AnnounceLunchMenu
import com.mintbeans.lunchbot.config.ConfigModule
import com.mintbeans.lunchbot.facebook.FacebookModule
import com.mintbeans.lunchbot.slack.SlackModule

object Main extends App with ConfigModule with SlackModule with FacebookModule {
  val system = ActorSystem("lunchbot", config)
  val announcer = system.actorOf(Props(classOf[Announcer], facebook, facebookPages, slack, slackChannel))

  announcer ! AnnounceLunchMenu
}
