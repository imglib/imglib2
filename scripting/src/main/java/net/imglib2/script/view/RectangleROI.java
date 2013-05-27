/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.script.view;

import java.util.List;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.NumericType;

/** Create a Rectangular ROI on the x,y plane, with other dimensions left the same.
 * 
 *
 * @author Albert Cardona
 */
public class RectangleROI<R extends NumericType<R>> extends ROI<R>
{
	/**
	 * 
	 * @param img
	 * @param bounds A list containing the bounds values x,y,width,height.
	 */
	public RectangleROI(final RandomAccessibleInterval<R> img, final List<Number> bounds) {
		this(img, bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3));
	}
	
	/**
	 * 
	 * @param img
	 * @param bounds An array containing the bounds values x,y,width,height.
	 */
	@SuppressWarnings("boxing")
	public RectangleROI(final RandomAccessibleInterval<R> img, final long[] bounds) {
		this(img, bounds[0], bounds[1], bounds[2], bounds[3]);
	}

	public RectangleROI(final RandomAccessibleInterval<R> img,
			final Number x, final Number y, final Number width, final Number height) {
		super(img,
				toMinArray(img, x.intValue(), y.intValue()),
				toMaxArray(img, width.intValue(), height.intValue()));
	}

	static private final long[] toMinArray(final RandomAccessibleInterval<?> img, final int p0, final int p1) {
		final long[] pos = new long[img.numDimensions()];
		pos[0] = p0;
		pos[1] = p1;
		return pos;
	}
	
	static private final long[] toMaxArray(final RandomAccessibleInterval<?> img, final int p0, final int p1) {
		final long[] pos = new long[img.numDimensions()];
		pos[0] = p0;
		pos[1] = p1;
		for (int i=2; i<pos.length; ++i) {
			pos[i] = img.dimension(i);
		}
		return pos;
	}
}
