package net.imglib2.newroi.util;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

public class WrappedPositionableRandomAccessible< T extends Localizable & Positionable > implements RandomAccessible< T >
{
	final T source;


	public WrappedPositionableRandomAccessible( final T source )
	{
		this.source = source;
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public WrappedPositionableRandomAccess< T > randomAccess()
	{
		return new WrappedPositionableRandomAccess< T >( source );
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

}
