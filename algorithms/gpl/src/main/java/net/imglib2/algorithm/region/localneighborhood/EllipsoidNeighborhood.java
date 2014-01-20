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
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;

/**
 * This class implements a {@link IterableInterval} representing the volume of a
 * 3D ellipsoid.
 * <p>
 * The semi-radiuses of the ellipsoid are set by the first 3 elements of the
 * {@link AbstractNeighborhood#span} array. They are such that the bounding box
 * of the ellipsoid are <code>2 x span[d] + 1</code> for dimension
 * <code>d</code>.
 * <p>
 * The ellipsoid can be positioned anywhere in a nD image (n >= 3), but will
 * always by 3D. Consequently, only the first 3 elements of the
 * {@link AbstractNeighborhood#span} array are considered.
 * 
 * @see EllipsoidCursor
 * @author Jean-Yves Tinevez (jeanyves.tinevez@gmail.com) - 2012
 * 
 * @param <T>
 */
public class EllipsoidNeighborhood<T> extends AbstractNeighborhood<T> {

	/*
	 * CONSTRUCTORS
	 */

	public EllipsoidNeighborhood(final RandomAccessibleInterval<T> source, final long[] center, final long[] radiuses, final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds) {
		super(source.numDimensions(), outOfBounds);
		if (numDimensions() < 2) {
			throw new IllegalArgumentException(
					"[EllipsoidNeighborhood] source must be at least of dimension 3.");
		}
		setSpan(radiuses);
		setPosition(center);
		updateSource(source);
	}

	public EllipsoidNeighborhood(final RandomAccessibleInterval<T> source, final long[] center, final long[] radiuses) {
		this(source, center, radiuses, new OutOfBoundsPeriodicFactory<T, RandomAccessibleInterval<T>>());
	}

	/*
	 * METHODS
	 */

	@Override
	public long size() {
		long pixel_count = 0;
		final long nzplanes = span[2];

		final int smallAxisdim, largeAxisDim;
		if (span[1] < span[0]) {
			smallAxisdim = 1;
			largeAxisDim = 0;
		} else {
			smallAxisdim = 0;
			largeAxisDim = 1; // ydim is the large axis
		}

		// Instantiate it once, and with large size, so that we do not have to
		// instantiate every time we move in Z
		final int[] local_rys = new int[(int) (nzplanes + 1)];
		final int[] local_rxs = new int[(int) (span[largeAxisDim] + 1)];
		int local_ry, local_rx;

		// Get all XY circles radiuses
		Utils.getXYEllipseBounds((int) span[largeAxisDim], (int) span[2],
				local_rys);

		// Deal with plane Z = 0
		Utils.getXYEllipseBounds((int) span[smallAxisdim],
				(int) span[largeAxisDim], local_rxs);
		local_ry = local_rys[0];
		local_rx = local_rxs[0]; // middle line
		pixel_count += 2 * local_rx + 1;
		for (int i = 1; i <= local_ry; i++) {
			local_rx = local_rxs[i];
			pixel_count += 2 * (2 * local_rx + 1); // Twice because we mirror
		}

		// Deal with other planes
		for (int j = 1; j <= nzplanes; j++) {
			local_ry = local_rys[j];
			if (local_ry == 0)
				continue;

			Utils.getXYEllipseBounds(
					Math.round((float) local_ry * span[smallAxisdim]
							/ span[largeAxisDim]), local_ry, local_rxs);

			local_rx = local_rxs[0]; // middle line
			pixel_count += 2 * (2 * local_rx + 1); // twice we mirror in Z
			for (int i = 1; i <= local_ry; i++) {
				local_rx = local_rxs[i];
				pixel_count += 4 * (2 * local_rx + 1); // 4 times because we
														// mirror in Z and in Y
			}
		}
		return pixel_count;
	}

	@Override
	public EllipsoidCursor<T> cursor() {
		return new EllipsoidCursor<T>(this);
	}

	@Override
	public EllipsoidCursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public EllipsoidCursor<T> iterator() {
		return cursor();
	}

	@Override
	public EllipsoidNeighborhood<T> copy() {
		return new EllipsoidNeighborhood<T>(source, center, span, outOfBounds);
	}

}
