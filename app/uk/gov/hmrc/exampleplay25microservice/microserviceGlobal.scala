/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.exampleplay25microservice

import com.kenshoo.play.metrics.MetricsFilter
import com.typesafe.config.Config
import play.api._
import uk.gov.hmrc.play.audit.filters.AuditFilter
import uk.gov.hmrc.play.auth.controllers.AuthParamsControllerConfig
import uk.gov.hmrc.play.config.{AppName, ControllerConfig, RunMode}
import uk.gov.hmrc.play.http.logging.filters.LoggingFilter
import uk.gov.hmrc.play.microservice.bootstrap.{DefaultMicroserviceGlobal, JsonErrorHandling}
import uk.gov.hmrc.play.auth.microservice.filters.AuthorisationFilter
import net.ceedubs.ficus.Ficus._
import org.slf4j.MDC
import play.api.mvc.{EssentialAction, EssentialFilter, Filters}
import uk.gov.hmrc.play.audit.http.config.ErrorAuditingSettings
import uk.gov.hmrc.play.filters.{MicroserviceFilterSupport, NoCacheFilter, RecoveryFilter}
import uk.gov.hmrc.play.graphite.GraphiteConfig
import uk.gov.hmrc.play.microservice.bootstrap.Routing.RemovingOfTrailingSlashes


object ControllerConfiguration extends ControllerConfig {
  lazy val controllerConfigs = Play.current.configuration.underlying.as[Config]("controllers")
}

object AuthParamsControllerConfiguration extends AuthParamsControllerConfig {
  lazy val controllerConfigs = ControllerConfiguration.controllerConfigs
}

object MicroserviceAuditFilter extends AuditFilter with AppName with MicroserviceFilterSupport {
  override val auditConnector = MicroserviceAuditConnector
  override def controllerNeedsAuditing(controllerName: String) = ControllerConfiguration.paramsForController(controllerName).needsAuditing
}

object MicroserviceLoggingFilter extends LoggingFilter with MicroserviceFilterSupport {
  override def controllerNeedsLogging(controllerName: String) = ControllerConfiguration.paramsForController(controllerName).needsLogging
}

object MicroserviceAuthFilter extends AuthorisationFilter with MicroserviceFilterSupport {
  override lazy val authParamsConfig = AuthParamsControllerConfiguration
  override lazy val authConnector = MicroserviceAuthConnector
  override def controllerNeedsAuth(controllerName: String): Boolean = ControllerConfiguration.paramsForController(controllerName).needsAuth
}

trait MicroserviceFiltersTest extends MicroserviceFilterSupport {

  def loggingFilter: LoggingFilter

  def microserviceAuditFilter: AuditFilter

  def metricsFilter: MetricsFilter = Play.current.injector.instanceOf[MetricsFilter]

  def authFilter: Option[EssentialFilter]

  protected lazy val defaultMicroserviceFilters: Seq[EssentialFilter] = Seq(
    //Some(metricsFilter),
    Some(microserviceAuditFilter),
    Some(loggingFilter),
    authFilter,
    Some(NoCacheFilter),
    Some(RecoveryFilter)).flatten

  def microserviceFilters: Seq[EssentialFilter] = defaultMicroserviceFilters
}

object MicroserviceGlobal
//  extends DefaultMicroserviceGlobal
//  with RunMode
    extends GlobalSettings
  with GraphiteConfig
  with RemovingOfTrailingSlashes
  with JsonErrorHandling
  with ErrorAuditingSettings
  with MicroserviceFiltersTest
   {

  lazy val appName = Play.current.configuration.getString("appName").getOrElse("APP NAME NOT SET")
  lazy val loggerDateFormat: Option[String] = Play.current.configuration.getString("logger.json.dateformat")

    override val auditConnector = MicroserviceAuditConnector

    override def microserviceMetricsConfig(implicit app: Application): Option[Configuration] = app.configuration.getConfig(s"microservice.metrics")

    override val loggingFilter = MicroserviceLoggingFilter

    override val microserviceAuditFilter = MicroserviceAuditFilter

    override val authFilter = Some(MicroserviceAuthFilter)

  override def onStart(app: Application) {
    Logger.info(s"Starting microservice : $appName : in mode : ${app.mode}")
    MDC.put("appName", appName)
    loggerDateFormat.foreach(str => MDC.put("logger.json.dateformat", str))
    super.onStart(app)
  }

  override def doFilter(a: EssentialAction): EssentialAction = {
    Filters(super.doFilter(a), microserviceFilters: _*)
  }

}
