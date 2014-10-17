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

package net.imglib2.roi;

import java.util.Iterator;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

/**
 * TODO
 * 
 * @author Stephan Saalfeld
 * @author Lee Kamentsky
 * @author leek
 */
public class BinaryMaskRegionOfInterest< T extends BitType, I extends RandomAccessibleInterval< T >> extends AbstractRegionOfInterest implements IterableRegionOfInterest
{
	final I img;

	/*
	 * One RandomAccess per thread so that the thread can call setPosition.
	 */
	final ThreadLocal< RandomAccess< T >> randomAccess;

	long cached_size = -1;

	long[] firstRelPos;

	long[] minima;

	long[] maxima;

	double[] origin;

	protected class BMROIIterationOrder
	{
		protected I getImg()
		{
			return img;
		}

		@Override
		public boolean equals( final Object obj )
		{
			if ( !( obj instanceof BinaryMaskRegionOfInterest.BMROIIterationOrder ) )
				return false;

			@SuppressWarnings( "unchecked" )
			final BMROIIterationOrder o = ( BMROIIterationOrder ) obj;
			return o.getImg() == getImg();
		}
	}

	protected class BMROIIterableInterval< TT extends Type< TT >> implements IterableInterval< TT >
	{
		final RandomAccess< TT > src;

		/**
		 * The cursor works by managing a cursor from the original image. It
		 * advances the underlying cursor to the next true position with each
		 * fwd() step.
		 */
		protected class BMROICursor extends AbstractCursor< TT >
		{
			boolean nextIsValid;

			boolean cursorHasNext;

			Cursor< T > cursor;

			final long[] position;

			protected BMROICursor()
			{
				super( BMROIIterableInterval.this.numDimensions() );
				cursor = Views.iterable( img ).localizingCursor();
				position = new long[ BMROIIterableInterval.this.numDimensions() ];
			}

			@Override
			public TT get()
			{
				src.setPosition( this );
				return src.get();
			}

			@Override
			public void fwd()
			{
				validateNext();
				cursor.localize( position );
				nextIsValid = false;
			}

			@Override
			public void reset()
			{
				cursor.reset();
				nextIsValid = false;
			}

			@Override
			public boolean hasNext()
			{
				validateNext();
				return cursorHasNext;
			}

			@Override
			public void localize( final long[] pos )
			{
				for ( int i = 0; i < numDimensions(); i++ )
					pos[ i ] = position[ i ] + ( long ) origin[ i ];
			}

			@Override
			public long getLongPosition( final int d )
			{
				return this.position[ d ] + ( long ) origin[ d ];
			}

			@Override
			public AbstractCursor< TT > copy()
			{
				return copyCursor();
			}

			@Override
			public AbstractCursor< TT > copyCursor()
			{
				final BMROICursor c = new BMROICursor();
				c.cursor = cursor.copyCursor();
				System.arraycopy( position, 0, c.position, 0, numDimensions() );
				c.nextIsValid = nextIsValid;
				return c;
			}

			private void validateNext()
			{
				if ( !nextIsValid )
				{
					while ( cursor.hasNext() )
					{
						if ( cursor.next().get() )
						{
							nextIsValid = true;
							cursorHasNext = true;
							return;
						}
					}
					nextIsValid = true;
					cursorHasNext = false;
				}
			}
		}

		protected BMROIIterableInterval( final RandomAccess< TT > src )
		{
			this.src = src;
		}

		@Override
		public long size()
		{
			return getCachedSize();
		}

		@Override
		public TT firstElement()
		{
			src.setPosition( getFirstRelativePosition() );
			return src.get();
		}

		@Override
		public Object iterationOrder()
		{
			return new BMROIIterationOrder();
		}

		@Override
		public boolean equalIterationOrder( final IterableRealInterval< ? > f )
		{
			return iterationOrder().equals( f.iterationOrder() );
		}

		@Override
		public double realMin( final int d )
		{
			return img.realMin( d ) + origin[ d ];
		}

		@Override
		public void realMin( final double[] min )
		{
			img.realMin( min );
			for ( int i = 0; i < min.length; i++ )
				min[ i ] += origin[ i ];
		}

		@Override
		public void realMin( final RealPositionable min )
		{
			img.realMin( min );
			min.move( origin );
		}

		@Override
		public double realMax( final int d )
		{
			return img.realMax( d ) + origin[ d ];
		}

		@Override
		public void realMax( final double[] max )
		{
			img.realMax( max );
			for ( int i = 0; i < max.length; i++ )
				max[ i ] += origin[ i ];
		}

		@Override
		public void realMax( final RealPositionable max )
		{
			img.realMax( max );
			max.move( origin );
		}

		@Override
		public int numDimensions()
		{
			return BinaryMaskRegionOfInterest.this.numDimensions();
		}

