package net.imglib2.examples;

import ij.ImageJ;

import java.awt.Polygon;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.RoundRectangle2D;

import mpicbg.imglib.container.shapelist.ShapeList;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/**
 * Create a ShapeList container that computes data on the fly by just storing the shapes.
 * This also an example for a 3-dimensional "algorithm"
 *
 * @author Stephan Saalfeld
 *
 */
public class Example10
{
	public Example10()
	{
		final int depth = 200;

		// Create ShapeList
		final ShapeList< RGBALegacyType > shapeList = new ShapeList< RGBALegacyType >( new int[]{ 500, 500, depth },  new RGBALegacyType() );
		final Image< RGBALegacyType > shapeListImage = new Image< RGBALegacyType >( shapeList, shapeList.getBackground(), "ShapeListContainer" );

		// add some shapes
		for ( int i = 0; i < depth; ++i )
		{
			shapeList.addShape( new RoundRectangle2D.Double( 10 + i, 20, 40, 70 + 2 * i, 25, 25 ), new RGBALegacyType( RGBALegacyType.rgba( 255, 0, 0, 0 ) ), new int[]{ i } );
			shapeList.addShape( new Polygon( new int[]{ 90 + i, 180 - 2 * i, 190 - 4 * i, 120 - 2 * i }, new int[]{ 90, 80 + i, 140 - 3 * i, 130 - 2 * i }, 4 ), new RGBALegacyType( RGBALegacyType.rgba( 0, 255, 0, 0 ) ), new int[]{ i } );
			shapeList.addShape( new CubicCurve2D.Double( 10 + i, 200- i, 30 - 2*i, 30-0.5*i, i, 80, 390-i, 50+i ), new RGBALegacyType( RGBALegacyType.rgba( 255, 255, 0, 0 ) ), new int[]{ i } );
		}

		// display the image
		shapeListImage.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( shapeListImage ).show();
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example10();
	}
}
