package mpicbg.imglib.workshop.prewritten;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.util.RealSum;
import ij.ImageJ;

/**
 * Perform a generic min, max search and compute the median and average intensity of the image
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example3
{
	public Example3()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with LOCI using an ArrayContainer
		Image<FloatType> image = LOCI.openLOCIFloatType( file.getAbsolutePath(), new ArrayContainerFactory() );

		// compute min and max of the Image
		FloatType min = image.createType();
		FloatType max = image.createType();

		computeMinMax( image, min, max );

		System.out.println( "minimum Value: " + min );
		System.out.println( "maximum Value: " + max );

		// compute average of the image
		double avg = computeAverage( image );
		System.out.println( "average Value: " + avg );

		// compute median of the image
		FloatType median = computeMedian( image );
		System.out.println( "median Value: " + median );

		// for completeness, compute the correct average of the image
		// (important for huge number of pixels when the precision of double is not sufficient)
		avg = computeRealAverage( image );
		System.out.println( "real average Value: " + avg );
	}

	public <T extends Comparable<T> & Type<T>> void computeMinMax( final Image<T> image, final T min, final T max )
	{
		// create a cursor for the image (the order does not matter)
		Cursor<T> cursor = image.createCursor();

		// initialize min and max with the first image value
		cursor.fwd();

		min.set( cursor.getType() );
		max.set( cursor.getType() );

		// loop over the image and determine min and max value
		while( cursor.hasNext() )
		{
			cursor.fwd();

			// we need this type more than once
			T type = cursor.getType();

			if ( type.compareTo( min ) < 0 )
				min.set( type );

			if ( type.compareTo( max ) > 0 )
				max.set( type );
		}
	}

	public <T extends RealType<T>> double computeAverage( final Image<T> image )
	{
		// create a cursor for the image (the order does not matter)
		Cursor<T> cursor = image.createCursor();

		// count all values
		double sum = 0;

		// loop over the image and determine min and max value
		while( cursor.hasNext() )
		{
			cursor.fwd();

			sum += cursor.getType().getRealDouble();
		}

		return sum / image.getNumPixels();
	}

	public <T extends RealType<T>> double computeRealAverage( final Image<T> image )
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

	public <T extends Comparable<T> & Type<T>> T computeMedian( final Image<T> image )
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
