/**
 * Copyright (c) 2011, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.imglib2.iterator;

import net.imglib2.Interval;
import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.Sampler;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Util;

/**
 * Use this class to iterate a virtual {@link Interval} in flat order, that is:
 * row by row, plane by plane, cube by cube, ...  This is useful for iterating
 * an arbitrary interval in a defined order.  For that, connect an
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
 * {@link Positionable} [{@link Sampler}], that is in a sparse sampling situation.
 * For localizing at each iteration step (as in the simplified example above),
 * use {@link LocalizingIntervalIterator} instead.
 *  
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class IntervalIterator implements Iterator, Localizable, Interval
{
	final protected int n;
	final protected long[] dimensions;
	final protected long[] min;
	final protected long[] max;
	final protected long[] steps;
	final protected long lastIndex;
	protected long index = -1;
	
	/**
	 * Iterates an {@link Interval} with <em>min</em>=0<sup><em>n</em></sup>.
	 * 
	 * @param dimensions
	 */
	public IntervalIterator( final long[] dimensions )
	{
		n = dimensions.length;
		final int m = n - 1;
		min = new long[ n ];
		max = new long[ n ];
		this.dimensions = new long[ n ];
		steps = new long[ n ];
		
		long k = steps[ 0 ] = 1;
		for ( int d = 0; d < m; )
		{
			final long dimd = dimensions[ d ];
			this.dimensions[ d ] = dimd;
			this.max[ d ] = dimd - 1;
			k *= dimd;
			steps[ ++d ] = k;	
		}
		final long dimm = dimensions[ m ];
		this.max[ m ] = dimm - 1;
		this.dimensions[ m ] = dimm;
		lastIndex = k * dimm - 1;
	}

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
		n = min.length;
		final int m = n - 1;
		this.min = new long[ n ];
		this.max = new long[ n ];
		
		dimensions = new long[ n ];
		steps = new long[ n ];
		long k = steps[ 0 ] = 1;
		for ( int d = 0; d < m; )
		{
			final long mind = min[ d ];
			final long maxd = max[ d ];
			this.min[ d ] = mind;
			this.max[ d ] = maxd;
			final long s = maxd - mind + 1; 
			dimensions[ d ] = s;
			k *= s;
			steps[ ++d ] = k;
		}
		final long minm = min[ m ];
		final long maxm = max[ m ];
		this.min[ m ] = minm;
		this.max[ m ] = maxm;
		final long sizem = maxm - minm + 1;
		dimensions[ m ] = sizem;
		lastIndex = k * sizem - 1;
	}

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
		n = interval.numDimensions();
		min = new long[ n ];
		max = new long[ n ];
		
		final int m = n - 1;
		dimensions = new long[ n ];
		steps = new long[ n ];
		long k = steps[ 0 ] = 1;
		for ( int d = 0; d < m; )
		{
			final long mind = interval.min( d );
			final long maxd = interval.max( d );
			final long dimd = interval.dimension( d );
			min[ d ] = mind;
			max[ d ] = maxd;
			dimensions[ d ] = dimd;
			k *= dimd;
			steps[ ++d ] = k;
		}
		final long minm = interval.min( m );
		final long maxm = interval.max( m );
		final long dimm = interval.dimension( m );
		min[ m ] = minm;
		max[ m ] = maxm;
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
	public void jumpFwd( final long i ) { index += i; }

	@Override
	public void fwd() { ++index; }

	@Override
	public void reset() { index = -1; }
	
	@Override
	public boolean hasNext() { return index < lastIndex; }
	
	/**
	 * @return - the current iteration index
	 */
	public long getIndex() { return index; }
	
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
		return ( int )IntervalIndexer.indexToPositionWithOffset( index, dimensions, steps, min, dim );
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

	
	/* EuclideanSpace */
	
	@Override
	public int numDimensions() { return n; }
	
	
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


	@Override
	public long max( final int d )
	{
		return max[ d ];
	}


	@Override
	public void max( final long[] target )
	{
		for ( int d = 0; d < n; ++d )
			target[ d ] = this.max[ d ];
	}

	@Override
	public void max( final Positionable target )
	{
		target.setPosition( this.max );
	}

	@Override
	public long min( final int d )
	{
		return min[ d ];
	}


	@Override
	public void min( final long[] target )
	{
		for ( int d = 0; d < n; ++d )
			target[ d ] = this.min[ d ];
	}

	@Override
	public void min( final Positionable target )
	{
		target.setPosition( this.min );
	}

	@Override
	public double realMax( final int d )
	{
		return max[ d ];
	}


	@Override
	public void realMax( final double[] target )
	{
		for ( int d = 0; d < n; ++d )
			target[ d ] = this.max[ d ];
	}

	@Override
	public void realMax( final RealPositionable target )
	{
		target.setPosition( this.max );
	}

	@Override
	public double realMin( final int d )
	{
		return min[ d ];
	}


	@Override
	public void realMin( final double[] target )
	{
		for ( int d = 0; d < n; ++d )
			target[ d ] = this.min[ d ];
	}

	@Override
	public void realMin( final RealPositionable target )
	{
		target.setPosition( this.min );
	}	
}
