/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.list;

import net.imglib2.AbstractCursor;
import net.imglib2.AbstractLocalizable;
import net.imglib2.AbstractLocalizingCursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.type.Type;
import net.imglib2.util.IntervalIndexer;

/**
 * Abstract base class for {@link Img} that store pixels in a single linear
 * list addressed by a long.  The number of entities that can be addressed is
 * limited to {@link Long#MAX_VALUE}.
 *
 * Derived classes need to implement the {@link #get(long)} and
 * {@link #set(long, Object)} methods that are used by accessors to access
 * pixels.
 *
 * @param <T>
 *            The value type of the pixels. You can us {@link Type}s or
 *            arbitrary {@link Object}s. If you use non-{@link Type} pixels you
 *            cannot use {@link Type#set(Type)} to change the value stored in
 *            every reference.  Instead, you can use the
 *            {@link LongListCursor#set(Object)} and
 *            {@link LongListRandomAccess#set(Object)} methods to alter pixels.
 *
 * @author Stephan Saalfeld
 * @author Stephan Preibisch
 * @author Tobias Pietzsch
 */
public abstract class AbstractLongListImg< T > extends AbstractImg< T >
{
	public class LongListCursor extends AbstractCursor< T >
	{
		private long i;

		final private long maxNumPixels;

		protected LongListCursor( final LongListCursor cursor )
		{
			super( cursor.numDimensions() );

			this.maxNumPixels = cursor.maxNumPixels;

			i = cursor.i;
		}

		public LongListCursor()
		{
			super( AbstractLongListImg.this.numDimensions() );

			this.maxNumPixels = size() - 1;

			reset();
		}

		@Override
		public T get()
		{
			return AbstractLongListImg.this.get( i );
		}

		public void set( final T t )
		{
			AbstractLongListImg.this.set( i, t );
		}

		@Override
		public LongListCursor copy()
		{
			return new LongListCursor( this );
		}

		@Override
		public LongListCursor copyCursor()
		{
			return copy();
		}

		@Override
		public boolean hasNext()
		{
			return i < maxNumPixels;
		}

		@Override
		public void jumpFwd( final long steps )
		{
			i += steps;
		}

		@Override
		public void fwd()
		{
			++i;
		}

		@Override
		public void reset()
		{
			i = -1;
		}

		@Override
		public void localize( final long[] position )
		{
			IntervalIndexer.indexToPosition( i, dimension, position );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return IntervalIndexer.indexToPosition( i, dimension, step, d );
		}
	}

	public class LongListLocalizingCursor extends AbstractLocalizingCursor< T >
	{
		private long i;

		final private long maxNumPixels;

		final private long[] max;

		public LongListLocalizingCursor( final LongListLocalizingCursor cursor )
		{
			super( cursor.numDimensions() );

			maxNumPixels = cursor.maxNumPixels;

			max = new long[ n ];
			for ( int d = 0; d < n; ++d )
			{
				max[ d ] = cursor.max[ d ];
				position[ d ] = cursor.position[ d ];
			}

			i = cursor.i;
		}

		public LongListLocalizingCursor()
		{
			super( AbstractLongListImg.this.numDimensions() );

			maxNumPixels = size() - 1;

			max = new long[ n ];
			max( max );

			reset();
		}

		@Override
		public void fwd()
		{
			++i;
			for ( int d = 0; d < n; d++ )
				if ( ++position[ d ] > max[ d ] )
					position[ d ] = 0;
				else
					break;
		}

		@Override
		public void jumpFwd( final long steps )
		{
			i += steps;
			IntervalIndexer.indexToPosition( i, dimension, position );
		}

		@Override
		public boolean hasNext()
		{
			return i < maxNumPixels;
		}

		@Override
		public void reset()
		{
			i = -1;

			position[ 0 ] = -1;

			for ( int d = 1; d < n; d++ )
				position[ d ] = 0;
		}

		@Override
		public T get()
		{
			return AbstractLongListImg.this.get( i );
		}

		public void set( final T t )
		{
			AbstractLongListImg.this.set( i, t );
		}

		@Override
		public LongListLocalizingCursor copy()
		{
			return new LongListLocalizingCursor( this );
		}

		@Override
		public LongListLocalizingCursor copyCursor()
		{
			return copy();
		}
	}

	public class LongListRandomAccess extends AbstractLocalizable implements RandomAccess< T >
	{
		private long i;

		public LongListRandomAccess( final LongListRandomAccess randomAccess )
		{
			super( randomAccess.numDimensions() );

			for ( int d = 0; d < n; ++d )
				position[ d ] = randomAccess.position[ d ];

			i = randomAccess.i;
		}

		public LongListRandomAccess()
		{
			super( AbstractLongListImg.this.numDimensions() );

			i = 0;
		}

		@Override
		public T get()
		{
			return AbstractLongListImg.this.get( i );
		}

		public void set( final T t )
		{
			AbstractLongListImg.this.set( i, t );
		}

		@Override
		public void fwd( final int d )
		{
			i += step[ d ];
			++position[ d ];
		}

		@Override
		public void bck( final int d )
		{
			i -= step[ d ];
			--position[ d ];
		}

		@Override
		public void move( final int distance, final int d )
		{
			i += step[ d ] * distance;
			position[ d ] += distance;
		}

		@Override
		public void move( final long distance, final int d )
		{
			i += step[ d ] * distance;
			position[ d ] += distance;
		}

		@Override
		public void move( final Localizable localizable )
		{
			for ( int d = 0; d < n; ++d )
				move( localizable.getLongPosition( d ), d );
		}

		@Override
		public void move( final int[] distance )
		{
			for ( int d = 0; d < n; ++d )
				move( distance[ d ], d );
		}

		@Override
		public void move( final long[] distance )
		{
			for ( int d = 0; d < n; ++d )
				move( distance[ d ], d );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			position[ 0 ] = i = localizable.getLongPosition( 0 );
			for ( int d = 1; d < n; ++d )
			{
				position[ d ] = localizable.getLongPosition( d );
				i += position[ d ] * step[ d ];
			}
		}

		@Override
		public void setPosition( final int[] position )
		{
			i = position[ 0 ];
			this.position[ 0 ] = i;
			for ( int d = 1; d < n; ++d )
			{
				final long p = position[ d ];
				i += p * step[ d ];
				this.position[ d ] = p;
			}
		}

		@Override
		public void setPosition( final long[] position )
		{
			i = position[ 0 ];
			this.position[ 0 ] = i;
			for ( int d = 1; d < n; ++d )
			{
				final long p = position[ d ];
				i += p * step[ d ];
				this.position[ d ] = p;
			}
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			i += step[ d ] * ( position - this.position[ d ] );
			this.position[ d ] = position;
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			i += step[ d ] * ( position - this.position[ d ] );
			this.position[ d ] = position;
		}

		@Override
		public LongListRandomAccess copy()
		{
			return new LongListRandomAccess( this );
		}

		@Override
		public LongListRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}

	final protected long[] step;

	protected AbstractLongListImg( final long[] dimensions )
	{
		super( dimensions );

		step = new long[ n ];
		IntervalIndexer.createAllocationSteps( dimension, step );
	}

	protected abstract T get( final long index );

	protected abstract void set( final long index, final T value );

	@Override
	public LongListCursor cursor()
	{
		return new LongListCursor();
	}

	@Override
	public LongListLocalizingCursor localizingCursor()
	{
		return new LongListLocalizingCursor();
	}

	@Override
	public LongListRandomAccess randomAccess()
	{
		return new LongListRandomAccess();
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( this );
	}
}
