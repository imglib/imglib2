import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;

import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;

/**
 * Opens a file with ImageJ and wraps it into an ImgLib {@link Img}.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example1a
{
	// within this method we define <T> to be a NumericType (depends on the type of ImagePlus)
	// you might want to define it as RealType if you know it cannot be an ImageJ RGB Color image
	public < T extends NumericType< T > & NativeType< T > > Example1a()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open a file with ImageJ
		final ImagePlus imp = new Opener().openImage( file.getAbsolutePath() );

		// display it via ImageJ
		imp.show();

		// wrap it into an ImgLib image (no copying)
		final Img< T > image = ImagePlusAdapter.wrap( imp );

		// display it via ImgLib using ImageJ
		ImageJFunctions.show( image );
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1a();
	}
}
