package mpicbg.imglib.workshop.prewritten;

import java.io.File;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.real.FloatType;
import ij.ImageJ;

/**
 * Here we want to copy an Image into another one using a generic method
 *  
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example2a
{
	
	public Example2a()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with LOCI using an ArrayContainer
		Image<FloatType> image = LOCI.openLOCIFloatType( file.getAbsolutePath(), new ArrayContainerFactory() );
		
		// copy the image
		Image<FloatType> duplicate = copyImage( image );
		
		// display the copy
		duplicate.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( duplicate ).show();
	}
	
	public <T extends Type<T>> Image<T> copyImage( final Image<T> input )
	{
		// create a new Image with the same properties
		Image<T> output = input.createNewImage();
		
		// create a cursor for both images
		Cursor<T> cursorInput = input.createCursor();
		Cursor<T> cursorOutput = output.createCursor();
		
		// iterate over the input cursor
		while ( cursorInput.hasNext() )
		{
			// move both forward
			cursorInput.fwd();
			cursorOutput.fwd();
			
			// set the value of this pixel of the output image, every Type supports T.set( T type )
			cursorOutput.getType().set( cursorInput.getType() );
		}
		
		// close the cursors
		cursorInput.close();
		cursorOutput.close();
		
		// rename the output image
		output.setName( "Copy of " + input.getName() );
		
		//. return the copy
		return output;
	}
	
	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();
		
		// run the example
		new Example2a();
	}
}
