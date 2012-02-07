package mpicbg.imglib.workshop.prewritten;

import java.io.File;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.special.HyperSphereIterator;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursorFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;
import ij.ImageJ;

/**
 * Here we use special cursors to find the local maxima and display them with spheres in another image
 * 
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example4
{
	public Example4()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with LOCI using an ArrayContainer
		Image<FloatType> image = LOCI.openLOCIFloatType( file.getAbsolutePath(), new ArrayContainerFactory() );

		// find local maxima and paint them into another image as spheres
		Image<BitType> display = findAndDisplayLocalMaxima( image, new BitType() );
		
		// display output and input
		image.getDisplay().setMinMax();
		display.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( image ).show();
		ImageJFunctions.copyToImagePlus( display ).show();
	}
	
	public static <T extends Comparable<T> & Type<T>, U extends RealType<U>> Image<U> findAndDisplayLocalMaxima( final Image<T> image, final U outputType )
	{
		// Create a new image of the provided RealType U
		ImageFactory<U> imageFactory = new ImageFactory<U>( outputType, image.getContainerFactory() );
		Image<U> output = imageFactory.createImage( image.getDimensions() );
		
		// create a Cursor that runs over the image and checks in a 3^n neighborhood if it is a maxima
		LocalizableCursor<T> cursor1 = image.createLocalizableCursor();
				
		// create a LocalizableByDimCursor that is used to check the local neighborhood of each pixel
		LocalizableByDimCursor<T> cursor2 = image.createLocalizableByDimCursor();
		
		// and a local neighborhood cursor on top of the localizablebydim
		LocalNeighborhoodCursor<T> nbCursor = LocalNeighborhoodCursorFactory.createLocalNeighborhoodCursor( cursor2 );
		
		// we need the number of dimensions a lot
		final int numDimensions = image.getNumDimensions();
		
		// we should have a temporary array to get the current position
		int[] tmp = new int[ image.getNumDimensions() ];
		
		// iterate over the image
A:		while ( cursor1.hasNext() )
		{
			cursor1.fwd();
			
			// get the current position
			cursor1.getPosition( tmp );
			
			// check if there is at least a distance of 1 to the border
			for ( int d = 0; d < numDimensions; ++d )
				if ( tmp[ d ] < 1 || tmp[ d ] > image.getDimension( d ) - 2 )
					continue A;
	
			// move the cursor to the current position
			cursor2.setPosition( cursor1 );
			
			// update the local neighborhood cursor
			nbCursor.update();
			
			// what is the value that we investigate
			final T centerValue = cursor2.getType().copy();
			
			boolean isMaximum = true;
			
			// check if all pixels are smaller
			while ( nbCursor.hasNext() && isMaximum )
			{
				nbCursor.fwd();
				
				// test if the center is smaller than the current pixel value
				if ( centerValue.compareTo( nbCursor.getType() ) <= 0 )
					isMaximum = false;
			}
			
			if ( isMaximum )
			{
				// draw a sphere of radius one in the new image
				HyperSphereIterator<U> sphere = new HyperSphereIterator<U>( output, cursor1, 1 );
				
				for ( U value : sphere )
					value.setOne();
				
				sphere.close();
			}			
		}
		
		return output;
	}
	
	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();
		
		// run the example
		new Example4();
	}
}