		@Override
		public Iterator< TT > iterator()
		{
			return new BMROICursor();
		}

		@Override
		public long min( final int d )
		{
			validate();
			return minima[ d ] + ( long ) origin[ d ];
		}

		@Override
		public void min( final long[] min )
		{
			validate();
			for ( int i = 0; i < numDimensions(); i++ )
				min[ i ] = minima[ i ] + ( long ) origin[ i ];
		}

		@Override
		public void min( final Positionable min )
		{
			validate();
			for ( int i = 0; i < min.numDimensions(); i++ )
			{
				min.setPosition( minima[ i ] + ( long ) origin[ i ], i );
			}
		}

		@Override
		public long max( final int d )
		{
			validate();
			return maxima[ d ] + ( long ) origin[ d ];
		}

		@Override
		public void max( final long[] max )
		{
			validate();
			for ( int i = 0; i < numDimensions(); i++ )
				max[ i ] = maxima[ i ] + ( long ) origin[ i ];
		}

		@Override
		public void max( final Positionable max )
		{
			validate();
			for ( int i = 0; i < max.numDimensions(); i++ )
			{
				max.setPosition( maxima[ i ] + ( long ) origin[ i ], i );
			}
		}

		@Override
		public void dimensions( final long[] dimensions )
		{
			img.dimensions( dimensions );
		}

		@Override
		public long dimension( final int d )
		{
			return img.dimension( d );
		}

		@Override
		public Cursor< TT > cursor()
		{
			return new BMROICursor();
		}

		@Override
		public Cursor< TT > localizingCursor()
		{
			return new BMROICursor();
		}
	}

	public BinaryMaskRegionOfInterest( final I img )
	{
		super( img.numDimensions() );
		this.img = img;
		origin = new double[ img.numDimensions() ];
		randomAccess = new ThreadLocal< RandomAccess< T >>()
		{

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.ThreadLocal#initialValue()
			 */
			@Override
			protected RandomAccess< T > initialValue()
			{
				return img.randomAccess();
			}
		};
	}

	@Override
	public < TT extends Type< TT >> IterableInterval< TT > getIterableIntervalOverROI( final RandomAccessible< TT > src )
	{
		return new BMROIIterableInterval< TT >( src.randomAccess() );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.roi.AbstractRegionOfInterest#isMember(double[])
	 */
	@Override
	public boolean contains( final double[] position )
	{
		/*
		 * Quantize by nearest-neighbor (-0.5 < x < 0.5)
		 */
		validate();
		for ( int i = 0; i < numDimensions(); i++ )
		{
			final long lPosition = ( long ) ( position[ i ] - origin[ i ] );
			if ( ( lPosition < minima[ i ] ) || ( lPosition > maxima[ i ] ) )
				return false;
			randomAccess.get().setPosition( lPosition, i );
		}
		return randomAccess.get().get().get();
	}

	@Override
	protected void getRealExtrema( final double[] min, final double[] max )
	{
		validate();
		for ( int i = 0; i < numDimensions(); i++ )
		{
			min[ i ] = this.minima[ i ] + origin[ i ];
			max[ i ] = this.maxima[ i ] + origin[ i ];
		}
	}

	/**
	 * Scan the image, counting bits once, then return the cached value.
	 */
	protected long getCachedSize()
	{
		validate();
		return cached_size;
	}

	protected long[] getFirstPosition()
	{
		final long[] pos = getFirstRelativePosition();
		for ( int i = 0; i < pos.length; i++ )
			pos[ i ] += origin[ i ];
		return pos;
	}

	protected long[] getFirstRelativePosition()
	{
		validate();
		return firstRelPos;
	}

	protected void validate()
	{
		if ( cached_size == -1 )
		{
			cached_size = 0;
			minima = new long[ numDimensions() ];
			maxima = new long[ numDimensions() ];
			final Cursor< T > c = Views.iterable( img ).localizingCursor();
			while ( c.hasNext() )
			{
				if ( c.next().get() )
				{
					cached_size = 1;
					firstRelPos = new long[ numDimensions() ];
					c.localize( firstRelPos );
					c.localize( minima );
					c.localize( maxima );
					break;
				}
			}
			while ( c.hasNext() )
			{
				if ( c.next().get() )
				{
					cached_size++;
					for ( int i = 0; i < numDimensions(); i++ )
					{
						final long pos = c.getLongPosition( i );
						minima[ i ] = Math.min( minima[ i ], pos );
						maxima[ i ] = Math.max( maxima[ i ], pos );
					}
				}
			}
		}
	}

	@Override
	public void move( final double displacement, final int d )
	{
		origin[ d ] += displacement;
	}

	public I getImg()
	{
		return img;
	}

	public double[] getOrigin()
	{
		return origin;
	}
}
