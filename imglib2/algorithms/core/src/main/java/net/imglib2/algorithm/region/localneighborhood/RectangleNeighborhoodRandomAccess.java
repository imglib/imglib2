package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;


public final class RectangleNeighborhoodRandomAccess< T > extends RectangleNeighborhoodLocalizableSampler< T > implements RandomAccess< RectangleSkipCenterNeighborhood< T > >
{
	public RectangleNeighborhoodRandomAccess( final RandomAccessibleInterval< T > source, final Interval span )
	{
		super( source, span );
	}

	private RectangleNeighborhoodRandomAccess( final RectangleNeighborhoodRandomAccess< T > c )
	{
		super( c );
	}

	@Override
	public void fwd( final int d )
	{
		++currentPos[ d ];
		++currentMin[ d ];
		++currentMax[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--currentPos[ d ];
		--currentMin[ d ];
		--currentMax[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		currentPos[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		currentPos[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d );
			currentPos[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] += distance[ d ];
			currentMin[ d ] += distance[ d ];
			currentMax[ d ] += distance[ d ];
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] += distance[ d ];
			currentMin[ d ] += distance[ d ];
			currentMax[ d ] += distance[ d ];
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long position = localizable.getLongPosition( d );
			currentPos[ d ] = position;
			currentMin[ d ] = position + span.min( d );
			currentMax[ d ] = position + span.max( d );
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] = position[ d ];
			currentMin[ d ] = position[ d ] + span.min( d );
			currentMax[ d ] = position[ d ] + span.max( d );
		}
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] = position[ d ];
			currentMin[ d ] = position[ d ] + span.min( d );
			currentMax[ d ] = position[ d ] + span.max( d );
		}
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		currentPos[ d ] = position;
		currentMin[ d ] = position + span.min( d );
		currentMax[ d ] = position + span.max( d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		currentPos[ d ] = position;
		currentMin[ d ] = position + span.min( d );
		currentMax[ d ] = position + span.max( d );
	}

	@Override
	public RectangleNeighborhoodRandomAccess< T > copy()
	{
		return new RectangleNeighborhoodRandomAccess< T >( this );
	}

	@Override
	public RectangleNeighborhoodRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
