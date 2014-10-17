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
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;

public class EllipseNeighborhood<T> extends AbstractNeighborhood<T> {
	
	/*
	 * CONSTRUCTORS
	 */
	
	public EllipseNeighborhood(final RandomAccessibleInterval<T> source, final long[] center, final long[] radiuses, final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds) {
		super(source.numDimensions(), outOfBounds);
		setSpan(radiuses);
		setPosition(center);
		updateSource(source);
	}

	public EllipseNeighborhood(final RandomAccessibleInterval<T> source, final long[] center, final long[] radiuses) {
		this(source, center, radiuses, new OutOfBoundsMirrorFactory<T, RandomAccessibleInterval<T>>(Boundary.DOUBLE));
	}
	
	/*
	 * METHODS
	 */
	
	
	@Override
	public long size() {
		long pixel_count = 0;
		final int[] local_rxs = new int [ (int) (span[1]  +  1) ];
		int local_rx;

		Utils.getXYEllipseBounds((int) span[0], (int) span[1], local_rxs);
		local_rx = local_rxs[0]; // middle line
		pixel_count += 2 * local_rx + 1;
		for (int i = 1; i <= span[1]; i++) {
			local_rx = local_rxs[i];
			pixel_count += 2 * (2 * local_rx + 1); // Twice because we mirror
		}

		return pixel_count;	
	}

	@Override
	public EllipseCursor<T> cursor() {
		return new EllipseCursor<T>(this);
	}

	@Override
	public EllipseCursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public EllipseCursor<T> iterator() {
		return cursor();
	}

	@Override
	public EllipseNeighborhood<T> copy() {
		return new EllipseNeighborhood<T>(source, center, span, outOfBounds);
	}

}
