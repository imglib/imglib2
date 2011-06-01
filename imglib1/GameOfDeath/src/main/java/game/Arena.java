package game;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.util.Random;

import mpicbg.imglib.algorithm.gauss.GaussianConvolution;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.image.display.imagej.ImageJVirtualStack;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyPeriodicFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;

public class Arena
{
	final static int raceA = 0;
	final static int raceB = 1;
	final static int raceC = 2;
	
	final int maxRace = 3;
	
	public Arena( )
	{		
		final ImageFactory<LifeForm> factory = new ImageFactory<LifeForm>( new LifeForm(), new ArrayContainerFactory() );
		Image<LifeForm> arena = factory.createImage( new int[] { 384, 256 } );
		
	
		final LocalizableByDimCursor<LifeForm> cursor = arena.createLocalizableByDimCursor();
		
		Random rnd = new Random( System.currentTimeMillis() );
		
		for ( int i = 0; i < 100000; ++i )
		{
			final int x = Math.round( rnd.nextFloat() * (arena.getDimension( 0 ) - 1) ); 
			final int y = Math.round( rnd.nextFloat() * (arena.getDimension( 1 ) - 1) );
			
			cursor.setPosition( new int[]{ x, y } );
			
			if ( i % 3 == 0 )
				cursor.getType().set( raceA, 1 );
			else if ( i % 3 == 1 )
				cursor.getType().set( raceB, 1 );
			else
				cursor.getType().set( raceC, 1 );
		}
		
		final ImagePlus imp = ImageJFunctions.copyToImagePlus( arena, ImageJFunctions.COLOR_RGB );
		imp.show();
	
		final float growth = 1.05f;
		
		while ( true )
		{			
			/* growth */
			for ( final LifeForm t : arena )
			{
				t.mul( growth );
		
				if ( t.getWeight() > 1.1f )
					t.setWeight( 0 );
			}
			
			/* diffusion */
			final GaussianConvolution<LifeForm> gauss = new GaussianConvolution<LifeForm>( arena, new OutOfBoundsStrategyMirrorFactory<LifeForm>(), 1.5f );
			
			if ( !gauss.checkInput() || ! gauss.process() )
			{
				System.out.println( gauss.getErrorMessage() );
				System.exit( 0 );
			}
			
			arena = gauss.getResult();
						
			//arena.getDisplay().setMinMax();
			arena.getDisplay().setMinMax( 0, arena.getDisplay().getMax() );
			
			updateDisplay( imp, arena );
		}
	}
	
	protected void updateDisplay( final ImagePlus imp, final Image<LifeForm> img )
	{
		final int[] pixels = ImageJVirtualStack.extractSliceRGB( img, img.getDisplay(), 0, 1, new int[ img.getNumDimensions() ] );		
		ColorProcessor cp = ((ColorProcessor)imp.getProcessor());
		cp.setPixels(pixels);
		imp.updateAndDraw();
	}
	
	public static void main( String[] args )
	{
		/* initImageJWindow() */
		//final String params[] = {""};
		//ij.ImageJ.main( );
		new ImageJ();
		
		/* Start the fight */
		new Arena();		
	}
}
