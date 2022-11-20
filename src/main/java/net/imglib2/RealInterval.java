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

package net.imglib2;

/**
 * <p>
 * {x&isin;R<sup><em>n</em></sup>|<em>min<sub>d</sub></em>&le;
 * <em>x<sub>d</sub></em>&le;<em>max<sub>d</sub></em>;<em>d</em>&isin;{0&hellip;
 * <em>n</em>-1}}
 * </p>
 *
 * An {@link RealInterval} over the real source domain. <em>Note</em> that this
 * does <em>not</em> imply that for <em>all</em> coordinates in the
 * {@link RealInterval} function values exist or can be generated. It only
 * defines where the minimum and maximum source coordinates are. E.g. an
 * {@link IterableRealInterval} has a limited number of values and a source
 * coordinate for each. By that, minimum and maximum are defined but the
 * {@link RealInterval} does not define a value for all coordinates in between.
 *
 * @author Stephan Saalfeld
 */
public interface RealInterval extends EuclideanSpace
{
	/**
	 * Get the minimum in dimension d.
	 *
	 * @param d
	 *            dimension
	 * @return minimum in dimension d.
	 */
	double realMin( int d );

	/**
	 * Write the minimum of each dimension into double[].
	 *
	 * @param min
	 */
	default void realMin( final double[] min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min[ d ] = realMin( d );
	}

	/**
	 * Sets a {@link RealPositionable} to the minimum of this {@link Interval}
	 *
	 * @param min
	 */
	default void realMin( final RealPositionable min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min.setPosition( realMin( d ), d );
	}

	/**
	 * Get the maximum in dimension d.
	 *
	 * @param d
	 *            dimension
	 * @return maximum in dimension d.
	 */
	double realMax( final int d );

	/**
	 * Write the maximum of each dimension into double[].
	 *
	 * @param max
	 */
	default void realMax( final double[] max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max[ d ] = realMax( d );
	}

	/**
	 * Sets a {@link RealPositionable} to the maximum of this {@link Interval}
	 *
	 * @param max
	 */
	default void realMax( final RealPositionable max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max.setPosition( realMax( d ), d );
	}

	/**
	 * Allocates a new double array with the minimum of this RealInterval.
	 *
	 * Please note that his method allocates a new array each time which
	 * introduces notable overhead in both compute and memory.
	 * If you query it frequently, you should allocate a dedicated array
	 * first and reuse it with {@link #realMin(double[])}.
	 *
	 * @return the min
	 */
	default double[] minAsDoubleArray()
	{
		final double[] min = new double[ numDimensions() ];
		realMin( min );
		return min;
	}

	/**
	 * Allocates a new {@link RealPoint} with the minimum of this RealInterval.
	 *
	 * Please note that his method allocates a new {@link RealPoint} each time
	 * which introduces notable overhead in both compute and memory.
	 * If you query it frequently, you should allocate a dedicated
	 * {@link RealPoint} first and reuse it with
	 * {@link #realMin(RealPositionable)}.
	 *
	 * @return the min
	 */
	default RealPoint minAsRealPoint()
	{
		final RealPoint min = new RealPoint( numDimensions() );
		realMin( min );
		return min;
	}

	/**
	 * Allocates a new double array with the maximum of this RealInterval.
	 *
	 * Please note that his method allocates a new array each time which
	 * introduces notable overhead in both compute and memory.
	 * If you query it frequently, you should allocate a dedicated array
	 * first and reuse it with {@link #realMax(double[])}.
	 *
	 * @return the max
	 */
	default double[] maxAsDoubleArray()
	{
		final double[] max = new double[ numDimensions() ];
		realMax( max );
		return max;
	}

	/**
	 * Allocates a new {@link RealPoint} with the maximum of this RealInterval.
	 *
	 * Please note that his method allocates a new {@link RealPoint} each time
	 * which introduces notable overhead in both compute and memory.
	 * If you query it frequently, you should allocate a dedicated
	 * {@link RealPoint} first and reuse it with
	 * {@link #realMax(RealPositionable)}.
	 *
	 * @return the max
	 */
	default RealPoint maxAsRealPoint()
	{
		final RealPoint max = new RealPoint( numDimensions() );
		realMax( max );
		return max;
	}
}
