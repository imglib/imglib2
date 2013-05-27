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

package net.imglib2.view;

import ij.ImageJ;
import ij.ImagePlus;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class OpenAndDisplayInterpolated
{
	public static <T extends NumericType< T > > void copyInterpolatedGeneric( RandomAccessible< T > from, IterableInterval< T > to, double[] offset, double scale, InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory )
	{
		final int n = to.numDimensions();
		final double[] fromPosition = new double[ n ];
		Cursor< T > cursor = to.localizingCursor();
		RealRandomAccess< T > interpolator =  interpolatorFactory.create( from );
		while ( cursor.hasNext() )
		{
			final T t = cursor.next();
			for ( int d = 0; d < n; ++d )
			{
				fromPosition[ d ] = scale * cursor.getDoublePosition( d ) + offset[ d ];
			}
			interpolator.setPosition( fromPosition );
			t.set( interpolator.get() );
		}
	}

	final static public void main( final String[] args )
	{
		new ImageJ();
		
		ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();
		Img< FloatType > img = null;
		try
		{
			final ImgOpener io = new ImgOpener();
			img = io.openImg( "/home/tobias/workspace/data/DrosophilaWing.tif", imgFactory, new FloatType() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}

		Img< FloatType > interpolatedImg = imgFactory.create( new long[] {200, 200}, new FloatType () );
				
		double[] offset;
		double scale;
		InterpolatorFactory< FloatType, RandomAccessible< FloatType > > interpolatorFactory;

		offset = new double[] {50, 10};
		scale = 1.0;
		interpolatorFactory = new NLinearInterpolatorFactory< FloatType >();
		final ImagePlus imp = ImageJFunctions.show( interpolatedImg );
		imp.getImageStack().getProcessor( 0 ).setMinAndMax( 0, 255 );
		for ( int i=0; i<2000; ++i ) {
			copyInterpolatedGeneric( img, interpolatedImg, offset, scale, interpolatorFactory );
			imp.getImageStack().getProcessor( 0 ); // update the internal img data in the underlying ImageJVirtualStack
			imp.updateAndDraw();
			offset[0] += 0.2;
			offset[0] += 0.04;
			scale *= 0.999;
		}

		offset = new double[] {50, 10};
		scale = 1.0;
		interpolatorFactory = new NearestNeighborInterpolatorFactory< FloatType >();
		for ( int i=0; i<2000; ++i ) {
			copyInterpolatedGeneric( img, interpolatedImg, offset, scale, interpolatorFactory );
			imp.getImageStack().getProcessor( 0 ); // update the internal img data in the underlying ImageJVirtualStack
			imp.updateAndDraw();
			offset[0] += 0.2;
			offset[0] += 0.04;
			scale *= 0.999;
		}
	}
}
