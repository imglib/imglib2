/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.view;

import java.util.HashMap;
import java.util.Map;

import net.imglib2.AbstractCursor;
import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.View;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * Combines an <em>n</em>-dimensional {@link RandomAccessibleInterval} of
 * same-sized <em>n</em>-dimensional {@link RandomAccessibleInterval}s into a
 * single <em>n</em>-dimensional {@link RandomAccessibleInterval}.
 *
 * @param <T>
 *            the pixel type
 *
 * @author Marcel Wiedenmann (University of Konstanz)
 * @author Christian Dietz (University of Konstanz)
 */
public class CombinedView< T > extends AbstractInterval implements RandomAccessibleInterval< T >, IterableInterval< T >, View
{
	private final RandomAccessibleInterval< RandomAccessibleInterval< T > > source;

	private final long numElements;

	private final long blockNumElements;

	private final long[] blockSize;

	private IterableInterval< RandomAccessibleInterval< T > > sourceAsIterable;

	public CombinedView( final RandomAccessibleInterval< RandomAccessibleInterval< T > > source )
	{
		super( source.numDimensions() );
		this.source = source;
		blockSize = new long[ n ];
		long numEl = 1;
		long blockNumEl = 1;
		final RandomAccessibleInterval< T > block = Util.getTypeFromInterval( source );
		for ( int d = 0; d < n; ++d )
		{
			blockSize[ d ] = block.dimension( d );
			blockNumEl *= blockSize[ d ];
			final long dim = source.dimension( d ) * blockSize[ d ];
			max[ d ] = dim - 1;
			numEl *= dim;
		}
		numElements = numEl;
		blockNumElements = blockNumEl;
	}

	public RandomAccessibleInterval< RandomAccessibleInterval< T > > getSource()
	{
		return source;
	}

	@Override
	public CombinedViewRandomAccess< T > randomAccess()
	{
		return new CombinedViewRandomAccess<>( source, blockSize );
	}

	@Override
	public CombinedViewRandomAccess< T > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	@Override
	public long size()
	{
		return numElements;
	}

