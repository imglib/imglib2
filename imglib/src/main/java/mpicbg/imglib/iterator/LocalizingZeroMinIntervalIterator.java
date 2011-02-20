/**
 * Copyright (c) 2009--2011, Stephan Preibisch & Stephan Saalfeld
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
import mpicbg.imglib.Iterator;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.location.AbstractLocalizable;
import mpicbg.imglib.util.IntervalIndexer;
import mpicbg.imglib.util.Util;

/**
 * Use this class to iterate a virtual rectangular {@link Interval} whose min
 * coordinates are at 0<sup><em>n</em></sup> in flat
 * order, that is: row by row, plane by plane, cube by cube, ...  This is useful for
 * iterating an arbitrary interval in a defined order.  For that,
 * connect a {@link LocalizingZeroMinIntervalIterator} to a {@link Positionable}.
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
 * Note that {@link LocalizingZeroMinIntervalIterator} is the right choice in
 * situations where, for <em>each</em> pixel, you want to localize and/or set
 * the {@link ImgRandomAccess}, that is in a dense sampling
 * situation.  For localizing sparsely (e.g. under an external condition),
 * use {@link ZeroMinIntervalIterator} instead.
 *  
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class LocalizingZeroMinIntervalIterator extends AbstractLocalizable implements Iterator, Interval
{
	final protected long[] dimensions;
	final protected long[] max;
	final long lastIndex;
	protected long index = -1;
	
	final static private long[] maxify( final long[] dimensions )
	{
		final long[] max = new long[ dimensions.length ];
		for ( int d = 0; d < dimensions.length; ++d )
			max[ d ] = dimensions[ d ] - 1;
		return max;
	}
	
	public LocalizingZeroMinIntervalIterator( final long[] dimensions )
	{
		super( dimensions.length );
		this.dimensions = dimensions.clone();
		max = maxify( dimensions );
		position[ 0 ] = -1;

		final int m = n - 1;
		long k = 1;
		for ( int d = 0; d < m; )
			k *= dimensions[ d ];
		lastIndex = k * dimensions[ m ] - 1;
	}

	public LocalizingZeroMinIntervalIterator( final int[] dimensions )
	{
		this( Util.int2long( dimensions ) );
	}

	public LocalizingZeroMinIntervalIterator( final Interval interval )
	{
		super( interval.numDimensions() );
		this.dimensions = Util.intervalDimensions( interval );
		max = maxify( dimensions );
		position[ 0 ] = -1;

		final int m = n - 1;
		long k = 1;
		for ( int d = 0; d < m; )
			k *= dimensions[ d ];
		lastIndex = k * dimensions[ m ] - 1;
	}
	

	public long getIndex() { return index; }
	
	/* Iterator */

	@Override
	public void fwd()
	{
		++index;

		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] >= dimensions[ d ] )
				position[ d ] = 0;
			else
				break;
		}
	}

	@Override
	public void jumpFwd( final long steps )
	{
		index += steps;
		IntervalIndexer.indexToPosition( index, dimensions, position );
	}

	@Override
	public void reset()
	{
		index = -1;
		position[ 0 ] = -1;

		for ( int d = 1; d < n; d++ )
			position[ d ] = 0;
	}
	
	@Override
	public boolean hasNext() { return index < lastIndex; }
	
	/* EuclideanSpace */
	
	@Override
	final public int numDimensions() { return n; }
	
	
	/* Object */
	
	@Override
	final public String toString()
	{
		final int[] l = new int[ dimensions.length ];
		localize( l );
		return Util.printCoordinates( l );
	}

	@Override
	public long dimension( final int d )
	{
		return dimensions[ d ];
	}

	@Override
	public void dimensions( final long[] dim )
	{
		for ( int d = 0; d < n; ++d )
			dim[ d ] = dimensions[ d ];
	}

	@Override
	public long max( final int d )
	{
		return max[ d ];
	}

	@Override
	public void max( final long[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public long min( final int d )
	{
		return 0;
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = 0;
	}

	@Override
	public double realMax( final int d )
	{
		return max[ d ];
	}

	@Override
	public void realMax( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public double realMin( final int d )
	{
		return 0;
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = 0;
	}
}
