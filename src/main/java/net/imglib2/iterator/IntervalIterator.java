/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.Sampler;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Util;

/**
 * Use this class to iterate a virtual {@link Interval} in flat order, that is:
 * row by row, plane by plane, cube by cube, ... This is useful for iterating an
 * arbitrary interval in a defined order. For that, connect an
 * {@link IntervalIterator} to a {@link Positionable}.
 * 
 * <pre>
 * ...
 * IntervalIterator i = new IntervalIterator(image);
 * RandomAccess<T> s = image.randomAccess();
 * while (i.hasNext()) {
 *   i.fwd();
 *   s.setPosition(i);
 *   s.get().performOperation(...);
 *   ...
 * }
 * ...
 * </pre>
 * 
 * Note that {@link IntervalIterator} is the right choice in situations where
 * <em>not</em> for each pixel you want to localize and/or set the
 * {@link Positionable} [{@link Sampler}], that is in a sparse sampling
 * situation. For localizing at each iteration step (as in the simplified
 * example above), use {@link LocalizingIntervalIterator} instead.
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld (saalfeld@mpi-cbg.de)
 */
public class IntervalIterator extends AbstractInterval implements Iterator, Localizable
{
	final protected long[] dimensions;

	final protected long[] steps;

	final protected long lastIndex;

	protected long index = -1;

	/**
	 * Iterates an {@link Interval} of the given dimensions with <em>min</em>=
	 * 0<sup><em>n</em></sup>
	 * 
	 * @param dimensions
	 */
	public IntervalIterator( final long[] dimensions )
	{
		super( dimensions );

		this.dimensions = new long[ n ];
		steps = new long[ n ];

		final int m = n - 1;
		long k = steps[ 0 ] = 1;
		for ( int d = 0; d < m; )
		{
			final long dimd = dimensions[ d ];
			this.dimensions[ d ] = dimd;
			k *= dimd;
			steps[ ++d ] = k;
		}
		final long dimm = dimensions[ m ];
		this.dimensions[ m ] = dimm;
		lastIndex = k * dimm - 1;
	}

	/**
	 * Iterates an {@link Interval} of the given dimensions with <em>min</em>=
	 * 0<sup><em>n</em></sup>
	 * 
	 * @param dimensions
	 */
	public IntervalIterator( final int[] dimensions )
	{
		this( Util.int2long( dimensions ) );
	}

	/**
	 * Iterates an {@link Interval} with given <em>min</em> and <em>max</em>.
	 * 
	 * @param min
	 * @param max
	 */
	public IntervalIterator( final long[] min, final long[] max )
	{
		super( min, max );

		dimensions = new long[ n ];
		steps = new long[ n ];

		final int m = n - 1;
		long k = steps[ 0 ] = 1;
		for ( int d = 0; d < m; )
		{
			final long s = max[ d ] - min[ d ] + 1;
			dimensions[ d ] = s;
			k *= s;
			steps[ ++d ] = k;
		}
		final long sizem = max[ m ] - min[ m ] + 1;
		dimensions[ m ] = sizem;
		lastIndex = k * sizem - 1;
	}

	/**
	 * Iterates an {@link Interval} with given <em>min</em> and <em>max</em>.
	 * 
	 * @param min
	 * @param max
	 */
	public IntervalIterator( final int[] min, final int[] max )
	{
		this( Util.int2long( min ), Util.int2long( max ) );
	}

	/**
	 * Iterates a given {@link Interval}.
	 * 
	 * @param interval
	 */
	public IntervalIterator( final Interval interval )
	{
		super( interval );

		dimensions = new long[ n ];
		steps = new long[ n ];

		final int m = n - 1;
		long k = steps[ 0 ] = 1;
		for ( int d = 0; d < m; )
		{
			final long dimd = interval.dimension( d );
			dimensions[ d ] = dimd;
			k *= dimd;
			steps[ ++d ] = k;
		}
		final long dimm = interval.dimension( m );
		dimensions[ m ] = dimm;
		lastIndex = k * dimm - 1;
	}

	static public IntervalIterator create( final Interval interval )
	{
		final int n = interval.numDimensions();
		for ( int d = 0; d < n; ++d )
			if ( interval.min( d ) != 0 )
				return new IntervalIterator( interval );
		return new ZeroMinIntervalIterator( interval );
	}

	/* Iterator */

	@Override
	public void jumpFwd( final long i )
	{
		index += i;
	}

	@Override
	public void fwd()
	{
		++index;
	}

	@Override
	public void reset()
	{
		index = -1;
	}

	@Override
	public boolean hasNext()
	{
		return index < lastIndex;
	}

	/**
	 * @return - the current iteration index
	 */
	public long getIndex()
	{
		return index;
	}

	/* Localizable */

	@Override
	public long getLongPosition( final int dim )
	{
		return IntervalIndexer.indexToPositionWithOffset( index, dimensions, steps, min, dim );
	}

	@Override
	public void localize( final long[] position )
	{
		IntervalIndexer.indexToPositionWithOffset( index, dimensions, min, position );
	}

	@Override
	public int getIntPosition( final int dim )
	{
		return ( int ) IntervalIndexer.indexToPositionWithOffset( index, dimensions, steps, min, dim );
	}

	@Override
	public void localize( final int[] position )
	{
		IntervalIndexer.indexToPositionWithOffset( index, dimensions, min, position );
	}

	@Override
	public double getDoublePosition( final int dim )
	{
		return IntervalIndexer.indexToPositionWithOffset( index, dimensions, steps, min, dim );
	}

	/* RealLocalizable */

	@Override
	public float getFloatPosition( final int dim )
	{
		return IntervalIndexer.indexToPositionWithOffset( index, dimensions, steps, min, dim );
	}

	@Override
	public void localize( final float[] position )
	{
		IntervalIndexer.indexToPositionWithOffset( index, dimensions, min, position );
	}

	@Override
	public void localize( final double[] position )
	{
		IntervalIndexer.indexToPositionWithOffset( index, dimensions, min, position );
	}

	/* Object */

	@Override
	public String toString()
	{
		final int[] l = new int[ dimensions.length ];
		localize( l );
		return Util.printCoordinates( l );
	}

	/* Interval */

	@Override
	public long dimension( final int d )
	{
		return dimensions[ d ];
	}

	@Override
	public void dimensions( final long[] dim )
	{
		for ( int d = 0; d < n; ++d )
			dim[ d ] = this.dimensions[ d ];
	}
}
