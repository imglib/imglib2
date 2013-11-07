package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccess;

public class DiamondNeighborhoodUnsafe< T > extends DiamondNeighborhood< T >
{
	public static < T > DiamondNeighborhoodFactory< T > factory()
	{
		return new DiamondNeighborhoodFactory< T >()
				{
			@Override
			public Neighborhood< T > create( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
			{
				return new DiamondNeighborhoodUnsafe< T >( position, radius, sourceRandomAccess );
			}
				};
	}

	private final LocalCursor theCursor;

	private final LocalCursor firstElementCursor;

	DiamondNeighborhoodUnsafe( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
	{
		super( position, radius, sourceRandomAccess );
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
