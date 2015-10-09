import akka.actor.{Props, ActorLogging, Actor}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.io.Source
import org.json4s._
import org.json4s.native.JsonMethods._

class ClusterMonitorService extends Actor with ActorLogging {
   import context.dispatcher
   val config = ConfigFactory.load()
   val tick = context.system.scheduler.schedule(500 millis, config.getInt("monitor.healthcheck_interval_sec") seconds, self, Tick)
   val check = context.system.scheduler.schedule(10 seconds, config.getInt("monitor.rescaling_interval_sec")  seconds, self, Check)

   val watermark = config.getInt("scaler.watermark")
   val threshold = config.getInt("scaler.threshold")

   var shutDownNodes = Set.empty[String]

   override def postStop() = {
      tick.cancel()
      check.cancel()
   }

   def receive = {
      case Tick =>
         context.system.actorOf(Props(new MesosScraper())) ! Scrap
      case Check =>
         log.info(s"Cluster check started. Current load is ${ClusterMetricRegistry.getLoad}")
         if(ClusterMetricRegistry.getLoad - watermark > threshold && shutDownNodes.nonEmpty){
            log.info("Cluster starving. Adding a bit more resources")
            (1 to (ClusterMetricRegistry.getLoad - watermark).toInt/threshold).foreach{_ =>
               context.system.actorOf(Props(new EC2NodeActor())) ! StartNode(shutDownNodes.head)
               shutDownNodes -= shutDownNodes.head
            }

         } else if (watermark - ClusterMetricRegistry.getLoad > threshold) {
            log.info("Cluster usage is less than specified threshold, scaling nodes down")

            (1 to (watermark - ClusterMetricRegistry.getLoad).toInt/threshold).foreach{_ =>
               context.system.actorOf(Props(new MesosScraper())) ! SlaveRequest
            }
         } else {
            log.info("Nothing to do here, cluster utilization is in defined range")
         }

      case KillCandidates(slaves) =>
         killNode(slaves)

   }

   def killNode(nodes: List[String]): Unit ={
      if(nodes.nonEmpty) {
         if (shutDownNodes.contains(nodes.head)) {
            killNode(nodes.tail)
         } else {
            shutDownNodes += nodes.head
            context.system.actorOf(Props(new EC2NodeActor())) ! StopNode(nodes.head)
         }
      }
   }
}

class MesosScraper extends Actor with ActorLogging {
   val config =  ConfigFactory.load()
   implicit val formats = DefaultFormats

   override def receive: Receive = {
      case Scrap =>
         val snapshot = Source.fromURL(s"http://${config.getString("mesos.master_host")}:${config.getInt("mesos.master_port")}/metrics/snapshot").mkString
         val json = parse(snapshot)
         val load = (json \\ "master/cpus_percent").extract[Double]

         ClusterMetricRegistry.histogram.update((load * 100).toLong)

         context.stop(self)

      case SlaveRequest =>
         val snapshot = Source.fromURL(s"http://${config.getString("mesos.master_host")}:${config.getInt("mesos.master_port")}/slaves").mkString
         val json = parse(snapshot)
         val load = (json \ "slaves" \ "hostname").extract[List[String]]

         sender ! KillCandidates(load)

         context.stop(self)
   }
}

case object Scrap
case object Tick
case object Check

case object SlaveRequest
case class KillCandidates(slaves: List[String])