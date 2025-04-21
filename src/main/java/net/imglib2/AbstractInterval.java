/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

/**
 * Implementation of the {@link Interval} interface.
 *
 *
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 */
public abstract class AbstractInterval extends AbstractEuclideanSpace implements Interval
{
	final protected long[] min;

	final protected long[] max;

	/**
	 * Creates an <em>n</em>-dimensional {@link AbstractInterval} with min and
	 * max = 0<sup>n</sup>.
	 *
	 * @param n
	 *            number of dimensions
	 */
	public AbstractInterval( final int n )
	{
		super( n );
		this.min = new long[ n ];
		this.max = new long[ n ];
	}

	/**
	 * Creates a {@link AbstractInterval} from another {@link Interval}
	 *
	 * @param interval
	 *            another {@link Interval}
	 */
	public AbstractInterval( final Interval interval )
	{
		this( interval.numDimensions() );

		interval.min( min );
		interval.max( max );
	}

	/**
	 * Creates an Interval with the boundaries [0, dimensions-1]
	 *
	 * @param dimensions
	 *            the size of the interval
	 */
	public AbstractInterval( final Dimensions dimensions )
	{
		this( dimensions.numDimensions() );
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = dimensions.dimension( d ) - 1;
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
	public AbstractInterval( final long[] min, final long[] max, final boolean copy )
	{
		super( min.length );
		assert min.length == max.length;
		this.min = copy ? min.clone() : min;
		this.max = copy ? max.clone() : max;
	}

	/**
	 * Creates an Interval with the boundaries [min, max] (both including)
	 *
	 * @param min
	 *            the position of the first elements in each dimension
	 * @param max
	 *            the position of the last elements in each dimension
	 */
	public AbstractInterval( final long[] min, final long[] max )
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
	public AbstractInterval( final Localizable min, final Localizable max )
	{
		this( min.positionAsLongArray(), max.positionAsLongArray(), false );
	}

	/**
	 * Creates an Interval with the boundaries [0, dimensions-1]
	 *
	 * @param dimensions
	 *            the size of the interval
	 */
	public AbstractInterval( final long[] dimensions )
	{
		this( dimensions.length );
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = dimensions[ d ] - 1;
	}

	@Override
	public double realMin( final int d )
	{
		assert d >= 0;
		assert d < n;

		return min[ d ];
	}

	@Override
	public void realMin( final double[] minimum )
	{
		assert minimum.length == n;

		for ( int d = 0; d < n; ++d )
			minimum[ d ] = this.min[ d ];
	}

	@Override
	public void realMin( final RealPositionable minimum )
	{
		assert minimum.numDimensions() == n;

		minimum.setPosition( this.min );
	}

	@Override
	public double realMax( final int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ];
	}

	@Override
	public void realMax( final double[] maximum )
	{
		assert maximum.length == n;

		for ( int d = 0; d < n; ++d )
			maximum[ d ] = this.max[ d ];
	}

	@Override
	public void realMax( final RealPositionable m )
	{
		assert m.numDimensions() == n;

		m.setPosition( this.max );
	}

	@Override
	public long min( final int d )
	{
		assert d >= 0;
		assert d < n;

		return min[ d ];
	}

	@Override
	public void min( final long[] minimum )
	{
		assert minimum.length == n;

		for ( int d = 0; d < n; ++d )
			minimum[ d ] = this.min[ d ];
	}

	@Override
	public void min( final Positionable m )
	{
		assert m.numDimensions() == n;

		m.setPosition( this.min );
	}

	@Override
	public long max( final int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ];
	}

	@Override
	public void max( final long[] maximum )
	{
		assert maximum.length == n;

		for ( int d = 0; d < n; ++d )
			maximum[ d ] = this.max[ d ];
	}

	@Override
	public void max( final Positionable m )
	{
		assert m.numDimensions() == n;

		m.setPosition( this.max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		assert dimensions.length == n;

		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = max[ d ] - min[ d ] + 1;
	}

	@Override
	public long dimension( final int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ] - min[ d ] + 1;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " " + Intervals.toString( this );
	}
}
