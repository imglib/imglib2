import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Here we want to copy an ArrayImg into a CellImg using a generic method,
 * but we cannot do it with simple Cursors as they have a different iteration order.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example2b
{

	public Example2b() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > img = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// copy the image into a CellImg with a cellsize of 20x20
		Img< FloatType > duplicate = copyImage( img, new CellImgFactory< FloatType >( 20 ) );

		// display the copy and the original
		ImageJFunctions.show( img );
		ImageJFunctions.show( duplicate );
	}

  public < T extends Type< T >> Img< T > copyImage( final Img< T > input,
    final ImgFactory< T > imgFactory )
  {
    // create a new Image with the same dimensions but the other imgFactory
    // note that the input provides the size for the new image by implementing the Interval interface
    Img< T > output = imgFactory.create( input, input.firstElement() );

    // create a cursor that automatically localizes itself on every move
    Cursor< T > cursorInput = input.localizingCursor();
    RandomAccess< T > randomAccess = output.randomAccess();

    // iterate over the input cursor
    while ( cursorInput.hasNext())
    {
      // move input cursor forward
      cursorInput.fwd();

      // set the output cursor to the position of the input cursor
      randomAccess.setPosition( cursorInput );

      // set the value of this pixel of the output image, every Type supports T.set( T type )
      randomAccess.get().set( cursorInput.get() );
    }

    // return the copy
    return output;
  }

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example2b();
	}
}
