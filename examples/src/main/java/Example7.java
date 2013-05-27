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
import net.imglib2.Cursor;
import net.imglib2.FinalRealInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.randomaccess.LanczosInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * Use three different interpolators to 10x magnify a small area
 */
public class Example7
{
	public Example7() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > img = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		ImageJFunctions.show( img );

		// create an InterpolatorFactory RealRandomAccessible using nearst neighbor interpolation
		NearestNeighborInterpolatorFactory< FloatType > factory1 =
			new NearestNeighborInterpolatorFactory< FloatType >();

		// create an InterpolatorFactory RealRandomAccessible using linear interpolation
		NLinearInterpolatorFactory< FloatType > factory2 =
			new NLinearInterpolatorFactory< FloatType >();

		// create an InterpolatorFactory RealRandomAccessible using lanczos interpolation
		LanczosInterpolatorFactory< FloatType > factory3 =
			new LanczosInterpolatorFactory< FloatType >();

		// create a RandomAccessible using the factory and views method
		// it is important to extend the image first, the interpolation scheme might
		// grep pixels outside of the boundaries even when locations inside are queried
		// as they integrate pixel information in a local neighborhood - the size of
		// this neighborhood depends on which interpolator is used
		RealRandomAccessible< FloatType > interpolant1 = Views.interpolate(
			Views.extendMirrorSingle( img ), factory1 );
		RealRandomAccessible< FloatType > interpolant2 = Views.interpolate(
			Views.extendMirrorSingle( img ), factory2 );
		RealRandomAccessible< FloatType > interpolant3 = Views.interpolate(
			Views.extendMirrorSingle( img ), factory3 );

		// define the area in the interpolated image
		double[] min = new double[]{ 105.12, 40.43 };
		double[] max = new double[]{ 129.56, 74.933 };

		FinalRealInterval interval = new FinalRealInterval( min, max );

		ImageJFunctions.show( magnify( interpolant1, interval,
			new ArrayImgFactory< FloatType >(), 10 ) ).setTitle( "Nearest Neighbor Interpolation" );
		ImageJFunctions.show( magnify( interpolant2, interval,
			new ArrayImgFactory< FloatType >(), 10 ) ).setTitle( "Linear Interpolation" );
		ImageJFunctions.show( magnify( interpolant3, interval,
			new ArrayImgFactory< FloatType >(), 10 ) ).setTitle( "Lanczos Interpolation" );
	}

	/**
	 * Compute a magnified version of a given real interval
	 *
	 * @param source - the input data
	 * @param interval - the real interval on the source that should be magnified
	 * @param factory - the image factory for the output image
	 * @param magnification - the ratio of magnification
	 * @return - an Img that contains the magnified image content
	 */
	public static < T extends Type< T > > Img< T > magnify( RealRandomAccessible< T > source,
		RealInterval interval, ImgFactory< T > factory, double magnification )
	{
		int numDimensions = interval.numDimensions();

		// compute the number of pixels of the output and the size of the real interval
		long[] pixelSize = new long[ numDimensions ];
		double[] intervalSize = new double[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			intervalSize[ d ] = interval.realMax( d ) - interval.realMin( d );
			pixelSize[ d ] = Math.round( intervalSize[ d ] * magnification ) + 1;
		}

		// create the output image
		Img< T > output = factory.create( pixelSize, Util.getTypeFromRealRandomAccess( source ) );

		// cursor to iterate over all pixels
		Cursor< T > cursor = output.localizingCursor();

		// create a RealRandomAccess on the source (interpolator)
		RealRandomAccess< T > realRandomAccess = source.realRandomAccess();

		// the temporary array to compute the position
		double[] tmp = new double[ numDimensions ];

		// for all pixels of the output image
		while ( cursor.hasNext() )
		{
			cursor.fwd();

			// compute the appropriate location of the interpolator
			for ( int d = 0; d < numDimensions; ++d )
				tmp[ d ] = cursor.getDoublePosition( d ) / output.realMax( d ) * intervalSize[ d ]
						+ interval.realMin( d );

			// set the position
			realRandomAccess.setPosition( tmp );

			// set the new value
			cursor.get().set( realRandomAccess.get() );
		}

		return output;
	}

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example7();
	}
}
