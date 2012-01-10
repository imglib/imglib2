package net.imglib2.converter;

import net.imglib2.Cursor;
import net.imglib2.converter.sampler.SamplerConverter;

public class ConvertedCursor< A, B > implements Cursor< B >
{
	private final SamplerConverter< A, B > converter;

	private final Cursor< A > source;

	private final B converted;

	public ConvertedCursor( final SamplerConverter< A, B > converter, final Cursor< A > source )
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
	public void jumpFwd( long steps )
	{
		source.jumpFwd( steps );
	}

	@Override
	public void fwd()
	{
		source.fwd();
	}

	@Override
	public void reset()
	{
		source.reset();
	}

	@Override
	public boolean hasNext()
	{
		return source.hasNext();
	}

	@Override
	public B next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
		source.remove();
	}

	@Override
	public B get()
	{
		return converted;
	}

	@Override
	public ConvertedCursor< A, B > copy()
	{
		return new ConvertedCursor< A, B >( converter, ( Cursor< A > ) source.copy() );
	}

	@Override
	public ConvertedCursor< A, B > copyCursor()
	{
		return copy();
	}
}
