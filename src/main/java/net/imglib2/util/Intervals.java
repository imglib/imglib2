/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.StringJoiner;

import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.transform.integer.Mixed;
import net.imglib2.view.ViewTransforms;

/**
 * Convenience methods for manipulating {@link Interval Intervals}.
 *
 * @author Tobias Pietzsch
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
	 * THIS METHOD WILL BE REMOVED IN A FUTURE RELEASE. It was mistakenly
	 * introduced, analogous to {@link #createMinSize(long...)} for integer
	 * intervals. Dimension is not defined for {@link RealInterval} and
	 * computing the <em>max</em> as <em>min + dim - 1</em> does not make sense.
	 *
	 * <p>
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
	@Deprecated
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
	 * Create a {@link FinalInterval}, which is the input interval plus border
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
	 * Grow/shrink an interval in all dimensions.
	 *
	 * Create a {@link FinalInterval}, which is the input interval plus border
	 * pixels on every side, in every dimension.
	 *
	 * @param interval
	 *            the input interval
	 * @param border
	 *            how many pixels to add on every side
	 * @return expanded interval
	 */
	public static FinalInterval expand( final Interval interval, final long... border )
	{
		return expand( interval, new FinalDimensions( border ) );
	}

	/**
	 * Grow/shrink an interval in all dimensions.
	 *
	 * Create a {@link FinalInterval}, which is the input interval plus border
	 * pixels on every side, in every dimension.
	 *
	 * @param interval
	 *            the input interval
	 * @param border
	 *            how many pixels to add on every side
	 * @return expanded interval
	 */
	public static FinalInterval expand( final Interval interval, final Dimensions border )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] -= border.dimension( d );
			max[ d ] += border.dimension( d );
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Grow/shrink an interval in one dimensions.
	 *
	 * Create a {@link FinalInterval}, which is the input interval plus border
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
	 * Create a {@link FinalInterval}, which is the input interval shifted by t
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
	 * Translate an interval.
	 *
	 * Create a {@link FinalInterval}, which is the input interval shifted by
	 * {@code translation}.
	 *
	 * @param interval
	 *            the input interval
	 * @param translation
	 *            by how many pixels to shift the interval
	 * @return translated interval
	 */
	public static FinalInterval translate( final Interval interval, final long... translation )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] += translation[ d ];
			max[ d ] += translation[ d ];
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Translate an interval by {@code -translation}.
	 *
	 * Create a {@link FinalInterval}, which is the input interval shifted by
	 * {@code -translation}.
	 *
	 * @param interval
	 *            the input interval
	 * @param translation
	 *            by how many pixels to inverse-shift the interval
	 * @return translated interval
	 */
	public static FinalInterval translateInverse( final Interval interval, final long... translation )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] -= translation[ d ];
			max[ d ] -= translation[ d ];
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Create new interval by adding a dimension to the source {@link Interval}.
	 * The {@link Interval} boundaries in the additional dimension are set to
	 * the specified values.
	 *
	 * The additional dimension is the last dimension.
	 *
	 * @param interval
	 *            the original interval
	 * @param minOfNewDim
	 *            Interval min in the additional dimension.
	 * @param maxOfNewDim
	 *            Interval max in the additional dimension.
	 */
	public static FinalInterval addDimension( final Interval interval, final long minOfNewDim, final long maxOfNewDim )
	{
		final int m = interval.numDimensions();
		final long[] min = new long[ m + 1 ];
		final long[] max = new long[ m + 1 ];
		for ( int d = 0; d < m; ++d )
		{
			min[ d ] = interval.min( d );
			max[ d ] = interval.max( d );
		}
		min[ m ] = minOfNewDim;
		max[ m ] = maxOfNewDim;
		return new FinalInterval( min, max );
	}

	/**
	 * Invert the bounds on the d-axis of the given interval
	 *
	 * @param interval
	 *            the source
	 * @param d
	 *            the axis to invert
	 */
	public static FinalInterval invertAxis( final Interval interval, final int d )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		final long tmp = min[ d ];
		min[ d ] = -max[ d ];
		max[ d ] = -tmp;
		return new FinalInterval( min, max );
	}

	/**
	 * Take a (n-1)-dimensional slice of a n-dimensional interval, dropping the
	 * d axis.
	 */
	public static FinalInterval hyperSlice( final Interval interval, final int d )
	{
		final int m = interval.numDimensions();
		final int n = m - 1;
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int e = 0; e < m; ++e )
		{
			if ( e < d )
			{
				min[ e ] = interval.min( e );
				max[ e ] = interval.max( e );
			}
			else if ( e > d )
			{
				min[ e - 1 ] = interval.min( e );
				max[ e - 1 ] = interval.max( e );
			}
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Create an interval with permuted axes. The {@code fromAxis} is moved to
	 * {@code toAxis}, while the order of the other axes is preserved.
	 *
	 * If fromAxis=2 and toAxis=4, and axis order of {@code interval} was XYCZT,
	 * then an interval with axis order XYZTC would be created.
	 */
	public static FinalInterval moveAxis( final Interval interval, final int fromAxis, final int toAxis )
	{
		final int n = interval.numDimensions();
		final Mixed t = ViewTransforms.moveAxis( n, fromAxis, toAxis );
		final int[] newAxisIndices = new int[ n ];
		t.getComponentMapping( newAxisIndices );

		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; d++ )
		{
			min[ newAxisIndices[ d ] ] = interval.min( d );
			max[ newAxisIndices[ d ] ] = interval.max( d );
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Create an interval with permuted axes. fromAxis and toAxis are swapped.
	 *
	 * If fromAxis=0 and toAxis=2, this means that the X-axis of the source
	 * interval is mapped to the Z-Axis of the permuted interval and vice versa.
	 * For a XYZ source, a ZYX interval would be created.
	 */
	public static FinalInterval permuteAxes( final Interval interval, final int fromAxis, final int toAxis )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		final long fromMinNew = min[ toAxis ];
		final long fromMaxNew = max[ toAxis ];
		min[ toAxis ] = min[ fromAxis ];
		max[ toAxis ] = max[ fromAxis ];
		min[ fromAxis ] = fromMinNew;
		max[ fromAxis ] = fromMaxNew;
		return new FinalInterval( min, max );
	}

	/**
	 * Create an interval that is rotated by 90 degrees. The rotation is
	 * specified by the fromAxis and toAxis arguments.
	 *
	 * If fromAxis=0 and toAxis=1, this means that the X-axis of the source
	 * interval is mapped to the Y-Axis of the rotated interval. That is, it
	 * corresponds to a 90 degree clock-wise rotation of the source interval in
	 * the XY plane.
	 *
	 * fromAxis=1 and toAxis=0 corresponds to a counter-clock-wise rotation in
	 * the XY plane.
	 */
	public static FinalInterval rotate( final Interval interval, final int fromAxis, final int toAxis )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		if ( fromAxis != toAxis )
		{
			final long fromMinNew = -max[ toAxis ];
			final long fromMaxNew = -min[ toAxis ];
			min[ toAxis ] = min[ fromAxis ];
			max[ toAxis ] = max[ fromAxis ];
			min[ fromAxis ] = fromMinNew;
			max[ fromAxis ] = fromMaxNew;
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Returns an {@link Interval} with the same dimensions as the given
	 * interval, but min is all zero.
	 */
	public static FinalInterval zeroMin( final Interval interval )
	{
		return new FinalInterval( dimensionsAsLongArray( interval ) );
	}

	/**
	 * Return an {@link RealInterval} that is scaled by the given factor.
	 */
	public static RealInterval scale( final RealInterval interval, final double scale )
	{
		final int n = interval.numDimensions();
		final double[] min = minAsDoubleArray( interval );
		final double[] max = maxAsDoubleArray( interval );
		for ( int i = 0; i < n; i++ )
		{
			min[ i ] *= scale;
			max[ i ] *= scale;
		}
		return new FinalRealInterval( min, max );
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
	 * Compute the intersection of two intervals.
	 *
	 * Create a {@link RealInterval} , which is the intersection of the input
	 * intervals (i.e., the area contained in both input intervals).
	 *
	 * @param intervalA
	 *            input interval
	 * @param intervalB
	 *            input interval
	 * @return intersection of input intervals
	 */
	public static FinalRealInterval intersect( final RealInterval intervalA, final RealInterval intervalB )
	{
		assert intervalA.numDimensions() == intervalB.numDimensions();

		final int n = intervalA.numDimensions();
		final double[] min = new double[ n ];
		final double[] max = new double[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Math.max( intervalA.realMin( d ), intervalB.realMin( d ) );
			max[ d ] = Math.min( intervalA.realMax( d ), intervalB.realMax( d ) );
		}
		return new FinalRealInterval( min, max );
	}

	/**
	 * Compute the smallest interval that contains both input intervals.
	 *
	 * Create a {@link FinalInterval} that represents that interval.
	 *
	 * May produce unexpected results for empty {@link Interval}s.
	 * Use {@link #union(Interval, Interval)} if either input interval could be empty.
	 *
	 * @param intervalA
	 *            input interval
	 * @param intervalB
	 *            input interval
	 * @return union of input intervals
	 */
	public static FinalInterval unionUnsafe( final Interval intervalA, final Interval intervalB )
	{
		assert intervalA.numDimensions() == intervalB.numDimensions();

		final int n = intervalA.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Math.min( intervalA.min( d ), intervalB.min( d ) );
			max[ d ] = Math.max( intervalA.max( d ), intervalB.max( d ) );
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Compute the smallest interval that contains both input intervals.
	 *
	 * Create a {@link FinalInterval} that represents that interval.
	 *
	 * @param intervalA
	 *            input interval
	 * @param intervalB
	 *            input interval
	 * @return union of input intervals
	 */
	public static FinalInterval union( final Interval intervalA, final Interval intervalB )
	{
		assert intervalA.numDimensions() == intervalB.numDimensions();

		if( isEmpty( intervalA ))
			return new FinalInterval( intervalB );
		else if( isEmpty( intervalB ))
			return new FinalInterval( intervalA );

		return unionUnsafe( intervalA, intervalB );
	}

	/**
	 * Compute the smallest interval that contains both input intervals.
	 *
	 * Create a {@link RealInterval} that represents that interval.
	 *
	 * May produce unexpected results for empty {@link RealInterval}s.
	 * Use {@link #union(RealInterval, RealInterval)} if either input interval could be empty.
	 *
	 * @param intervalA
	 *            input interval
	 * @param intervalB
	 *            input interval
	 * @return union of input intervals
	 */
	public static FinalRealInterval unionUnsafe( final RealInterval intervalA, final RealInterval intervalB )
	{
		assert intervalA.numDimensions() == intervalB.numDimensions();

		final int n = intervalA.numDimensions();
		final double[] min = new double[ n ];
		final double[] max = new double[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Math.min( intervalA.realMin( d ), intervalB.realMin( d ) );
			max[ d ] = Math.max( intervalA.realMax( d ), intervalB.realMax( d ) );
		}
		return new FinalRealInterval( min, max );
	}

	/**
	 * Compute the smallest interval that contains both input intervals.
	 *
	 * Create a {@link RealInterval} that represents that interval.
	 *
	 * @param intervalA
	 *            input interval
	 * @param intervalB
	 *            input interval
	 * @return union of input intervals
	 */
	public static FinalRealInterval union( final RealInterval intervalA, final RealInterval intervalB )
	{
		assert intervalA.numDimensions() == intervalB.numDimensions();

		if( isEmpty( intervalA ))
			return new FinalRealInterval( intervalB );
		else if( isEmpty( intervalB ))
			return new FinalRealInterval( intervalA );

		return unionUnsafe( intervalA, intervalB );
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
	 * Check whether the given interval is empty, that is, the maximum is
	 * smaller than the minimum in some dimension.
	 *
	 * @param interval
	 *            interval to check
	 * @return true when the interval is empty, that is, the maximum is smaller
	 *         than the minimum in some dimension.
	 */
	public static boolean isEmpty( final RealInterval interval )
	{
		final int n = interval.numDimensions();
		for ( int d = 0; d < n; ++d )
			if ( interval.realMin( d ) > interval.realMax( d ) )
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
		long numPixels = Math.max( interval.dimension( 0 ), 0 );
		final int n = interval.numDimensions();
		for ( int d = 1; d < n; ++d )
			numPixels *= Math.max( interval.dimension( d ), 0 );
		return numPixels;
	}

	/**
	 * Compute the number of elements contained in an (integer) interval.
	 *
	 * @param dimensions
	 *            dimensions of the interval.
	 * @return number of elements in the interval.
	 */
	public static long numElements( final int... dimensions )
	{
		long numPixels = Math.max( dimensions[ 0 ], 0 );
		for ( int d = 1; d < dimensions.length; ++d )
			numPixels *= Math.max( dimensions[ d ], 0 );
		return numPixels;
	}

	/**
	 * Compute the number of elements contained in an (integer) interval.
	 *
	 * @param dimensions
	 *            dimensions of the interval.
	 * @return number of elements in the interval.
	 */
	public static long numElements( final long... dimensions )
	{
		long numPixels = Math.max( dimensions[ 0 ], 0 );
		for ( int d = 1; d < dimensions.length; ++d )
			numPixels *= Math.max( dimensions[ d ], 0 );
		return numPixels;
	}

	/**
	 * Tests whether two intervals are equal in their min / max.
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
	 * Tests whether two {@link RealInterval}s are equal in their min / max.
	 */
	public static boolean equals( final RealInterval a, final RealInterval b )
	{
		if ( a.numDimensions() != b.numDimensions() )
			return false;

		for ( int d = 0; d < a.numDimensions(); ++d )
			if ( a.realMin( d ) != b.realMin( d ) || a.realMax( d ) != b.realMax( d ) )
				return false;

		return true;
	}

	/**
	 * Tests whether two {@link RealInterval}s are equal in their min / max.
	 * With respect to the given tolerance.
	 */
	public static boolean equals( final RealInterval a, final RealInterval b,
			final double tolerance)
	{
		if ( a.numDimensions() != b.numDimensions() )
			return false;

		for ( int d = 0; d < a.numDimensions(); ++d )
		{
			final double differenceMin = Math.abs( a.realMin( d ) - b.realMin( d ) );
			final double differenceMax = Math.abs( a.realMax( d ) - b.realMax( d ) );
			if ( differenceMin > tolerance || differenceMax > tolerance )
				return false;
		}

		return true;
	}

	/**
	 * Tests whether two {@link Dimensions} have the same size.
	 */
	public static boolean equalDimensions( final Dimensions a, final Dimensions b )
	{
		if ( a.numDimensions() != b.numDimensions() )
			return false;

		for ( int d = 0; d < a.numDimensions(); ++d )
			if ( a.dimension( d ) != b.dimension( d ) )
				return false;

		return true;
	}

	/**
	 * Tests whether two intervals have equal dimensions (same size).
	 */
	@Deprecated
	public static boolean equalDimensions( final Interval a, final Interval b )
	{
		return equalDimensions( ( Dimensions ) a, ( Dimensions ) b );
	}

	/**
	 * Create a <code>long[]</code> with the dimensions of a {@link Dimensions}.
	 *
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly. See
	 * {@link Dimensions#dimensions(long[])}.
	 * </p>
	 * <p>
	 * Consider using the more convenient {@link Dimensions#dimensionsAsLongArray}.
	 * This method may be deprecated in a future release.
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
	 * <p>
	 * Consider using the more convenient {@link Interval#minAsLongArray}.
	 * This method may be deprecated in a future release.
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
	 * <p>
	 * Consider using the more convenient {@link Interval#maxAsLongArray}.
	 * This method may be deprecated in a future release.
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
	 * <p>
	 * Consider using the more convenient {@link RealInterval#maxAsDoubleArray}.
	 * This method may be deprecated in a future release.
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
	 * <p>
	 * Consider using the more convenient {@link RealInterval#minAsDoubleArray}
	 * This method may be deprecated in a future release.
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

	/**
	 * Returns an image, where each pixel value is the position of the pixel
	 * represented as {@link Localizable}.
	 *
	 * @param interval
	 *            Interval of the returned image.
	 */
	public static RandomAccessibleInterval< Localizable > positions( final Interval interval ) {
		return Localizables.randomAccessibleInterval( interval);
	}

	/**
	 * Returns a string that contains min, max and the dimensions of the
	 * {@link Interval}.
	 */
	public static String toString( final Interval value )
	{
		final StringBuilder sb = new StringBuilder();

		sb.append( "[(" );
		final int n = value.numDimensions();
		for ( int d = 0; d < n; d++ )
		{
			sb.append( value.min( d ) );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( ") -- (" );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( value.max( d ) );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( ") = " );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( value.dimension( d ) );
			if ( d < n - 1 )
				sb.append( "x" );
		}
		sb.append( "]" );

		return sb.toString();
	}

	/**
	 * Returns a string that contains min and max of the {@link RealInterval}.
	 */
	public static String toString( final RealInterval value )
	{
		final StringBuilder sb = new StringBuilder();

		sb.append( "[(" );
		final int n = value.numDimensions();
		for ( int d = 0; d < n; d++ )
		{
			sb.append( value.realMin( d ) );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( ") -- (" );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( value.realMax( d ) );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( ")]" );

		return sb.toString();
	}

	/**
	 * Converts the {@link Dimensions} into a string.
	 */
	public static String toString( final Dimensions value )
	{
		final StringJoiner joiner = new StringJoiner( "x" );
		for ( int d = 0; d < value.numDimensions(); d++ )
			joiner.add( Long.toString( value.dimension( d ) ) );
		return joiner.toString();
	}
}
