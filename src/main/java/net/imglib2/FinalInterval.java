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

package net.imglib2;

import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

import java.util.Arrays;

/**
 * Implementation of the {@link Interval} interface.
 *
 *
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 */
public final class FinalInterval extends AbstractInterval
{
	/**
	 * Creates an Interval from another {@link Interval}
	 *
	 * @param interval
	 *            another {@link Interval}
	 */
	public FinalInterval( final Interval interval )
	{
		super( interval );
	}

	/**
	 * Creates an Interval with the boundaries [0, dimensions-1]
	 *
	 * @param dimensions
	 *            the size of the interval
	 */
	public FinalInterval( final Dimensions dimensions )
	{
		super( dimensions );
	}

	/**
	 * Creates an Interval with the boundaries [min, max] (both including)
	 *
	 * @param min
	 *            the position of the first elements in each dimension
	 * @param max
	 *            the position of the last elements in each dimension
	 * @param copy
	 *            flag indicating whether min and max arrays should be duplicated.
	 */
	protected FinalInterval( final long[] min, final long[] max, final boolean copy )
	{
		super( min, max, copy );
	}

	/**
	 * Creates an Interval with the boundaries [min, max] (both including)
	 *
	 * @param min
	 *            the position of the first elements in each dimension
	 * @param max
	 *            the position of the last elements in each dimension
	 */
	public FinalInterval( final long[] min, final long[] max )
	{
		this( min, max, true );
	}

	/**
	 * Creates an Interval with the boundaries [min, max] (both including)
	 *
	 * @param min
	 *            the position of the first elements in each dimension
	 * @param max
	 *            the position of the last elements in each dimension
	 */
	public FinalInterval( final Localizable min, final Localizable max )
	{
		super( min, max );
	}

	/**
	 * Creates an Interval with the boundaries [0, dimensions-1]
	 *
	 * @param dimensions
	 *            the size of the interval
	 */
	public FinalInterval( final long... dimensions )
	{
		super( dimensions );
	}

	@Override
	public boolean equals( final Object obj )
	{
		return obj instanceof FinalInterval && Intervals.equals( this, ( FinalInterval ) obj );
	}

	@Override
	public int hashCode()
	{
		return Util.combineHash( Arrays.hashCode( min ), Arrays.hashCode( max ) );
	}

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
		final int n = minsize.length / 2;
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = minsize[ d ];
			max[ d ] = min[ d ] + minsize[ d + n ] - 1;
		}
		return new FinalInterval( min, max );
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
		final int n = minmax.length / 2;
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = minmax[ d ];
			max[ d ] = minmax[ d + n ];
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Create an Interval with the given minimum coordinates and size.
	 *
	 * @param min
	 *            the minimum of the interval
	 * @param size
	 *            the dimensions of the interval
	 * @return interval with the specified boundaries
	 */
	public static FinalInterval createMinSize( final long[] min, final long[] size )
	{
		final int n = min.length;
		assert n == size.length;
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			max[ d ] = min[ d ] + size[ d ] - 1;
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Create an Interval that stores its min and max in the provided arrays.
	 *
	 * @param min
	 *            array for storing the position of the first elements in each dimension
	 * @param max
	 *            array for storing the the position of the last elements in each dimension
	 */
	public static FinalInterval wrap( final long[] min, final long[] max )
	{
		return new FinalInterval( min, max, false );
	}
}
