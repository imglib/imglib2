package net.imglib2.converter;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.sampler.SamplerConverter;

public class ConvertedRandomAccessible< A, B > implements RandomAccessible< B >
{
	private final RandomAccessible< A > source;

	private final SamplerConverter< A, B > converter;

	public ConvertedRandomAccessible( final RandomAccessible< A > source, final SamplerConverter< A, B > converter )
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
	public ConvertedRandomAccess< A, B > randomAccess()
	{
		return new ConvertedRandomAccess< A, B >( converter, source.randomAccess() );
	}

	@Override
	public ConvertedRandomAccess< A, B > randomAccess( Interval interval )
	{
		return new ConvertedRandomAccess< A, B >( converter, source.randomAccess( interval ) );
	}
}
