package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;

public class DiamondTipsNeighborhoodRandomAccess< T > extends DiamondTipsNeighborhoodLocalizableSampler< T > implements RandomAccess< Neighborhood< T > >
{

	protected DiamondTipsNeighborhoodRandomAccess( final RandomAccessibleInterval< T > source, final long radius, final DiamondTipsNeighborhoodFactory< T > factory )
	{
		super( source, radius, factory );
	}

	private DiamondTipsNeighborhoodRandomAccess( final DiamondTipsNeighborhoodRandomAccess< T > c )
	{
		super( c );
	}

	@Override
	public void fwd( final int d )
	{
		++currentPos[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--currentPos[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		currentPos[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		currentPos[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d );
			currentPos[ d ] += distance;
		}
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] += distance[ d ];
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] += distance[ d ];
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long position = localizable.getLongPosition( d );
			currentPos[ d ] = position;
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] = position[ d ];
		}
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] = position[ d ];
		}
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		currentPos[ d ] = position;
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		currentPos[ d ] = position;
	}

	@Override
	public DiamondTipsNeighborhoodRandomAccess< T > copy()
	{
		return new DiamondTipsNeighborhoodRandomAccess< T >( this );
	}

	@Override
	public DiamondTipsNeighborhoodRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
