import akka.actor.{ActorLogging, Actor}
import com.typesafe.config.ConfigFactory

class EC2NodeActor extends Actor with ActorLogging {
   val config = ConfigFactory.load()

   override def receive: Receive = {
      case StartNode(n) =>
         import scala.sys.process._
         log.info(s"Starting new Mesos Agent @ $n")
         "ssh -i" + config.getString("ssh.key") + " ubuntu@"+ n +" sudo service mesos-slave start" !

         context.stop(self)
      case StopNode(n) =>
         import scala.sys.process._
         log.info(s"Shutting down node: $n")
         "ssh -i" + config.getString("ssh.key") + " ubuntu@"+ n +" sudo service mesos-slave stop" !

         context.stop(self)
   }
}

case class StopNode(node: String)
case class StartNode(node: String)
