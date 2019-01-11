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

package net.imglib2;

/**
 * <p>
 * {x&isin;Z<sup><em>n</em></sup>|<em>min<sub>d</sub></em>&le;
 * <em>x<sub>d</sub></em>&le;<em>max<sub>d</sub></em>;<em>d</em>&isin;{0&hellip;
 * <em>n</em>-1}}
 * </p>
 * 
 * <p>
 * An {@link Interval} over the discrete source domain. <em>Note</em> that this
 * does <em>not</em> imply that for <em>all</em> coordinates in the
 * {@link Interval} function values exist or can be generated. It only defines
 * where the minimum and maximum source coordinates are. E.g. an
 * {@link IterableInterval} has a limited number of values and a source
 * coordinate for each. By that, minimum and maximum are defined but the
 * {@link Interval} does not define a value for all coordinates in between.
 * </p>
 * 
 * @author Stephan Saalfeld
 * @author Stephan Preibisch
 */
public interface Interval extends RealInterval, Dimensions
{
	/**
	 * Get the minimum in dimension d.
	 *
	 * @param d
	 *            dimension
	 * @return minimum in dimension d.
	 */
	long min( final int d );

	/**
	 * Write the minimum of each dimension into long[].
	 *
	 * @param min
	 */
	default void min( final long[] min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min[ d ] = min( d );
	}


	/**
	 * Sets a {@link Positionable} to the minimum of this {@link Interval}
	 *
	 * @param min
	 */
	default void min( final Positionable min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min.setPosition( min( d ), d );
	}

	/**
	 * Get the maximum in dimension d.
	 *
	 * @param d
	 *            dimension
	 * @return maximum in dimension d.
	 */
	long max( final int d );

	/**
	 * Write the maximum of each dimension into long[].
	 *
	 * @param max
	 */
	default void max( final long[] max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max[ d ] = max( d );
	}

	/**
	 * Sets a {@link Positionable} to the maximum of this {@link Interval}
	 *
	 * @param max
	 */
	default void max( final Positionable max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max.setPosition( max( d ), d );
	}

	/** Default implementation of {@link RealInterval#realMin(int)}. */
	@Override
	default double realMin( final int d )
	{
		return min( d );
	}

	/** Default implementation of {@link RealInterval#realMin(double[])} */
	@Override
	default void realMin( final double[] min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min[ d ] = realMin( d );
	}

	/** Default implementation of {@link RealInterval#realMin(RealPositionable)} */
	@Override
	default void realMin( final RealPositionable min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min.setPosition( realMin( d ), d );
	}

	/** Default implementation of {@link RealInterval#realMax(int)}. */
	@Override
	default double realMax( final int d )
	{
		return max( d );
	}

	/** Default implementation of {@link RealInterval#realMax(double[])}. */
	@Override
	default void realMax( final double[] max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max[ d ] = realMax( d );
	}

	/** Default implementation of {@link RealInterval#realMax(RealPositionable)}. */
	@Override
	default void realMax( final RealPositionable max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max.setPosition( realMax( d ), d );
	}

	/** Default implementation of {@link Dimensions#dimensions(long[])}. */
	@Override
	default void dimensions( final long[] dimensions )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			dimensions[ d ] = dimension( d );
	}

	/** Default implementation of {@link Dimensions#dimension(int)}. */
	@Override
	default long dimension( int d )
	{
		return max( d ) - min( d ) + 1;
	}
}
