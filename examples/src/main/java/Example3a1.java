import ij.ImageJ;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;

/**
 * Perform a generic min & max search
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example3a1
{
	public < T extends RealType< T > & NativeType< T > > Example3a1()
		throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener (he will decide which Img is best)
		Img< T > img = new ImgOpener().openImg( "DrosophilaWing.tif" );

		// create two empty variables
		T min = img.firstElement().createVariable();
		T max = img.firstElement().createVariable();

		// compute min and max of the Image
		computeMinMax( img, min, max );

		System.out.println( "minimum Value (img): " + min );
		System.out.println( "maximum Value (img): " + max );
	}

	/**
	 * Compute the min and max for any {@link Iterable}, like an {@link Img}.
	 *
	 * The only functionality we need for that is to iterate. Therefore we need no {@link Cursor}
	 * that can localize itself, neither do we need a {@link RandomAccess}. So we simply use the
	 * most simple interface in the hierarchy.
	 *
	 * @param input - the input that has to just be {@link Iterable}
	 * @param min - the type that will have min
	 * @param max - the type that will have max
	 */
	public < T extends Comparable< T > & Type< T > > void computeMinMax(
		final Iterable< T > input, final T min, final T max )
	{
		// create a cursor for the image (the order does not matter)
		final Iterator< T > iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set( type );
		max.set( type );

		// loop over the rest of the data and determine min and max value
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

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example3a1();
	}
}
