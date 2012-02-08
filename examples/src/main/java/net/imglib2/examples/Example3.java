package net.imglib2.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import net.imglib.examples.util.RealSum;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.numeric.RealType;

import ij.ImageJ;

/**
 * Perform a generic min, max search and compute the median and average intensity of the image
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example3
{
	public Example3()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with ImgOpener using an ArrayContainer
		Img<FloatType> img = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory<FloatType>(), new FloatType() );

		// compute min and max of the Image
		FloatType min = img.firstElement().createVariable();
		FloatType max = img.firstElement().createVariable();

		computeMinMax( img, min, max );

		System.out.println( "minimum Value: " + min );
		System.out.println( "maximum Value: " + max );

		// compute average of the image
		double avg = computeAverage( img );
		System.out.println( "average Value: " + avg );

		// compute median of the image
		FloatType median = computeMedian( img );
		System.out.println( "median Value: " + median );

		// for completeness, compute the correct average of the image
		// (important for huge number of pixels when the precision of double is not sufficient)
		avg = computeRealAverage( img );
		System.out.println( "real average Value: " + avg );
	}

	/**
	 * Compute the min and max for any {@link Iterable}, like an {@link Img}.
	 *
	 * The only functionality we need for that is to iterate. Therefore we need no {@link Cursor} that can localize itself,
	 * neither do we need a {@link RandomAccess}. So we simply use the most simple interface in the hierarchy.
	 *
	 * @param input - the input that has to just be {@link Iterable}
	 * @param min - the type that will have min
	 * @param max - the type that will have max
	 */
	public <T extends Comparable<T> & Type<T>> void computeMinMax( final Iterable<T> input, final T min, final T max )
	{
		// create a cursor for the image (the order does not matter)
		final Iterator< T > iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set( type );
		max.set( type );

		// loop over the image and determine min and max value
		while ( iterator.hasNext() )
		{
			// we need this type more than once
			type = iterator.next();

			if ( type.compareTo( min ) < 0 )
				min.set( type );

			if ( type.compareTo( max ) > 0 )
				max.set( type );
		}
	}

	/**
	 * Compute the average intensity for an {@link Iterable}.
	 *
	 * @param input
	 * @return
	 */
	public <T extends RealType<T>> double computeAverage( final Iterable<T> input )
	{
		// count all values
		double sum = 0;
		long count = 0;

		for ( final T type : input )
		{
			sum += type.getRealDouble();
			++count;
		}

		return sum / count;
	}

	public <T extends RealType<T>> double computeRealAverage( final Img<T> image )
	{
		// create a cursor for the image (the order does not matter)
		Cursor<T> cursor = image.createCursor();

		// count all values
		RealSum realsum = new RealSum();

		// loop over the image and determine min and max value
		while( cursor.hasNext() )
		{
			cursor.fwd();

			realsum.add( cursor.getType().getRealDouble() );
		}

		return realsum.getSum() / image.getNumPixels();
	}

	public <T extends Comparable<T> & Type<T>> T computeMedian( final Img<T> image )
	{
		// create an ArrayList of values
		ArrayList<T> values = new ArrayList<T>();

		// loop over the image and add all values
		for ( final T value : image )
			values.add( value.copy() );

		// let Java sort it for us
		Collections.sort( values );

		// collect median value
		T median = values.get( values.size()/2 );

		return median;
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example3();
	}
}
