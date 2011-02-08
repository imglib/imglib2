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
package mpicbg.imglib.iterator;

import mpicbg.imglib.Interval;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.Iterator;
import mpicbg.imglib.Sampler;
import mpicbg.imglib.util.IntervalIndexer;
import mpicbg.imglib.util.Util;

/**
 * Use this class to iterate a virtual rectangular {@link Interval} in flat
 * order, that is: row by row, plane by plane, cube by cube, ...  This is useful for
 * iterating an arbitrary interval in a defined order.  For that,
 * connect a {@link ZeroMinIntervalIterator} to a {@link Positionable}.
 * 
 * <pre>
 * ...
 * ZeroMinIntervalIterator i = new ZeroMinIntervalIterator(image);
 * RandomAccess<T> s = image.randomAccess();
 * while (i.hasNext()) {
 *   i.fwd();
 *   s.setPosition(i);
 *   s.type().performOperation(...);
 *   ...
 * }
 * ...
 * </pre>
 * 
 * Note that {@link ZeroMinIntervalIterator} is the right choice in situations where
 * <em>not</em> for each pixel you want to localize and/or set the
 * {@link Positionable} [{@link Sampler}], that is in a sparse sampling situation.
 * For localizing at each iteration step (as in the simplified example above),
 * use {@link FlatIterator} instead.
 *  
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class IntervalIterator implements Iterator, Localizable
{
	final protected long[] dimensions;
	final protected long[] min;
	final protected long[] max;
	final protected long[] steps;
	final protected int n;
	final protected long lastIndex;
	protected long index = -1;
	
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
			final long dim = dimensions[ d ];
			this.dimensions[ d ] = dim;
			this.max[ d ] = dim - 1;
			k *= dim;
			steps[ ++d ] = k;	
		}
		final long dimm = dimensions[ m ];
		this.max[ m ] = dimm - 1;
		this.dimensions[ m ] = dimm;
		lastIndex = k * dimm - 1;
	}
	
	public IntervalIterator( final long[] min, final long[] max )
	{
		n = min.length;
		final int m = n - 1;
		this.min = min.clone();
		this.max = max.clone();
		
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

	public IntervalIterator( final Interval interval )
	{
		n = interval.numDimensions();
		min = new long[ n ];
		max = new long[ n ];
		interval.min( min );
		interval.max( max );
		
		final int m = n - 1;
		dimensions = new long[ n ];
		steps = new long[ n ];
		long k = steps[ 0 ] = 1;
		for ( int d = 0; d < m; )
		{
			final long mind = interval.min( d );
			final long maxd = interval.max( d );
			this.min[ d ] = mind;
			this.max[ d ] = maxd;
			final long s = maxd - mind + 1; 
			dimensions[ d ] = s;
			k *= s;
			steps[ ++d ] = k;
		}
		final long minm = interval.min( m );
		final long maxm = interval.max( m );
		this.min[ m ] = minm;
		this.max[ m ] = maxm;
		final long sizem = maxm - minm + 1;
		dimensions[ m ] = sizem;
		lastIndex = k * sizem - 1;
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
	
	
	/* IntegerLocalizable */

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
}
