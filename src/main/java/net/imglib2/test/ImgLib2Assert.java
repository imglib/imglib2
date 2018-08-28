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

package net.imglib2.test;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.operators.ValueEquals;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.BiPredicate;

public class ImgLib2Assert
{
	private ImgLib2Assert()
	{
		// prevent from instantiation
	}

	/**
	 * Throws an AssertionError, if the content or intervals of the two images differ.
	 * Comparision is done pixel wise using {@link ValueEquals#valueEquals(Object)}.
	 */
	public static < A extends ValueEquals< B >, B >
	void assertImageEquals( final RandomAccessibleInterval< ? extends A > actual, final RandomAccessibleInterval< ? extends B > expected )
	{
		assertImageEquals( actual, expected, ValueEquals::valueEquals );
	}

	/**
	 * Throws an AssertionError, if the content or intervals of the two images differ.
	 * Comparision is done pixel wise. Two pixels are considered equal, if the values
	 * returned by {@link RealType#getRealDouble()} differ by less than "tolerance".
	 */
	public static void assertImageEqualsRealType( final RandomAccessibleInterval< ? extends RealType< ? > > actual, final RandomAccessibleInterval< ? extends RealType< ? > > expected, double tolerance )
	{
		assertImageEquals( actual, expected, ( a, e ) -> Math.abs( a.getRealDouble() - e.getRealDouble() ) <= tolerance );
	}

	/**
	 * Throws an AssertionError, if the content or intervals of the two images differ.
	 * Comparision is done pixel wise. Two pixels are considered equal, if the values
	 * returned by {@link IntegerType#getIntegerLong()} are equal.
	 */
	public static void assertImageEqualsIntegerType( final RandomAccessibleInterval< ? extends IntegerType< ? > > actual, final RandomAccessibleInterval< ? extends IntegerType< ? > > expected )
	{
		assertImageEquals( actual, expected, ( a, e ) -> a.getIntegerLong() == e.getIntegerLong() );
	}

	/**
	 * Throws an AssertionError, if the content or intervals of the two images differ.
	 * Comparision is done pixel wise. Two pixels are considered equal, if the give
	 * predicate returns true.
	 */
	public static < A, B >
	void assertImageEquals( final RandomAccessibleInterval< ? extends A > a, final RandomAccessibleInterval< ? extends B > b, BiPredicate< A, B > equals )
	{
		assertIntervalEquals( a, b );
		IntervalView< ? extends Pair< ? extends A, ? extends B > > pairs = Views.interval( Views.pair( a, b ), b );
		Cursor< ? extends Pair< ? extends A, ? extends B > > cursor = pairs.cursor();
		while ( cursor.hasNext() )
		{
			Pair< ? extends A, ? extends B > p = cursor.next();
			if ( !equals.test( p.getA(), p.getB() ) )
				fail( "Pixel values differ on coordinate " +
						positionToString( cursor ) + ", expected: "
						+ p.getA() + " actual: " + p.getB() );
		}
	}

	/**
	 * Throws an AssertionError, if the two Intervals differ.
	 */
	public static void assertIntervalEquals( Interval a, Interval b )
	{
		if ( !Intervals.equals( a, b ) )
			fail( "Intervals are different, expected: " + intervalToString( a ) + ", actual: " + intervalToString( b ) );
	}

	// -- Helper methods --

	private static String positionToString( Localizable localizable )
	{
		StringJoiner joiner = new StringJoiner( ", " );
		for ( int i = 0, n = localizable.numDimensions(); i < n; i++ )
			joiner.add( String.valueOf( localizable.getIntPosition( i ) ) );
		return "(" + joiner + ")";
	}

	static String intervalToString( Interval a )
	{
		return "{min=" + Arrays.toString( Intervals.minAsLongArray( a ) ) +
				", max=" + Arrays.toString( Intervals.maxAsLongArray( a ) ) + "}";
	}

	private static void fail( String message )
	{
		throw new AssertionError( message );
	}
}
