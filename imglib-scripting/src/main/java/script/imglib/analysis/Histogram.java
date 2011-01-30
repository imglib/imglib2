package script.imglib.analysis;

import ij.IJ;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;
import script.imglib.algorithm.fn.AlgorithmUtil;

/** An histogram of the image (or an image computed from an IFunction)
 * between its minimum and maximum values,
 * with as many bins as desired (defaults to 256 bins).
 * 
 * The number of bins is then the {@link Histogram#size()},
 * and the value of each bin (each element in this {@link ArrayList})
 * is an integer, which is the count of voxels whose value falls within
 * the bin.
 */
public class Histogram<T extends RealType<T>> extends TreeMap<Double,Integer>
{
	private static final long serialVersionUID = 1L;

	private final Image<T> img;
	private final double min, max, increment;

	public Histogram(final Object fn) throws Exception {
		this(fn, 256);
	}

	public Histogram(final Object fn, final Number nBins) throws Exception {
		this(fn, nBins.intValue());
	}

	@SuppressWarnings("unchecked")
	public Histogram(final Object fn, final int nBins) throws Exception {
		this.img = AlgorithmUtil.wrap(fn);
		Display<T> display = img.getDisplay();
		display.setMinMax();
		this.min = display.getMin();
		this.max = display.getMax();
		this.increment = process(img, nBins, min, max);
	}

	@SuppressWarnings("unchecked")
	public Histogram(final Object fn, final int nBins, final double min, final double max) throws Exception {
		this.img = AlgorithmUtil.wrap(fn);
		this.min = min;
		this.max = max;
		this.increment = process(img, nBins, min, max);
	}

	private final double process(final Image<T> img, final int nBins, final double min, final double max) throws Exception {
		final double range = max - min;
		final double increment = range / nBins;
		final int[] bins = new int[nBins];
		//
		if (0.0 == range) {
			bins[0] = img.size();
		} else {
			final Cursor<T> c = img.createCursor();
			// zero-based:
			final int N = nBins -1;
			// Analyze the image
			while (c.hasNext()) {
				c.fwd();
				int v = (int)(((c.getType().getRealDouble() - min) / range) * N);
				if (v < 0) v = 0;
				else if (v > N) v = N;
				bins[v] += 1;
			}
			c.close();
		}
		// Put the contents of the bins into this ArrayList:
		for (int i=0; i<bins.length; i++) {
			this.put( min + i * increment, bins[i] );
		}
		
		return increment;
	}
	
	public double getMin() { return min; }
	public double getMax() { return max; }
	public double getIncrement() { return increment; }
	public Image<T> getImage() { return img; }

	public Image<RGBALegacyType> asImage() {
		return asImage(-1, -1);
	}

	public Image<RGBALegacyType> asImage(int width, int height) {
		ChartPanel panel = new ChartPanel(asChart(false));
		Dimension d = panel.getPreferredSize();
		if (-1 == width && -1 == height) {
			width = d.width;
			height = d.height;
			panel.setSize(d);
		} else {
			panel.setSize(width, height);
		}
		layoutComponent(panel);
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		if (!panel.isOpaque()){
			g.setColor(panel.getBackground() );
			g.fillRect(0, 0, width, height);
		}
		panel.paint(g);
		int[] pixels = new int[width * height];
		PixelGrabber pg = new PixelGrabber(bi, 0, 0, width, height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {}
		g.dispose();

		Array<RGBALegacyType, IntAccess> a = new Array<RGBALegacyType, IntAccess>(new ArrayContainerFactory(), new IntArray(pixels), new int[]{width, height}, 1);
		// create a Type that is linked to the container
		final RGBALegacyType linkedType = new RGBALegacyType( a );
		// pass it to the DirectAccessContainer
		a.setLinkedType( linkedType );

		return new Image<RGBALegacyType>(a, new RGBALegacyType());	
	}

	private static final void layoutComponent(final Component c) {
		synchronized (c.getTreeLock()) {
			c.doLayout();
			if (c instanceof Container) {
				for (Component child : ((Container)c).getComponents()) {
					layoutComponent(child);
				}
			}
		}
		c.validate();
	}

	public JFreeChart asChart() {
		return asChart(false);
	}

	/** Return the JFreeChart with this histogram, and as a side effect, show it in a JFrame
	 * that provides the means to edit the dimensions and also the plot properties via a popup menu. */
	public JFreeChart asChart(final boolean show) {
		double[] d = new double[this.size()];
		int i = 0;
		IJ.log("d length is " + d.length);
		for (Number num : this.values()) d[i++] = num.doubleValue();
		HistogramDataset hd = new HistogramDataset();
		hd.setType(HistogramType.RELATIVE_FREQUENCY);
		String title = "Histogram";
		hd.addSeries(title, d, d.length);
		JFreeChart chart = ChartFactory.createHistogram(title, "", "", hd,
				PlotOrientation.VERTICAL, false, false, false);
		System.out.println("chart is: " + chart.getClass());
		setTheme(chart);
		if (show) {
			JFrame frame = new JFrame(title);
			frame.getContentPane().add(new ChartPanel(chart));
			frame.pack();
			frame.setVisible(true);
		}
		return chart;
	}
	
	static public final void setTheme(final JFreeChart chart) {
		XYPlot plot = (XYPlot) chart.getPlot();
		XYBarRenderer r = (XYBarRenderer) plot.getRenderer();
		StandardXYBarPainter bp = new StandardXYBarPainter();
		r.setBarPainter(bp);
		r.setSeriesOutlinePaint(0, Color.lightGray);
		r.setShadowVisible(false);
		r.setDrawBarOutline(false);
		setBackgroundDefault(chart);
	}

	static private final void setBackgroundDefault(final JFreeChart chart) {
		BasicStroke gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2.0f, 1.0f}, 0.0f);
		XYPlot plot = (XYPlot) chart.getPlot();
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
}