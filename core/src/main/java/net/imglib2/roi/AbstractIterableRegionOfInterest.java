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

import java.util.Arrays;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.type.Type;

/**
 * TODO
 * 
 * @author Stephan Saalfeld
 */
public abstract class AbstractIterableRegionOfInterest extends AbstractRegionOfInterest implements IterableRegionOfInterest
{
	static final private long SIZE_NOT_CACHED = -1;

	private long cached_size = SIZE_NOT_CACHED;

	private long[] cached_min;

	private long[] cached_max;

	protected AbstractIterableRegionOfInterest( final int nDimensions )
	{
		super( nDimensions );
	}

	/**
	 * Advance the position to the next raster.
	 * 
	 * The AbstractRegionOfInterest successively adds one to the lowest
	 * dimension of the position until the position is outside the ROI as
	 * determined by isMember. At this point, it calls nextRaster to find the
	 * start of the next raster.
	 * 
	 * As an example, a rectangle might have x, y, width and height. The code
	 * would determine if the y position was before, within or after the
	 * rectangle bounds. If before, set position[0] = x, position[1] = y and
	 * return true, if within, set position[0] = x, position[1] += 1, if after,
	 * return false.
	 * 
	 * @param position
	 *            on entry, the position of the raster after advancement to its
	 *            end (or initial or final position)
	 * @param end
	 *            on exit, the coordinates of the end of the raster. Index 0 is
	 *            generally the only pertinent variable, subsequent indices
	 *            should be duplicates of the start raster. Nevertheless, using
	 *            an array lets the caller pass the results as a modification of
	 *            the array.
	 * @return true if there is a raster after this one.
	 */
	protected abstract boolean nextRaster( long[] position, long[] end );

	/**
	 * Advance the position to the next raster, taking care that the raster is
	 * wholly within the given interval.
	 * 
	 * Consider overriding if knowing the interval can be used to skip rasters
	 * outside of the interval.
	 * 
	 * @param position
	 *            on entry, the position of the raster after advancement to its
	 *            end (or initial or final position)
	 * @param end
	 *            on exit, the coordinates of the end of the raster. Index 0 is
	 *            generally the only pertinent variable, subsequent indices
	 *            should be duplicates of the start raster. Nevertheless, using
	 *            an array lets the caller pass the results as a modification of
	 *            the array.
	 * @param interval
	 *            the raster must be within this interval.
	 * @return true if there is a raster after this one.
	 */
	protected boolean nextRaster( final long[] position, final long[] end, final Interval interval )
	{
		loop: while ( nextRaster( position, end ) )
		{

			for ( int i = 0; i < position.length; i++ )
			{
				if ( position[ i ] > interval.max( i ) )
					continue loop;
			}
			for ( int i = 1; i < position.length; i++ )
			{
				if ( position[ i ] < interval.min( i ) )
					continue loop;
			}
			if ( end[ 0 ] <= interval.min( 0 ) )
				continue;
			position[ 0 ] = Math.max( position[ 0 ], interval.min( 0 ) );
			end[ 0 ] = Math.min( end[ 0 ], interval.max( 0 ) + 1 );
			return true;
		}
		return false;
	}

	/**
	 * Jump forward a certain number of steps from the given position.
	 * 
	 * The implementer may want to override this function. For instance, for a
	 * rectangle, the implementer may want to advance the position by the number
	 * of steps if the number of steps is less than x - width or perform a more
	 * complicated operation involving division by the width if the number of
	 * steps is greater.
	 * 
	 * @param totalSteps
	 *            - number of steps to move
	 * @param position
	 *            - the internal position which should be advanced by the number
	 *            of steps
	 * @param end
	 *            - the end position of the current raster on entry and on exit.
	 * @param interval
	 *            - constraining interval. Pixels will only be counted while
	 *            jumping for the region within the interval. If interval is
	 *            null, there is no constraining interval.
	 * @return true if taking that number of steps still lands within the ROI.
	 */
	protected boolean jumpFwd( final long totalSteps, final long[] position, final long[] end, final Interval interval )
	{
		long steps = totalSteps;
		while ( true )
		{
			if ( position[ 0 ] + steps < end[ 0 ] )
			{
				position[ 0 ] += steps;
				return true;
			}
			steps -= end[ 0 ] - position[ 0 ];
			position[ 0 ] = end[ 0 ];
			if ( interval != null )
			{
				if ( !nextRaster( position, end, interval ) )
					return false;
			}
			else if ( !nextRaster( position, end ) )
				return false;
		}
	}

