/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.view.Views;

public class Localizables
{
	/**
	 * @deprecated
	 * Use {@link Localizable#positionAsLongArray}.
	 */
	@Deprecated
	public static long[] asLongArray( final Localizable localizable )
	{
		final long[] result = new long[ localizable.numDimensions() ];
		localizable.localize( result );
		return result;
	}

	/**
	 * Create an n-dimensional {@link RandomAccessible} whose value domain is
	 * its source domain.
	 *
	 * @param n
	 * @return
	 */
	public static RandomAccessible< Localizable > randomAccessible( final int n )
	{
		return new LocationRandomAccessible( n );
	}

	public static RandomAccessibleInterval< Localizable > randomAccessibleInterval( final Interval interval )
	{
		return Views.interval( randomAccessible( interval.numDimensions() ), interval );
	}

	/**
	 * Create an n-dimensional {@link RealRandomAccessible} whose value domain is
	 * its source domain.
	 *
	 * @param n
	 * @return
	 */
	public static RealRandomAccessible< RealLocalizable > realRandomAccessible( final int n )
	{
		return new RealLocationRealRandomAccessible( n );
	}

	/**
	 * Return true if both {@link Localizable} refer to the same position.
	 */
	public static boolean equals( final Localizable a, final Localizable b )
	{
		final int n = a.numDimensions();
		if ( n != b.numDimensions() )
			return false;

		for ( int d = 0; d < n; d++ )
			if ( a.getLongPosition( d ) != b.getLongPosition( d ) )
				return false;

		return true;
	}

	/**
	 * Return true if the two {@link RealLocalizable}s refer to the same
	 * position.
	 */
	public static boolean equals( final RealLocalizable a, final RealLocalizable b )
	{
		final int n = a.numDimensions();

		if ( n != b.numDimensions() )
			return false;

		for ( int d = 0; d < n; d++ )
			if ( a.getDoublePosition( d ) != b.getDoublePosition( d ) )
				return false;

		return true;
	}

	/**
	 * Return true if the two {@link RealLocalizable}s refer to the same
	 * position up to a given tolerance.
	 */
	public static boolean equals( final RealLocalizable a, final RealLocalizable b, final double tolerance )
	{
		final int n = a.numDimensions();

		if ( n != b.numDimensions() )
			return false;

		for ( int d = 0; d < n; d++ )
			if ( Math.abs( a.getDoublePosition( d ) - b.getDoublePosition( d ) ) > tolerance )
				return false;

		return true;
	}

	/**
	 * Return the current position as string.
	 */
	public static String toString( final Localizable value )
	{
		final StringBuilder sb = new StringBuilder();
		char c = '(';
		for ( int i = 0; i < value.numDimensions(); i++ )
		{
			sb.append( c );
			sb.append( value.getLongPosition( i ) );
			c = ',';
		}
		sb.append( ")" );
		return sb.toString();
	}

	/**
	 * Return the current position as string.
	 */
	public static String toString( final RealLocalizable value )
	{
		final StringBuilder sb = new StringBuilder();
		char c = '(';
		for ( int i = 0; i < value.numDimensions(); i++ )
		{
			sb.append( c );
			sb.append( value.getDoublePosition( i ) );
			c = ',';
		}
		sb.append( ")" );
		return sb.toString();
	}

	// -- Helper classes --

	private static class LocationRandomAccessible extends AbstractEuclideanSpace implements RandomAccessible< Localizable >
	{
		public LocationRandomAccessible( final int n )
		{
			super( n );
		}

		@Override
		public RandomAccess< Localizable > randomAccess()
		{
			return new LocationRandomAccess( n );
		}

		@Override
		public RandomAccess< Localizable > randomAccess( final Interval interval )
		{
			return randomAccess();
		}
	}

	/**
	 * A RandomAccess that returns its current position as value.
	 */
	private static class LocationRandomAccess extends Point implements RandomAccess< Localizable >
	{
		public LocationRandomAccess( final int n )
		{
			super( n );
		}

		public LocationRandomAccess( final Localizable initialPosition )
		{
			super( initialPosition );
		}

		@Override
		public Localizable get()
		{
			return this;
		}

		@Override
		public RandomAccess< Localizable > copy()
		{
			return new LocationRandomAccess( this );
		}
	}

	private static class RealLocationRealRandomAccessible extends AbstractEuclideanSpace implements RealRandomAccessible< RealLocalizable >
	{
		public RealLocationRealRandomAccessible( final int n )
		{
			super( n );
		}

		@Override
		public RealLocationRealRandomAccess realRandomAccess()
		{
			return new RealLocationRealRandomAccess( n );
		}

		@Override
		public RealLocationRealRandomAccess realRandomAccess( final RealInterval interval )
		{
			return realRandomAccess();
		}
	}

	/**
	 * A RandomAccess that returns its current position as value.
	 */
	private static class RealLocationRealRandomAccess extends RealPoint implements RealRandomAccess< RealLocalizable >
	{
		public RealLocationRealRandomAccess( final int n )
		{
			super( n );
		}

		public RealLocationRealRandomAccess( final RealLocalizable initialPosition )
		{
			super( initialPosition );
		}

		@Override
		public RealLocationRealRandomAccess get()
		{
			return this;
		}

		@Override
		public RealLocationRealRandomAccess copy()
		{
			return new RealLocationRealRandomAccess( this );
		}
	}
}
