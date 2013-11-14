package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccess;

public class HorizontalLineNeighborhoodUnsafe< T > extends HorizontalLineNeighborhood< T >
{

	public static < T > HorizontalLineNeighborhoodFactory< T > factory()
	{
		return new HorizontalLineNeighborhoodFactory< T >()
		{
			@Override
			public Neighborhood< T > create( final long[] position, final long span, final int dim, final boolean skipCenter, final RandomAccess< T > sourceRandomAccess )
			{
				return new HorizontalLineNeighborhoodUnsafe< T >( position, span, dim, skipCenter, sourceRandomAccess );
			}
		};
	}

	private final LocalCursor theCursor;

	private final LocalCursor firstElementCursor;

	HorizontalLineNeighborhoodUnsafe( final long[] position, final long span, final int dim, final boolean skipCenter, final RandomAccess< T > sourceRandomAccess )
	{
		super( position, span, dim, skipCenter, sourceRandomAccess );
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
