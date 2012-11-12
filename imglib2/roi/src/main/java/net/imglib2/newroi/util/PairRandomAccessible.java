package net.imglib2.newroi.util;

import net.imglib2.Interval;
import net.imglib2.Pair;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

public class PairRandomAccessible< A, B > implements RandomAccessible< Pair< A, B > >
{
	final RandomAccessible< A > a;

	final RandomAccessible< B > b;

	public PairRandomAccessible( final RandomAccessible< A > a, final RandomAccessible< B > b )
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public int numDimensions()
	{
		return a.numDimensions();
	}

	@Override
	public RandomAccess< Pair< A, B >> randomAccess()
	{
		return new PairRandomAccess< A, B >( a.randomAccess(), b.randomAccess() );
	}

	@Override
	public RandomAccess< Pair< A, B >> randomAccess( final Interval interval )
	{
		return new PairRandomAccess< A, B >( a.randomAccess( interval ), b.randomAccess( interval ) );
	}
};
