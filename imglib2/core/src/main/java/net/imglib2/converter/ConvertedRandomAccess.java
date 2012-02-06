package net.imglib2.converter;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.converter.sampler.SamplerConverter;

public final class ConvertedRandomAccess< A, B > implements RandomAccess< B >
{
	private final SamplerConverter< A, B > converter;

	private final RandomAccess< A > source;

	private final B converted;

	public ConvertedRandomAccess( final SamplerConverter< A, B > converter, final RandomAccess< A > source )
	{
		this.converter = converter;
		this.source = source;
		this.converted = converter.convert( source );
	}

	@Override
	public void localize( int[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( long[] position )
	{
		source.localize( position );
	}

	@Override
	public int getIntPosition( int d )
	{
		return source.getIntPosition( d );
	}

	@Override
	public long getLongPosition( int d )
	{
		return source.getLongPosition( d );
	}

	@Override
	public void localize( float[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( double[] position )
	{
		source.localize( position );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return source.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return source.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public void fwd( int d )
	{
		source.fwd( d );
	}

	@Override
	public void bck( int d )
	{
		source.bck( d );
	}

	@Override
	public void move( int distance, int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( long distance, int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( Localizable localizable )
	{
		source.move( localizable );
	}

	@Override
	public void move( int[] distance )
	{
		source.move( distance );
	}

	@Override
	public void move( long[] distance )
	{
		source.move( distance );
	}

	@Override
	public void setPosition( Localizable localizable )
	{
		source.setPosition( localizable );
	}

	@Override
	public void setPosition( int[] position )
	{
		source.setPosition( position );
	}

	@Override
	public void setPosition( long[] position )
	{
		source.setPosition( position );
	}

	@Override
	public void setPosition( int position, int d )
	{
		source.setPosition( position, d );
	}

	@Override
	public void setPosition( long position, int d )
	{
		source.setPosition( position, d );
	}

	@Override
	public B get()
	{
		return converted;
	}

	@Override
	public ConvertedRandomAccess< A, B > copy()
	{
		return new ConvertedRandomAccess< A, B >( converter, ( RandomAccess< A > ) source.copy() );
	}

	@Override
	public ConvertedRandomAccess< A, B > copyRandomAccess()
	{
		return copy();
	}
}
