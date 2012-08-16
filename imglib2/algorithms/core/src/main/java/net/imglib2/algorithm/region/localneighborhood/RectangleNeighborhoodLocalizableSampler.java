package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;

public abstract class RectangleNeighborhoodLocalizableSampler< T > extends AbstractInterval implements Localizable, Sampler< RectangleSkipCenterNeighborhood< T > >
{
	protected final RandomAccessibleInterval< T > source;

	protected final Interval span;

	protected final RectangleSkipCenterNeighborhood< T > currentNeighborhood;

	protected final long[] currentPos;

	protected final long[] currentMin;

	protected final long[] currentMax;

	public RectangleNeighborhoodLocalizableSampler( final RandomAccessibleInterval< T > source, final Interval span )
	{
		super( source );
		this.source = source;
		this.span = span;
		currentPos = new long[ n ];
		currentMin = new long[ n ];
		currentMax = new long[ n ];
		currentNeighborhood = new RectangleSkipCenterNeighborhood< T >( currentPos, currentMin, currentMax, span, source.randomAccess() );
	}

	protected RectangleNeighborhoodLocalizableSampler( final RectangleNeighborhoodLocalizableSampler< T > c )
	{
		super( c.source );
		source = c.source;
		span = c.span;
		currentPos = c.currentPos.clone();
		currentMin = c.currentMin.clone();
		currentMax = c.currentMax.clone();
		currentNeighborhood = new RectangleSkipCenterNeighborhood< T >( currentPos, currentMin, currentMax, span, source.randomAccess() );
	}

	@Override
	public RectangleSkipCenterNeighborhood< T > get()
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
