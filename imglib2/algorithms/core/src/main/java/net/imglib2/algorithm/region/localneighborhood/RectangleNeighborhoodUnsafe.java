package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;

public final class RectangleNeighborhoodUnsafe< T > extends RectangleNeighborhood< T > implements Neighborhood< T >
{
	public static < T > RectangleNeighborhoodFactory< T > factory()
	{
		return new RectangleNeighborhoodFactory< T >()
		{
			@Override
			public Neighborhood< T > create( final long[] position, final long[] currentMin, final long[] currentMax, final Interval span, final RandomAccess< T > sourceRandomAccess )
			{
				return new RectangleNeighborhoodUnsafe< T >( position, currentMin, currentMax, span, sourceRandomAccess );
			}
		};
	}

	private final LocalCursor theCursor;

	private final LocalCursor firstElementCursor;

	RectangleNeighborhoodUnsafe( final long[] position, final long[] currentMin, final long[] currentMax, final Interval span, final RandomAccess< T > sourceRandomAccess )
	{
		super( position, currentMin, currentMax, span, sourceRandomAccess );
		theCursor = super.cursor();
		firstElementCursor = super.cursor();
	}

	@Override
	public T firstElement()
	{
		firstElementCursor.reset();
		return firstElementCursor.next();
	}

	@Override
	public LocalCursor cursor()
	{
		theCursor.reset();
		return theCursor;
	}
}
