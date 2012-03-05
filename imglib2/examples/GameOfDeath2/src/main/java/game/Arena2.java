/**
 * Copyright (c) 2011, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package game;

import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.util.Random;

import net.imglib2.RandomAccess;
import net.imglib2.algorithm.gauss.GaussGeneral;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;

public class Arena2
{
	final boolean exportAsStack = true;
	final int numFramesMovie = 1000;
	
	final static int raceA = 0;
	final static int raceB = 1;
	final static int raceC = 2;
	
	final int maxRace = 3;
	
	final int width = 384;
	final int height = 256;
	
	public Arena2( )
	{
		ImageStack stack = null;
		
		if ( exportAsStack )
			stack = new ImageStack( width, height );
		
		final ArrayImgFactory<LifeForm> factory = new ArrayImgFactory<LifeForm>();
		Img<LifeForm> arena = factory.create( new long[] { width, height }, new LifeForm() );
	
		final RandomAccess<LifeForm> cursor = arena.randomAccess();
		
		Random rnd = new Random( System.currentTimeMillis() );
		
		for ( int i = 0; i < 100000; ++i )
		{
			final int x = Math.round( rnd.nextFloat() * (arena.dimension( 0 ) - 1) ); 
			final int y = Math.round( rnd.nextFloat() * (arena.dimension( 1 ) - 1) );
			
			cursor.setPosition( new int[]{ x, y } );
			
			if ( i % 3 == 0 )
				cursor.get().set( raceA, 1 );
			else if ( i % 3 == 1 )
				cursor.get().set( raceB, 1 );
			else
				cursor.get().set( raceC, 1 );
		}
		
		final LifeFormARGBConverter display = new LifeFormARGBConverter( 0, 1 );
		final ImagePlus imp = ImageJFunctions.wrapRGB( arena, display, "Arena2" ); //ImageJFunctions.copyToImagePlus( arena, ImageJFunctions.COLOR_RGB );
		imp.show();
		
		if ( exportAsStack )
		{
			ImageProcessor ip = (ImageProcessor)imp.getProcessor().clone();
			stack.addSlice( "", ip );
		}
		
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
			final GaussGeneral<LifeForm> gauss = new GaussGeneral<LifeForm>( new double[]{ 1.5, 1.5 }, arena, new OutOfBoundsMirrorFactory<LifeForm,Img<LifeForm>>( Boundary.SINGLE ) );
			gauss.call();
			arena = (Img<LifeForm>) gauss.getResult();
			
			++numFrames;
			final long time = System.currentTimeMillis() - start;
			double fps = numFrames*1000 / (double)time;
			
			if ( numFrames % 25 == 0 )
				System.out.println( "fps: " + fps );
			
			//arena.getDisplay().setMinMax();
			display.setMin( 0 );
			display.setMax( getMax( arena ) );

			updateDisplay( imp, arena, display );
			
			if ( exportAsStack )
			{
				if ( numFrames <= numFramesMovie )
				{
					ImageProcessor ip = (ImageProcessor)imp.getProcessor().clone();
					stack.addSlice( "", ip );
				}
				
				if ( numFrames == numFramesMovie )
					new ImagePlus( "movie", stack ).show();
			}
		}
	}
	
	public float getMax( final Img< LifeForm > img )
	{
		final LifeForm max = img.firstElement();
		
		for ( final LifeForm l : img )
			if ( l.compareTo( max ) > 0 )
				max.set( l );
		
		return max.getWeight();
	}
	
	protected void updateDisplay( final ImagePlus imp, final Img<LifeForm> img, final LifeFormARGBConverter display )
	{
		final ImagePlus impNew = ImageJFunctions.wrapRGB( img, display, "Arena" ); //ImageJFunctions.copyToImagePlus( arena, ImageJFunctions.COLOR_RGB );
		
		//final int[] pixels = ImageJVirtualStack.extractSliceRGB( img, img.getDisplay(), 0, 1, new int[ img.getNumDimensions() ] );
		final int[] pixels = (int[])impNew.getProcessor().getPixels();
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
		new Arena2();		
	}
}