	protected class AROIIterationOrder
	{
		private AbstractIterableRegionOfInterest getEnclosingClass()
		{
			return AbstractIterableRegionOfInterest.this;
		}

		@Override
		public boolean equals( final Object obj )
		{
			if ( !( obj instanceof AROIIterationOrder ) )
				return false;

			final AROIIterationOrder o = ( AROIIterationOrder ) obj;
			return o.getEnclosingClass() == getEnclosingClass();
		}
	}

	protected class AROIIterableInterval< T extends Type< T >>
			implements IterableInterval< T >
	{
		protected RandomAccessible< T > src;

		protected T cached_first_element;

		public AROIIterableInterval( final RandomAccessible< T > src )
		{
			this.src = src;
		}

		protected class AROICursor implements Cursor< T >
		{
			private RandomAccess< T > randomAccess = AROIIterableInterval.this.src.randomAccess();

			private final long[] position = new long[ AbstractIterableRegionOfInterest.this.numDimensions() ];

			private final long[] next_position = new long[ AbstractIterableRegionOfInterest.this.numDimensions() ];

			private final long[] raster_end = new long[ AbstractIterableRegionOfInterest.this.numDimensions() ];

			private final long[] next_raster_end = new long[ AbstractIterableRegionOfInterest.this.numDimensions() ];

			private boolean next_is_valid = false;

			private boolean has_next;

			private boolean src_is_valid = false;

			private void mark_dirty()
			{
				next_is_valid = false;
				src_is_valid = false;
			}

			protected AROICursor( final AROICursor cursor )
			{
				randomAccess = cursor.randomAccess.copyRandomAccess();
				for ( int d = 0; d < position.length; ++d )
				{
					position[ d ] = cursor.position[ d ];
					next_position[ d ] = cursor.next_position[ d ];
					raster_end[ d ] = cursor.raster_end[ d ];
					next_raster_end[ d ] = cursor.next_raster_end[ d ];
					next_is_valid = cursor.next_is_valid;
					has_next = cursor.has_next;
					src_is_valid = cursor.src_is_valid;
				}
			}

			public AROICursor()
			{
				reset();
			}

			@Override
			public void localize( final float[] pos )
			{
				for ( int d = 0; d < pos.length; d++ )
					pos[ d ] = this.position[ d ];
			}

			@Override
			public void localize( final double[] pos )
			{
				for ( int d = 0; d < pos.length; d++ )
					pos[ d ] = this.position[ d ];
			}

			@Override
			public void localize( final int[] pos )
			{
				for ( int d = 0; d < pos.length; d++ )
					pos[ d ] = ( int ) this.position[ d ];

			}

			@Override
			public void localize( final long[] pos )
			{
				for ( int d = 0; d < pos.length; d++ )
					pos[ d ] = this.position[ d ];
			}

			@Override
			public float getFloatPosition( final int dim )
			{
				return position[ dim ];
			}

			@Override
			public double getDoublePosition( final int dim )
			{
				return position[ dim ];
			}

			@Override
			public int getIntPosition( final int dim )
			{
				return ( int ) position[ dim ];
			}

			@Override
			public long getLongPosition( final int dim )
			{
				return position[ dim ];
			}

			@Override
			public int numDimensions()
			{
				return AbstractIterableRegionOfInterest.this.numDimensions();
			}

			@Override
			public T get()
			{
				if ( !src_is_valid )
				{
					randomAccess.setPosition( position );
				}
				return randomAccess.get();
			}

			@Override
			public void jumpFwd( final long steps )
			{
				final Interval interval = ( src instanceof Interval ) ? ( Interval ) src : null;
				if ( !AbstractIterableRegionOfInterest.this.jumpFwd( steps, position, raster_end, interval ) ) { throw new IllegalAccessError( "Jumped past end of sequence" ); }
				mark_dirty();
			}

			@Override
			public void fwd()
			{
				if ( !hasNext() ) { throw new IllegalAccessError( "fwd called at end of sequence" ); }
				for ( int i = 0; i < position.length; i++ )
				{
					position[ i ] = next_position[ i ];
					raster_end[ i ] = next_raster_end[ i ];
				}
				mark_dirty();
			}

			@Override
			public void reset()
			{
				for ( final long[] a : new long[][] { position, next_position, raster_end, next_raster_end } )
				{
					Arrays.fill( a, Long.MIN_VALUE );
				}
				next_raster_end[ 0 ]++;
				mark_dirty();
			}

			@Override
			public boolean hasNext()
			{
				if ( !next_is_valid )
				{
					final Interval interval = ( src instanceof Interval ) ? ( Interval ) src : null;
					has_next = AbstractIterableRegionOfInterest.this.jumpFwd( 1, next_position, next_raster_end, interval );
				}
				next_is_valid = true;
				return has_next;
			}

			@Override
			public T next()
			{
				fwd();
				return get();
			}

			@Override
			public void remove()
			{
				AbstractIterableRegionOfInterest.this.remove( position );
				mark_dirty();
			}

			@Override
			public AROICursor copy()
			{
				return new AROICursor( this );
			}

			@Override
			public AROICursor copyCursor()
			{
				return copy();
			}
		}

		@Override
		public long size()
		{
			return getCachedSize();
		}

		@Override
		public T firstElement()
		{
			if ( cached_first_element == null )
			{
				final RandomAccess< T > r = src.randomAccess();
				final long[] position = new long[ numDimensions() ];
				final long[] raster_end = new long[ numDimensions() ];
				Arrays.fill( position, Long.MIN_VALUE );
				Arrays.fill( raster_end, Long.MIN_VALUE );
				boolean hasNext;
				if ( src instanceof Interval )
				{
					hasNext = nextRaster( position, raster_end, ( Interval ) src );
				}
				else
				{
					hasNext = nextRaster( position, raster_end );
				}
				if ( !hasNext ) { throw new IllegalAccessError( "Tried to get first element, but ROI has no elements" ); }
				r.setPosition( position );
				cached_first_element = r.get();
			}
			return cached_first_element;
		}

		@Override
		public Object iterationOrder()
		{
			return new AROIIterationOrder();
		}

		@Override
		public boolean equalIterationOrder( final IterableRealInterval< ? > f )
		{
			return iterationOrder().equals( f.iterationOrder() );
		}

		@Override
		public double realMin( final int d )
		{
			return AbstractIterableRegionOfInterest.this.realMin( d );
		}

		@Override
		public void realMin( final double[] min )
		{
			AbstractIterableRegionOfInterest.this.realMin( min );
		}

		@Override
		public void realMin( final RealPositionable min )
		{
			AbstractIterableRegionOfInterest.this.realMin( min );
		}

		@Override
		public double realMax( final int d )
		{
			return AbstractIterableRegionOfInterest.this.realMax( d );
		}

		@Override
		public void realMax( final double[] max )
		{
			AbstractIterableRegionOfInterest.this.realMax( max );
		}

		@Override
		public void realMax( final RealPositionable max )
		{
			AbstractIterableRegionOfInterest.this.realMax( max );
		}

		@Override
		public int numDimensions()
		{
			return AbstractIterableRegionOfInterest.this.numDimensions();
		}

		@Override
		public Iterator< T > iterator()
		{
			return new AROICursor();
		}

		@Override
		public long min( final int d )
		{
			return AbstractIterableRegionOfInterest.this.min( d );
		}

		@Override
		public void min( final long[] min )
		{
			AbstractIterableRegionOfInterest.this.min( min );
		}

		@Override
		public void min( final Positionable min )
		{
			AbstractIterableRegionOfInterest.this.min( min );
		}

		@Override
		public long max( final int d )
		{
			return AbstractIterableRegionOfInterest.this.max( d );
		}

		@Override
		public void max( final long[] max )
		{
			AbstractIterableRegionOfInterest.this.max( max );
		}

		@Override
		public void max( final Positionable max )
		{
			AbstractIterableRegionOfInterest.this.max( max );
		}

		@Override
		public void dimensions( final long[] dimensions )
		{
			AbstractIterableRegionOfInterest.this.dimensions( dimensions );
		}

		@Override
		public long dimension( final int d )
		{
			return AbstractIterableRegionOfInterest.this.dimension( d );
		}

		@Override
		public Cursor< T > cursor()
		{
			return new AROICursor();
		}

		@Override
		public Cursor< T > localizingCursor()
		{
			return new AROICursor();
		}

	}

