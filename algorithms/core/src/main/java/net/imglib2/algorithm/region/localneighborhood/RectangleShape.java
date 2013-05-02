package net.imglib2.algorithm.region.localneighborhood;

import java.util.Iterator;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;

/**
 * A factory for Accessibles on rectangular neighboorhoods.
 * 
 * TODO: support non-isotropic, non-symmetric rectangular neighboorhood shapes.
 * (the Neighborhood implementation supports it already, we just need to change
 * this factory.)
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class RectangleShape implements Shape
{
	final int span;

	final boolean skipCenter;

	/**
	 * @param span
	 * @param skipCenter
	 */
	public RectangleShape( final int span, final boolean skipCenter )
	{
		this.span = span;
		this.skipCenter = skipCenter;
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		return neighborhoodsRandomAccessible( source );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessibleInterval< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenterUnsafe.< T >factory() : RectangleNeighborhoodUnsafe.< T >factory();
		final Interval spanInterval = createSpan( source.numDimensions() );
		return new NeighborhoodsAccessible< T >( source, spanInterval, f );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return neighborhoodsRandomAccessibleSafe( source );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessibleInterval< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenter.< T >factory() : RectangleNeighborhood.< T >factory();
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

	public static final class NeighborhoodsAccessible< T > extends AbstractInterval implements RandomAccessibleInterval< Neighborhood< T > >, IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final Interval span;

		final RectangleNeighborhoodFactory< T > factory;

		final long size;

		public NeighborhoodsAccessible( final RandomAccessibleInterval< T > source, final Interval span, final RectangleNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.span = span;
			this.factory = factory;
			long s = source.dimension( 0 );
			for ( int d = 1; d < n; ++d )
				s *= source.dimension( d );
			size = s;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new RectangleNeighborhoodRandomAccess< T >( source, span, factory );
		}

		@Override
		public Cursor< Neighborhood< T >> cursor()
		{
			return new RectangleNeighborhoodCursor< T >( source, span, factory );
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess( final Interval interval )
		{
			return randomAccess();
		}

		@Override
		public long size()
		{
			return size;
		}

		@Override
		public Neighborhood< T > firstElement()
		{
			return cursor().next();
		}

		@Override
		public Object iterationOrder()
		{
			return new FlatIterationOrder( this );
		}

		@Override
		public boolean equalIterationOrder( final IterableRealInterval< ? > f )
		{
			return iterationOrder().equals( f.iterationOrder() );
		}

		@Override
		public Iterator< Neighborhood< T >> iterator()
		{
			return cursor();
		}

		@Override
		public Cursor< Neighborhood< T >> localizingCursor()
		{
			return cursor();
		}
	}
}
