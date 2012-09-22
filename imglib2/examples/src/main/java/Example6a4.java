import ij.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Use of Gaussian Convolution on the Image
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example6a4
{
	public Example6a4() throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// perform all (n-1)-dimensional gaussian (in this case it means 1d) on
		// some of the row/columns
		double[] sigma = new double[ image.numDimensions() - 1 ];

		for ( int d = 0; d < sigma.length; ++d )
			sigma[ d ] = 16;

		// iterate over all dimensions, take always a hyperslice
		for ( int dim = 0; dim < image.numDimensions(); ++dim )
			// iterate over all possible hyperslices
			for ( long pos = 0; pos < image.dimension( dim ); ++pos )
				// convolve a subset of the 1-dimensional views
				if ( pos/30 % 2 == 1 )
				{
					// get the n-1 dimensional "slice"
					RandomAccessibleInterval< FloatType > view =
						Views.hyperSlice( image, dim, pos );

					// compute the gauss in-place on the view
					Gauss3.gauss( sigma, Views.extendMirrorSingle( view ), view );
				}

		// show the result
		ImageJFunctions.show( image );
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example6a4();
	}
}
