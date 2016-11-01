package uk.gov.hmrc.exampleplay25microservice.controllers

import org.scalatest.TestData
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.{Application, Play}
import play.api.inject.guice.GuiceApplicationBuilder

class AppConfigurationSampleSpec extends PlaySpec with OneAppPerTest {

  override def newAppForTest(testData: TestData): Application = {
    val builder = new GuiceApplicationBuilder()
    testData.name match {
      case a if a.matches("^.*one$") => builder.configure("foo" -> "one").build()
      case b if b.matches("^.*two$") => builder.configure("foo" -> "two").build()
      case c if c.matches("^.*three$") => builder.configure("foo" -> "three").build()
      case _ => builder.configure("foo" -> "default").build()
    }
  }

  "configuration of individual case" should {

    "one" in {
      Play.current.configuration.getString("foo") must be (Some("one"))
    }

    "two" in {
      Play.current.configuration.getString("foo") must be (Some("two"))
    }

    "three" in {
      Play.current.configuration.getString("foo") must be (Some("three"))
    }

  }

}
