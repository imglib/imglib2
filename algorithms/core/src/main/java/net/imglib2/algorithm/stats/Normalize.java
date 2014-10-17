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
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.algorithm.stats;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.NumericType;

public class Normalize
{
	/**
	 * Normalize values of an {@link IterableInterval} to the range [min, max].
	 * 
	 * @param iterable
	 *            the interval to be normalized.
	 * @param min
	 *            target minimum value of the normalized interval.
	 * @param max
	 *            target maximum value of the normalized interval.
	 */
	public static < T extends NumericType< T > & Comparable< T > > void normalize( final IterableInterval< T > iterable, final T min, final T max )
	{
		final Cursor< T > cursor = iterable.cursor();
		final T currentMax = cursor.next().copy();
		final T currentMin = currentMax.copy();
		for ( final T t : iterable )
			if ( t.compareTo( currentMax ) > 0 )
				currentMax.set( t );
			else if ( t.compareTo( currentMin ) < 0 )
				currentMin.set( t );

		final T scale = max.copy();
		scale.sub( min );
		final T currentScale = currentMax; // no need to currentMax.copy(). We
											// don't use currentMax after this.
		currentScale.sub( currentMin );

		for ( final T t : iterable )
		{
			t.sub( currentMin );
			t.mul( scale );
			t.div( currentScale );
			t.add( min );
		}
	}
}
