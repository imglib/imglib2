package mpicbg.imglib.test;

import ij.ImagePlus;

import java.awt.Polygon;
import java.awt.Rectangle;

import mpicbg.imglib.container.shapelist.ByteShapeList;
import mpicbg.imglib.container.shapelist.ShapeListContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.ByteType;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @version 0.1a
 */
public class TestShapeList
{
	/**
	 * @param args
	 */
	public static < T extends RealType< T > > void main( String[] args )
	{
		final ShapeListContainerFactory shapeListFactory = new ShapeListContainerFactory();
		final ImageFactory< ByteType > imageFactory = new ImageFactory< ByteType >( new ByteType( ( byte )0 ), shapeListFactory );
		
		final Image< ByteType > image = imageFactory.createImage( new int[]{ 200, 200, 10 } );
		
		/* Pfui! */
		final ByteShapeList< ? > shapeList = ( ByteShapeList< ? > )image.getContainer();
		
		/* add some shapes */
		for ( int i = 0; i < 10; ++i )
		{
			shapeList.addShape( new Rectangle( 10 + i, 20, 40, 70 + 2 * i ), new ByteType( ( byte )64 ), new int[]{ i } );
			shapeList.addShape( new Polygon( new int[]{ 30 + i, 120 - 2 * i, 130 - 4 * i, 60 - 2 * i }, new int[]{ 30, 20 + i, 80 - 3 * i, 70 - 2 * i }, 4 ), new ByteType( ( byte )127 ), new int[]{ i } );
		}
		
		final ImagePlus imp = ImageJFunctions.displayAsVirtualStack( image );
		imp.show();
		imp.getProcessor().setMinAndMax( 0, 255 );
		
		try
		{
			Thread.sleep( 1000 );
		}
		catch ( final InterruptedException e ){}
	}
}
