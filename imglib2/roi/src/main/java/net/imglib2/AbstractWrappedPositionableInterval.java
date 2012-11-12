package net.imglib2;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;

public abstract class AbstractWrappedPositionableInterval< I extends Interval & Localizable & Positionable > extends AbstractWrappedInterval< I > implements Positionable, Localizable
{
	public AbstractWrappedPositionableInterval( final I source )
	{
		super( source );
	}

	@Override
	public void localize( final float[] position )
	{
		sourceInterval.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		sourceInterval.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return sourceInterval.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return sourceInterval.getDoublePosition( d );
	}

	@Override
	public void localize( final int[] position )
	{
		sourceInterval.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		sourceInterval.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return sourceInterval.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return sourceInterval.getLongPosition( d );
	}

	@Override
	public void fwd( final int d )
	{
		sourceInterval.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		sourceInterval.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		sourceInterval.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		sourceInterval.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		sourceInterval.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		sourceInterval.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		sourceInterval.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		sourceInterval.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		sourceInterval.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		sourceInterval.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		sourceInterval.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		sourceInterval.setPosition( position, d );
	}
}
