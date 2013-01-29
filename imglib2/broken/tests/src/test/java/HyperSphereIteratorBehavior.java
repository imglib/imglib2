/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

import ij.ImageJ;

import java.util.Random;

import net.imglib2.RandomAccess;
import net.imglib2.cursor.LocalizableByDimCursor;
import net.imglib2.cursor.special.HyperSphereIterator;
import net.imglib2.img.Image;
import net.imglib2.img.ImageFactory;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.outofbounds.OutOfBoundsStrategyValueFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * Copyright (c) 2010, Stephan Preibisch & Stephan Saalfeld
 */
public class HyperSphereIteratorBehavior
{
	private HyperSphereIteratorBehavior(){}
	
	final static public void main( final String[] args )
	{
		final ArrayImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>();
		
		final int dim = 3;
		final int imgSize = 400;
		final int numSpheres = 100000;
		final int maxRadius = 10;

		final long[] size = new int[ dim ];
		for ( int d = 0; d < dim; ++d )
			size[ d ] = imgSize;
		
		final Img<FloatType> img1 = factory.create( size, new FloatType() );
		
		final RandomAccess<FloatType> c = img1.randomAccess();
				
		final Random rnd = new Random( 350345 );		
		final long t = System.currentTimeMillis();
				
		for ( int d = 0; d < img1.numDimensions(); ++d )
			c.setPosition( img1.dimension( d ) / 2, d );
			
		final HyperSphereIterator<FloatType> referenceSphere = new HyperSphereIterator<FloatType>( img1, c, (imgSize)/2 - maxRadius, new OutOfBoundsStrategyValueFactory<FloatType>() );
		
		int numPixels = 0;
		
		while ( referenceSphere.hasNext() )
		{
			referenceSphere.fwd();
			++numPixels;
		}
		
		final float probability = numSpheres / (float)numPixels;

		referenceSphere.reset();
		
		while ( referenceSphere.hasNext() )
		{
			referenceSphere.fwd();
			float p = rnd.nextFloat();

			if ( p < probability )
			{
				final int radius = Util.round( rnd.nextFloat() * maxRadius );
				final float intensity = rnd.nextFloat();
				
				final HyperSphereIterator<FloatType> sphere = new HyperSphereIterator<FloatType>( img1, referenceSphere, radius, new OutOfBoundsStrategyValueFactory<FloatType>() );
				//final SphereCursor<FloatType> sphere = new SphereCursor<FloatType>( img1, referenceSphere, radius );
				
				while ( sphere.hasNext() )
				{
					sphere.fwd();
					sphere.getType().set( intensity );
				}
				
				sphere.close();
			}			
		}
		
		referenceSphere.close();
		
		System.out.println( "Computation time: " + (System.currentTimeMillis() - t) + " ms." );
		
		new ImageJ();		
		img1.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( img1 ).show();		
	}
}
