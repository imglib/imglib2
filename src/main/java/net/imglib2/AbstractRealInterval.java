/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
 * Implementation of the {@link RealInterval} interface.
 *
 *
 * @author Stephan Preibisch
 */
public class AbstractRealInterval extends AbstractEuclideanSpace implements RealInterval
{
	final protected double[] min;

	final protected double[] max;

	/**
	 * Creates an <em>n</em>-dimensional {@link AbstractInterval} with min and
	 * max = 0<sup>n</sup>.
	 *
	 * @param n
	 *            number of dimensions
	 */
	public AbstractRealInterval( final int n )
	{
		super( n );
		this.min = new double[ n ];
		this.max = new double[ n ];
	}

	/**
	 * Creates a new {@link AbstractRealInterval} using an existing
	 * {@link RealInterval}
	 *
	 * @param interval
	 */
	public AbstractRealInterval( final RealInterval interval )
	{
		this( interval.numDimensions() );
		interval.realMin( min );
		interval.realMax( max );
	}

	/**
	 * Creates a new {@link AbstractRealInterval} from min and max coordinates
	 *
	 * @param min
	 * @param max
	 * @param copy
	 *            flag indicating whether min and max arrays should be duplicated.
	 */
	public AbstractRealInterval( final double[] min, final double[] max, final boolean copy )
	{
		super( min.length );
		assert min.length == max.length;
		this.min = copy ? min.clone() : min;
		this.max = copy ? max.clone() : max;
	}

	/**
	 * Creates a new {@link AbstractRealInterval} from min and max coordinates
	 *
	 * @param min
	 * @param max
	 */
	public AbstractRealInterval( final double[] min, final double[] max )
	{
		this( min, max, true );
	}

	/**
	 * Creates a new {@link AbstractRealInterval} from min and max coordinates
	 *
	 * @param min
	 * @param max
	 */
	public AbstractRealInterval( final RealLocalizable min, final RealLocalizable max )
	{
		this( min.positionAsDoubleArray(), max.positionAsDoubleArray(), false );
	}

	@Override
	public double realMin( final int d )
	{
		return min[ d ];
	}

	@Override
	public void realMin( final double[] realMin )
	{
		for ( int d = 0; d < n; ++d )
			realMin[ d ] = this.min[ d ];
	}

	@Override
	public void realMin( final RealPositionable realMin )
	{
		for ( int d = 0; d < n; ++d )
			realMin.setPosition( this.min[ d ], d );
	}

	@Override
	public double realMax( final int d )
	{
		return max[ d ];
	}

	@Override
	public void realMax( final double[] realMax )
	{
		for ( int d = 0; d < n; ++d )
			realMax[ d ] = this.max[ d ];
	}

	@Override
	public void realMax( final RealPositionable realMax )
	{
		for ( int d = 0; d < n; ++d )
			realMax.setPosition( this.max[ d ], d );
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + Intervals.toString( this );
	}

}
