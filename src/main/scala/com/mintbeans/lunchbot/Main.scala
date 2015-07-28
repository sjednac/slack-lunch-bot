package com.mintbeans.lunchbot

import akka.actor.{Props, ActorSystem}
import com.mintbeans.lunchbot.actors.Announcer
import com.mintbeans.lunchbot.actors.Announcer.AnnounceLunchMenu
import com.mintbeans.lunchbot.config.ConfigModule
import com.mintbeans.lunchbot.facebook.FacebookModule
import com.mintbeans.lunchbot.slack.SlackModule
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

object Main extends App with ConfigModule with SlackModule with FacebookModule {
  val system = ActorSystem("lunchbot", config)
  val scheduler = QuartzSchedulerExtension(system)
  val announcer = system.actorOf(Props(classOf[Announcer], config.getMillis("lunch.duration"), facebook, facebookPages, slack, slackChannel))

  scheduler.schedule("LunchTime", announcer, AnnounceLunchMenu)
}
