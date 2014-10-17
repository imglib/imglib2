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

import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * A {@link Positionable} {@link IterableInterval} that serves as a local
 * neighborhood, e.g. in filtering operation.
 * <p>
 * This particular class implements a movable nD domain, defined by a
 * <code>span long[]</code> array. The <code>span</code> array is such that the
 * bounding box of the neighborhood in dimension <code>d</code> will be
 * <code>2 x span[d] + 1</code>.
 */
public abstract class AbstractNeighborhood<T>
		implements Positionable, IterableInterval<T> {

	/** The pixel coordinates of the center of this regions. */
	protected final long[] center;
	/**
	 * The span of this neighborhood, such that the size of the bounding box in
	 * dimension <code>d</code> will be <code>2 x span[d] + 1</code>.
	 */
	protected final long[] span;
	protected ExtendedRandomAccessibleInterval<T, RandomAccessibleInterval<T>> extendedSource;
	protected RandomAccessibleInterval<T> source;
	protected OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds;
	protected int n;

	/*
	 * CONSTRUCTOR
	 */

	public AbstractNeighborhood(final int numDims,
			final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds) {
		this.n = numDims;
		this.outOfBounds = outOfBounds;
		this.center = new long[numDims];
		this.span = new long[numDims];
	}

	/*
	 * METHODS
	 */

	/**
	 * Set the span of this neighborhood.
	 * <p>
	 * The neighborhood span is such that the size of the neighborhood in
	 * dimension <code>d</code> will be <code>2 x span[d] + 1</code>.
	 * 
	 * @param span
	 *            this array content will be copied to the neighborhood internal
	 *            field.
	 */
	public void setSpan(long[] span) {
		for (int d = 0; d < span.length; d++) {
			this.span[d] = span[d];
		}
	}

	@Override
	public int numDimensions() {
		return n;
	}

	@Override
	public void fwd(int d) {
		center[d]++;
	}

	@Override
	public void bck(int d) {
		center[d]--;
	}

	@Override
	public void move(int distance, int d) {
		center[d] = center[d] + distance;
	}

	@Override
	public void move(long distance, int d) {
		center[d] = center[d] + distance;
	}

	@Override
	public void move(Localizable localizable) {
		for (int d = 0; d < center.length; d++) {
			center[d] += localizable.getLongPosition(d);
		}
	}

	@Override
	public void move(int[] distance) {
		for (int d = 0; d < center.length; d++) {
			center[d] += distance[d];
		}
	}

	@Override
	public void move(long[] distance) {
		for (int d = 0; d < center.length; d++) {
			center[d] += distance[d];
		}
	}

	@Override
	public void setPosition(Localizable localizable) {
		for (int d = 0; d < center.length; d++) {
			center[d] = localizable.getLongPosition(d);
		}
	}

	@Override
	public void setPosition(int[] position) {
		for (int d = 0; d < center.length; d++) {
			center[d] = position[d];
		}
	}

	@Override
	public void setPosition(long[] position) {
		for (int d = 0; d < center.length; d++) {
			center[d] = position[d];
		}
	}

	@Override
	public void setPosition(int position, int d) {
		center[d] = position;
	}

	@Override
	public void setPosition(long position, int d) {
		center[d] = position;
	}

	/**
	 * Return the element at the top-left corner of this nD neighborhood.
	 */
	@Override
	public T firstElement() {
		RandomAccess<T> ra = source.randomAccess();
		for (int d = 0; d < span.length; d++) {
			ra.setPosition(center[d] - span[d], d);
		}
		return ra.get();
	}

	@Override
	public Object iterationOrder() {
		return this;
	}

	@Override
	@Deprecated
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		if (!(f instanceof RectangleNeighborhoodGPL)) {
			return false;
		}
		RectangleNeighborhoodGPL<?> otherRectangle = (RectangleNeighborhoodGPL<?>) f;
		if (otherRectangle.numDimensions() != numDimensions()) {
			return false;
		}
		for (int d = 0; d < span.length; d++) {
			if (otherRectangle.span[d] != span[d]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public double realMin(int d) {
		return center[d] - span[d];
	}

	@Override
	public void realMin(double[] min) {
		for (int d = 0; d < center.length; d++) {
			min[d] = center[d] - span[d];
		}

	}

	@Override
	public void realMin(RealPositionable min) {
		for (int d = 0; d < center.length; d++) {
			min.setPosition(center[d] - span[d], d);
		}
	}

	@Override
	public double realMax(int d) {
		return center[d] + span[d];
	}

	@Override
	public void realMax(double[] max) {
		for (int d = 0; d < center.length; d++) {
			max[d] = center[d] + span[d];
		}
	}

	@Override
	public void realMax(RealPositionable max) {
		for (int d = 0; d < center.length; d++) {
			max.setPosition(center[d] + span[d], d);
		}
	}

	@Override
	public long min(int d) {
		return center[d] - span[d];
	}

	@Override
	public void min(long[] min) {
		for (int d = 0; d < center.length; d++) {
			min[d] = center[d] - span[d];
		}
	}

	@Override
	public void min(Positionable min) {
		for (int d = 0; d < center.length; d++) {
			min.setPosition(center[d] - span[d], d);
		}
	}

	@Override
	public long max(int d) {
		return center[d] + span[d];
	}

	@Override
	public void max(long[] max) {
		for (int d = 0; d < center.length; d++) {
			max[d] = center[d] + span[d];
		}
	}

	@Override
	public void max(Positionable max) {
		for (int d = 0; d < center.length; d++) {
			max.setPosition(center[d] + span[d], d);
		}
	}

	@Override
	public void dimensions(long[] dimensions) {
		for (int d = 0; d < span.length; d++) {
			dimensions[d] = 2 * span[d] + 1;
		}
	}

	@Override
	public long dimension(int d) {
		return (2 * span[d] + 1);
	}

	/**
	 * Updates the source.
	 */
	public void updateSource(RandomAccessibleInterval<T> source) {
		this.source = source;
		this.extendedSource = Views.extend(source, outOfBounds);
	}

	/**
	 * Copies the {@link AbstractNeighborhood}.
	 */
	public abstract AbstractNeighborhood<T> copy();

}
