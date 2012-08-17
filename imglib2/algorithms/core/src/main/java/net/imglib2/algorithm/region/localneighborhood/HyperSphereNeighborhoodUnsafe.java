package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccess;

public final class HyperSphereNeighborhoodUnsafe< T > extends HyperSphereNeighborhood< T > implements Neighborhood< T >
{
	public static < T > HyperSphereNeighborhoodFactory< T > factory()
	{
		return new HyperSphereNeighborhoodFactory< T >() {
			@Override
			public Neighborhood< T > create( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
			{
				return new HyperSphereNeighborhoodUnsafe< T >( position, radius, sourceRandomAccess );
			}
		};
	}

	private final LocalCursor theCursor;

	private final LocalCursor firstElementCursor;

	HyperSphereNeighborhoodUnsafe( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
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