	protected class AROIClippedIterableInterval< T extends Type< T >> extends AROIIterableInterval< T >
	{

		public AROIClippedIterableInterval( final RandomAccessibleInterval< T > src )
		{
			super( src );
		}

		protected Interval getSrcInterval()
		{
			return ( Interval ) src;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #realMin(int)
		 */
		@Override
		public double realMin( final int d )
		{
			return Math.max( super.realMin( d ), getSrcInterval().realMin( d ) );
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #realMin(double[])
		 */
		@Override
		public void realMin( final double[] min )
		{
			for ( int i = 0; i < min.length; i++ )
			{
				min[ i ] = realMin( i );
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #realMin(net.imglib2.RealPositionable)
		 */
		@Override
		public void realMin( final RealPositionable min )
		{
			for ( int i = 0; i < min.numDimensions(); i++ )
			{
				min.setPosition( realMin( i ), i );
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #realMax(int)
		 */
		@Override
		public double realMax( final int d )
		{
			return Math.min( super.realMax( d ), getSrcInterval().realMax( d ) );
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #realMax(double[])
		 */
		@Override
		public void realMax( final double[] max )
		{
			for ( int i = 0; i < max.length; i++ )
			{
				max[ i ] = realMax( i );
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #realMax(net.imglib2.RealPositionable)
		 */
		@Override
		public void realMax( final RealPositionable max )
		{
			for ( int i = 0; i < max.numDimensions(); i++ )
			{
				max.setPosition( realMax( i ), i );
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #min(int)
		 */
		@Override
		public long min( final int d )
		{
			return Math.max( super.min( d ), getSrcInterval().min( d ) );
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #min(long[])
		 */
		@Override
		public void min( final long[] min )
		{
			for ( int i = 0; i < min.length; i++ )
			{
				min[ i ] = min( i );
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #min(net.imglib2.Positionable)
		 */
		@Override
		public void min( final Positionable min )
		{
			for ( int i = 0; i < min.numDimensions(); i++ )
			{
				min.setPosition( min( i ), i );
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #max(int)
		 */
		@Override
		public long max( final int d )
		{
			return Math.min( super.max( d ), getSrcInterval().max( d ) );
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #max(long[])
		 */
		@Override
		public void max( final long[] max )
		{
			for ( int i = 0; i < max.length; i++ )
			{
				max[ i ] = max( i );
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.imglib2.roi.AbstractIterableRegionOfInterest.AROIIterableInterval
		 * #max(net.imglib2.Positionable)
		 */
		@Override
		public void max( final Positionable max )
		{
			for ( int i = 0; i < max.numDimensions(); i++ )
			{
				max.setPosition( max( i ), i );
			}
		}
	}

	@Override
	public < T extends Type< T >> IterableInterval< T > getIterableIntervalOverROI( final RandomAccessible< T > src )
	{
		if ( src instanceof RandomAccessibleInterval )
			return new AROIClippedIterableInterval< T >( ( RandomAccessibleInterval< T > ) src );
		return new AROIIterableInterval< T >( src );
	}

	/**
	 * Return the # of elements available from a cursor over the ROI. The
	 * default method acquires successive rasters using nextRaster to get a sum
	 * of pixels. The implementer should consider overriding this to provide a
	 * more efficient implementation.
	 */
	protected long size()
	{
		final long[] position = new long[ numDimensions() ];
		final long[] end = new long[ numDimensions() ];
		Arrays.fill( position, Long.MIN_VALUE );
		long accumulator = 0;
		while ( nextRaster( position, end ) )
		{
			accumulator += end[ 0 ] - position[ 0 ];
			position[ 0 ] = end[ 0 ];
		}
		return accumulator;
	}

	/**
	 * Get the minimum and maximum corners of a bounding hypercube around all
	 * points in the ROI.
	 * 
	 * The implementer should strongly consider implementing this to provide a
	 * more efficient implementation.
	 * 
	 * @param minima
	 *            - minimum coordinates of the ROI
	 * @param maxima
	 *            - maximum coordinates of the ROI
	 */
	protected void getExtrema( final long[] minima, final long[] maxima )
	{
		final long[] position = new long[ numDimensions() ];
		final long[] end = new long[ numDimensions() ];
		Arrays.fill( position, Long.MIN_VALUE );
		Arrays.fill( minima, Long.MAX_VALUE );
		Arrays.fill( maxima, Long.MIN_VALUE );
		while ( nextRaster( position, end ) )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				minima[ i ] = Math.min( minima[ i ], position[ i ] );
				if ( i == 0 )
				{
					// 0 - the end has the maximum position, non-inclusive
					maxima[ i ] = Math.max( maxima[ i ], end[ i ] - 1 );
				}
				else
				{
					maxima[ i ] = Math.max( maxima[ i ], position[ i ] );
				}
			}
			position[ 0 ] = end[ 0 ];
		}
	}

	@Override
	protected void invalidateCachedState()
	{
		super.invalidateCachedState();
		cached_min = null;
		cached_max = null;
		cached_size = SIZE_NOT_CACHED;
	}

	/**
	 * Get the minimum and maximum corners of a bounding hypercube using real
	 * coordinates (which might have fractional components)
	 * 
	 * The implementer should only override this if the ROI is described in real
	 * coordinates. Otherwise, the pixel extrema are used.
	 * 
	 * @param minima
	 * @param maxima
	 */
	@Override
	protected void getRealExtrema( final double[] minima, final double[] maxima )
	{
		validateExtremaCache();
		for ( int i = 0; i < numDimensions(); i++ )
		{
			minima[ i ] = cached_min[ i ];
			maxima[ i ] = cached_max[ i ];
		}
	}

	/**
	 * Remove a pixel from a ROI if possible.
	 * 
	 * The implementer can override this to add a removal behavior to their ROI,
	 * for instance, turning off a mask bit at the indicated position.
	 * 
	 * @param position
	 *            - position that should be removed from the ROI.
	 */
	protected void remove( final long[] position )
	{
		/* default behavior is to do nothing */
	}

	private void validateExtremaCache()
	{
		if ( cached_max == null )
		{
			final long[] min = new long[ numDimensions() ];
			final long[] max = new long[ numDimensions() ];
			getExtrema( min, max );
			this.cached_min = min;
			this.cached_max = max;
		}
	}

	protected long getCachedSize()
	{
		if ( cached_size == SIZE_NOT_CACHED )
		{
			cached_size = size();
		}
		return cached_size;
	}

	protected long dimension( final int d )
	{
		validateExtremaCache();
		return cached_max[ d ] - cached_min[ d ] + 1;
	}

	protected void dimensions( final long[] d )
	{
		for ( int i = 0; i < d.length; i++ )
		{
			d[ i ] = dimension( i );
		}
	}

	protected void max( final long[] max )
	{
		validateExtremaCache();
		for ( int i = 0; i < max.length; i++ )
		{
			max[ i ] = cached_max[ i ];
		}
	}

	protected void max( final Positionable max )
	{
		validateExtremaCache();
		max.setPosition( cached_max );
	}

	protected long max( final int d )
	{
		validateExtremaCache();
		return cached_max[ d ];
	}

	protected void min( final long[] min )
	{
		validateExtremaCache();
		for ( int i = 0; i < min.length; i++ )
		{
			min[ i ] = cached_min[ i ];
		}

	}

	protected void min( final Positionable min )
	{
		validateExtremaCache();
		min.setPosition( cached_min );
	}

	protected long min( final int d )
	{
		validateExtremaCache();
		return cached_min[ d ];
	}

}
