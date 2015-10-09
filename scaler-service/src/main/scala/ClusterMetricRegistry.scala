import java.util.concurrent.TimeUnit

import com.codahale.metrics.{Histogram, SlidingTimeWindowReservoir, MetricRegistry}
import com.typesafe.config.ConfigFactory

object ClusterMetricRegistry {
   val config = ConfigFactory.load()
   private val metricRegistry = new MetricRegistry()
   private val reservoir = new SlidingTimeWindowReservoir(config.getInt("monitor.timewindow_sec"), TimeUnit.SECONDS)

   val histogram = new Histogram(reservoir)

   def getLoad = histogram.getSnapshot.getMedian()
}
