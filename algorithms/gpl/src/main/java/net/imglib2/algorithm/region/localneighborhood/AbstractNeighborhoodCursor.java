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

import net.imglib2.Bounded;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBounds;

public abstract class AbstractNeighborhoodCursor<T> implements Cursor<T>,
		Bounded {

	protected AbstractNeighborhood<T> neighborhood;
	protected final OutOfBounds<T> ra;

	/*
	 * CONSTRUCTOR
	 */

	public AbstractNeighborhoodCursor(AbstractNeighborhood<T> neighborhood) {
		this.neighborhood = neighborhood;
		this.ra = neighborhood.extendedSource.randomAccess();
	}

	/*
	 * METHODS
	 */

	@Override
	public void localize(float[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(double[] position) {
		ra.localize(position);
	}

	@Override
	public float getFloatPosition(int d) {
		return ra.getFloatPosition(d);
	}

	@Override
	public double getDoublePosition(int d) {
		return ra.getDoublePosition(d);
	}

	@Override
	public int numDimensions() {
		return ra.numDimensions();
	}

	@Override
	public T get() {
		return ra.get();
	}

	/**
	 * This dummy method just calls {@link #fwd()} multiple times.
	 */
	@Override
	public void jumpFwd(long steps) {
		for (int i = 0; i < steps; i++) {
			fwd();
		}
	}

	@Override
	public T next() {
		fwd();
		return ra.get();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove() is not implemented for "+ getClass().getCanonicalName());
	}

	@Override
	public void localize(int[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(long[] position) {
		ra.localize(position);
	}

	@Override
	public int getIntPosition(int d) {
		return ra.getIntPosition(d);
	}

	@Override
	public long getLongPosition(int d) {
		return ra.getLongPosition(d);
	}

	@Override
	public boolean isOutOfBounds() {
		return ra.isOutOfBounds();
	}

}
