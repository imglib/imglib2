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
 * A factory for Accessibles on {@link PeriodicLineNeighborhood}s, that iterate
 * over what is termed "Periodic lines", and is best explained in Ronald Jones
 * and Pierre Soilles publication:
 * <p>
 * <tt>Jones and Soilles. Periodic lines: Definition, cascades, and application
 * to granulometries. Pattern Recognition Letters (1996) vol. 17 (10) pp. 1057-1063</tt>
 *
 * @author Jean-Yves Tinevez, 2013
 */
public class PeriodicLineShape implements Shape
{
	private final long span;

	private final int[] increments;

	/**
	 * Creates a new periodic line shape, that will iterate over
	 * <code>2 × span + 1</code> pixels as follow:
	 *
	 * <pre>
	 * position - span x increments,
	 * ...
	 * position - 2 × increments,
	 * position - increments,
	 * position,
	 * position + increments,
	 * position + 2 × increments,
	 * ...
	 * position + span x increments
	 * </pre>
	 *
	 * @param span
	 *            the span of the neighborhood, so that it will iterate over
	 *            <code>2 × span + 1</code> pixels.
	 * @param increments
	 *            the values by which each element of the position vector is to
	 *            be incremented when iterating.
	 */
	public PeriodicLineShape( final long span, final int[] increments )
	{
		this.span = span;
		this.increments = increments;
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		return neighborhoodsRandomAccessible( source );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessibleInterval< T > source )
	{
		final PeriodicLineNeighborhoodFactory< T > f = PeriodicLineNeighborhoodUnsafe.< T >factory();
		return new NeighborhoodsAccessible< T >( source, span, increments, f );
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return neighborhoods( source );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessibleInterval< T > source )
	{
		final PeriodicLineNeighborhoodFactory< T > f = PeriodicLineNeighborhood.< T >factory();
		return new NeighborhoodsAccessible< T >( source, span, increments, f );
	}

	public static final class NeighborhoodsAccessible< T > extends AbstractInterval implements RandomAccessibleInterval< Neighborhood< T > >, IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final long span;

		final PeriodicLineNeighborhoodFactory< T > factory;

		final long size;

		final int[] increments;

		public NeighborhoodsAccessible( final RandomAccessibleInterval< T > source, final long span, final int[] increments, final PeriodicLineNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.span = span;
			this.increments = increments;
			this.factory = factory;
			long s = source.dimension( 0 );
			for ( int d = 1; d < n; ++d )
				s *= source.dimension( d );
			size = s;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new PeriodicLineNeighborhoodRandomAccess< T >( source, span, increments, factory );
		}

		@Override
		public Cursor< Neighborhood< T >> cursor()
		{
			return new PeriodicLineNeighborhoodCursor< T >( source, span, increments, factory );
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
