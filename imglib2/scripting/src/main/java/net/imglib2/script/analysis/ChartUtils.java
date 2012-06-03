/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.script.analysis;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.util.Fraction;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * TODO
 *
 */
public class ChartUtils {

	public static final Img<ARGBType> asImage(final JFreeChart chart) {
		return asImage(chart, -1, -1);
	}

	public static final Img<ARGBType> asImage(final JFreeChart chart, int width, int height) {
		final ChartPanel panel = new ChartPanel(chart);
		final Dimension d = panel.getPreferredSize();
		if (-1 == width && -1 == height) {
			width = d.width;
			height = d.height;
			panel.setSize(d);
		} else {
			panel.setSize(width, height);
		}
		layoutComponent(panel);
		final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = bi.createGraphics();
		if (!panel.isOpaque()){
			g.setColor(panel.getBackground() );
			g.fillRect(0, 0, width, height);
		}
		panel.paint(g);
		final int[] pixels = new int[width * height];
		final PixelGrabber pg = new PixelGrabber(bi, 0, 0, width, height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (final InterruptedException e) {}
		g.dispose();
		
		final ArrayImg<ARGBType, IntArray> a = new ArrayImg<ARGBType, IntArray>(new IntArray(pixels), new long[]{width, height}, new Fraction());
		
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
				for (final Component child : ((Container)c).getComponents()) {
					layoutComponent(child);
				}
			}
		}
		c.validate();
	}
}
