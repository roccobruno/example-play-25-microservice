package uk.gov.hmrc.exampleplay25microservice.controllers

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec, WsScalaTestClient}
import play.api.test.Helpers._

class MicroserviceHelloWorldFunctionalSpec extends PlaySpec with OneServerPerSuite with WsScalaTestClient {

  "hello world" should {

    "return a JSON message" in {
      (await(wsUrl("/example-play-25-microservice/hello-world").get()).json \ "message").as[String] must be("Hello world")
    }

  }

}
