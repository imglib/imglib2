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

package net.imglib2.script.math.fn;

import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.list.ListImg;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.IterableRandomAccessibleInterval;

/**
 * TODO
 *
 */
public class Util
{
	/** Return an IFunction constructed from the {@param ob}.
	 * 
	 * @param ob May be any of {@link IterableRealInterval}, {@link IFunction} or {@link Number}.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static public final IFunction wrap(final Object ob) {
		if (ob instanceof IterableRealInterval<?>) return new ImageFunction((IterableRealInterval<? extends RealType<?>>)ob);
		if (ob instanceof IFunction) return (IFunction)ob;
		if (ob instanceof Number) return new NumberFunction((Number)ob);
		throw new IllegalArgumentException("Cannot compose a function with " + ob);
	}
	
	static public final long[] intervalDimensions(final RealInterval iri) {
		final long[] dim = new long[iri.numDimensions()];
		for (int d=0; d<dim.length; ++d)
			dim[d] = (long) (iri.realMax(d) - iri.realMin(d) + 1);
		return dim;
	}

	public static final long size(final IterableRealInterval<?> b) {
		long size = 1;
		for (int i=b.numDimensions() -1; i > -1; --i)
			size *= (b.realMax(i) - b.realMin(i) + 1);
		return size;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends RealType<T>> IterableRealInterval<T> flatIterable(final RealInterval ri) {
		// If it's any of the known classes that iterates flat, accept as is:
		if ( ArrayImg.class.isInstance( ri )
		  || ListImg.class.isInstance( ri )
		  || IterableRandomAccessibleInterval.class.isInstance(ri)) {
			return (IterableRealInterval<T>) ri;
		}
		// If it's a random accessible, then wrap:
		if ( ri instanceof RandomAccessibleInterval ) {
			return new IterableRandomAccessibleInterval<T>((RandomAccessibleInterval<T>)ri);
		}
		throw new IllegalArgumentException("Don't know how to flat-iterate image " + ri);
	}
}
