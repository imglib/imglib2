package net.imglib2.examples;

import java.io.File;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import ij.ImageJ;
import ij.ImagePlus;


/**
 * Opens a file with ImgOpener Bioformats as an ImgLib {@link Image}.
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example1b
{
	// within this method we define <T> to be a RealType
	public < T extends RealType<T> & NativeType<T> > Example1b()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with ImgOpener using an ArrayContainer
		Img<T> image = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory<T>() );

		// display it via ImgLib using ImageJ
		ImageJFunctions.show( image ).show();

		// open with ImgOpener as Float using an ArrayContainer
		Img<FloatType> imageFloat = new ImgOpener().openImg( file.getAbsolutePath(), new CellImgFactory<FloatType>( 10 ) );

		// display it via ImgLib using ImageJ
		final ImagePlus imp = ImageJFunctions.show( imageFloat );
		imp.resetDisplayRange();
		imp.show();
	}


	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1b();
	}
}
