package net.imglib2.interpolation;

import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

public final class InterpolatedRandomAccessible< T > implements RealRandomAccessible< T >
{
	private final RandomAccessible< T > source;

	private final InterpolatorFactory< T, RandomAccessible< T > > factory;

	public InterpolatedRandomAccessible( final RandomAccessible< T > source, final InterpolatorFactory< T, RandomAccessible< T > > factory )
	{
		this.source = source;
		this.factory = factory;
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public RealRandomAccess< T > realRandomAccess()
	{
		return factory.create( source );
	}
}
