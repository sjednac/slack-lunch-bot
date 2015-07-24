package com.mintbeans.lunchbot.facebook

import java.time.LocalDateTime

import com.mintbeans.lunchbot.facebook.Facebook.{Page, Post}

trait Facebook {
  def lastPost(page: Page, since: Option[LocalDateTime] = None): Option[Post]
}

object Facebook {
  case class Page(id: String, label: String)
  case class Post(time: LocalDateTime, message: Option[String], picture: Option[String])
}