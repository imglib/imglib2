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