	@Override
	public T firstElement()
	{
		return Util.getTypeFromInterval( Util.getTypeFromInterval( source ) );
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public CombinedViewCursor< T > iterator()
	{
		return cursor();
	}

	@Override
	public CombinedViewCursor< T > cursor()
	{
		if ( sourceAsIterable == null )
		{
			sourceAsIterable = Views.iterable( source );
		}
		return new CombinedViewCursor<>( sourceAsIterable, blockSize, blockNumElements );
	}

	@Override
	public CombinedViewCursor< T > localizingCursor()
	{
		return cursor();
	}

	public static class CombinedViewRandomAccess< T > extends Point implements RandomAccess< T >
	{
		private final RandomAccessibleInterval< RandomAccessibleInterval< T > > source;

		private final RandomAccess< RandomAccessibleInterval< T > > sourceAccess;

		private final long[] blockSize;

		private final HashMap< Long, RandomAccess< T > > blockAccesses;

		private final long[] tempIndex;

		private final long[] tempOffset;

		private RandomAccess< T > tempBlockAccess;

		public CombinedViewRandomAccess( final RandomAccessibleInterval< RandomAccessibleInterval< T > > source, final long[] blockSize )
		{
			super( source.numDimensions() );
			this.source = source;
			sourceAccess = source.randomAccess();
			this.blockSize = blockSize;
			blockAccesses = new HashMap<>();
			tempIndex = new long[ n ];
			tempOffset = new long[ n ];
		}

		private CombinedViewRandomAccess( final CombinedViewRandomAccess< T > ra )
		{
			super( ra.position, true );
			source = ra.source;
			sourceAccess = ra.sourceAccess.copyRandomAccess();
			blockSize = ra.blockSize;
			blockAccesses = new HashMap<>( ra.blockAccesses.size() );
			for ( final Map.Entry< Long, RandomAccess< T > > entry : ra.blockAccesses.entrySet() )
			{
				blockAccesses.put( entry.getKey(), entry.getValue().copyRandomAccess() );
			}
			tempIndex = ra.tempIndex.clone();
			tempOffset = ra.tempOffset.clone();
		}

		@Override
		public T get()
		{
			long flatIndex = 0;
			for ( int d = n - 1; d >= 0; --d )
			{
				final long normalizedPosition = position[ d ] - source.min( d );
				tempIndex[ d ] = normalizedPosition / blockSize[ d ];
				tempOffset[ d ] = normalizedPosition % blockSize[ d ];
				flatIndex = flatIndex * source.dimension( d ) + tempIndex[ d ];
			}
			if ( blockAccesses.containsKey( flatIndex ) )
			{
				tempBlockAccess = blockAccesses.get( flatIndex );
			}
			else
			{
				sourceAccess.setPosition( tempIndex );
				// TODO: [Review] There are more efficient ways than creating a
				// new view each time. E.g, we could wrap the block's random
				// access in an own random access that deals with translation
				// (unfortunately, net.imglib2.view.TranslationRandomAccess has
				// no public constructor).
				tempBlockAccess = Views.zeroMin( sourceAccess.get() ).randomAccess();
				blockAccesses.put( flatIndex, tempBlockAccess );
			}
			tempBlockAccess.setPosition( tempOffset );
			return tempBlockAccess.get();
		}

		@Override
		public CombinedViewRandomAccess< T > copy()
		{
			return new CombinedViewRandomAccess<>( this );
		}

		@Override
		public CombinedViewRandomAccess< T > copyRandomAccess()
		{
			return copy();
		}
	}

	// NB: This cursor speeds up iteration if source is ArrangedView (which has
	// a flat list of RAIs as source).
	public static class CombinedViewCursor< T > extends AbstractCursor< T >
	{
		private final IterableInterval< RandomAccessibleInterval< T > > source;

		private final Cursor< RandomAccessibleInterval< T > > sourceCursor;

		private final long[] blockSize;

		private final long blockMaxIndex;

		private long tempIndex;

		private Cursor< T > tempBlockCursor;

		public CombinedViewCursor( final IterableInterval< RandomAccessibleInterval< T > > source, final long[] blockSize, final long blockNumElements )
		{
			super( source.numDimensions() );
			this.source = source;
			sourceCursor = source.cursor();
			this.blockSize = blockSize;
			this.blockMaxIndex = blockNumElements - 1;
			incrementBlock();
		}

		private CombinedViewCursor( final CombinedViewCursor< T > cursor )
		{
			super( cursor.n );
			source = cursor.source;
			sourceCursor = cursor.sourceCursor.copyCursor();
			blockSize = cursor.blockSize;
			blockMaxIndex = cursor.blockMaxIndex;
			tempIndex = cursor.tempIndex;
			tempBlockCursor = cursor.tempBlockCursor.copyCursor();
		}

		@Override
		public T get()
		{
			return tempBlockCursor.get();
		}

		@Override
		public void fwd()
		{
			if ( tempIndex >= blockMaxIndex )
			{
				incrementBlock();
			}
			++tempIndex;
			tempBlockCursor.fwd();
		}

		@Override
		public void jumpFwd( long steps )
		{
			tempIndex += steps;
			if ( tempIndex >= blockMaxIndex )
			{
				steps = tempIndex - blockMaxIndex - 1;
				incrementBlock();
				tempIndex += steps;
			}
			tempBlockCursor.jumpFwd( steps );
		}

		@Override
		public void reset()
		{
			sourceCursor.reset();
			incrementBlock();
		}

		@Override
		public boolean hasNext()
		{
			return tempIndex < blockMaxIndex || sourceCursor.hasNext();
		}

		@Override
		public void localize( final long[] position )
		{
			sourceCursor.localize( position );
			for ( int d = 0; d < n; ++d )
			{
				position[ d ] = position[ d ] * blockSize[ d ] + tempBlockCursor.getLongPosition( d );
			}
		}

		@Override
		public long getLongPosition( final int d )
		{
			return sourceCursor.getLongPosition( d ) * blockSize[ d ] + tempBlockCursor.getLongPosition( d );
		}

		@Override
		public CombinedViewCursor< T > copy()
		{
			return new CombinedViewCursor<>( this );
		}

		@Override
		public CombinedViewCursor< T > copyCursor()
		{
			return copy();
		}

		private void incrementBlock()
		{
			tempIndex = -1;
			tempBlockCursor = Views.iterable( sourceCursor.next() ).cursor();
		}
	}
}
