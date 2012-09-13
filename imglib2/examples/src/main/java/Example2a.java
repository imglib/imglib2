import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Here we want to copy an Image into another one using a generic method
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example2a
{
	public Example2a() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > img = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// copy the image, as it is a generic method it also works with FloatType
		Img< FloatType > duplicate = copyImage( img );

		// display the copy
		ImageJFunctions.show( duplicate );
	}

	/**
	 * Generic, type-agnostic method to create an identical copy of an Img
	 *
	 * @param input - the Img to copy
	 * @return - the copy of the Img
	 */
	public < T extends Type< T > > Img< T > copyImage( final Img< T > input )
	{
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it implements
		// the Interval interface
		Img< T > output = input.factory().create( input, input.firstElement() );

		// create a cursor for both images
		Cursor< T > cursorInput = input.cursor();
		Cursor< T > cursorOutput = output.cursor();

		// iterate over the input
		while ( cursorInput.hasNext())
		{
			// move both cursors forward by one pixel
			cursorInput.fwd();
			cursorOutput.fwd();

			// set the value of this pixel of the output image to the same as the input,
			// every Type supports T.set( T type )
			cursorOutput.get().set( cursorInput.get() );
		}

		// return the copy
		return output;
	}

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example2a();
	}
}
