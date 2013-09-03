package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccess;

public class PeriodicLineNeighborhoodUnsafe< T > extends PeriodicLineNeighborhood< T >
{

	public static < T > PeriodicLineNeighborhoodFactory< T > factory()
	{
		return new PeriodicLineNeighborhoodFactory< T >()
		{
			@Override
			public Neighborhood< T > create( final long[] position, final long span, final int[] increments, final RandomAccess< T > sourceRandomAccess )
			{
				return new PeriodicLineNeighborhoodUnsafe< T >( position, span, increments, sourceRandomAccess );
			}
		};
	}

	private final LocalCursor theCursor;

	private final LocalCursor firstElementCursor;

	PeriodicLineNeighborhoodUnsafe( final long[] position, final long span, final int[] increments, final RandomAccess< T > sourceRandomAccess )
	{
		super( position, span, increments, sourceRandomAccess );
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
