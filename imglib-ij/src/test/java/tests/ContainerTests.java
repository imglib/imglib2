package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Random;

import mpicbg.imglib.algorithm.fft.FFTFunctions;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.cell.CellContainerFactory;
import mpicbg.imglib.container.imageplus.ImagePlusContainerFactory;
import mpicbg.imglib.container.planar.PlanarContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyPeriodicFactory;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.util.Util;

public class ContainerTests
{
	// which dimensions to test
	final int[][] dim = 
		new int[][]{ 
			{ 127 },
			{ 288 },	
			{ 135, 111 },
			{ 172, 131 },
			{  15,  13, 33 },		
			{ 110,  38, 30 },
			{ 109,  34, 111 },
			{  12,  43,  92, 10 },
			{  21,  34,  29, 13 },
			{   5,  12,  30,  4,  21 },
			{  14,  21,  13,  9,  12 }
		}; 
	
	/**
	 * Test only the ArrayContainers
	 */
	@Test public void testArrayContainer()
	{
		for ( int i = 0; i < dim.length; ++i )
		{
			assertTrue( "ArrayContainer failed for: dim=" + Util.printCoordinates( dim[ i ] ), 
			            testContainer( dim[ i ], new ArrayContainerFactory(), new ArrayContainerFactory() ) );
		}
	}

	/**
	 * Test CellContainer
	 */
	@Test public void testCellContainer()
	{
		for ( int i = 0; i < dim.length; ++i )
		{
			assertTrue( "ArrayContainer vs CellContainer failed for dim = " + Util.printCoordinates( dim[ i ] ),
			            testContainer( dim[ i ], new ArrayContainerFactory(), new CellContainerFactory( 10 ) ) );
			assertTrue( "CellContainer vs ArrayContainer failed for dim = " + Util.printCoordinates( dim[ i ] ), 
			            testContainer( dim[ i ], new CellContainerFactory(), new ArrayContainerFactory() ) );
			assertTrue( "CellContainer vs CellContainer failed for dim = " + Util.printCoordinates( dim[ i ] ),
			            testContainer( dim[ i ], new CellContainerFactory( 5 ), new CellContainerFactory() ) );
		}
	}

	/**
	 * Test ImagePlusContainer
	 */
	@Test public void testImagePlusContainer()
	{
		for ( int i = 0; i < dim.length; ++i )
		{
			if ( dim[ i ].length < 6 )
			{
				assertTrue( "ArrayContainer vs ImagePlusContainer failed for dim = " + Util.printCoordinates( dim[ i ] ),
				            testContainer( dim[ i ], new ArrayContainerFactory(), new ImagePlusContainerFactory() ) );
				assertTrue( "ImagePlusContainer vs ArrayContainer failed for dim = " + Util.printCoordinates( dim[ i ] ), 
				            testContainer( dim[ i ], new ImagePlusContainerFactory(), new ArrayContainerFactory() ) );
				assertTrue( "ImagePlusContainer vs ImagePlusContainer failed for dim = " + Util.printCoordinates( dim[ i ] ),
				            testContainer( dim[ i ], new ImagePlusContainerFactory(), new ImagePlusContainerFactory() ) );
			}
		}
	}
	
	/**
	 * Test PlanarContainer
	 */
	@Test public void testPlanarContainer()
	{
		for ( int i = 0; i < dim.length; ++i )
		{
			if ( dim[ i ].length < 6 )
			{
				assertTrue( "ArrayContainer vs PlanarContainer failed for dim = " + Util.printCoordinates( dim[ i ] ),
				            testContainer( dim[ i ], new ArrayContainerFactory(), new PlanarContainerFactory() ) );
				assertTrue( "PlanarContainer vs ArrayContainer failed for dim = " + Util.printCoordinates( dim[ i ] ), 
				            testContainer( dim[ i ], new PlanarContainerFactory(), new ArrayContainerFactory() ) );
				assertTrue( "PlanarContainer vs PlanarContainer failed for dim = " + Util.printCoordinates( dim[ i ] ),
				            testContainer( dim[ i ], new PlanarContainerFactory(), new PlanarContainerFactory() ) );
			}
		}
	}
	
	/**
	 * Test MultiThreading
	 */
	@Test public void testMultiThreading()
	{
		assertTrue( "ArrayContainer MultiThreading failed", testThreading( new ArrayContainerFactory() ) );
		assertTrue( "CellContainer MultiThreading failed", testThreading( new CellContainerFactory() ) );
		assertTrue( "ImagePlusContainer MultiThreading failed", testThreading( new ImagePlusContainerFactory() ) );	
	}
	
