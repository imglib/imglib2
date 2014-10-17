/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

import java.util.Iterator;

/**
 * A subset of an {@link IterableInterval} defined by the index of its first
 * element and the number of iterable elements.
 * 
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class IterableIntervalSubset< T > extends AbstractWrappedInterval< IterableInterval< T > > implements IterableInterval< T >
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
				this.cursor = sourceInterval.localizingCursor();
			else
				this.cursor = sourceInterval.cursor();

			index = cursor.index;
			cursor.jumpFwd( index + 1 );
		}

		IISCursor( final boolean localizing )
		{
			this.localizing = localizing;
			if ( localizing )
				cursor = sourceInterval.localizingCursor();
			else
				cursor = sourceInterval.cursor();

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
			return sourceInterval.numDimensions();
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
		final public void remove()
		{
			// NB: no action.
		}

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

	private class IISIterationOrder
	{
		long firstIndex()
		{
			return firstIndex;
		}

		long size()
		{
			return size;
		}

		IterableInterval< T > interval()
		{
			return sourceInterval;
		}

		@Override
		public boolean equals( final Object obj )
		{
			if ( !( obj instanceof IterableIntervalSubset.IISIterationOrder ) )
				return false;

			@SuppressWarnings( "unchecked" )
			final IISIterationOrder o = ( IISIterationOrder ) obj;

			return o.firstIndex() == firstIndex() &&
					o.size() == size() &&
					o.interval().iterationOrder().equals( interval().iterationOrder() );
		}
	}

	final protected long firstIndex;

	final private long size;

	final protected long lastIndex;

	/**
	 * Make sure that size and last index are dictated by the parent
	 * {@link IterableInterval} or the {@link IterableIntervalSubset}, depending
	 * on which finishes earlier.
	 * 
	 * @param interval
	 * @param firstIndex
	 * @param size
	 */
	public IterableIntervalSubset( final IterableInterval< T > interval, final long firstIndex, final long size )
	{
		super( interval );
		this.firstIndex = firstIndex;
		this.size = Math.min( size, interval.size() - firstIndex );
		lastIndex = firstIndex + this.size - 1;
	}

	@Override
	final public Cursor< T > cursor()
	{
		if ( firstIndex == 0 && size == sourceInterval.size() )
			return sourceInterval.cursor();
		return new IISCursor( false );
	}

	@Override
	final public Cursor< T > localizingCursor()
	{
		if ( firstIndex == 0 && size == sourceInterval.size() )
			return sourceInterval.localizingCursor();
		return new IISCursor( true );
	}

	@Override
	public Object iterationOrder()
	{
		return new IISIterationOrder();
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
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
	final public Iterator< T > iterator()
	{
		return cursor();
	}
}
