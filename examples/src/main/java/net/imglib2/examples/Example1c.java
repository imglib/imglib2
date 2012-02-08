package net.imglib2.examples;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
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
		// create the ImgFactory based on cells (cellsize = 5x5x5...x5) that will instantiate the Img
		final ImgFactory< FloatType > imgFactory = new CellImgFactory< FloatType >( 5 );

		// create an 3d-Img with dimensions 20x30x40 (here cellsize is 5x5x5)Ø
		final Img< FloatType > image1 = imgFactory.create( new long[] { 20, 30, 40 }, new FloatType() );

		// display both (but they are empty)
		ImageJFunctions.show( image1 ).show();
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1c();
	}
}
