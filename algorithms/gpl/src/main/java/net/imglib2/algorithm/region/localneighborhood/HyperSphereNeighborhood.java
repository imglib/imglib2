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

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.outofbounds.OutOfBoundsFactory;

public class HyperSphereNeighborhood<T>
		extends AbstractNeighborhood<T> {

	private long radius;

	/*
	 * CONSTRUCTORS
	 */
	public HyperSphereNeighborhood(final RandomAccessibleInterval<T> source,
			final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds, final long radius) {
		super(source.numDimensions(), outOfBounds);
		this.radius = radius;
	}

	/*
	 * CONSTRUCTORS
	 */
	public HyperSphereNeighborhood(final int numDims,
			final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds, final long radius) {
		super(numDims, outOfBounds);
		this.radius = radius;
	}

	@Override
	public HyperSphereCursor<T> cursor() {
		return new HyperSphereCursor<T>(extendedSource, center, radius);
	}

	@Override
	public HyperSphereCursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public HyperSphereCursor<T> iterator() {
		return cursor();
	}

	@Override
	public long size() {
		return computeSize();
	}

	/**
	 * Compute the number of elements for iteration
	 */
	protected long computeSize() {
		final HyperSphereCursor<T> cursor = new HyperSphereCursor<T>(source,
				this.center, radius);

		// "compute number of pixels"
		long size = 0;
		while (cursor.hasNext()) {
			cursor.fwd();
			++size;
		}

		return size;
	}

	@Override
	public AbstractNeighborhood<T> copy() {
		if (source != null)
			return new HyperSphereNeighborhood<T>(source, outOfBounds,
					radius);
		return new HyperSphereNeighborhood<T>(n, outOfBounds, radius);
	}

}
