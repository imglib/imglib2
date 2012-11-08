package net.imglib2.newroi;

import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Pair;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.newroi.util.PairRandomAccess;
import net.imglib2.type.BooleanType;

public class Regions
{
	public static < P, T > RandomAccessible< Pair< P, T > > bind( final RandomAccessible< P > region, final RandomAccessible< T > img )
	{
		return new RandomAccessible< Pair< P, T > >()
		{
			@Override
			public int numDimensions()
			{
				return region.numDimensions();
			}

			@Override
			public RandomAccess< Pair< P, T >> randomAccess()
			{
				return new PairRandomAccess< P, T >( region.randomAccess(), img.randomAccess() );
			}

			@Override
			public RandomAccess< Pair< P, T >> randomAccess( final Interval interval )
			{
				return new PairRandomAccess< P, T >( region.randomAccess( interval ), img.randomAccess( interval ) );
			}
		};
	}

	public static < P extends BooleanType< P > > IterableInterval< P > iterable( final RandomAccessibleInterval< P > region )
	{
		// TODO
		return null;
	}
}
