package net.imglib2.examples;

import java.io.File;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImagePlusAdapter;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.type.numeric.RealType;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;


/**
 * Opens a file with ImageJ and wraps it into an ImgLib {@link Image}.
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example1a
{
	// within this method we define <T> to be a RealType
	public < T extends RealType<T> > Example1a()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open a file with ImageJ
		final ImagePlus imp = new Opener().openImage( file.getAbsolutePath() );

		// display it via ImageJ
		imp.show();

		// wrap it into an ImgLib image (no copying)
		final Image<T> image = ImagePlusAdapter.wrap( imp );

		// display it via ImgLib using ImageJ
		ImageJFunctions.displayAsVirtualStack( image ).show();
	}


	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1a();
	}
}
