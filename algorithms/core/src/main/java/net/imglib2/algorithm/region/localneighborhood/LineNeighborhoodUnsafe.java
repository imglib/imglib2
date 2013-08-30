package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccess;

public class LineNeighborhoodUnsafe< T > extends LineNeighborhood< T >
{

	public static < T > LineNeighborhoodFactory< T > factory()
	{
		return new LineNeighborhoodFactory< T >()
		{
			@Override
			public Neighborhood< T > create( final long[] position, final long span, final int dim, final boolean skipCenter, final RandomAccess< T > sourceRandomAccess )
			{
				return new LineNeighborhoodUnsafe< T >( position, span, dim, skipCenter, sourceRandomAccess );
			}
		};
	}

	private final LocalCursor theCursor;

	private final LocalCursor firstElementCursor;

	LineNeighborhoodUnsafe( final long[] position, final long span, final int dim, final boolean skipCenter, final RandomAccess< T > sourceRandomAccess )
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
