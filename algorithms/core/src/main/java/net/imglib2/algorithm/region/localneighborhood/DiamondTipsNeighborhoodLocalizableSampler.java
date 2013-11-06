package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.AbstractInterval;
import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;

public abstract class DiamondTipsNeighborhoodLocalizableSampler< T > extends AbstractInterval implements Localizable, Sampler< Neighborhood< T >>
{
	protected final RandomAccessibleInterval< T > source;

	protected final long radius;

	protected final DiamondTipsNeighborhoodFactory< T > factory;

	protected final long[] currentPos;

	protected final Neighborhood< T > currentNeighborhood;

	public DiamondTipsNeighborhoodLocalizableSampler( final RandomAccessibleInterval< T > source, final long radius, final DiamondTipsNeighborhoodFactory< T > factory )
	{
		super( source);
		this.source = source;
		this.radius = radius;
		this.factory = factory;
		this.currentPos = new long[ n ];

		final long[] accessMin = new long[ n ];
		final long[] accessMax = new long[ n ];
		source.min( accessMin );
		source.max( accessMax );
		for ( int d = 0; d < n; ++d )
		{
			accessMin[ d ] = currentPos[ d ] - radius;
			accessMax[ d ] = currentPos[ d ] + radius;
		}
		this.currentNeighborhood = factory.create( currentPos, radius, source.randomAccess( new FinalInterval( accessMin, accessMax ) ) );
	}

	protected DiamondTipsNeighborhoodLocalizableSampler( final DiamondTipsNeighborhoodLocalizableSampler< T > c )
	{
		super( c.source );
		this.source = c.source;
		this.radius = c.radius;
		this.factory = c.factory;
		this.currentPos = c.currentPos.clone();
		this.currentNeighborhood = factory.create( currentPos, radius, source.randomAccess() );
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

	@Override
	public Neighborhood< T > get()
	{
		return currentNeighborhood;
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

}
