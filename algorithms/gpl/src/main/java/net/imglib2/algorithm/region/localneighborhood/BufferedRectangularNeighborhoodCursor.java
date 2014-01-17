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

import java.lang.reflect.Array;
import java.util.Arrays;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;

/**
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Benjamin Schmid
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author Christian Dietz <christian.dietz@uni-konstanz.de>
 */
public class BufferedRectangularNeighborhoodCursor<T extends Type<T>> extends
		AbstractNeighborhoodCursor<T> {
	private final long[] bufferElements;

	private final long[] min;

	private final long[] max;

	private final long[] bck;

	private final long[] currentPos;

	private final T[] buffer;

	private final int maxCount;

	private int bufferOffset;

	private int activeDim;

	private int bufferPtr;

	private int count;

	private final int n;

	@SuppressWarnings("unchecked")
	public BufferedRectangularNeighborhoodCursor(
			BufferedRectangularNeighborhood<T> neighborhood) {
		super(neighborhood);

		n = neighborhood.numDimensions();
		currentPos = neighborhood.center.clone();

		max = new long[n];
		min = new long[n];
		bufferElements = new long[n];
		bck = new long[n];

		Arrays.fill(bufferElements, 1);

		int tmp = 1;
		for (int d = 0; d < neighborhood.span.length; d++) {
			tmp *= (neighborhood.span[d] * 2) + 1;
			bck[d] = (-2 * neighborhood.span[d]);

			for (int dd = 0; dd < n; dd++)
				if (dd != d)
					bufferElements[d] *= (neighborhood.span[d] * 2) + 1;
		}

		T type = ra.get().createVariable();

		maxCount = tmp;
		buffer = (T[]) Array.newInstance(type.getClass(), maxCount);

		for (int t = 0; t < buffer.length; t++) {
			buffer[t] = type.copy();
		}

	}

	protected BufferedRectangularNeighborhoodCursor(
			final BufferedRectangularNeighborhoodCursor<T> c) {
		this(
				(BufferedRectangularNeighborhood<T>) c.neighborhood);
	}

	@Override
	public T get() {
		return buffer[bufferPtr];
	}

	@Override
	public T next() {
		fwd();
		return buffer[bufferPtr];
	}

	@Override
	public void fwd() {

		if (++bufferPtr >= buffer.length)
			bufferPtr = 0;

		if (activeDim < 0 || count >= buffer.length - bufferElements[activeDim]) {
			for (int d = n - 1; d > -1; d--) {
				if (d == activeDim)
					continue;

				if (ra.getLongPosition(d) < max[d]) {
					ra.fwd(d);
					break;
				}
				ra.move(bck[d], d);
			}
			buffer[bufferPtr].set(ra.get());
		}

		count++;
	}

	@Override
	public void reset() {
		activeDim = -1;

		for (int d = 0; d < neighborhood.center.length; d++) {
			long tmp = currentPos[d];
			currentPos[d] = neighborhood.center[d];

			min[d] = neighborhood.center[d] - neighborhood.span[d];
			max[d] = neighborhood.center[d] + neighborhood.span[d];

			if (neighborhood.center[d] - tmp != 0) {
				if (activeDim != -1) {
					activeDim = -1;
					break;
				}
				activeDim = d;
			}
		}

		if (activeDim >= 0
				&& bufferOffset + bufferElements[activeDim] < buffer.length)
			bufferOffset += bufferElements[activeDim];
		else
			bufferOffset = 0;

		bufferPtr = bufferOffset - 1;

		for (int d = 0; d < n; d++) {
			if (d == activeDim)
				ra.setPosition(max[d], d);
			else
				ra.setPosition(min[d], d);
		}

		ra.bck(ra.numDimensions() - 1);
		count = 0;
	}

	@Override
	public boolean hasNext() {
		return count < maxCount;
	}

	@Override
	public float getFloatPosition(final int d) {
		return ra.getFloatPosition(d);
	}

	@Override
	public double getDoublePosition(final int d) {
		return ra.getDoublePosition(d);
	}

	@Override
	public int getIntPosition(final int d) {
		return ra.getIntPosition(d);
	}

	@Override
	public long getLongPosition(final int d) {
		return ra.getLongPosition(d);
	}

	@Override
	public void localize(final long[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(final float[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(final double[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(final int[] position) {
		ra.localize(position);
	}

	@Override
	public BufferedRectangularNeighborhoodCursor<T> copy() {
		return new BufferedRectangularNeighborhoodCursor<T>(this);
	}

	@Override
	public BufferedRectangularNeighborhoodCursor<T> copyCursor() {
		return copy();
	}
}
