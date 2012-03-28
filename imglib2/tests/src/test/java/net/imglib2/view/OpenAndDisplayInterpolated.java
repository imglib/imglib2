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
