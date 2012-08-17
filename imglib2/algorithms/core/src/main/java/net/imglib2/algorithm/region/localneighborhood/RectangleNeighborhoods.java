package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.AbstractInterval;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;

public class RectangleNeighborhoods implements Neighborhoods
{
	final int span;

	final boolean skipCenter;

	public RectangleNeighborhoods( final int span, final boolean skipCenter )
	{
		this.span = span;
		this.skipCenter = skipCenter;
	}

	@Override
	public < T > RandomAccessibleInterval< Neighborhood< T > > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ?
				RectangleNeighborhoodSkipCenter.< T >factory() :
				RectangleNeighborhood.< T >factory();
		final Interval spanInterval = createSpan( source.numDimensions() );
		return new NeighborhoodsAccessible< T >( source, spanInterval, f );
	}

	@Override
	public < T > RandomAccessibleInterval< Neighborhood< T > > neighborhoodsSingle( final RandomAccessibleInterval< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ?
				RectangleNeighborhoodSkipCenterSingle.< T >factory() :
				RectangleNeighborhoodSingle.< T >factory();
		final Interval spanInterval = createSpan( source.numDimensions() );
		return new NeighborhoodsAccessible< T >( source, spanInterval, f );
	}

	private Interval createSpan( final int n )
	{
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = -span;
			max[ d ] = span;
		}
		return new FinalInterval( min, max );
	}

	private static final class NeighborhoodsAccessible< T > extends AbstractInterval implements RandomAccessibleInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;
		final Interval span;
		final RectangleNeighborhoodFactory< T > factory;

		public NeighborhoodsAccessible( final RandomAccessibleInterval< T > source, final Interval span, final RectangleNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.span = span;
			this.factory = factory;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new RectangleNeighborhoodRandomAccess< T >( source, span, factory );
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess( final Interval interval )
		{
			return randomAccess();
		}
	}
}
