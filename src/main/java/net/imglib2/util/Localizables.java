/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.util;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;
import net.imglib2.view.Views;

public class Localizables
{

	public static long[] asLongArray( Localizable localizable ) {
		long[] result = new long[ localizable.numDimensions() ];
		localizable.localize( result );
		return result;
	}

	public static RandomAccessible< Localizable > randomAccessible( int n ) {
		return new LocationRandomAccessible( n );
	}

	public static RandomAccessibleInterval< Localizable > randomAccessibleInterval( Interval interval ) {
		return Views.interval( randomAccessible( interval.numDimensions() ), interval );
	}

	// -- Helper classes --

	private static class LocationRandomAccessible extends AbstractEuclideanSpace implements RandomAccessible< Localizable >
	{
		public LocationRandomAccessible( int n )
		{
			super( n );
		}

		@Override public RandomAccess< Localizable > randomAccess()
		{
			return new LocationRandomAccess( n );
		}

		@Override public RandomAccess< Localizable > randomAccess( Interval interval )
		{
			return randomAccess();
		}
	}

	/**
	 * A RandomAccess that returns it's current position as value.
	 */
	private static class LocationRandomAccess extends Point implements RandomAccess< Localizable >
	{
		public LocationRandomAccess( int n )
		{
			super( n );
		}

		public LocationRandomAccess( Localizable initialPosition )
		{
			super( initialPosition );
		}

		@Override public RandomAccess< Localizable > copyRandomAccess()
		{
			return new LocationRandomAccess( this );
		}

		@Override public Localizable get()
		{
			return this;
		}

		@Override public Sampler< Localizable > copy()
		{
			return copyRandomAccess();
		}
	}
}
