package com.mintbeans.lunchbot.actors

import java.time.LocalDateTime

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import com.flyberrycapital.slack.SlackClient
import com.mintbeans.lunchbot.actors.Announcer._
import com.mintbeans.lunchbot.actors.Publisher.ChannelMessage
import com.mintbeans.lunchbot.actors.Scrapper.ScrappedPost
import com.mintbeans.lunchbot.facebook.Facebook
import com.mintbeans.lunchbot.facebook.Facebook.{Page, Post}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, GivenWhenThen, WordSpecLike}

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class AnnouncerSpec extends TestKit(ActorSystem("AnnouncerSpec")) with WordSpecLike with BeforeAndAfterAll with MockitoSugar with GivenWhenThen {

  sealed trait TestContext {
    val timeoutTolerance = (500 milliseconds)
    
    val time = LocalDateTime.now()

    val facebook = mock[Facebook]
    val pages = Set(Page("Fancy", "Fancy restaurant"), Page("Cheap", "Cheap restaurant"))

    def post(page: Page): Post = Post(time, Some(s"Post for ${page.label}"), None)
    def scrappedPost(page: Page): ScrappedPost = ScrappedPost(page, post(page))

    val slack = mock[SlackClient]
    val channel = "#test"

    val scrapperProbes: Map[Page, TestProbe] = pages.map(page => (page, TestProbe())).toMap
    val publisherProbe = TestProbe()

    val watcher = {
      val watcher = TestProbe()
      
      scrapperProbes.foreach { case (_, scrapperProbe) => watcher watch scrapperProbe.ref }
      watcher watch publisherProbe.ref
      
      watcher
    }

    def props(lunchDuration: FiniteDuration = 5 seconds) = Props(new Announcer(lunchDuration, facebook, pages, slack, channel) {
      override def newScrapper(page: Page) = scrapperProbes(page).ref
      override def newPublisher = publisherProbe.ref
    })
  }

  override def afterAll() {
    system.shutdown()
  }

  "Announcer" should {
    "publish scrapped Facebook pages" in new TestContext {
      Given("an announcer instance")
      val announcer = system.actorOf(props())

      When("a lunch announcement is requested")
      announcer ! AnnounceLunchMenu

      And("all scrappers publish a post")
      scrapperProbes.foreach({ case (page, probe) => announcer.tell(scrappedPost(page), probe.ref) })

      Then("each published post should be passed to the publisher")
      scrapperProbes.foreach({ case (page, _) => publisherProbe.expectMsg(ChannelMessage(channel, scrappedPost(page).slackMessage))})
    }

    "timeout when lunch is over" in new TestContext {
      Given("a timeout")
      val timeout = 1 second

      And("an announcer instance")
      val announcer = system.actorOf(props(timeout))

      When("a lunch announcement is requested")
      announcer ! AnnounceLunchMenu

      Then("all scrappers should be terminated after the timeout")
      scrapperProbes.foreach({ case (_, scrapperProbe) => watcher.expectTerminated(scrapperProbe.ref, timeout + timeoutTolerance) })

      And("the publisher should be terminated ")
      watcher.expectTerminated(publisherProbe.ref, timeout + timeoutTolerance)
    }
  }

}
