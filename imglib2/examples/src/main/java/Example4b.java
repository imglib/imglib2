import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.localneighborhood.LocalNeighborhood;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * Here we use special cursors to find the local minima and
 * display them with spheres in another image
 */
public class Example4b
{
	public < T extends RealType< T > & NativeType< T > > Example4b()
		throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener
		Img< T > img = new ImgOpener().openImg( "DrosophilaWing.tif" );

		// first we do a small in-place gaussian smoothing with a sigma of 1
		Gauss.inDoubleInPlace( new double[]{ 1, 1 }, img );

		// find local minima and paint them into another image as spheres
		Img< BitType > display =
			findAndDisplayLocalMinima( img, new ArrayImgFactory< BitType >(), new BitType() );

		// display output and input
		ImageJFunctions.show( img );
		ImageJFunctions.show( display );
	}

	/**
	 * Checks all pixels in the image if they are a local minima and
	 * draws a circle into the output if they are
	 *
	 * @param source - the image data to work on
	 * @param imageFactory - the factory for the output img
	 * @param outputType - the output type
	 * @return - an Img with circles on locations of a local minimum
	 */
	public static < T extends Comparable< T >, U extends RealType< U > > Img< U >
		findAndDisplayLocalMinima(
			RandomAccessibleInterval< T > source,
			ImgFactory< U > imageFactory, U outputType )
	{
		// we need the number of dimensions a lot
		final int numDimensions = source.numDimensions();

		// Create a new image for the output
		Img< U > output = imageFactory.create( source, outputType );

		// define an interval that is one pixel smaller on each side in each dimension,
		// so that the search in the 8-neighborhood (3x3x3...x3) never goes outside
		// of the defined interval
		long[] min = new long[ numDimensions ];
		long[] max = new long[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			min[ d ] = source.min( d ) + 1;
			max[ d ] = source.max( d ) - 1;
		}

		// create a view on the source with this interval
		source = Views.interval( source, min, max );

		// create a Cursor that iterates over the source and checks in a 8-neighborhood
		// if it is a minima
		Cursor< T > cursor = Views.iterable( source ).localizingCursor();

		// instantiate a local neighborhood that we will use all the time
		// it iterates all pixels adjacent to the center, but skips the center
		// pixel (this corresponds to an 8-neighborhood in 2d or 26-neighborhood in 3d, ...)
		LocalNeighborhood< T > localNeighborhood =
			new LocalNeighborhood< T >( source, cursor );

		// iterate over the image
		while ( cursor.hasNext() )
		{
			cursor.fwd();

			// update the local neighborhood cursor to the position of the cursor
			localNeighborhood.updateCenter( cursor );

			// what is the value that we investigate
			final T centerValue = cursor.get();

			// keep this boolean true as long as no other value in the local neighborhood
			// is larger or equal
			boolean isMinimum = true;

			// check if all pixels in the local neighborhood that are smaller
			for ( final T value : localNeighborhood )
			{
				// test if the center is smaller than the current pixel value
				if ( centerValue.compareTo( value ) >= 0 )
				{
					isMinimum = false;
					break;
				}
			}

			if ( isMinimum )
			{
				// draw a sphere of radius one in the new image
				HyperSphere< U > hyperSphere = new HyperSphere< U >( output, cursor, 1 );

				// set every value inside the sphere to 1
				for ( U value : hyperSphere )
					value.setOne();
			}
		}

		return output;
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example4b();
	}
}
