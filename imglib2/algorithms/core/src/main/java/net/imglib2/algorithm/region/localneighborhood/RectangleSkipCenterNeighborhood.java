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
import net.imglib2.util.Util;

public final class RectangleSkipCenterNeighborhood< T > extends AbstractLocalizable implements Neighborhood< T >
{
	private final long[] currentMin;

	private final long[] currentMax;

	private final RandomAccess< T > sourceRandomAccess;

	private final long size;

	private final long[] dimensions;

	private final Interval span;

	RectangleSkipCenterNeighborhood( final long[] position, final long[] currentMin, final long[] currentMax, final Interval span, final RandomAccess< T > sourceRandomAccess )
	{
		super( position );
		this.currentMin = currentMin;
		this.currentMax = currentMax;
		dimensions = new long[ n ];
		span.dimensions( dimensions );
		size = Util.pow( ( int ) dimensions[ 0 ], n ) - 1; // TODO: do it right, span is not isotropic
		this.sourceRandomAccess = sourceRandomAccess;
		this.span = span;
	}

	@Override
	public Interval getStructuringElementBoundingBox()
	{
		return span;
	}

	@Override
	public long size()
	{
		return size;
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
	public void realMin( final double[] minimum )
	{
		for ( int d = 0; d < n; ++d )
			minimum[ d ] = currentMin[ d ];
	}

	@Override
	public void realMin( final RealPositionable minimum )
	{
		for ( int d = 0; d < n; ++d )
			minimum.setPosition( currentMin[ d ], d );
	}

	@Override
	public double realMax( final int d )
	{
		return currentMax[ d ];
	}

	@Override
	public void realMax( final double[] maximum )
	{
		for ( int d = 0; d < n; ++d )
			maximum[ d ] = currentMax[ d ];
	}

	@Override
	public void realMax( final RealPositionable maximum )
	{
		for ( int d = 0; d < n; ++d )
			maximum.setPosition( currentMax[ d ], d );
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
	public void min( final long[] minimum )
	{
		for ( int d = 0; d < n; ++d )
			minimum[ d ] = currentMin[ d ];
	}

	@Override
	public void min( final Positionable minimum )
	{
		for ( int d = 0; d < n; ++d )
			minimum.setPosition( currentMin[ d ], d );
	}

	@Override
	public long max( final int d )
	{
		return currentMax[ d ];
	}

	@Override
	public void max( final long[] maximum )
	{
		for ( int d = 0; d < n; ++d )
			maximum[ d ] = currentMax[ d ];
	}

	@Override
	public void max( final Positionable maximum )
	{
		for ( int d = 0; d < n; ++d )
			maximum.setPosition( currentMax[ d ], d );
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
	public CurrentNeighborhoodCursor cursor()
	{
		return new CurrentNeighborhoodCursor( sourceRandomAccess.copyRandomAccess() );
	}

	@Override
	public CurrentNeighborhoodCursor localizingCursor()
	{
		return cursor();
	}

	public final class CurrentNeighborhoodCursor extends AbstractEuclideanSpace implements Cursor< T >
	{
		final RandomAccess< T > source;

		private final long maxCount; // TODO: move to outer class?

		private final long midIndex; // TODO: move to outer class?

		private long index;

		private long maxIndexOnLine;

		public CurrentNeighborhoodCursor( final RandomAccess< T > source )
		{
			super( source.numDimensions() );
			maxCount = ( long ) Math.pow( dimensions[ 0 ], n ); // TODO: do it right, span is not isotropic
			midIndex = maxCount / 2 + 1; // TODO do it right, span is not symmetric
			this.source = source;
			reset();
		}

		private CurrentNeighborhoodCursor( final CurrentNeighborhoodCursor c )
		{
			super( c.numDimensions() );
			source = c.source.copyRandomAccess();
			maxCount = c.maxCount;
			midIndex = c.midIndex;
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
			return index < maxCount;
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
		public CurrentNeighborhoodCursor copy()
		{
			return new CurrentNeighborhoodCursor( this );
		}

		@Override
		public CurrentNeighborhoodCursor copyCursor()
		{
			return copy();
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
	}
}