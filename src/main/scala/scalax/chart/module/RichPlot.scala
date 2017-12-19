package scalax.chart
package module

import java.util.Collection

import scala.collection.JavaConverters.asScalaIteratorConverter

import org.jfree.chart.axis.{ CategoryAxis, ValueAxis }
import org.jfree.chart.plot._

/** $RichPlotInfo */
object RichPlot extends RichPlot

/** $RichPlotInfo
  *
  * @define RichPlotShortInfo [[RichPlot]] contains enriched plot types to conveniently work with
  * the plot domain, range and marking.
  *
  * {{{
  * chart.plot.domain.axis.label.text = "my label"
  * chart.plot.range.markers += ((1,1))
  * }}}
  *
  * @define RichPlotInfo '' '' $RichPlotShortInfo
  *
  * == Marking ==
  *
  * There are enrichments concerning the handling of markers in plots:
  *
  * {{{
  * chart.plot.domain.markers += 1
  * chart.plot.range.markers += ((1,1))
  *
  * for (marker <- chart.plot.range.markers) {
  *   // do something with marker
  * }
  * }}}
  *
  * == Marker Conversions ==
  *
  * $MarkerConversionsShortInfo
  */
trait RichPlot extends MarkerConversions {

  /** Enriches a `CategoryPlot`. */
  implicit class RichCategoryPlot(val peer: CategoryPlot) {
    object domain {

      /** Returns the domain axis. */
      object axis extends Axis {
        type Peer = CategoryAxis
        override final def peer: CategoryAxis = RichCategoryPlot.this.peer.getDomainAxis
      }

      object markers {
        def +=[A: ToCategoryMarker](a: A): this.type = {
          val marker = ToCategoryMarker[A].toMarker(a)
          peer.addDomainMarker(marker)
          this
        }

        def iterator: Iterator[CategoryMarker] = {
          val jcoll = peer.getDomainMarkers(Layer.Foreground).asInstanceOf[Collection[CategoryMarker]]
          jcoll.iterator.asScala
        }
      }
    }

    object range {

      /** Returns the range axis. */
      object axis extends Axis {
        type Peer = ValueAxis
        override final def peer: ValueAxis = RichCategoryPlot.this.peer.getRangeAxis
      }

      object markers {
        def +=[A: ToMarker](a: A): this.type = {
          val marker = ToMarker[A].toMarker(a)
          peer.addRangeMarker(marker)
          this
        }

        def iterator: Iterator[Marker] = {
          val jcoll = peer.getRangeMarkers(Layer.Foreground).asInstanceOf[Collection[Marker]]
          jcoll.iterator.asScala
        }
      }
    }
  }

  /** Enriches a `FastScatterPlot`. */
  implicit class RichFastScatterPlot(val peer: FastScatterPlot) {
    object domain {

      /** Returns the domain axis. */
      object axis extends Axis {
        type Peer = ValueAxis
        override final def peer: ValueAxis = RichFastScatterPlot.this.peer.getDomainAxis
      }
    }

    object range {

      /** Returns the range axis. */
      object axis extends Axis {
        type Peer = ValueAxis
        override final def peer: ValueAxis = RichFastScatterPlot.this.peer.getRangeAxis
      }
    }
  }

  /** Enriches a `ThermometerPlot`. */
  implicit class RichThermometerPlot(val peer: ThermometerPlot) {
    object range {

      /** Returns the range axis. */
      object axis extends Axis {
        type Peer = ValueAxis
        override final def peer: ValueAxis = RichThermometerPlot.this.peer.getRangeAxis
      }
    }
  }

  /** Enriches an `XYPlot`. */
  implicit class RichXYPlot(val peer: XYPlot) {
    object domain {

      /** Returns the domain axis. */
      object axis extends Axis {
        type Peer = ValueAxis
        override final def peer: ValueAxis = RichXYPlot.this.peer.getDomainAxis
      }

      object markers {
        def +=[A: ToMarker](a: A): this.type = {
          val marker = ToMarker[A].toMarker(a)
          peer.addDomainMarker(marker)
          this
        }

        def iterator: Iterator[Marker] = {
          val jcoll = peer.getDomainMarkers(Layer.Foreground).asInstanceOf[Collection[Marker]]
          jcoll.iterator.asScala
        }
      }
    }

    object range {

      /** Returns the range axis. */
      object axis extends Axis {
        type Peer = ValueAxis
        override final def peer: ValueAxis = RichXYPlot.this.peer.getRangeAxis
      }

      object markers {
        def +=[A: ToMarker](a: A): this.type = {
          val marker = ToMarker[A].toMarker(a)
          peer.addRangeMarker(marker)
          this
        }

        def iterator: Iterator[Marker] = {
          val jcoll = peer.getRangeMarkers(Layer.Foreground).asInstanceOf[Collection[Marker]]
          jcoll.iterator.asScala
        }
      }
    }
  }

}
