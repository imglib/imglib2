import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
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
import net.imglib2.util.Intervals;
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
		// Create a new image for the output
		Img< U > output = imageFactory.create( source, outputType );

		// define an interval that is one pixel smaller on each side in each dimension,
		// so that the search in the 8-neighborhood (3x3x3...x3) never goes outside
		// of the defined interval
		Interval interval = Intervals.expand( source, -1 );

		// create a view on the source with this interval
		source = Views.interval( source, interval );

		// create a Cursor that iterates over the source and checks in a 8-neighborhood
		// if it is a minima
		final Cursor< T > center = Views.iterable( source ).cursor();

		// instantiate a RectangleShape to access rectangular local neighborhoods
		// of radius 1 (that is 3x3x...x3 neighborhoods), skipping the center pixel
		// (this corresponds to an 8-neighborhood in 2d or 26-neighborhood in 3d, ...)
		final RectangleShape shape = new RectangleShape( 1, true );

		// iterate over the set of neighborhoods in the image
		for ( final Neighborhood< T > localNeighborhood : shape.neighborhoods( source ) )
		{
			// what is the value that we investigate?
			// (the center cursor runs over the image in the same iteration order as neighborhood)
			final T centerValue = center.next();

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
				HyperSphere< U > hyperSphere = new HyperSphere< U >( output, center, 1 );

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
