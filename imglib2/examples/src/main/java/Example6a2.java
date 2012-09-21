import ij.ImageJ;
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
 * but convolve with a different outofboundsstrategy
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example6a2
{
	public Example6a2() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// perform gaussian convolution with float precision
		double[] sigma = new double[ image.numDimensions() ];

		for ( int d = 0; d < image.numDimensions(); ++d )
			sigma[ d ] = 8;

		// first extend the image to infinity, zeropad
		RandomAccessible< FloatType > infiniteImg = Views.extendValue( image, new FloatType() );

		// now we convolve the whole image manually in-place
		// note that is is basically the same as the call above, just called in a more generic way
		//
		// sigma .. the sigma
		// infiniteImg ... the RandomAccessible that is the source for the convolution
		// image ... defines the Interval in which convolution is performed
		// image ... defines the RandomAccessible target of the convolution
		// new Point( image.numDimensions() ) ... defines the offset for the target, here it is (0,0)
		// image.factory ... the image factory which is required to create temporary images
		Gauss.inFloat( sigma, infiniteImg, image, image,
			new Point( image.numDimensions() ), image.factory() );

		// show the in-place convolved image (note the different outofboundsstrategy at the edges)
		ImageJFunctions.show( image );
	}

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example6a2();
	}
}
