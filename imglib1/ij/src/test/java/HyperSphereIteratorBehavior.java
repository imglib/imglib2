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

import ij.ImageJ;

import java.util.Random;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.HyperSphereIterator;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class HyperSphereIteratorBehavior
{
	private HyperSphereIteratorBehavior(){}
	
	final static public void main( final String[] args )
	{
		final ImageFactory<FloatType> factory = new ImageFactory<FloatType>( new FloatType(), new ArrayContainerFactory() );
		
		final int dim = 3;
		final int imgSize = 400;
		final int numSpheres = 100000;
		final int maxRadius = 10;

		final int[] size = new int[ dim ];
		for ( int d = 0; d < dim; ++d )
			size[ d ] = imgSize;
		
		final Image<FloatType> img1 = factory.createImage( size );
		
		final LocalizableByDimCursor<FloatType> c = img1.createLocalizableByDimCursor();
				
		final Random rnd = new Random( 350345 );		
		final long t = System.currentTimeMillis();
				
		for ( int d = 0; d < img1.getNumDimensions(); ++d )
			c.setPosition( img1.getDimension( d ) / 2, d );
			
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
