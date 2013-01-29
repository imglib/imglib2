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

import net.imglib2.FinalInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccessible;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.collection.KDTree;
import net.imglib2.collection.RealPointSampleList;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.neighborsearch.NearestNeighborInterpolatorFactory;
import net.imglib2.neighborsearch.NearestNeighborSearch;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Working with sparse data
 *
 * Create some random points with random values, display them and perform
 * a Gaussian convolution on the VIRTUAL data
 */
public class Example8a
{
	public Example8a()
	{
		// the interval in which to create random points
		FinalInterval interval = new FinalInterval( new long[] { 375, 200 } );

		// create an IterableRealInterval
		IterableRealInterval< FloatType > realInterval = createRandomPoints( interval, 250 );

		// using nearest neighbor search we will be able to return a value an any position in space
		NearestNeighborSearch< FloatType > search =
			new NearestNeighborSearchOnKDTree<FloatType>(
				new KDTree< FloatType> ( realInterval ) );

		// make it into RealRandomAccessible using nearest neighbor search
		RealRandomAccessible< FloatType > realRandomAccessible =
			Views.interpolate( search, new NearestNeighborInterpolatorFactory< FloatType >() );

		// convert it into a RandomAccessible which can be displayed
		RandomAccessible< FloatType > randomAccessible = Views.raster( realRandomAccessible );

		// set the initial interval as area to view
		RandomAccessibleInterval< FloatType > view = Views.interval( randomAccessible, interval );

		// display the view
		ImageJFunctions.show( view );

		// compute a gauss on it
		Img< FloatType > convolved =
			new ArrayImgFactory< FloatType >().create( interval, new FloatType() );

		Gauss.inFloat( new double[] { 3, 3 }, view, interval, convolved,
			new Point( view.numDimensions() ), convolved.factory() );

		// display the view
		ImageJFunctions.show( convolved );
	}

	/**
	 * Create a number of n-dimensional random points in a certain interval
	 * having a random intensity 0...1
	 *
	 * @param interval - the interval in which points are created
	 * @param numPoints - the amount of points
	 *
	 * @return a RealPointSampleList (which is an IterableRealInterval)
	 */
	public static RealPointSampleList< FloatType > createRandomPoints(
		RealInterval interval, int numPoints )
	{
		// the number of dimensions
		int numDimensions = interval.numDimensions();

		// a random number generator
		Random rnd = new Random( System.currentTimeMillis() );

		// a list of Samples with coordinates
		RealPointSampleList< FloatType > elements =
			new RealPointSampleList<FloatType>( numDimensions );

		for ( int i = 0; i < numPoints; ++i )
		{
			RealPoint point = new RealPoint( numDimensions );

			for ( int d = 0; d < numDimensions; ++d )
				point.setPosition( rnd.nextDouble() *
					( interval.realMax( d ) - interval.realMin( d ) ) + interval.realMin( d ), d );

			// add a new element with a random intensity in the range 0...1
			elements.add( point, new FloatType( rnd.nextFloat() ) );
		}

		return elements;
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example8a();
	}
}
