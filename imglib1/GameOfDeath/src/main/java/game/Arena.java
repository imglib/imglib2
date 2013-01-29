/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

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

/**
 * TODO
 *
 */
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
		
		long numFrames = 0;
		final long start = System.currentTimeMillis();

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

			++numFrames;
			final long time = System.currentTimeMillis() - start;
			double fps = numFrames*1000 / (double)time;
			
			if ( numFrames % 25 == 0 )
				System.out.println( "fps: " + fps );

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
