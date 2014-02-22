/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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

package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;

/**
 * A {@link Positionable} {@link IterableInterval} that serves as a local
 * neighborhood, e.g. in filtering operation.
 * <p>
 * This particular class implements a movable nD rectangle, defined by a
 * <code>span long[]</code> array. The <code>span</code> array is such that the
 * size of the rectangle in dimension <code>d</code> will be
 * <code>2 x span[d] + 1</code>. {@link Cursor}s can be instantiated from this
 * neighborhood, that will iterate through the rectangle in raster order.
 */
public class RectangleNeighborhoodGPL<T>
		extends AbstractNeighborhood<T> {

	/*
	 * CONSTRUCTOR
	 */

	/**
	 * Instantiate a new rectangular neighborhood, on the given image, with the
	 * given factory to return out of bounds values.
	 * <p>
	 * The rectangle is initiated centered on the first pixel of the source, and
	 * span a single pixel.
	 */
	public RectangleNeighborhoodGPL(final int numDims,
			final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds) {
		super(numDims, outOfBounds);
	}

	/**
	 * Instantiate a new rectangular neighborhood, on the given image, with the
	 * given factory to return out of bounds values.
	 * <p>
	 * The rectangle is initiated centered on the first pixel of the source, and
	 * span a single pixel.
	 */
	public RectangleNeighborhoodGPL(final RandomAccessibleInterval<T> source,
			final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds) {
		super(source.numDimensions(), outOfBounds);
		updateSource(source);
	}

	/**
	 * Instantiate a rectangular neighborhood, with a
	 * {@link OutOfBoundsPeriodicFactory}
	 * 
	 * @param source
	 */
	public RectangleNeighborhoodGPL(RandomAccessibleInterval<T> source) {
		this(source.numDimensions(), new OutOfBoundsPeriodicFactory<T, RandomAccessibleInterval<T>>());
		updateSource(source);
	}

	/*
	 * SPECIFIC METHODS
	 */

	/**
	 * @return <b>the</b> cursor over this neighborhood.
	 */
	@Override
	public RectangleCursor<T> cursor() {
		RectangleCursor<T> cursor = new RectangleCursor<T>(this);
		cursor.reset();
		return cursor;
	}

	/**
	 * @return <b>the</b> cursor over this neighborhood.
	 */
	@Override
	public RectangleCursor<T> localizingCursor() {
		return cursor();
	}

	/**
	 * @return <b>the</b> cursor over this neighborhood.
	 */
	@Override
	public RectangleCursor<T> iterator() {
		return cursor();
	}

	@Override
	public long size() {
		long size = 1;
		for (int d = 0; d < span.length; d++) {
			size *= (2 * span[d] + 1);
		}
		return size;
	}

	@Override
	public AbstractNeighborhood<T> copy() {
		if (source != null)
			return new RectangleNeighborhoodGPL<T>(source, outOfBounds);
		return new RectangleNeighborhoodGPL<T>(n, outOfBounds);
	}

}
