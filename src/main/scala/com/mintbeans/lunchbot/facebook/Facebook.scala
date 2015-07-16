package com.mintbeans.lunchbot.facebook

import java.time.LocalDateTime

import com.mintbeans.lunchbot.facebook.Facebook.Post

trait Facebook {
  def lastPost(pageId: String, since: Option[LocalDateTime] = None): Option[Post]
}

object Facebook {
  case class Post(time: LocalDateTime, message: Option[String])
}