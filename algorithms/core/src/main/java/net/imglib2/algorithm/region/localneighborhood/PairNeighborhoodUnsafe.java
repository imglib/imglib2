package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccess;

public class PairNeighborhoodUnsafe< T > extends PairNeighborhood< T >
{

	public static < T > PairNeighborhoodFactory< T > factory()
	{
		return new PairNeighborhoodFactory< T >()
		{
			@Override
			public Neighborhood< T > create( final long[] position, final long[] offset, final RandomAccess< T > sourceRandomAccess )
			{
				return new PairNeighborhoodUnsafe< T >( position, offset, sourceRandomAccess );
			}
		};
	}

	private final LocalCursor theCursor;

	private final LocalCursor firstElementCursor;

	PairNeighborhoodUnsafe( final long[] position, final long[] offset, final RandomAccess< T > sourceRandomAccess )
	{
		super( position, offset, sourceRandomAccess );
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
