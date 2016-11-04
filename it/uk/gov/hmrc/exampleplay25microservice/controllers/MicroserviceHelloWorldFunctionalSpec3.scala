package uk.gov.hmrc.exampleplay25microservice.controllers

import org.scalatestplus.play.{OneServerPerTest, PlaySpec, WsScalaTestClient}
import play.api.test.Helpers._

class MicroserviceHelloWorldFunctionalSpec3 extends PlaySpec with OneServerPerTest with WsScalaTestClient {

  "hello world" should {

    "return a JSON message" in {
      (await(wsUrl("/example-play-25-microservice/hello-world").get()).json \ "message").as[String] must be("Hello world")
    }

  }

  "hello world 2" should {

    "return a JSON message" in {
      (await(wsUrl("/example-play-25-microservice/hello-world").get()).json \ "message").as[String] must be("Hello world")
    }

  }
}
