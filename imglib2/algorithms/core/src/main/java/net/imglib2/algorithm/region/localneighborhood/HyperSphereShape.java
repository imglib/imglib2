package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * A factory for Accessibles on hyper-sphere neighboorhoods.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class HyperSphereShape implements Shape
{
	final long radius;

	public HyperSphereShape( final long radius )
	{
		this.radius = radius;
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoods( final RandomAccessibleInterval< T > source )
	{
		return Views.iterable( neighborhoodsRandomAccessible( source ) );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsAccessible< T >( source, radius, HyperSphereNeighborhoodUnsafe.< T >factory() );
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return Views.iterable( neighborhoodsRandomAccessible( source ) );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsAccessible< T >( source, radius, HyperSphereNeighborhood.< T >factory() );
	}

	private static final class NeighborhoodsAccessible< T > extends AbstractInterval implements RandomAccessibleInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final long radius;

		final HyperSphereNeighborhoodFactory< T > factory;

		public NeighborhoodsAccessible( final RandomAccessibleInterval< T > source, final long radius, final HyperSphereNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.radius = radius;
			this.factory = factory;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new HyperSphereNeighborhoodRandomAccess< T >( source, radius, factory );
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess( final Interval interval )
		{
			return randomAccess();
		}
	}
}
