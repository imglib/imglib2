package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.AbstractInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;

public class HyperSphereNeighborhoodRandomAccess< T > extends AbstractInterval implements RandomAccess< Neighborhood< T > >
{
	protected final RandomAccessibleInterval< T > source;

	protected final long radius;

	protected final HyperSphereNeighborhoodFactory< T > neighborhoodFactory;

	protected final Neighborhood< T > currentNeighborhood;

	protected final long[] currentPos;

	public HyperSphereNeighborhoodRandomAccess( final RandomAccessibleInterval< T > source, final long radius, final HyperSphereNeighborhoodFactory< T > factory )
	{
		super( source );
		this.source = source;
		this.radius = radius;
		neighborhoodFactory = factory;
		currentPos = new long[ n ];
		currentNeighborhood = neighborhoodFactory.create( currentPos, radius, source.randomAccess() );
	}

	protected HyperSphereNeighborhoodRandomAccess( final HyperSphereNeighborhoodRandomAccess< T > c )
	{
		super( c.source );
		source = c.source;
		radius = c.radius;
		neighborhoodFactory = c.neighborhoodFactory;
		currentPos = c.currentPos.clone();
		currentNeighborhood = neighborhoodFactory.create( currentPos, radius, source.randomAccess() );
	}

	@Override
	public Neighborhood< T > get()
	{
		return currentNeighborhood;
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
			currentPos[ d ] += localizable.getLongPosition( d );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			currentPos[ d ] += distance[ d ];
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			currentPos[ d ] += distance[ d ];
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			currentPos[ d ] = localizable.getLongPosition( d );
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			currentPos[ d ] = position[ d ];
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			currentPos[ d ] = position[ d ];
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
	public HyperSphereNeighborhoodRandomAccess< T > copy()
	{
		return new HyperSphereNeighborhoodRandomAccess< T >( this );
	}

	@Override
	public HyperSphereNeighborhoodRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}

	@Override
	public void localize( final int[] position )
	{
		currentNeighborhood.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		currentNeighborhood.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return currentNeighborhood.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return currentNeighborhood.getLongPosition( d );
	}

	@Override
	public void localize( final float[] position )
	{
		currentNeighborhood.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		currentNeighborhood.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return currentNeighborhood.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return currentNeighborhood.getDoublePosition( d );
	}
}
