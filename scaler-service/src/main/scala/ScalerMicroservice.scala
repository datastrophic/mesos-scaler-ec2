import akka.actor.{Props, ActorSystem}
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.math._
import spray.json.DefaultJsonProtocol

//case class IpInfo(ip: String, country: Option[String], city: Option[String], latitude: Option[Double], longitude: Option[Double])
//
//case class IpPairSummaryRequest(ip1: String, ip2: String)
//
//case class IpPairSummary(distance: Option[Double], ip1Info: IpInfo, ip2Info: IpInfo)
//
//object IpPairSummary {
//  def apply(ip1Info: IpInfo, ip2Info: IpInfo): IpPairSummary = IpPairSummary(calculateDistance(ip1Info, ip2Info), ip1Info, ip2Info)
//}

trait Protocols extends DefaultJsonProtocol {
//  implicit val ipInfoFormat = jsonFormat5(IpInfo.apply)
//  implicit val ipPairSummaryRequestFormat = jsonFormat2(IpPairSummaryRequest.apply)
//  implicit val ipPairSummaryFormat = jsonFormat3(IpPairSummary.apply)
}

trait Service extends Protocols {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

  lazy val telizeConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.telizeHost"), config.getInt("services.telizePort"))

  def telizeRequest(request: HttpRequest): Future[HttpResponse] = Source.single(request).via(telizeConnectionFlow).runWith(Sink.head)

  val routes = {
    logRequestResult("akka-http-microservice") {
      pathPrefix("stat") {
        complete(s"Current cluster load: ${ClusterMetricRegistry.histogram.getSnapshot.get75thPercentile()}")
      }
    }
  }
}

object ScalerMicroservice extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
  system.actorOf(Props(new ClusterMonitorService))
}
