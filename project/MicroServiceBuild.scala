import sbt._

object MicroServiceBuild extends Build with MicroService {

  val appName = "example-play-25-microservice"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val microserviceBootstrapVersion = "4.4.0"
  private val playAuthVersion = "3.4.0"
  private val playHealthVersion = "1.1.0"
  private val playJsonLoggerVersion = "2.1.1"
  private val playUrlBindersVersion = "1.1.0"
  private val playConfigVersion = "2.1.0"
  private val domainVersion = "3.7.0"
  private val hmrcTestVersion = "1.9.0"
  private val scalaTestVersion = "2.2.6"
  private val pegdownVersion = "1.6.0"


  val compile = Seq(

    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "play-authorisation" % playAuthVersion,
    "uk.gov.hmrc" %% "play-health" % playHealthVersion,
    "uk.gov.hmrc" %% "play-url-binders" % playUrlBindersVersion,
    "uk.gov.hmrc" %% "play-config" % playConfigVersion,
    "uk.gov.hmrc" %% "play-json-logger" % playJsonLoggerVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.scalatestplus" %% "play" % "1.2.0" % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}

