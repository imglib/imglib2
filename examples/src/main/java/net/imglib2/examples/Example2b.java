package net.imglib2.examples;

import java.io.File;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

import ij.ImageJ;

/**
 * Here we want to copy an Image into another with a different Container one using a generic method,
 * but we cannot do it with simple Cursors as we use different {@link ImgFactory}s
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example2b
{

	public Example2b() throws ImgIOException
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with ImgOpener using an ArrayImgFactory as FloatType
		Img< FloatType > image = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory<FloatType>(), new FloatType() );

		// copy the image
		Img<FloatType> duplicate = copyImage( image, new CellImgFactory< FloatType >( 20 ) );

		// display the copy
		ImageJFunctions.show( duplicate );
	}

	public <T extends Type<T>> Img<T> copyImage( final Img<T> input, final ImgFactory<T> imgFactory )
	{
		// create a new Image with the same dimensions but the other imgFactory
		// note that the input provides the size for the new image as it implements the Interval interface
		Img<T> output = imgFactory.create( input, input.firstElement() );

		// create a cursor for both images
		Cursor<T> cursorInput = input.cursor();
		Cursor<T> cursorOutput = output.cursor();

		// iterate over the input cursor
		while ( cursorInput.hasNext() )
		{
			// move both forward
			cursorInput.fwd();
			cursorOutput.fwd();

			// set the value of this pixel of the output image, every Type supports T.set( T type )
			cursorOutput.get().set( cursorInput.get() );
		}

		//. return the copy
		return output;
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		try
		{
			new Example2b();
		}
		catch (ImgIOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
