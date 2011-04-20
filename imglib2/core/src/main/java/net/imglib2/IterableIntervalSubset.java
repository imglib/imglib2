/**
 * Copyright (c) 2011, Stephan Saalfeld
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

package net.imglib2;

import java.util.Iterator;

/**
 * A subset of an {@link IterableInterval} defined by the index of its first
 * element and the number of iterable elements.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class IterableIntervalSubset< T > implements IterableInterval< T >
{
	final private class IISCursor implements Cursor< T >
	{
		private long index;
		
		final private Cursor< T > cursor;
		final private boolean localizing; 
		
		private IISCursor( final IISCursor cursor )
		{
			this.localizing = cursor.localizing;
			if ( localizing )
				this.cursor = interval.localizingCursor();
			else
				this.cursor = interval.cursor();
			
			index = cursor.index;
			cursor.jumpFwd( index + 1 );
		}
		
		IISCursor( final boolean localizing )
		{
			this.localizing = localizing;
			if ( localizing )
				cursor = interval.localizingCursor();
			else
				cursor = interval.cursor();
			
			index = firstIndex - 1;
			cursor.jumpFwd( firstIndex );
		}

		@Override
		final public double getDoublePosition( final int d )
		{
			return cursor.getDoublePosition( d );
		}

		@Override
		final public float getFloatPosition( final int d )
		{
			return cursor.getFloatPosition( d );
		}

		@Override
		final public void localize( final float[] position )
		{
			cursor.localize( position );
		}

		@Override
		final public void localize( final double[] position )
		{
			cursor.localize( position );
		}

		@Override
		final public int numDimensions()
		{
			return interval.numDimensions();
		}

		@Override
		final public T get()
		{
			return cursor.get();
		}

		@Override
		final public IISCursor copy()
		{
			return new IISCursor( this );
		}

		@Override
		final public IISCursor copyCursor()
		{
			return copy();
		}

		@Override
		final public void fwd()
		{
			++index;
			cursor.fwd();
		}

		@Override
		final public boolean hasNext()
		{
			return index < lastIndex;
		}

		@Override
		final public void jumpFwd( final long steps )
		{
			index += steps;
			cursor.jumpFwd( steps );
		}

		@Override
		final public void reset()
		{
			index = firstIndex - 1;
			cursor.reset();
			cursor.jumpFwd( firstIndex );
		}

		@Override
		final public T next()
		{
			fwd();
			return get();
		}

		@Override
		final public void remove() {}

		@Override
		final public int getIntPosition( final int d )
		{
			return cursor.getIntPosition( d );
		}

		@Override
		final public long getLongPosition( final int d )
		{
			return cursor.getLongPosition( d );
		}

		@Override
		final public void localize( final int[] position )
		{
			cursor.localize( position );
		}

		@Override
		final public void localize( final long[] position )
		{
			cursor.localize( position );
		}
	}
	
	final private long firstIndex;
	final private long size;
	final private long lastIndex;
	
	final private IterableInterval< T > interval;
	
	/**
	 * Make sure that size and last index are dictated by the parent
	 * {@link IterableInterval} or the {@link IterableIntervalSubset},
	 * depending on which finishes earlier.
	 * 
	 * @param interval
	 * @param firstIndex
	 * @param size
	 */
	public IterableIntervalSubset( final IterableInterval< T > interval, final long firstIndex, final long size )
	{
		this.firstIndex = firstIndex;
		this.size = Math.min( size, interval.size() - firstIndex );
		lastIndex = firstIndex + this.size - 1;
		this.interval = interval;
	}

	@Override
	final public Cursor< T > cursor()
	{
		if ( firstIndex == 0 && size == interval.size() )
			return interval.cursor();
		else
			return new IISCursor( false );
	}

	@Override
	final public Cursor< T > localizingCursor()
	{
		if ( firstIndex == 0 && size == interval.size() )
			return interval.localizingCursor();
		else
			return new IISCursor( true );
	}

	@Override
	final public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		if ( f instanceof IterableIntervalSubset< ? > )
		{
			final IterableIntervalSubset< ? > fi = ( IterableIntervalSubset< ? > )f;
			return
				fi.firstIndex == firstIndex &&
				fi.size == size &&
				interval.equalIterationOrder( fi.interval );
		}
		else
			return false;
	}

	@Override
	final public T firstElement()
	{
		return cursor().next();
	}

	@Override
	final public long size()
	{
		return size;
	}

	@Override
	final public double realMax( final int d )
	{
		return interval.realMax( d );
	}

	@Override
	final public void realMax( final double[] max )
	{
		interval.realMax( max );		
	}

	@Override
	final public double realMin( final int d )
	{
		return realMin( d );
	}

	@Override
	final public void realMin( final double[] min )
	{
		interval.realMin( min );
	}

	@Override
	final public int numDimensions()
	{
		return interval.numDimensions();
	}

	@Override
	final public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	final public long dimension( final int d )
	{
		return interval.dimension( d );
	}

	@Override
	final public void dimensions( final long[] dimensions )
	{
		dimensions( dimensions );
	}

	@Override
	final public long max( final int d )
	{
		return interval.max( d );
	}

	@Override
	final public void max( final long[] max )
	{
		interval.max( max );
	}

	@Override
	final public long min( final int d )
	{
		return interval.min( d );
	}

	@Override
	final public void min( final long[] min )
	{
		interval.min( min );
	}
}
