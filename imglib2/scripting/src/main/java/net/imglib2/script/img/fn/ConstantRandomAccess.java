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

import net.imglib2.Point;
import net.imglib2.RandomAccess;

/** A {@link RandomAccess} that keeps track of its position
 * but always returns the same value, which is given in the constructor.
 *
 * @param <T> The type of the value.
 * @author Albert Cardona
 */
public class ConstantRandomAccess<T> extends Point implements RandomAccess<T>
{
	protected final T value;
	protected final long[] dimension, pos;

	public ConstantRandomAccess(final long[] dimension, final T value) {
		super(dimension.length);
		this.dimension = dimension;
		this.pos = new long[dimension.length];
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public ConstantRandomAccess<T> copy() {
		return new ConstantRandomAccess<T>(dimension, value);
	}

	@Override
	public ConstantRandomAccess<T> copyRandomAccess() {
		return copy();
	}
}
