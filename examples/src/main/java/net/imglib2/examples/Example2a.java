package net.imglib2.examples;

import java.io.File;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import ij.ImageJ;
import ij.ImagePlus;

/**
 * Here we want to copy an Image into another one using a generic method
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example2a<T extends RealType<T> & NativeType<T>>
{

	public Example2a() throws ImgIOException, IncompatibleTypeException
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with ImgOpener using an ArrayContainer
		Img<T> image = new ImgOpener().openImg( file.getAbsolutePath());

		// copy the image
		Img<T> duplicate = image.copy();

		// display the copy
		ImageJFunctions.show( duplicate );
	}

	public static<T extends RealType<T> & NativeType<T>> void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example2a<T>();
	}
}
