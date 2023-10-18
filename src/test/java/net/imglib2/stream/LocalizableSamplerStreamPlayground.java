package net.imglib2.stream;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import net.imglib2.Localizable;
import net.imglib2.LocalizableSampler;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Util;

public class LocalizableSamplerStreamPlayground
{
	public static void main( String[] args )
	{
		Img< IntType > img = ArrayImgs.ints( 5, 5 );
		Streams.localizable( img ).forEach( s ->
				s.get().set( s.getIntPosition( 0 ) ) );

		Streams.localizable( img ).forEach( adapt(
				( pos, t ) -> t.set( ( int ) ( pos.x() + pos.y() ) )
		) );

		img.getAt( 2, 2 ).set( 9 );

		for ( int y = 0; y < 5; ++y )
		{
			for ( int x = 0; x < 5; ++x )
			{
				System.out.print( img.getAt( x, y ).get() + ",  " );
			}
			System.out.println();
		}

		final Optional< LocalizableSampler< IntType > > max2 = Streams.localizable( img )
				.filter( c -> c.getIntPosition( 0) % 2 == 1 )
				.map( LocalizableSampler::copy )
				.max( Comparator.comparingInt( c -> c.get().get() ) );
		System.out.println( "max at " + Util.printCoordinates( max2.get() ) );


//		BinaryOperator< LocalizableSampler< IntType > > max_accumulate = ( result, element ) -> {
//			if ( result == null || result.get().compareTo( element.get() ) <= 0 )
//				return element.copy();
//			else
//				return result;
//		};

//		final LocalizableSampler< IntType > reduce = Streams.localizable( img )
//				.parallel()
//				.filter( c -> c.getIntPosition( 0) % 2 == 1 )
//				.reduce( null,max_accumulate );
//		System.out.println( "max at " + Util.printCoordinates( reduce ) );


		final Optional< LocalizableSampler< IntType > > max =
				Streams.localizable( img )
						.parallel()
						.filter( c -> c.getIntPosition( 0 ) % 2 == 1 )
						.max( Comparator.comparingInt( s -> s.get().get() ) );
		System.out.println( "max at " + Util.printCoordinates( max.get() ) );



		bigRandom();
	}


	static void bigRandom()
	{
		long[] dims = { 50, 50, 50 };
		Img<IntType> img = ArrayImgs.ints( dims );

		Random random = new Random();
		img.forEach( t -> t.set( random.nextInt( 1000 ) ) );

		img.getAt( 21, 24, 12 ).set( 1000 );

		final Optional< LocalizableSampler< IntType > > max1 =
				Streams.localizable( img )
						.parallel()
						.filter( c -> c.getIntPosition( 0 ) % 2 == 1 )
						.map( LocalizableSampler::copy )
						.max( Comparator.comparingInt( s -> s.get().get() ) );
		System.out.println( "max1 at " + Util.printCoordinates( max1.get() ) );

		final Optional< LocalizableSampler< IntType > > max2 =
				Streams.localizable( img )
						.map( LocalizableSampler::copy )
						.parallel()
						.filter( c -> c.getIntPosition( 0 ) % 2 == 1 )
						.max( Comparator.comparingInt( s -> s.get().get() ) );
		System.out.println( "max2 at " + Util.printCoordinates( max2.get() ) );

		final Optional< LocalizableSampler< IntType > > max3 =
				Streams.localizable_( img )
						.parallel()
						.filter( c -> c.getIntPosition( 0 ) % 2 == 1 )
						.max( Comparator.comparingInt( s -> s.get().get() ) );
		System.out.println( "max3 at " + Util.printCoordinates( max3.get() ) );
	}



	public static class Pos
	{
		Localizable l;

		Pos of( Localizable l )
		{
			this.l = l;
			return this;
		}

		public long x()
		{
			return l.getLongPosition( 0 );
		}

		public long y()
		{
			return l.getLongPosition( 1 );
		}
	}

	static < T > Consumer< LocalizableSampler< T > > adapt( BiConsumer< Pos, T > consumer )
	{
		final Pos pos = new Pos();
		return tCursor -> {
			consumer.accept( pos.of( tCursor ), tCursor.get() );
		};
	}
}
