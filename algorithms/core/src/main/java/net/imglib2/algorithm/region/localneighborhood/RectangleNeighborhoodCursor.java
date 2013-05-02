package net.imglib2.algorithm.region.localneighborhood;


import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.IntervalIndexer;

public final class RectangleNeighborhoodCursor< T > extends RectangleNeighborhoodLocalizableSampler< T > implements Cursor< Neighborhood< T > >
{
	private final long[] dimensions;

	private long index;

	private final long maxIndex;

	private long maxIndexOnLine;

	public RectangleNeighborhoodCursor( final RandomAccessibleInterval< T > source, final Interval span, final RectangleNeighborhoodFactory< T > factory )
	{
		super( source, span, factory );

		dimensions = new long[ n ];
		dimensions( dimensions );
		long size = dimensions[ 0 ];
		for ( int d = 1; d < n; ++d )
			size *= dimensions[ d ];
		maxIndex = size;
		reset();
	}

	private RectangleNeighborhoodCursor( final RectangleNeighborhoodCursor< T > c )
	{
		super( c );
		dimensions = c.dimensions.clone();
		maxIndex = c.maxIndex;
		index = c.index;
		maxIndexOnLine = c.maxIndexOnLine;
	}

	@Override
	public void fwd()
	{
		++currentPos[ 0 ];
		++currentMin[ 0 ];
		++currentMax[ 0 ];
		if ( ++index > maxIndexOnLine )
			nextLine();
	}

	private void nextLine()
	{
		currentPos[ 0 ] = min[ 0 ];
		currentMin[ 0 ] -= dimensions[ 0 ];
		currentMax[ 0 ] -= dimensions[ 0 ];
		maxIndexOnLine += dimensions[ 0 ];
		for ( int d = 1; d < n; ++d )
		{
			++currentPos[ d ];
			++currentMin[ d ];
			++currentMax[ d ];
			if ( currentPos[ d ] > max[ d ] )
			{
				currentPos[ d ] = min[ d ];
				currentMin[ d ] -= dimensions[ d ];
				currentMax[ d ] -= dimensions[ d ];
			}
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		index = 0;
		maxIndexOnLine = dimensions[ 0 ];
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] = ( d == 0 ) ? min[ d ] - 1 : min[ d ];
			currentMin[ d ] = currentPos[ d ] + span.min( d );
			currentMax[ d ] = currentPos[ d ] + span.max( d );
		}
	}

	@Override
	public boolean hasNext()
	{
		return index < maxIndex;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		index += steps;
		maxIndexOnLine = ( index < 0 ) ? dimensions[ 0 ] : ( 1 + index / dimensions[ 0 ] ) * dimensions[ 0 ];
		IntervalIndexer.indexToPositionWithOffset( index + 1, dimensions, min, currentPos );
		for ( int d = 0; d < n; ++d )
		{
			currentMin[ d ] = currentPos[ d ] + span.min( d );
			currentMax[ d ] = currentPos[ d ] + span.max( d );
		}
	}

	@Override
	public Neighborhood< T > next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
		// NB: no action.
	}

	@Override
	public RectangleNeighborhoodCursor< T > copy()
	{
		return new RectangleNeighborhoodCursor< T >( this );
	}


	@Override
	public RectangleNeighborhoodCursor< T > copyCursor()
	{
		return copy();
	}

}
