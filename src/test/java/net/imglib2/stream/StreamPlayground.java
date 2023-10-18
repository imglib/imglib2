package net.imglib2.stream;

import java.util.Spliterator;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;

public class StreamPlayground
{
	public static void main( String[] args )
	{
		final Img< IntType > img = ArrayImgs.ints( 10 );
		final Cursor< IntType > cursor = img.cursor();
		for ( int i = 0; i < 10; i++ )
		{
			cursor.next().set( i );
		}

		Spliterator< IntType > split = img.spliterator();
		split.forEachRemaining( t -> System.out.println( t.get() ) );

		System.out.println();

		split = img.spliterator();
		System.out.println( "split.estimateSize() = " + split.estimateSize() );
		final Spliterator< IntType > split2 = split.trySplit();
		System.out.println( "split.estimateSize() = " + split.estimateSize() );
		split.forEachRemaining( t -> System.out.println( t.get() ) );
		System.out.println( "split2.estimateSize() = " + split2.estimateSize() );
		split2.forEachRemaining( t -> System.out.println( t.get() ) );

//		img.stream().parallel().forEach( t -> t.set( ThreadLocalRandom.current().nextInt( 1000 ) ) );
	}

/*
	static < T extends Type< T > > void fill( Img< T > img, T value )
	{
		img.stream().forEach( t -> t.set( value ) );

		// same as ...
		img.forEach( t -> t.set( value ) );

		// ... but with easy parallelization
		img.parallelStream().forEach( t -> t.set( value ) );

		// ... but also easily parallelizable with LoopBuilder
		LoopBuilder.setImages( img ).multiThreaded().forEachPixel( t -> t.set( value ) );
	}

	static double sum( Img< DoubleType > img )
	{

		return img.stream()
				.mapToDouble( DoubleType::get )
				.sum();

		// same as ...
		double sum = 0.0;
		for ( DoubleType t : img )
		{
			sum += t.get();
		}
		return sum;

		// ... still with easy parallelization
		return img.parallelStream()
				.mapToDouble( DoubleType::get )
				.sum();

		// no easy LoopBuilder equivalent
	}

	static double max( Img< DoubleType > img )
	{

		return img.stream()
				.mapToDouble( DoubleType::get )
				.max().getAsDouble();

		// same as ...
		double max = Double.NEGATIVE_INFINITY;
		for ( DoubleType t : img )
		{
			max = Math.max( max, t.get() );
		}
		return max;

		// ... still with easy parallelization
		return img.parallelStream()
				.mapToDouble( DoubleType::get )
				.max().getAsDouble();

		// no easy LoopBuilder equivalent
	}

	static void max( Img< IntType > img )
	{
		Optional< LocalizableSampler< IntType > > optionalMax =
				Streams.localizable( img )
						.parallel()
						//.map(LocalizableSampler::copy)
						.max( Comparator.comparingInt( c -> c.get().get() ) );

		LocalizableSampler< IntType > max = optionalMax.get();

		System.out.println( "max position = " + Util.printCoordinates( max ) );
		System.out.println( "max value = " + max.get().getInteger() );
	}
*/
}
