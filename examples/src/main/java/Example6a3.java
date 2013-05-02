import ij.ImageJ;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

/**
 * Use of Gaussian Convolution on the Image
 * but convolve just a part of the image
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example6a3
{
	public Example6a3() throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// perform gaussian convolution with float precision
		double sigma = 8;

		// we need to extend it nevertheless as the algorithm needs more pixels from around
		// the convolved area and we are not sure how much exactly (altough we could compute
		// it with some effort from the sigma).
		// Here we let the Views framework take care of the details. The Gauss convolution
		// knows which area of the source image is required, and if the extension is not needed,
		// it will operate on the original image with no runtime overhead.
		RandomAccessible< FloatType> infiniteImg = Views.extendMirrorSingle( image );

		// define the area of the image which we want to compute
		FinalInterval interval = Intervals.createMinMax( 100, 30, 500, 250 );
		RandomAccessibleInterval< FloatType > region = Views.interval( image, interval );

		// call the gauss, we convolve only a region and write it back to the exact same coordinates
		Gauss3.gauss( sigma, infiniteImg, region );

		ImageJFunctions.show( image );
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example6a3();
	}
}
