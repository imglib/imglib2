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

package net.imglib2.algorithm.gradient;

import net.imglib2.Interval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class GradientExample
{
	public static < T extends RealType< T > & NativeType< T > > void doit( final T type ) throws ImgIOException
	{
		final ImgPlus< T > input = new ImgOpener().openImg( "/home/tobias/workspace/data/img1.tif", new ArrayImgFactory< T >(), type );
		ImageJFunctions.show( input );

		final int n = input.numDimensions();
		final long[] dim = new long[ n + 1 ];
		for ( int d = 0; d < n; ++d )
			dim[ d ] = input.dimension( d );
		dim[ n ] = n;
		final Img< T > gradients = new ArrayImgFactory< T >().create( dim, type );

		// bounding box for computation of gradients
		// we require a border of 1 pixel wrt. to the input image
		final Interval gradientComputationInterval = Intervals.expand( input, -1 );

		// compute partial derivatives of input in all dimension
		for ( int d = 0; d < n; ++d )
			PartialDerivative.gradientCentralDifference( input, Views.interval( Views.hyperSlice( gradients, n, d ), gradientComputationInterval ), d );

		ImageJFunctions.show( gradients );

//		final int numRuns = 20;
//		final boolean printIndividualTimes = true;
//		final ArrayList< Long > times = BenchmarkHelper.benchmark( numRuns, new Runnable() {
//			@Override
//			public void run()
//			{
//				for ( int i = 0; i < 10; ++i )
//					for ( int d = 0; d < n; ++d )
//						PartialDerivative.gradientCentralDifference( input, Views.interval( Views.hyperSlice( gradients, n, d ), gradientComputationInterval ), d );
//			}
//		} );
//		if ( printIndividualTimes )
//		{
//			for ( int i = 0; i < numRuns; ++i )
//				System.out.println( "run " + i + ": " + times.get( i ) + " ms" );
//			System.out.println();
//		}
//		System.out.println( "median: " + BenchmarkHelper.median( times ) + " ms" );
	}

	public static void main( final String[] args )
	{
		try
		{
			doit( new FloatType() );
		}
		catch ( final ImgIOException e )
		{
			e.printStackTrace();
		}
	}
}
