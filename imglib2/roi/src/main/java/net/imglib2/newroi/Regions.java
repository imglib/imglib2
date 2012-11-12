package net.imglib2.newroi;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Pair;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.newroi.util.IterableRandomAccessibleRegion;
import net.imglib2.newroi.util.PairRandomAccessible;
import net.imglib2.newroi.util.PositionableIterableInterval;
import net.imglib2.newroi.util.PositionableIterableRandomAccessibleRegion;
import net.imglib2.newroi.util.PositionableSamplingIterableInterval;
import net.imglib2.newroi.util.SamplingIterableInterval;
import net.imglib2.newroi.util.WrappedPositionableRandomAccessible;
import net.imglib2.type.BooleanType;

public class Regions
{
	public static < P, T > RandomAccessible< Pair< P, T > > bind( final RandomAccessible< P > region, final RandomAccessible< T > img )
	{
		return new PairRandomAccessible< P, T >( region, img );
	}

//	public static < B extends BooleanType< B >, T > IterableInterval< T > sample( final RandomAccessibleInterval< B > region, final RandomAccessible< T > img )
//	{
//		// TODO (?)
//		return null;
//	}

//	public static < B extends BooleanType< B >, T > IterableInterval< T > sample( final RandomAccessible< B > region, final RandomAccessibleInterval< T > img )
//	{
//		// TODO (?)
//		return null;
//	}

	public static < B extends BooleanType< B >, T > IterableInterval< T > sample( final IterableInterval< B > region, final RandomAccessible< T > img )
	{
		return SamplingIterableInterval.create( region, img );
	}

//	public static < B extends BooleanType< B >, I extends RandomAccessibleInterval< B > & Positionable & Localizable, T >
//		PositionableIterableInterval< T > samplePositionable( final I region, final RandomAccessibleInterval< T > img )
//	{
//		// TODO (?)
//		return null;
//	}

//	public static < B extends BooleanType< B >, I extends RandomAccessible< B > & Positionable & Localizable, T >
//		PositionableIterableInterval< T > samplePositionable( final I region, final RandomAccessible< T > img )
//	{
//		// TODO (?)
//		return null;
//	}

	public static < B extends BooleanType< B >, I extends IterableInterval< B > & Positionable & Localizable, T >
		PositionableIterableInterval< T > samplePositionable( final I region, final RandomAccessible< T > img )
	{
		return PositionableSamplingIterableInterval.create( region, img );
	}

	public static < T extends Positionable & Localizable > RandomAccessible< T > accessible( final T region )
	{
		return new WrappedPositionableRandomAccessible< T >( region );
	}

	public static < B extends BooleanType< B > > IterableInterval< B > iterable( final RandomAccessibleInterval< B > region )
	{
		if ( region instanceof IterableInterval )
			return ( IterableInterval< B > ) region;
		else
			return IterableRandomAccessibleRegion.create( region );
	}

	public static < B extends BooleanType< B >, I extends RandomAccessibleInterval< B > & Positionable & Localizable > PositionableIterableInterval< B > iterablePositionable( final I region )
	{
		if ( region instanceof PositionableIterableInterval )
			return ( PositionableIterableInterval< B > ) region;
		else
			return PositionableIterableRandomAccessibleRegion.create( region );
	}
}
