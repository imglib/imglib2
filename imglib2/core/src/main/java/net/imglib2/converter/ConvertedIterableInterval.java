package net.imglib2.converter;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.converter.sampler.SamplerConverter;

public class ConvertedIterableInterval< A, B > implements IterableInterval< B >
{
	private final IterableInterval< A > source;

	private final SamplerConverter< A, B > converter;

	public ConvertedIterableInterval( final IterableInterval< A > source, final SamplerConverter< A, B > converter )
	{
		this.source = source;
		this.converter = converter;
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public long min( int d )
	{
		return source.min( d );
	}

	@Override
	public void min( long[] min )
	{
		source.min( min );
	}

	@Override
	public void min( Positionable min )
	{
		source.min( min );
	}

	@Override
	public long max( int d )
	{
		return source.max( d );
	}

	@Override
	public void max( long[] max )
	{
		source.max( max );
	}

	@Override
	public void max( Positionable max )
	{
		source.max( max );
	}

	@Override
	public void dimensions( long[] dimensions )
	{
		source.dimensions( dimensions );
	}

	@Override
	public long dimension( int d )
	{
		return source.dimension( d );
	}

	@Override
	public double realMin( int d )
	{
		return source.realMin( d );
	}

	@Override
	public void realMin( double[] min )
	{
		source.realMin( min );
	}

	@Override
	public void realMin( RealPositionable min )
	{
		source.realMin( min );
	}

	@Override
	public double realMax( int d )
	{
		return source.realMax( d );
	}

	@Override
	public void realMax( double[] max )
	{
		source.realMax( max );
	}

	@Override
	public void realMax( RealPositionable max )
	{
		source.realMax( max );
	}

	@Override
	public long size()
	{
		return source.size();
	}

	@Override
	public boolean equalIterationOrder( IterableRealInterval< ? > f )
	{
		return source.equalIterationOrder( f );
	}

	@Override
	public Iterator< B > iterator()
	{
		return cursor();
	}

	@Override
	public B firstElement()
	{
		return cursor().next();
	}

	@Override
	public ConvertedCursor< A, B > cursor()
	{
		return new ConvertedCursor< A, B >( converter, source.cursor() );
	}

	@Override
	public ConvertedCursor< A, B > localizingCursor()
	{
		return new ConvertedCursor< A, B >( converter, source.localizingCursor() );
	}
}
