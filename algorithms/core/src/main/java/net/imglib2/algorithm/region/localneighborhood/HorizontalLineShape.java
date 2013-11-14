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
 * A {@link Shape} representing finite, centered, symmetric lines, that are
 * parallel to the image axes.
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 */
public class HorizontalLineShape implements Shape
{
	private final long span;

	private final boolean skipCenter;

	private final int dim;

	/**
	 * Create a new line shape.
	 *
	 * @param span
	 *            the span of the line in both directions, so that its total
	 *            extend is given by <code>2 x span + 1</code>.
	 * @param dim
	 *            the dimension along which to lay this line.
	 * @param skipCenter
	 *            if <code>true</code>, the shape will skip the center point of
	 *            the line.
	 */
	public HorizontalLineShape( final long span, final int dim, final boolean skipCenter )
	{
		this.span = span;
		this.dim = dim;
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
		final HorizontalLineNeighborhoodFactory< T > f = HorizontalLineNeighborhoodUnsafe.< T >factory();
		return new NeighborhoodsAccessible< T >( source, span, dim, skipCenter, f );
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return neighborhoods( source );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessibleInterval< T > source )
	{
		final HorizontalLineNeighborhoodFactory< T > f = HorizontalLineNeighborhood.< T >factory();
		return new NeighborhoodsAccessible< T >( source, span, dim, skipCenter, f );
	}

	public static final class NeighborhoodsAccessible< T > extends AbstractInterval implements RandomAccessibleInterval< Neighborhood< T > >, IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final long span;

		final HorizontalLineNeighborhoodFactory< T > factory;

		final long size;

		final int dim;

		final boolean skipCenter;

		public NeighborhoodsAccessible( final RandomAccessibleInterval< T > source, final long span, final int dim, final boolean skipCenter, final HorizontalLineNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.span = span;
			this.dim = dim;
			this.skipCenter = skipCenter;
			this.factory = factory;
			long s = source.dimension( 0 );
			for ( int d = 1; d < n; ++d )
				s *= source.dimension( d );
			size = s;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new HorizontalLineNeighborhoodRandomAccess< T >( source, span, dim, skipCenter, factory );
		}

		@Override
		public Cursor< Neighborhood< T >> cursor()
		{
			return new HorizontalLineNeighborhoodCursor< T >( source, span, dim, skipCenter, factory );
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
