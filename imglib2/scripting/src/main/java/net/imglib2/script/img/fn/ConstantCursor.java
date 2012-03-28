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

package net.imglib2.script.img.fn;

import net.imglib2.AbstractCursor;
import net.imglib2.util.IntervalIndexer;

/**
 * A Cursor that always returns the same value.
 * 
 * @param <T> The type of the value.
 * @author Albert Cardona
 */
public class ConstantCursor<T> extends AbstractCursor<T>
{
	protected long index = -1;
	protected final long size;
	private final long[] tmp, dimension;
	private T value;
	
	public ConstantCursor(
			final long[] dimension,
			final T value) {
		super(dimension.length);
		this.dimension = dimension;
		this.tmp = new long[dimension.length];
		this.value = value;
		long s = 1;
		for (int i=0; i<dimension.length; ++i) s *= dimension[i];
		this.size = s;
	}

	@Override
	public void localize(long[] position) {
		IntervalIndexer.indexToPosition(index, dimension, position);
	}

	@Override
	public long getLongPosition(int d) {
		IntervalIndexer.indexToPosition(index, dimension, tmp);
		return tmp[d];
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public void fwd() {
		++index;
	}

	@Override
	public void reset() {
		index = -1;
	}

	@Override
	public boolean hasNext() {
		return index < size;
	}

	@Override
	public AbstractCursor<T> copy() {
		return new ConstantCursor<T>(dimension, value);
	}

	@Override
	public AbstractCursor<T> copyCursor() {
		return copy();
	}
}
