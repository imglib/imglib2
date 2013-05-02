/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */
package net.imglib2.roi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

/**
 * A region of interest that is defined as a list of closed Beziér curves, combined via the
 * even/odd winding rule.
 *
 * TODO: re-implement it without using AWT, to allow use of ImgLib, say, on Android
 *
 * @author Johannes Schindelin
 */
public class GeneralPathRegionOfInterest extends
	AbstractIterableRegionOfInterest implements GeneralPathSegmentHandler
{

	private GeneralPath path;
	private long[] stripes; // one-dimensional array for efficiency; these are really { xStart, xEnd, y } triplets
	private int index;

	public GeneralPathRegionOfInterest() {
		super(2);
		path = new GeneralPath();
	}

	@Override
	public void moveTo(double x, double y) {
		path.moveTo(x, y);
	}

	@Override
	public void lineTo(double x, double y) {
		path.lineTo(x, y);
	}

	@Override
	public void quadTo(double x1, double y1, double x, double y) {
		path.quadTo(x1, y1, x, y);
	}

	@Override
	public void cubicTo(double x1, double y1, double x2, double y2, double x, double y) {
		path.curveTo(x1, y1, x2, y2, x, y);
	}

	@Override
	public void close() {
		path.closePath();
	}

	public void reset() {
		path.reset();
	}

	// TODO: remove
	public void setGeneralPath(final GeneralPath path) {
		this.path = path;
		this.stripes = null;
	}

	// TODO: remove
	public GeneralPath getGeneralPath() {
		return path;
	}

	// TODO: use an Interval
	public void iteratePath(final GeneralPathSegmentHandler handler) {
		final double[] coords = new double[6];
		for (final PathIterator iterator = path.getPathIterator(null); !iterator.isDone(); iterator.next()) {
			int type = iterator.currentSegment(coords);
			switch (type) {
				case PathIterator.SEG_MOVETO:
					handler.moveTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_LINETO:
					handler.lineTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_QUADTO:
					handler.quadTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					handler.cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
					break;
				case PathIterator.SEG_CLOSE:
					handler.close();
					break;
				default:
					throw new RuntimeException("Unsupported segment type: " + type);
			}
		}
	}

	@Override
	protected boolean nextRaster(long[] position, long[] end) {
		ensureStripes();
		if (index >= stripes.length) {
			index = 0;
			return false;
		}
		position[0] = stripes[index];
		end[0] = stripes[index + 1];
		position[1] = end[1] = stripes[index + 2];
		index += 3;
		return true;
	}

	@Override
	public boolean contains(double[] position) {
		return path.contains(position[0], position[1]);
	}

	private void ensureStripes() {
		if (stripes != null) return;

		Rectangle2D bounds = path.getBounds2D();
		int left = (int)Math.floor(bounds.getMinX());
		int top = (int)Math.floor(bounds.getMinY());
		int width = (int)(Math.ceil(bounds.getMaxX()) - left);
		int height = (int)(Math.ceil(bounds.getMaxY()) - top);

		byte[] pixels = new byte[width * height];
		final ColorModel colorModel = new IndexColorModel(1, 2, new byte[] { 0, 1 }, new byte[] { 0, 1 }, new byte[] { 0, 1 });
		final SampleModel sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height, 8);
		final DataBuffer dataBuffer = new DataBufferByte(pixels, width * height);
		final WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
		final BufferedImage image = new BufferedImage(colorModel, raster, false, null );
		final GeneralPath transformed = new GeneralPath(path);
		transformed.transform(AffineTransform.getTranslateInstance(-bounds.getMinX(), -bounds.getMinY()));
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(0));
		g2d.fill(transformed);

		long[] stripes = new long[3 * width * height / 2]; // avoid re-allocation
		int index = 0;
		for (int y = 0; y < height; y++) {
			long start = -1;
			for (int x = 0; x < width; x++) {
				boolean inside = pixels[x + width * y] != 0;
				if (start < 0) {
					if (inside) {
						start = x;
						stripes[index] = x + left;
						stripes[index + 2] = y + top;
					}
				} else if (!inside) {
					start = -1;
					stripes[index + 1] = x + left;
					index += 3;
				}
			}
			if (start >= 0) {
				start = -1;
				stripes[index + 1] = width + left;
				index += 3;
			}
		}

		this.stripes = new long[index];
		System.arraycopy(stripes, 0, this.stripes, 0, index);
		this.index = 0;
	}

	@Override
	public void move(double displacement, int d) {
		if (d != 0 && d != 1)
			throw new IllegalArgumentException("Cannot move 2D ROI in dimension " + d);
		AffineTransform transform = AffineTransform.getTranslateInstance(d == 0 ? displacement : 0, d == 1 ? displacement : 0);
		path.transform(transform);
	}

	@Override
	public void move(double[] displacement) {
		if (displacement.length != 2)
			throw new IllegalArgumentException("Cannot move 2D ROI in " + displacement.length + " dimensions");
		AffineTransform transform = AffineTransform.getTranslateInstance(displacement[0], displacement[1]);
		path.transform(transform);
	}

}
