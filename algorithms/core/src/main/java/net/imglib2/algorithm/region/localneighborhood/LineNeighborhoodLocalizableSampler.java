package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.AbstractInterval;
import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;

public abstract class LineNeighborhoodLocalizableSampler< T > extends AbstractInterval implements Localizable, Sampler< Neighborhood< T > >
{
	protected final RandomAccessibleInterval< T > source;

	protected final long span;

	protected final int dim;

	protected final long[] currentPos;

	protected final LineNeighborhoodFactory< T > neighborhoodFactory;

	protected final Neighborhood< T > currentNeighborhood;

	private final boolean skipCenter;

	public LineNeighborhoodLocalizableSampler( final RandomAccessibleInterval< T > source, final long span, final int dim, final boolean skipCenter, final LineNeighborhoodFactory< T > factory )
	{
		super( source );
		this.source = source;
		this.span = span;
		this.dim = dim;
		this.skipCenter = skipCenter;
		this.currentPos = new long[ n ];
		neighborhoodFactory = factory;
		final long[] accessMin = new long[ n ];
		final long[] accessMax = new long[ n ];
		source.min( accessMin );
		source.max( accessMax );
		for ( int d = 0; d < n; ++d )
		{
			accessMin[ d ] = currentPos[ d ] - span;
			accessMax[ d ] = currentPos[ d ] + span;
		}
		currentNeighborhood = neighborhoodFactory.create( currentPos, span, dim, false, source.randomAccess( new FinalInterval( accessMin, accessMax ) ) );
	}

	protected LineNeighborhoodLocalizableSampler( final LineNeighborhoodLocalizableSampler< T > c )
	{
		super( c.source );
		source = c.source;
		span = c.span;
		skipCenter = c.skipCenter;
		dim = c.dim;
		neighborhoodFactory = c.neighborhoodFactory;
		currentPos = c.currentPos.clone();
		currentNeighborhood = neighborhoodFactory.create( currentPos, span, dim, skipCenter, source.randomAccess() );
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
