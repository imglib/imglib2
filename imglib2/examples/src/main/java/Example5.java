import ij.ImageJ;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Illustrate outside strategies
 *
 */
public class Example5
{
	public Example5() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( "DrosophilaWingSmall.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// create an infinite view where all values outside of the Interval are 0
		RandomAccessible< FloatType> infinite1 =
			Views.extendValue( image, new FloatType( 0 ) );

		// create an infinite view where all values outside of the Interval are 128
		RandomAccessible< FloatType> infinite2 =
			Views.extendValue( image, new FloatType( 128 ) );

		// create an infinite view where all outside valuesare random in a range of 0-255
		RandomAccessible< FloatType> infinite3 = Views.extendRandom( image, 0, 255 );

		// create an infinite view where all values outside of the Interval are
		// the mirrored content, the mirror is the last pixel
		RandomAccessible< FloatType> infinite4 = Views.extendMirrorSingle( image );

		// create an infinite view where all values outside of the Interval are
		// the mirrored content, the mirror is BEHIND the last pixel,
		// i.e. the first and last pixel are always duplicated
		RandomAccessible< FloatType> infinite5 = Views.extendMirrorDouble( image );

		// all values outside of the Interval periodically repeat the image content
		// (like the Fourier space assumes)
		RandomAccessible< FloatType> infinite6 = Views.extendPeriodic( image );

		// if you implemented your own strategy that you want to instantiate, it will look like this
		RandomAccessible< FloatType> infinite7 =
			new ExtendedRandomAccessibleInterval< FloatType, Img< FloatType > >( image,
				new OutOfBoundsConstantValueFactory< FloatType, Img< FloatType > >(
				new FloatType( 255 ) ) );

		// visualize the outofbounds strategies

		// in order to visualize them, we have to define a new interval
		// on them which can be displayed
		long[] min = new long[ image.numDimensions() ];
		long[] max = new long[ image.numDimensions() ];

		for ( int d = 0; d < image.numDimensions(); ++d )
		{
			// we add/subtract another 30 pixels here to illustrate
			// that it is really infinite and does not only work once
			min[ d ] = -image.dimension( d ) - 90 ;
			max[ d ] = image.dimension( d ) * 2 - 1 + 90;
		}

		// define the Interval on the infinite random accessibles
		FinalInterval interval = new FinalInterval( min, max );

		// now define the interval on the infinite view and display
		ImageJFunctions.show( Views.interval( infinite1, interval ) );
		ImageJFunctions.show( Views.interval( infinite2, interval ) );
		ImageJFunctions.show( Views.interval( infinite3, interval ) );
		ImageJFunctions.show( Views.interval( infinite4, interval ) );
		ImageJFunctions.show( Views.interval( infinite5, interval ) );
		ImageJFunctions.show( Views.interval( infinite6, interval ) );
		ImageJFunctions.show( Views.interval( infinite7, interval ) );
	}

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example5();
	}
}
