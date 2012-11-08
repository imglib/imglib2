package net.imglib2.newroi.util;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;

public abstract class AbtractPositionableInterval extends AbstractInterval implements Localizable, Positionable
{
	protected final long[] position;

	protected final Interval originInterval;

	public AbtractPositionableInterval( final Interval interval )
	{
		super( interval );
		originInterval = interval;
		position = new long[ n ];
	}

	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = position[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = position[ d ];
	}

	@Override
	public void localize( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = ( int ) position[ d ];
	}

	@Override
	public void localize( final long[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = position[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int ) position[ d ];
	}

	@Override
	public long getLongPosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public void fwd( final int d )
	{
		++position[ d ];
		++min[ d ];
		++max[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];
		--min[ d ];
		--max[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;
		min[ d ] += distance;
		max[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
		min[ d ] += distance;
		max[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d );
			position[ d ] += distance;
			min[ d ] += distance;
			max[ d ] += distance;
		}
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; d++ )
		{
			position[ d ] += distance[ d ];
			min[ d ] += distance[ d ];
			max[ d ] += distance[ d ];
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; d++ )
		{
			position[ d ] += distance[ d ];
			min[ d ] += distance[ d ];
			max[ d ] += distance[ d ];
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long pos = localizable.getLongPosition( d );
			position[ d ] = pos;
			min[ d ] = pos + originInterval.min( d );
			max[ d ] = pos + originInterval.max( d );
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
			min[ d ] = position[ d ] + originInterval.min( d );
			max[ d ] = position[ d ] + originInterval.max( d );
		}
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
			min[ d ] = position[ d ] + originInterval.min( d );
			max[ d ] = position[ d ] + originInterval.max( d );
		}
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		this.position[ d ] = position;
		min[ d ] = position + originInterval.min( d );
		max[ d ] = position + originInterval.max( d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		this.position[ d ] = position;
		min[ d ] = position + originInterval.min( d );
		max[ d ] = position + originInterval.max( d );
	}
}
