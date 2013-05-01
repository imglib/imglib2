package net.imglib2.newroi.util;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.converter.AbstractConvertedRandomAccess;

/**
 * TODO: This should share a common base class with {@link AbstractConvertedRandomAccess}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class WrappedPositionableRandomAccess< T extends Localizable & Positionable > implements RandomAccess< T >
{
	T source;

	public WrappedPositionableRandomAccess( final T source )
	{
		this.source = source;
	}

	@Override
	public void localize( final int[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		source.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return source.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return source.getLongPosition( d );
	}

	@Override
	public void localize( final float[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		source.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return source.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return source.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public void fwd( final int d )
	{
		source.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		source.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		source.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		source.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		source.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		source.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		source.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		source.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		source.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		source.setPosition( position, d );
	}

	@Override
	public T get()
	{
		return source;
	}

	@Override
	public Sampler< T > copy()
	{
		throw new UnsupportedOperationException( "not supported because source is not copyable" );
	}

	@Override
	public RandomAccess< T > copyRandomAccess()
	{
		throw new UnsupportedOperationException( "not supported because source is not copyable" );
	}

}
