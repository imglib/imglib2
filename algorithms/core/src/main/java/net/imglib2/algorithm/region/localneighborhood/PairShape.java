package net.imglib2.algorithm.region.localneighborhood;

import java.util.Iterator;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;

/**
 * A {@link Shape} representing a pair of points.
 * <p>
 * The Shape as its origin at the first point, and the second one is simply
 * found by adding the value of the <code>offset</code> array to its position.
 *
 * @author Jean-Yves Tinevez, 2013
 */
public class PairShape implements Shape
{
	private final long[] offset;

	/**
	 * Create a new pair of points shape.
	 * <p>
	 * 
	 * @param offset
	 *            the offset of the second point with respect to the origin, as
	 *            a <code>long[]</code> array.
	 */
	public PairShape( final long[] offset )
	{
		this.offset = offset;
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		return neighborhoodsRandomAccessible( source );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessibleInterval< T > source )
	{
		final PairNeighborhoodFactory< T > f = PairNeighborhood.< T >factory();
		return new NeighborhoodsAccessible< T >( source, offset, f );
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return neighborhoods( source );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessibleInterval< T > source )
	{
		final PairNeighborhoodFactory< T > f = PairNeighborhood.< T >factory();
		return new NeighborhoodsAccessible< T >( source, offset, f );
	}

	public static final class NeighborhoodsAccessible< T > extends AbstractInterval implements RandomAccessibleInterval< Neighborhood< T > >, IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;


		final PairNeighborhoodFactory< T > factory;

		final long[] offset;

		public NeighborhoodsAccessible( final RandomAccessibleInterval< T > source, final long[] offset, final PairNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.offset = offset;
			this.factory = factory;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new PairNeighborhoodRandomAccess< T >( source, offset, factory );
		}

		@Override
		public Cursor< Neighborhood< T >> cursor()
		{
			return new PairNeighborhoodCursor< T >( source, offset, factory );
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess( final Interval interval )
		{
			return randomAccess();
		}

		@Override
		public long size()
		{
			return 2;
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
