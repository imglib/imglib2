/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package game;

import ij.ImageJ;
import ij.ImagePlus;

import java.text.NumberFormat;
import java.util.Random;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;

/**
 * In order to show the power of generality ImgLib2 offers, we develop a program that
 * simulates the growth of life forms (i.e. bacteria) in a certain area, called arena.
 * 
 * The simulation seeds the ground with several races of life forms. Each life form has
 * a certain name (race) and a weight representing its population at a spot. They grow
 * every round by a certain percentage (e.g. 10%) and die of hunger if the population becomes
 * too large. In every round they spread to their local neighborhood, trying to invade new
 * space. If the same race is present in their vicinity, their population simply adds up. If 
 * another race is present they will fight for the spot. The race with higher population 
 * will survive, however they will loose as much population as the attacking (loosing)
 * race lost.
 * 
 * This complex behaviour can be simulated by gaussian convolution on a new NumericType
 * called LifeForm. Both operations, growth and fight can be simulated by implementing 
 * specialized mul() and add() methods, respectively (see LifeForm.java). 
 * Multiplying a LifeForm with a certain value represent growth (or shrinkage) while
 * addition of two LifeForms represents the fight for a certain spot.
 * 
 * The simulation always converges to a point where one race wins at the end. Therefore, we
 * added the possibility of a epidemic which kills 90% of the dominating race, keeping the
 * entire system in a equilibrium.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class Arena
{
	// a central random number generator
	final static Random rnd = new Random( System.currentTimeMillis() );
	
	// number of seeds for LifeForms
	final int numSeeds = 100000;
	
	// we simulate with 5 races
	final int numRaces = 5;

	// the overall growth of all races per round
	final float growth = 1.05f;

	// all races above this weight will die of lack of food
	final float maxWeight = 1.1f;
	
	// chance for a epedemic (in percent)
	final float epidemic = 0.1f;
	
	// the width and height of the image
	final int width = 384;
	final int height = 384;
	
	// the out of bounds strategy to use for gaussian convolution
	// makes a significant difference to the result
	final OutOfBoundsFactory< LifeForm, RandomAccessibleInterval< LifeForm > > outofbounds = new OutOfBoundsPeriodicFactory< LifeForm, RandomAccessibleInterval< LifeForm > >();
	
	public Arena( )
	{
		// create a new ArrayImgFactory for LifeForm
		final ArrayImgFactory< LifeForm > factory = new ArrayImgFactory< LifeForm >();
		
		// create the ArrayImg containing the simulation
		Img<LifeForm> arena = factory.create( new long[] { width, height }, new LifeForm() );

		// seed the arena with a number of random life forms
		seedArena( arena, numSeeds, numRaces );
		
		// init the display
		final LifeFormARGBConverter display = new LifeFormARGBConverter();
		
		// show the initial image (will be updated in each step)
		final ImagePlus imp = ImageJFunctions.wrapRGB( arena, display, "Arena" );
		imp.show();

		// for computing the frames per second
		final long start = System.currentTimeMillis();
		long numFrames = 0;
		
		// repeat until cancelled
		while ( true )
		{			
			// growth of each life form every round
			for ( final LifeForm t : arena )
			{
				t.mul( growth );
		
				// if they grow too much they will die because of lack of food
				if ( t.getWeight() > maxWeight )
					t.setWeight( 0.001f );
			}
			
			// simulate diffusion by gaussian convolution
			Gauss.inNumericTypeInPlace( new double[]{ 1.5, 1.5 }, arena, outofbounds );
			
			// compute and display frames per second
			final double fps = ++numFrames*1000 / (double)( System.currentTimeMillis() - start );
			imp.setTitle( "fps: " +  NumberFormat.getInstance().format( fps ) + " frame: " + numFrames );
			
			// we regularly have an epidemic
			epidemic( arena, epidemic, numRaces );
			
			// update the LifeFormARGBConverter to the current min and max value of the weight
			display.setMin( 0 );
			display.setMax( getMax( arena ) );

			// update the ImageJ display to the current state of the simulation
			updateDisplay( imp, arena, display );
		}
	}

	/**
	 * Given a certain chance there is an epidemic killing 90% of the dominant race
	 * 
	 * @param arena - the simulation
	 * @param chance - the chance of having an epidemic
	 * @param numRaces - the number of races
	 */
	protected void epidemic( final Img< LifeForm > arena, final float chance, final int numRaces )
	{
		// is there an epedemic?
		if ( rnd.nextFloat() * 100 < chance )
		{
			// which race does it hit?
			final int race = dominantLifeForm( arena, numRaces );
			
			for ( final LifeForm l : arena )
				if ( l.getName() == race )
					l.mul( 0.1f );
		}
	}

	/**
	 * Returns which LifeForm is currently dominating
	 * 
	 * @param arena - the simulation
	 * @return - index of the dominant LifeForm 
	 */
	protected int dominantLifeForm( final Img< LifeForm > arena, final int numRaces )
	{
		final double[] countRaces = new double[ numRaces ];
		
		for ( final LifeForm l : arena )
			countRaces[ l.getName() ] += l.getWeight();
		
		double last = countRaces[ 0 ];
		int race = 0;
		
		for ( int i = 1; i < numRaces; ++i )
			if ( countRaces[ i ] > last )
			{
				last = countRaces[ i ];
				race = i;
			}
		
		return race;
	}
	
	/**
	 * Seed the arena with a number of random life forms
	 * 
	 * @param arena - the Img containing the Life forms
	 * @param numSeeds - the number of seeds
	 * @param numRaces - the number of races to use
	 */
	protected void seedArena( final Img<LifeForm> arena, final int numSeeds, final int numRaces )
	{
		final int numDimensions = arena.numDimensions();
		final RandomAccess<LifeForm> randomAccess = arena.randomAccess();
		
		for ( int i = 0; i < numSeeds; ++i )
		{
			for ( int d = 0; d < numDimensions; ++d )
				randomAccess.setPosition( Math.round( rnd.nextFloat() * ( arena.dimension( d ) - 1 ) ), d );
			
			randomAccess.get().set( i % numRaces, 1 );
		}		
	}
	
	/**
	 * Compute the maximum weight of all pixels
	 * 
	 * @param img - the Img containing the state of the simulation
	 * @return the maximum weight
	 */
	protected float getMax( final Img< LifeForm > img )
	{
		final LifeForm max = img.firstElement();
		
		for ( final LifeForm l : img )
			if ( l.compareTo( max ) > 0 )
				max.set( l );
		
		return max.getWeight();
	}
	
	/**
	 * Update the ImageJ display window with the current state of the simulation
	 * 
	 * @param imp - the displayed ImagePlus
	 * @param img - the Img containing the state of the simulation
	 * @param display - the LifeFormARGB converter that can convert a LifeForm into an ARGB representation 
	 */
	protected void updateDisplay( final ImagePlus imp, final Img<LifeForm> img, final LifeFormARGBConverter display )
	{
		// create a new ImagePlus based on the current state of the simulation
		final ImagePlus impNew = ImageJFunctions.wrapRGB( img, display, "Arena" );
		
		// set the pixels of the new ImagePlus to the already displayed ImagePlus
		imp.getProcessor().setPixels( impNew.getProcessor().getPixels() );
		
		// update the already displayed ImagePlus
		imp.updateAndDraw();
	}
	
	public static void main( String[] args )
	{
		// init ImageJ Window
		new ImageJ();
		
		// Start the fight
		new Arena();		
	}
}
