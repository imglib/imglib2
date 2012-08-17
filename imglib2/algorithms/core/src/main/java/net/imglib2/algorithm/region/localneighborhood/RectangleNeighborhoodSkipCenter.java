package net.imglib2.algorithm.region.localneighborhood;

import java.util.Iterator;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.AbstractLocalizable;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.util.IntervalIndexer;

public class RectangleNeighborhoodSkipCenter< T > extends AbstractLocalizable implements Neighborhood< T >
{
	public static < T > RectangleNeighborhoodFactory< T > factory()
	{
		return new RectangleNeighborhoodFactory< T >() {
			@Override
			public Neighborhood< T > create( final long[] position, final long[] currentMin, final long[] currentMax, final Interval span, final RandomAccess< T > sourceRandomAccess )
			{
				return new RectangleNeighborhoodSkipCenter< T >( position, currentMin, currentMax, span, sourceRandomAccess );
			}
		};
	}

	private final long[] currentMin;

	private final long[] currentMax;

	private final long[] dimensions;

	private final RandomAccess< T > sourceRandomAccess;

	private final Interval structuringElementBoundingBox;

	private final long maxIndex;

	private final long midIndex;

	RectangleNeighborhoodSkipCenter( final long[] position, final long[] currentMin, final long[] currentMax, final Interval span, final RandomAccess< T > sourceRandomAccess )
	{
		super( position );
		this.currentMin = currentMin;
		this.currentMax = currentMax;
		dimensions = new long[ n ];
		span.dimensions( dimensions );

		long mi = dimensions[ 0 ];
		for ( int d = 1; d < n; ++d )
			mi *= dimensions[ d ];
		maxIndex = mi;

		final long[] centerOffset = new long[ n ];
		for ( int d = 0; d < n; ++d )
			centerOffset[ d ] = -span.min( d );
		midIndex = IntervalIndexer.positionToIndex( centerOffset, dimensions ) + 1;

		this.sourceRandomAccess = sourceRandomAccess;
		this.structuringElementBoundingBox = span;
	}

	@Override
	public Interval getStructuringElementBoundingBox()
	{
		return structuringElementBoundingBox;
	}

	@Override
	public long size()
	{
		return maxIndex - 1; // -1 because we skip the center pixel
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return this; // iteration order is only compatible with ourselves
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public double realMin( final int d )
	{
		return currentMin[ d ];
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentMin[ d ];
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentMin[ d ], d );
	}

	@Override
	public double realMax( final int d )
	{
		return currentMax[ d ];
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentMax[ d ];
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentMax[ d ], d );
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public long min( final int d )
	{
		return currentMin[ d ];
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentMin[ d ];
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentMin[ d ], d );
	}

	@Override
	public long max( final int d )
	{
		return currentMax[ d ];
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentMax[ d ];
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentMax[ d ], d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = this.dimensions[ d ];
	}

	@Override
	public long dimension( final int d )
	{
		return dimensions[ d ];
	}

	@Override
	public LocalCursor cursor()
	{
		return new LocalCursor( sourceRandomAccess.copyRandomAccess() );
	}

	@Override
	public LocalCursor localizingCursor()
	{
		return cursor();
	}

	public final class LocalCursor extends AbstractEuclideanSpace implements Cursor< T >
	{
		private final RandomAccess< T > source;

		private long index;

		private long maxIndexOnLine;

		public LocalCursor( final RandomAccess< T > source )
		{
			super( source.numDimensions() );
			this.source = source;
			reset();
		}

		protected LocalCursor( final LocalCursor c )
		{
			super( c.numDimensions() );
			source = c.source.copyRandomAccess();
			index = c.index;
		}

		@Override
		public T get()
		{
			return source.get();
		}

		@Override
		public void fwd()
		{
			source.fwd( 0 );
			if ( ++index > maxIndexOnLine )
				nextLine();
			if ( index == midIndex )
				fwd();
		}

		private void nextLine()
		{
			source.setPosition( currentMin[ 0 ], 0 );
			maxIndexOnLine += dimensions[ 0 ];
			for ( int d = 1; d < n; ++d )
			{
				source.fwd( d );
				if ( source.getLongPosition( d ) > currentMax[ d ] )
					source.setPosition( currentMin[ d ], d );
				else
					break;
			}
		}

		@Override
		public void jumpFwd( final long steps )
		{
			for ( long i = 0; i < steps; ++i )
				fwd();
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
			// NB: no action.
		}

		@Override
		public void reset()
		{
			source.setPosition( currentMin );
			source.bck( 0 );
			index = 0;
			maxIndexOnLine = dimensions[ 0 ];
		}

		@Override
		public boolean hasNext()
		{
			return index < maxIndex;
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return source.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return source.getDoublePosition( d );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return source.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return source.getLongPosition( d );
		}

		@Override
		public void localize( final long[] position )
		{
			source.localize( position );
		}

		@Override
		public void localize( final float[] position )
		{
			source.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			source.localize( position );
		}

		@Override
		public void localize( final int[] position )
		{
			source.localize( position );
		}

		@Override
		public LocalCursor copy()
		{
			return new LocalCursor( this );
		}

		@Override
		public LocalCursor copyCursor()
		{
			return copy();
		}
	}
}
