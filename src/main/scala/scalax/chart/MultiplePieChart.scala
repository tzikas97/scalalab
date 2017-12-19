package scalax.chart

import org.jfree.chart.title.TextTitle
import org.jfree.ui.RectangleEdge
import org.jfree.util.TableOrder

import module.CategoryDatasetConversions._

/** Represents categorized numeric data with multiple pies. */
abstract class MultiplePieChart protected () extends Chart
    with Labels[PieLabelGenerator]
    with Tooltips[PieToolTipGenerator] {

  type Plot = MultiplePiePlot

  final def underlying: PieChart = PieChart.fromPeer(plot.getPieChart)

  override def plot: MultiplePiePlot = peer.getPlot.asInstanceOf[MultiplePiePlot]

  override def labelGenerator: Option[PieLabelGenerator] = underlying.labelGenerator
  override def labelGenerator_=(generator: Option[PieLabelGenerator]): Unit = {
    underlying.labelGenerator = generator
  }

  override def tooltipGenerator: Option[PieToolTipGenerator] = underlying.tooltipGenerator
  override def tooltipGenerator_=(generator: Option[PieToolTipGenerator]): Unit = {
    underlying.tooltipGenerator = generator
  }
}

/** Factory for ${chart}s.
  *
  * @define chart multiple pie chart
  * @define Chart MultiplePieChart
  */
object MultiplePieChart extends ChartCompanion[MultiplePieChart] {

  override final def fromPeer(jfree: JFreeChart): MultiplePieChart = {
    require(jfree.getPlot.isInstanceOf[Plot], "Illegal peer plot type.")

    new MultiplePieChart {
      override final lazy val peer = jfree
    }
  }

  /** Creates a new $chart.
    *
    * @param data  $data
    * @param theme $theme
    *
    * @usecase def apply(data: CategoryDataset): MultiplePieChart = ???
    *   @inheritdoc
    */
  def apply[A: ToCategoryDataset](data: A)
    (implicit theme: ChartTheme = ChartTheme.Default): MultiplePieChart = {
    val dataset = ToCategoryDataset[A].convert(data)

    val plot = new MultiplePiePlot(dataset)
    plot.setDataExtractOrder(TableOrder.BY_COLUMN)
    plot.setBackgroundPaint(null)
    plot.setOutlineStroke(null)

    MultiplePieChart(plot, title = "", legend = true)
  }

  /** Creates a new $chart with three dimensional visualization.
    *
    * @param data  $data
    * @param theme $theme
    *
    * @usecase def threeDimensional(data: CategoryDataset): MultiplePieChart = ???
    *   @inheritdoc
    */
  def threeDimensional[A: ToCategoryDataset](data: A)
    (implicit theme: ChartTheme = ChartTheme.Default): MultiplePieChart = {
    val dataset = ToCategoryDataset[A].convert(data)

    val plot = new MultiplePiePlot(dataset)
    plot.setDataExtractOrder(TableOrder.BY_COLUMN)
    plot.setBackgroundPaint(null)
    plot.setOutlineStroke(null)

    val piePlot = new PiePlot3D()

    val pieChart = new JFreeChart(piePlot)
    pieChart.setTitle(new TextTitle("dummy title for setting edge"))
    pieChart.getTitle.setPosition(RectangleEdge.BOTTOM)
    pieChart.removeLegend()
    plot.setPieChart(pieChart)

    MultiplePieChart(plot, title = "", legend = true)
  }

}
