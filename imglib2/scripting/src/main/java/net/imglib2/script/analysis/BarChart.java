package net.imglib2.script.analysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Collection;
import java.util.Map;

import javax.swing.JFrame;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChart extends JFrame
{
	private static final long serialVersionUID = 7347559630675867104L;

	private final JFreeChart chart;

	public BarChart(final Collection<? extends Number> data) {
		this(data, "Bar chart", "", "");
	}

	public BarChart(final Collection<? extends Number> data, final String title,
			final String xLabel, final String yLabel) {
		super(title);
		this.chart = createChart(data, title, xLabel, yLabel);
		this.getContentPane().add(new ChartPanel(chart));
		this.pack();
		this.setVisible(true);
	}

	public BarChart(final Map<? extends Number, ? extends Number> data) {
		this(data, "Bar chart", "", "");
	}

	public BarChart(final Map<? extends Number, ? extends Number> data, final String title,
			final String xLabel, final String yLabel) {
		super(title);
		this.chart = createChart(data, title, xLabel, yLabel);
		this.getContentPane().add(new ChartPanel(chart));
		this.pack();
		this.setVisible(true);
	}

	public JFreeChart getChart() {
		return chart;
	}

	@SuppressWarnings("boxing")
	static private final JFreeChart createChart(final Collection<? extends Number> data, final String title,
			final String xLabel, final String yLabel) {
		DefaultCategoryDataset dcd = new DefaultCategoryDataset();
		int k = 1;
		for (final Number value : data) {
			dcd.addValue(value, "", k++);
		}
		boolean legend = false;
		boolean tooltips = true;
		boolean urls = false;
		JFreeChart chart = ChartFactory.createBarChart(title, xLabel, yLabel, dcd,
				PlotOrientation.VERTICAL, legend, tooltips, urls);
		setBarTheme(chart);
		return chart;
	}
	
	@SuppressWarnings("rawtypes")
	static private final JFreeChart createChart(final Map<? extends Number, ? extends Number> data, final String title,
			final String xLabel, final String yLabel) {
		DefaultCategoryDataset dcd = new DefaultCategoryDataset();
		for (final Map.Entry<? extends Number, ? extends Number> e : data.entrySet()) {
			dcd.addValue(e.getValue(), "", (Comparable) e.getKey());
		}
		boolean legend = false;
		boolean tooltips = true;
		boolean urls = false;
		JFreeChart chart = ChartFactory.createBarChart(title, xLabel, yLabel, dcd,
				PlotOrientation.VERTICAL, legend, tooltips, urls);
		setBarTheme(chart);
		return chart;
	}
	

	static private final void setBarTheme(final JFreeChart chart) {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesOutlinePaint(0, Color.lightGray);
		renderer.setShadowVisible(false);
		renderer.setDrawBarOutline(true);
		setBackgroundDefault(chart);
	}

 	static private void setBackgroundDefault(final JFreeChart chart) {
		BasicStroke gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2.0f, 1.0f}, 0.0f);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setRangeGridlineStroke(gridStroke);
		plot.setDomainGridlineStroke(gridStroke);
		plot.setBackgroundPaint(new Color(235, 235, 235));
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setOutlineVisible(false);
		plot.getDomainAxis().setAxisLineVisible(false);
		plot.getRangeAxis().setAxisLineVisible(false);
		plot.getDomainAxis().setLabelPaint(Color.gray);
		plot.getRangeAxis().setLabelPaint(Color.gray);
		plot.getDomainAxis().setTickLabelPaint(Color.gray);
		plot.getRangeAxis().setTickLabelPaint(Color.gray);
		chart.getTitle().setPaint(Color.gray);
	}
 
 	public Img<ARGBType> asImage() {
		return ChartUtils.asImage(chart);
	}
}
