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
import ij.ImagePlus;

import java.util.Random;

import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.img.imageplus.ImagePlusImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;

/**
 * Draw a sphere full of little spheres
 */
public class Example4a
{
	public Example4a()
	{
		// open with ImgOpener using an ImagePlusImg
		ImagePlusImg< UnsignedByteType, ?> img
			= new ImagePlusImgFactory< UnsignedByteType >().create(
				new long[] { 256, 256, 256 }, new UnsignedByteType() );

		// draw a small sphere for every pixel of a larger sphere
		drawSpheres( img, 0, 255 );

		// display output and input
		try
		{
			ImagePlus imp = img.getImagePlus();
			imp.show();
		}
		catch ( ImgLibException e )
		{
			System.out.println( "This ImagePlusImg does not hold a native " +
				"ImagePlus as container, either because the dimensionality is too " +
				"high or because the type is not supported." );
			ImageJFunctions.show( img );
		}
	}

	/**
	 * Draws a sphere that contains lots of small spheres into the center of the interval
	 *
	 * @param randomAccessible - the image data to write to
	 * @param minValue - the minimal intensity of one of the small spheres
	 * @param maxValue - the maximal intensity of one of the small spheres
	 */
	public < T extends RealType< T > > void drawSpheres(
		final RandomAccessibleInterval< T > randomAccessible,
		final double minValue, final double maxValue )
	{
		// the number of dimensions
		int numDimensions = randomAccessible.numDimensions();

		// define the center and radius
		Point center = new Point( randomAccessible.numDimensions() );
		long minSize = randomAccessible.dimension( 0 );

		for ( int d = 0; d < numDimensions; ++d )
		{
			long size = randomAccessible.dimension( d );

			center.setPosition( size / 2 , d );
			minSize = Math.min( minSize, size );
		}

		// define the maximal radius of the small spheres
		int maxRadius = 5;

		// compute the radius of the large sphere so that we do not draw
		// outside of the defined interval
		long radiusLargeSphere = minSize / 2 - maxRadius - 1;

		// instantiate a random number generator
		Random rnd = new Random( System.currentTimeMillis() );

		// define a hypersphere (n-dimensional sphere)
		HyperSphere< T > hyperSphere =
			new HyperSphere<T>( randomAccessible, center, radiusLargeSphere );

		// create a cursor on the hypersphere
		HyperSphereCursor< T > cursor = hyperSphere.cursor();

		while ( cursor.hasNext() )
		{
			cursor.fwd();

			// the random radius of the current small hypersphere
			int radius = rnd.nextInt( maxRadius ) + 1;

			// instantiate a small hypersphere at the location of the current pixel
			// in the large hypersphere
			HyperSphere< T > smallSphere =
				new HyperSphere< T >( randomAccessible, cursor, radius );

			// define the random intensity for this small sphere
			double randomValue = rnd.nextDouble();

			// take only every 4^dimension'th pixel by chance so that it is not too crowded
			if ( Math.round( randomValue * 100 ) % Util.pow( 4, numDimensions ) == 0 )
			{
				// scale to right range
				randomValue = rnd.nextDouble() * ( maxValue - minValue ) + minValue;

				// set the value to all pixels in the small sphere if the intensity is
				// brighter than the existing one
				for ( final T value : smallSphere )
					value.setReal( Math.max( randomValue, value.getRealDouble() ) );
			}
		}
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example4a();
	}
}
