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

package net.imglib2.util;

import net.imglib2.Dimensions;
import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;

/**
 * Convenience methods for manipulating {@link Interval Intervals}.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Intervals
{
	/**
	 * Create a {@link FinalInterval} from a parameter list comprising minimum
	 * coordinates and size. For example, to create a 2D interval from (10, 10)
	 * to (20, 40) use createMinSize( 10, 10, 11, 31 ).
	 * 
	 * @param minsize
	 *            a list of <em>2*n</em> parameters to create a <em>n</em>
	 *            -dimensional interval. The first <em>n</em> parameters specify
	 *            the minimum of the interval, the next <em>n</em> parameters
	 *            specify the dimensions of the interval.
	 * @return interval with the specified boundaries
	 */
	public static FinalInterval createMinSize( final long... minsize )
	{
		return FinalInterval.createMinSize( minsize );
	}

	/**
	 * Create a {@link FinalInterval} from a parameter list comprising minimum
	 * and maximum coordinates. For example, to create a 2D interval from (10,
	 * 10) to (20, 40) use createMinMax( 10, 10, 20, 40 ).
	 * 
	 * @param minmax
	 *            a list of <em>2*n</em> parameters to create a <em>n</em>
	 *            -dimensional interval. The first <em>n</em> parameters specify
	 *            the minimum of the interval, the next <em>n</em> parameters
	 *            specify the maximum of the interval.
	 * @return interval with the specified boundaries
	 */
	public static FinalInterval createMinMax( final long... minmax )
	{
		return FinalInterval.createMinMax( minmax );
	}

	/**
	 * Create a {@link FinalRealInterval} from a parameter list comprising
	 * minimum coordinates and size. For example, to create a 2D interval from
	 * (10, 10) to (20, 40) use createMinSize( 10, 10, 11, 31 ).
	 * 
	 * @param minsize
	 *            a list of <em>2*n</em> parameters to create a <em>n</em>
	 *            -dimensional interval. The first <em>n</em> parameters specify
	 *            the minimum of the interval, the next <em>n</em> parameters
	 *            specify the dimensions of the interval.
	 * @return interval with the specified boundaries
	 */
	public static FinalRealInterval createMinSizeReal( final double... minsize )
	{
		return FinalRealInterval.createMinSize( minsize );
	}

	/**
	 * Create a {@link FinalRealInterval} from a parameter list comprising
	 * minimum and maximum coordinates. For example, to create a 2D interval
	 * from (10, 10) to (20, 40) use createMinMax( 10, 10, 20, 40 ).
	 * 
	 * @param minmax
	 *            a list of <em>2*n</em> parameters to create a <em>n</em>
	 *            -dimensional interval. The first <em>n</em> parameters specify
	 *            the minimum of the interval, the next <em>n</em> parameters
	 *            specify the maximum of the interval.
	 * @return interval with the specified boundaries
	 */
	public static FinalRealInterval createMinMaxReal( final double... minmax )
	{
		return FinalRealInterval.createMinMax( minmax );
	}

	/**
	 * Grow/shrink an interval in all dimensions.
	 * 
	 * Create a {@link FinalInterval} , which is the input interval plus border
	 * pixels on every side, in every dimension.
	 * 
	 * @param interval
	 *            the input interval
	 * @param border
	 *            how many pixels to add on every side
	 * @return expanded interval
	 */
	public static FinalInterval expand( final Interval interval, final long border )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] -= border;
			max[ d ] += border;
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Grow/shrink an interval in one dimensions.
	 * 
	 * Create a {@link FinalInterval} , which is the input interval plus border
	 * pixels on every side, in dimension d.
	 * 
	 * @param interval
	 *            the input interval
	 * @param border
	 *            how many pixels to add on every side
	 * @param d
	 *            in which dimension
	 * @return expanded interval
	 */
	public static FinalInterval expand( final Interval interval, final long border, final int d )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		min[ d ] -= border;
		max[ d ] += border;
		return new FinalInterval( min, max );
	}

	/**
	 * Translate an interval in one dimension.
	 * 
	 * Create a {@link FinalInterval} , which is the input interval shifted by t
	 * in dimension d.
	 * 
	 * @param interval
	 *            the input interval
	 * @param t
	 *            by how many pixels to shift the interval
	 * @param d
	 *            in which dimension
	 * @return translated interval
	 */
	public static FinalInterval translate( final Interval interval, final long t, final int d )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		min[ d ] += t;
		max[ d ] += t;
		return new FinalInterval( min, max );
	}

	/**
	 * Compute the intersection of two intervals.
	 * 
	 * Create a {@link FinalInterval} , which is the intersection of the input
	 * intervals (i.e., the area contained in both input intervals).
	 * 
	 * @param intervalA
	 *            input interval
	 * @param intervalB
	 *            input interval
	 * @return intersection of input intervals
	 */
	public static FinalInterval intersect( final Interval intervalA, final Interval intervalB )
	{
		assert intervalA.numDimensions() == intervalB.numDimensions();

		final int n = intervalA.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Math.max( intervalA.min( d ), intervalB.min( d ) );
			max[ d ] = Math.min( intervalA.max( d ), intervalB.max( d ) );
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Compute the smallest {@link Interval} containing the specified
	 * {@link RealInterval}.
	 * 
	 * @param ri
	 *            input interval.
	 * @return the smallest integer interval that completely contains the input
	 *         interval.
	 */
	public static Interval smallestContainingInterval( final RealInterval ri )
	{
		final int n = ri.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = ( long ) Math.floor( ri.realMin( d ) );
			max[ d ] = ( long ) Math.ceil( ri.realMax( d ) );
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Compute the largest {@link Interval} that is contained in the specified
	 * {@link RealInterval}.
	 * 
	 * @param ri
	 *            input interval.
	 * @return the largest integer interval that is completely contained in the
	 *         input interval.
	 */
	public static Interval largestContainedInterval( final RealInterval ri )
	{
		final int n = ri.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = ( long ) Math.ceil( ri.realMin( d ) );
			max[ d ] = ( long ) Math.floor( ri.realMax( d ) );
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Check whether the given interval is empty, that is, the maximum is
	 * smaller than the minimum in some dimension.
	 * 
	 * @param interval
	 *            interval to check
	 * @return true when the interval is empty, that is, the maximum is smaller
	 *         than the minimum in some dimension.
	 */
	public static boolean isEmpty( final Interval interval )
	{
		final int n = interval.numDimensions();
		for ( int d = 0; d < n; ++d )
			if ( interval.min( d ) > interval.max( d ) )
				return true;
		return false;
	}

	/**
	 * Test whether the {@code containing} interval contains the
	 * {@code contained} point. The interval is closed, that is, boundary points
	 * are contained.
	 * 
	 * @return true, iff {@code contained} is in {@code containing}.
	 */
	public static boolean contains( final Interval containing, final Localizable contained )
	{
		assert containing.numDimensions() == contained.numDimensions();

		final int n = containing.numDimensions();
		for ( int d = 0; d < n; ++d )
		{
			final long p = contained.getLongPosition( d );
			if ( p < containing.min( d ) || p > containing.max( d ) )
				return false;
		}
		return true;
	}

	/**
	 * Test whether the {@code containing} interval contains the
	 * {@code contained} point. The interval is closed, that is, boundary points
	 * are contained.
	 * 
	 * @return true, iff {@code contained} is in {@code containing}.
	 */
	public static boolean contains( final RealInterval containing, final RealLocalizable contained )
	{
		assert containing.numDimensions() == contained.numDimensions();

		final int n = containing.numDimensions();
		for ( int d = 0; d < n; ++d )
		{
			final double p = contained.getDoublePosition( d );
			if ( p < containing.realMin( d ) || p > containing.realMax( d ) )
				return false;
		}
		return true;
	}

	/**
	 * Test whether the {@code containing} interval completely contains the
	 * {@code contained} interval.
	 */
	final static public boolean contains( final Interval containing, final Interval contained )
	{
		assert containing.numDimensions() == contained.numDimensions();

		final int n = containing.numDimensions();
		for ( int d = 0; d < n; ++d )
		{
			if ( containing.min( d ) > contained.min( d ) || containing.max( d ) < contained.max( d ) )
				return false;
		}
		return true;
	}

	/**
	 * Test whether the {@code containing} interval completely contains the
	 * {@code contained} interval.
	 */
	final static public boolean contains( final RealInterval containing, final RealInterval contained )
	{
		assert containing.numDimensions() == contained.numDimensions();

		final int n = containing.numDimensions();
		for ( int d = 0; d < n; ++d )
		{
			if ( containing.realMin( d ) > contained.realMin( d ) || containing.realMax( d ) < contained.realMax( d ) )
				return false;
		}
		return true;
	}

	/**
	 * Compute the number of elements contained in an (integer) {@link Interval}
	 * .
	 * 
	 * @return number of elements in {@code interval}.
	 */
	public static long numElements( final Dimensions interval )
	{
		long numPixels = interval.dimension( 0 );
		final int n = interval.numDimensions();
		for ( int d = 1; d < n; ++d )
			numPixels *= interval.dimension( d );
		return numPixels;
	}

	/**
	 * Tests weather two intervals are equal in their min / max
	 */
	public static boolean equals( final Interval a, final Interval b )
	{

		if ( a.numDimensions() != b.numDimensions() )
			return false;

		for ( int d = 0; d < a.numDimensions(); ++d )
			if ( a.min( d ) != b.min( d ) || a.max( d ) != b.max( d ) )
				return false;

		return true;
	}

	/**
	 * Tests weather two intervals have equal dimensions (same size)
	 */
	public static boolean equalDimensions( final Interval a, final Interval b )
	{
		if ( a.numDimensions() != b.numDimensions() )
			return false;

		for ( int d = 0; d < a.numDimensions(); ++d )
			if ( a.dimension( d ) != b.dimension( d ) )
				return false;

		return true;
	}

	/**
	 * Create a <code>long[]</code> with the dimensions of a {@link Dimensions}.
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly. See
	 * {@link Dimensions#dimensions(long[])}.
	 * </p>
	 * 
	 * @param dimensions
	 *            something which has dimensions
	 * 
	 * @return dimensions as a new <code>long[]</code>
	 */
	public static long[] dimensionsAsLongArray( final Dimensions dimensions )
	{
		final long[] dims = new long[ dimensions.numDimensions() ];
		dimensions.dimensions( dims );
		return dims;
	}

	/**
	 * Create a <code>int[]</code> with the dimensions of an {@link Interval}.
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly.
	 * </p>
	 * 
	 * @param dimensions
	 *            something which has dimensions
	 * 
	 * @return dimensions as a new <code>int[]</code>
	 */
	public static int[] dimensionsAsIntArray( final Dimensions dimensions )
	{
		final int n = dimensions.numDimensions();
		final int[] dims = new int[ n ];
		for ( int d = 0; d < n; ++d )
			dims[ d ] = ( int ) dimensions.dimension( d );
		return dims;
	}

	/**
	 * Create a <code>long[]</code> with the minimum of an {@link Interval}.
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly. See {@link Interval#min(long[])}.
	 * </p>
	 * 
	 * @param interval
	 *            something with interval boundaries
	 * 
	 * @return minimum as a new <code>long[]</code>
	 */
	public static long[] minAsLongArray( final Interval interval )
	{
		final long[] min = new long[ interval.numDimensions() ];
		interval.min( min );
		return min;
	}

	/**
	 * Create a <code>int[]</code> with the minimum of an {@link Interval}.
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly.
	 * </p>
	 * 
	 * @param interval
	 *            something with interval boundaries
	 * 
	 * @return minimum as a new <code>int[]</code>
	 */
	public static int[] minAsIntArray( final Interval interval )
	{
		final int n = interval.numDimensions();
		final int[] min = new int[ n ];
		for ( int d = 0; d < n; ++d )
			min[ d ] = ( int ) interval.min( d );
		return min;
	}

	/**
	 * Create a <code>long[]</code> with the maximum of an {@link Interval}.
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly. See {@link Interval#max(long[])}.
	 * </p>
	 * 
	 * @param interval
	 *            something with interval boundaries
	 * 
	 * @return maximum as a new <code>long[]</code>
	 */
	public static long[] maxAsLongArray( final Interval interval )
	{
		final long[] max = new long[ interval.numDimensions() ];
		interval.max( max );
		return max;
	}

	/**
	 * Create a <code>int[]</code> with the maximum of an {@link Interval}.
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly.
	 * </p>
	 * 
	 * @param interval
	 *            something with interval boundaries
	 * 
	 * @return maximum as a new <code>int[]</code>
	 */
	public static int[] maxAsIntArray( final Interval interval )
	{
		final int n = interval.numDimensions();
		final int[] max = new int[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = ( int ) interval.max( d );
		return max;
	}

	/**
	 * Create a <code>double[]</code> with the maximum of a {@link RealInterval}
	 * .
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly. See
	 * {@link RealInterval#realMax(double[])}.
	 * </p>
	 * 
	 * @param interval
	 *            something with interval boundaries
	 * 
	 * @return maximum as a new double[]
	 */
	public static double[] maxAsDoubleArray( final RealInterval interval )
	{
		final double[] max = new double[ interval.numDimensions() ];
		interval.realMax( max );
		return max;
	}

	/**
	 * Create a <code>double[]</code> with the minimum of a {@link RealInterval}
	 * .
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly. See
	 * {@link RealInterval#realMin(double[])}.
	 * </p>
	 * 
	 * @param interval
	 *            something with interval boundaries
	 * 
	 * @return minimum as a new double[]
	 */
	public static double[] minAsDoubleArray( final RealInterval interval )
	{
		final double[] min = new double[ interval.numDimensions() ];
		interval.realMin( min );
		return min;
	}
}
