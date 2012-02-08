package net.imglib2.examples;

import java.io.File;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
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
	public < T extends RealType<T> & NativeType<T> > Example1b() throws ImgIOException, IncompatibleTypeException
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with ImgOpener using an ArrayImgFactory, here the return type will be defined by the opener
		// the opener will ignore the Type of the ArrayImgFactory
		Img<T> image = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory< T >() );

		// display it via ImgLib using ImageJ
		ImageJFunctions.show( image ).show();

		// open with ImgOpener as Float using a CellImgFactory, it will be opened as float independent of the type of the image
		// to enforce to open it as FloatType, an instance of FloatType has to be passed along
		Img<FloatType> imageFloat = new ImgOpener().openImg( file.getAbsolutePath(), new CellImgFactory<FloatType>( 10 ), new FloatType() );

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
		try
		{
			new Example1b();
		}
		catch (ImgIOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IncompatibleTypeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
