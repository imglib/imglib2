import ij.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Open an ArrayImg< FloatType > and display partly and rotated
 */
public class Example1d
{
	public Example1d() throws ImgIOException
	{
		// open file as float
		Img< FloatType > img = new ImgOpener().openImg( "DrosophilaWing.tif",
				new ArrayImgFactory< FloatType >(), new FloatType() );

		// display image
		ImageJFunctions.show( img );

		// use a View to define an interval (min and max coordinate, including) to display
		RandomAccessibleInterval< FloatType > view =
				Views.interval( img, new long[] { 200, 200 }, new long[]{ 500, 350 } );

		// display only the part of the Img
		ImageJFunctions.show( view );

		// or the same area rotated by 90 degrees (x-axis (0) and y-axis (1) switched)
		ImageJFunctions.show( Views.rotate( view, 0, 1 ) );
	}

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1d();
	}
}
