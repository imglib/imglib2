/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
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
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;

/**
 * TODO
 * 
 * @author Stephan Saalfeld
 * @author Lee Kamentsky
 * @author leek
 */
public class BinaryMaskRegionOfInterest< T extends BitType, I extends Img< T >> extends AbstractRegionOfInterest implements IterableRegionOfInterest
{
	final I img;

	/*
	 * One RandomAccess per thread so that the thread can call setPosition.
	 */
	final ThreadLocal< RandomAccess< T >> randomAccess;

	long cached_size = -1;

	long[] firstPosition;

	long[] minima;

	long[] maxima;

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
				cursor = img.localizingCursor();
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
			public void localize( long[] position )
			{
				System.arraycopy( this.position, 0, position, 0, numDimensions() );
			}

			@Override
			public long getLongPosition( int d )
			{
				return this.position[ d ];
			}

			@Override
			public AbstractCursor< TT > copy()
			{
				return copyCursor();
			}

			@Override
			public AbstractCursor< TT > copyCursor()
			{
				BMROICursor c = new BMROICursor();
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
			src.setPosition( getFirstPosition() );
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
		public double realMin( int d )
		{
			return img.realMin( d );
		}

		@Override
		public void realMin( double[] min )
		{
			img.realMin( min );
		}

		@Override
		public void realMin( RealPositionable min )
		{
			img.realMin( min );
		}

		@Override
		public double realMax( int d )
		{
			return img.realMax( d );
		}

		@Override
		public void realMax( double[] max )
		{
			img.realMax( max );
		}

		@Override
		public void realMax( RealPositionable max )
		{
			img.realMax( max );
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
		public long min( int d )
		{
			validate();
			return minima[ d ];
		}

		@Override
		public void min( long[] min )
		{
			validate();
			System.arraycopy( minima, 0, min, 0, numDimensions() );
		}

		@Override
		public void min( Positionable min )
		{
			validate();
			min.setPosition( minima );
		}

		@Override
		public long max( int d )
		{
			validate();
			return maxima[ d ];
		}

		@Override
		public void max( long[] max )
		{
			validate();
			System.arraycopy( maxima, 0, max, 0, numDimensions() );
		}

		@Override
		public void max( Positionable max )
		{
			validate();
			max.setPosition( maxima );
		}

		@Override
		public void dimensions( long[] dimensions )
		{
			img.dimensions( dimensions );
		}

		@Override
		public long dimension( int d )
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
	public < TT extends Type< TT >> IterableInterval< TT > getIterableIntervalOverROI( RandomAccessible< TT > src )
	{
		return new BMROIIterableInterval< TT >( src.randomAccess() );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.roi.AbstractRegionOfInterest#isMember(double[])
	 */
	@Override
	protected boolean isMember( double[] position )
	{
		/*
		 * Quantize by nearest-neighbor (-0.5 < x < 0.5)
		 */
		validate();
		for ( int i = 0; i < numDimensions(); i++ )
		{
			long lPosition = Math.round( position[ i ] );
			if ( ( lPosition < minima[ i ] ) || ( lPosition > maxima[ i ] ) )
				return false;
			randomAccess.get().setPosition( lPosition, i );
		}
		return randomAccess.get().get().get();
	}

	@Override
	protected void getRealExtrema( double[] minima, double[] maxima )
	{
		validate();
		for ( int i = 0; i < numDimensions(); i++ )
		{
			minima[ i ] = this.minima[ i ];
			maxima[ i ] = this.maxima[ i ];
		}
	}

	/**
	 * Scan the image, counting bits once, then return the cached value.
	 * 
	 * @return
	 */
	protected long getCachedSize()
	{
		validate();
		return cached_size;
	}

	protected long[] getFirstPosition()
	{
		validate();
		return firstPosition;
	}

	protected void validate()
	{
		if ( cached_size == -1 )
		{
			cached_size = 0;
			minima = new long[ numDimensions() ];
			maxima = new long[ numDimensions() ];
			Cursor< T > c = img.localizingCursor();
			while ( c.hasNext() )
			{
				if ( c.next().get() )
				{
					cached_size = 1;
					firstPosition = new long[ numDimensions() ];
					c.localize( firstPosition );
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
						long pos = c.getLongPosition( i );
						minima[ i ] = Math.min( minima[ i ], pos );
						maxima[ i ] = Math.max( maxima[ i ], pos );
					}
				}
			}
		}
	}
}
