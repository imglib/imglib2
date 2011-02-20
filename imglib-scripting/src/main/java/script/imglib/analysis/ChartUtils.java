package script.imglib.analysis;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.ARGBType;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class ChartUtils {

	public static final Img<ARGBType> asImage(final JFreeChart chart) {
		return asImage(chart, -1, -1);
	}

	public static final Img<ARGBType> asImage(final JFreeChart chart, int width, int height) {
		ChartPanel panel = new ChartPanel(chart);
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
		
		Array<ARGBType, IntAccess> a = new Array<ARGBType, IntAccess>(new IntArray(pixels), new long[]{width, height}, 1);
		
		// create a Type that is linked to the container
		final ARGBType linkedType = new ARGBType( a );
		// pass it to the DirectAccessContainer
		a.setLinkedType( linkedType );

		return a;
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
}