	protected boolean testThreading( final ContainerFactory factory )
	{
		// create the image factory
		final ImageFactory<FloatType> imageFactory = new ImageFactory<FloatType>( new FloatType(), factory );

		// create the image
		final Image<FloatType> img = imageFactory.createImage( new int[]{ 101, 99, 301 } );

		// get a reference to compare to
		final float[] reference = createReference( img );
		
		// swap quadrants forth and back
		FFTFunctions.rearrangeFFTQuadrants( img, 8 );
		FFTFunctions.rearrangeFFTQuadrants( img, 8 );
		
		boolean succesful = test( img, reference );
		
		return succesful;
	}
	
	protected float[] createReference( final Image<FloatType> img )
	{
		// use a random number generator
		final Random rnd = new Random( 1241234 );
		
		// create reference array
		final float[] reference = new float[ img.getNumPixels() ];
		
		// iterate over image and reference array and fill with data
		final Cursor<FloatType> cursor = img.createCursor();			
		int i = 0;
		
		while( cursor.hasNext() )
		{
			cursor.fwd();

			final float value = rnd.nextFloat();
			reference[ i++ ] = value;
			cursor.getType().set( value );
		}
		
		cursor.close();
		
		return reference;
	}
	
	protected boolean test( final Image<FloatType> img, final float[] reference )
	{
		boolean allEqual = true;
		
		final Cursor<FloatType> cursor = img.createCursor();
		int i = 0;
		
		while( cursor.hasNext() )
		{
			cursor.fwd();				
			allEqual &= cursor.getType().get() == reference[ i++ ];
		}		
		
		return allEqual;
	}
	
	protected boolean testContainer( final int[] dim, final ContainerFactory factory1, final ContainerFactory factory2 )
	{
		// create the image factory
		final ImageFactory<FloatType> imageFactory1 = new ImageFactory<FloatType>( new FloatType(), factory1 );
		final ImageFactory<FloatType> imageFactory2 = new ImageFactory<FloatType>( new FloatType(), factory2 );
		
		// create the image
		final Image<FloatType> img1 = imageFactory1.createImage( dim );
		final Image<FloatType> img2 = imageFactory2.createImage( dim );
	
		final int numDimensions = img1.getNumDimensions();

		// get a reference to compare to
		final float[] reference = createReference( img1 );
		
		// copy into a second image using simple cursors
		final Cursor<FloatType> cursor1 = img1.createCursor();
		final Cursor<FloatType> cursor2 = img2.createCursor();			
		
		while( cursor1.hasNext() )
		{
			cursor1.fwd();
			cursor2.fwd();
			
			cursor2.getType().set( cursor1.getType() );
		}
		
		cursor1.reset();
		cursor2.reset();
		
		// and copy right back
		while( cursor2.hasNext() )
		{
			cursor1.fwd();
			cursor2.fwd();
			
			cursor1.getType().set( cursor2.getType() );
		}		
		
		cursor1.close();
		cursor2.close();

		// copy back into a second image using localizable and positionable cursors			
		final LocalizableCursor<FloatType> localizableCursor1 = img1.createLocalizableCursor();			
		final LocalizableByDimCursor<FloatType> localizableByDimCursor2 = img2.createLocalizableByDimCursor();			
		
		int i = 0;
		
		while ( localizableCursor1.hasNext() )
		{
			localizableCursor1.fwd();
			++i;
			
			if ( i % 2 == 0 )
				localizableByDimCursor2.moveTo( localizableCursor1 );
			else
				localizableByDimCursor2.setPosition( localizableCursor1 );
			
			localizableByDimCursor2.getType().set( localizableCursor1.getType() );
		}
		
		localizableByDimCursor2.close();
		
		// copy again to the first image using a LocalizableByDimOutsideCursor and a LocalizableByDimCursor
		final LocalizableByDimCursor<FloatType> outsideCursor2 = img2.createLocalizableByDimCursor( new OutOfBoundsStrategyPeriodicFactory<FloatType>() );
		localizableCursor1.reset();
		
		final int[] pos = new int[ numDimensions ];			
		i = 0;
		int direction = 1;
		
		while ( localizableCursor1.hasNext() )
		{
			localizableCursor1.fwd();
			localizableCursor1.getPosition( pos );
			++i;
			
			// how many times far away from the original image do we grab the pixel
			final int distance = i % 5;
			direction *= -1;				
			
			pos[ i % numDimensions ] += img1.getDimension( i % numDimensions ) * distance * direction;

			if ( i % 7 == 0 )
				outsideCursor2.setPosition( pos );
			else
				outsideCursor2.moveTo( pos );
	
			localizableCursor1.getType().set( outsideCursor2.getType() );
		}

		final boolean success = test( img1, reference );
		
		img1.close();
		img2.close();
		
		return success;
	}
}
