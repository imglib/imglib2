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

package net.imglib2.iterator;

import net.imglib2.AbstractRealInterval;
import net.imglib2.Iterator;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Util;

/**
 * Use this class to iterate a virtual {@link RealInterval} in flat order, that
 * is: with the first dimension varying most quickly and the last dimension
 * varying most slowly. This is useful for iterating an arbitrary real interval
 * in a defined order. 
 *
 * @author John Bogovic
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class LocalizingRealIntervalIterator extends AbstractRealInterval implements Iterator, RealLocalizable
{
	final protected double[] step;

	final protected double[] location;

	/**
	 * Iterates an {@link RealInterval} with given <em>min</em> and <em>max</em> with
	 * the provided step along each dimension.
	 *
	 * @param interval the real interval
	 * @param step iteration step
	 */
	public LocalizingRealIntervalIterator( final RealInterval interval, final double[] step )
	{
		super( interval );
		this.step = step;
		this.location = new double[ interval.numDimensions() ];
		reset();
	}

	/**
	 * Iterates an {@link RealInterval} with given <em>min</em> and <em>max</em> the
	 * the provided step along each dimension.
	 *
	 * @param min real interval min
	 * @param max real interval min
	 * @param step iteration steps
	 */
	public LocalizingRealIntervalIterator( final double[] min, final double[] max, final double[] step )
	{
		super( min, max );
		this.step = step;
		this.location = new double[ numDimensions() ];
		reset();
	}

	@Override
	public void reset()
	{
		realMin( location );
		location[ 0 ] -= step[ 0 ];
	}

	@Override
	public boolean hasNext()
	{
		for ( int d = 0; d < numDimensions(); d++ )
			if ( ( location[ d ] + step[ d ] ) <= realMax( d ) )
			{
				return true;
			}

		return false;
	}

	@Override
	public String toString()
	{
		return Util.printCoordinates( this );
	}

	@Override
	public void localize( float[] position )
	{
		for ( int d = 0; d < position.length; d++ )
			position[ d ] = ( float ) location[ d ];
	}

	@Override
	public void localize( double[] position )
	{
		System.arraycopy( location, 0, position, 0, position.length );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return ( float ) location[ d ];
	}

	@Override
	public double getDoublePosition( int d )
	{
		return location[ d ];
	}

	@Override
	public void jumpFwd( long steps )
	{
		for ( int i = 0; i < steps; i++ )
			fwd();
	}

	@Override
	public void fwd()
	{
		for ( int d = 0; d < numDimensions(); d++ )
		{
			fwdDim( d );
			if ( location[ d ] <= realMax( d ) )
				break;
			else
				location[ d ] = realMin( d );
		}
	}

	private void fwdDim( final int d )
	{
		location[ d ] += step[ d ];
	}

}
