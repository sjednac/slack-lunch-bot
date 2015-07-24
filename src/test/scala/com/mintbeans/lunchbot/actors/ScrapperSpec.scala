package com.mintbeans.lunchbot.actors

import java.time.{LocalDateTime, LocalDate}

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import com.mintbeans.lunchbot.actors.Scrapper.ScrappedPost
import com.mintbeans.lunchbot.facebook.Facebook
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class ScrapperSpec extends TestKit(ActorSystem("ScrapperSpec")) with WordSpecLike with BeforeAndAfterAll with MockitoSugar {

  sealed trait TestContext {
    val facebook = mock[Facebook]
    val page = Facebook.Page("FancyRestaurant", "Fancy Restaurant")
    val post = Facebook.Post(LocalDateTime.now(), Some("Fancy menu..."), None)

    val probe = TestProbe()

    val since = Some(LocalDate.now.atStartOfDay)
    val interval = 500 milliseconds
  }

  override def afterAll() {
    system.shutdown()
  }

  "Scrapper" should {
    "publish last Facebook post in given interval" in new TestContext {
      when(facebook.lastPost(page, since)).thenReturn(Some(post))

      val scrapper = system.actorOf(Props(classOf[Scrapper], probe.ref, facebook, page, since, interval))

      probe.expectMsg(interval + (100 milliseconds), ScrappedPost(page, post))
      probe.expectMsg(interval + (100 milliseconds), ScrappedPost(page, post))
      probe.expectMsg(interval + (100 milliseconds), ScrappedPost(page, post))
    }

    "not publish anything, when no Facebook post is available" in new TestContext {
      when(facebook.lastPost(page, since)).thenReturn(None)

      val scrapper = system.actorOf(Props(classOf[Scrapper], probe.ref, facebook, page, since, interval))

      probe.expectNoMsg(interval * 2)
    }

  }

}