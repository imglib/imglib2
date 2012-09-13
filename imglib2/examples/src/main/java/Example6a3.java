import ij.ImageJ;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
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
	public Example6a3() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// perform gaussian convolution with float precision
		double[] sigma = new double[ image.numDimensions() ];

		for ( int d = 0; d < image.numDimensions(); ++d )
			sigma[ d ] = 8;

		// we need to extend it nevertheless as the algorithm needs more pixels from around
		// the convolved area and we are not sure how much exactly (altough we could compute
		// it with some effort from the sigma)
		RandomAccessible< FloatType> infiniteImg = Views.extendMirrorSingle( image );

		// define the area
		long[] min = new long[] { 100, 30 };
		long[] max = new long[] { 500, 250 };
		FinalInterval region = new FinalInterval( min, max );

		// call the gauss, we convolve only a region and write it back to the exact same coordinates
		Gauss.inFloat( sigma, infiniteImg, region, image, new Point( min ), image.factory() );

		ImageJFunctions.show( image );
	}

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example6a3();
	}
}
