package com.mintbeans.lunchbot.config

import java.util.concurrent.TimeUnit._

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.FiniteDuration

trait ConfigModule {
  lazy val config = ConfigFactory.load()

  implicit class ConfigDuration(val c: Config) {
    def getMillis(path: String): FiniteDuration = FiniteDuration(config.getDuration(path, MILLISECONDS), MILLISECONDS)
  }
}
