package net.imglib2.examples;

import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import ij.ImageJ;

/**
 * Create a new ImgLib {@link Image} of {@link Type} {@link FloatType}
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example1c
{
	public Example1c()
	{
		// create the Factory that will instantiate the Image
		final ImageFactory<FloatType> imageFactory = new ImageFactory<FloatType>( new FloatType(), new CellContainerFactory() );

		// create an Image
		final Img<FloatType> image1 = imageFactory.createImage( new int[] { 20, 30, 40 } );

		// create another Image with exactly the same properties
		final Img<FloatType> image2 = image1.createNewImage();

		// display both (but they are empty)
		ImageJFunctions.show( image1 ).show();
		ImageJFunctions.show( image2 ).show();
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1c();
	}
}
