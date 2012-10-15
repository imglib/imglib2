import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;

/**
 * Perform a generic min/max search.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example3a3
{
	public < T extends RealType< T > & NativeType< T > > Example3a3()
		throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener (he will decide which Img is best)
		Img< T > img = new ImgOpener().openImg( "DrosophilaWing.tif" );

		// create two location objects
		Point locationMin = new Point( img.numDimensions() );
		Point locationMax = new Point( img.numDimensions() );

		// compute location of min and max
		computeMinMaxLocation( img, locationMin, locationMax );

		System.out.println( "location of minimum Value (img): " + locationMin );
		System.out.println( "location of maximum Value (img): " + locationMax );
	}

	/**
	 * Compute the location of the minimal and maximal intensity for any IterableInterval,
	 * like an {@link Img}.
	 *
	 * The functionality we need is to iterate and retrieve the location. Therefore we need a
	 * Cursor that can localize itself.
	 * Note that we do not use a LocalizingCursor as localization just happens from time to time.
	 *
	 * @param input - the input that has to just be {@link IterableInterval}
	 * @param minLocation - the location for the minimal value
	 * @param maxLocation - the location of the maximal value
	 */
	public < T extends Comparable< T > & Type< T > > void computeMinMaxLocation(
		final IterableInterval< T > input, final Point minLocation, final Point maxLocation )
	{
		// create a cursor for the image (the order does not matter)
		final Cursor< T > cursor = input.cursor();

		// initialize min and max with the first image value
		T type = cursor.next();
		T min = type.copy();
		T max = type.copy();

		// loop over the rest of the data and determine min and max value
		while ( cursor.hasNext() )
		{
			// we need this type more than once
			type = cursor.next();

			if ( type.compareTo( min ) < 0 )
			{
				min.set( type );
				minLocation.setPosition( cursor );
			}

			if ( type.compareTo( max ) > 0 )
			{
				max.set( type );
				maxLocation.setPosition( cursor );
			}
		}
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example3a3();
	}
}
