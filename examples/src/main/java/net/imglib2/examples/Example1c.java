package net.imglib2.examples;

import mpicbg.imglib.container.cell.CellContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.real.FloatType;
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
		final Image<FloatType> image1 = imageFactory.createImage( new int[] { 20, 30, 40 } );

		// create another Image with exactly the same properties
		final Image<FloatType> image2 = image1.createNewImage();

		// display both (but they are empty)
		ImageJFunctions.displayAsVirtualStack( image1 ).show();
		ImageJFunctions.displayAsVirtualStack( image2 ).show();
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1c();
	}
}
