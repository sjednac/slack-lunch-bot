package com.mintbeans.lunchbot.config

import java.util.concurrent.TimeUnit._

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.FiniteDuration

trait ConfigModule {
  lazy val config = ConfigFactory.load()

  implicit class ConfigDuration(cfg: Config) {
    def getMillis(path: String): FiniteDuration = FiniteDuration(cfg.getDuration(path, MILLISECONDS), MILLISECONDS)
  }
}
