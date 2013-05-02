import ij.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Create a new ImgLib2 Img of Type FloatType
 */
public class Example1c
{
	public Example1c()
	{
		// create the ImgFactory based on cells (cellsize = 5x5x5...x5) that will
		// instantiate the Img
		final ImgFactory< FloatType > imgFactory = new CellImgFactory< FloatType >( 5 );

		// create an 3d-Img with dimensions 20x30x40 (here cellsize is 5x5x5)Ø
		final Img< FloatType > img1 = imgFactory.create( new long[]{ 20, 30, 40 }, new FloatType() );

		// create another image with the same size
		// note that the input provides the size for the new image as it implements
		// the Interval interface
		final Img< FloatType > img2 = imgFactory.create( img1, img1.firstElement() );

		// display both (but they are empty)
		ImageJFunctions.show( img1 );
		ImageJFunctions.show( img2 );
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1c();
	}
}
