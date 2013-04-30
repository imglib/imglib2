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
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Illustrate outside strategies
 *
 */
public class Example5
{
	public Example5() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( "DrosophilaWingSmall.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// create an infinite view where all values outside of the Interval are 0
		RandomAccessible< FloatType> infinite1 =
			Views.extendValue( image, new FloatType( 0 ) );

		// create an infinite view where all values outside of the Interval are 128
		RandomAccessible< FloatType> infinite2 =
			Views.extendValue( image, new FloatType( 128 ) );

		// create an infinite view where all outside valuesare random in a range of 0-255
		RandomAccessible< FloatType> infinite3 = Views.extendRandom( image, 0, 255 );

		// create an infinite view where all values outside of the Interval are
		// the mirrored content, the mirror is the last pixel
		RandomAccessible< FloatType> infinite4 = Views.extendMirrorSingle( image );

		// create an infinite view where all values outside of the Interval are
		// the mirrored content, the mirror is BEHIND the last pixel,
		// i.e. the first and last pixel are always duplicated
		RandomAccessible< FloatType> infinite5 = Views.extendMirrorDouble( image );

		// all values outside of the Interval periodically repeat the image content
		// (like the Fourier space assumes)
		RandomAccessible< FloatType> infinite6 = Views.extendPeriodic( image );

		// if you implemented your own strategy that you want to instantiate, it will look like this
		RandomAccessible< FloatType> infinite7 =
			new ExtendedRandomAccessibleInterval< FloatType, Img< FloatType > >( image,
				new OutOfBoundsConstantValueFactory< FloatType, Img< FloatType > >(
				new FloatType( 255 ) ) );

		// visualize the outofbounds strategies

		// in order to visualize them, we have to define a new interval
		// on them which can be displayed
		long[] min = new long[ image.numDimensions() ];
		long[] max = new long[ image.numDimensions() ];

		for ( int d = 0; d < image.numDimensions(); ++d )
		{
			// we add/subtract another 30 pixels here to illustrate
			// that it is really infinite and does not only work once
			min[ d ] = -image.dimension( d ) - 90 ;
			max[ d ] = image.dimension( d ) * 2 - 1 + 90;
		}

		// define the Interval on the infinite random accessibles
		FinalInterval interval = new FinalInterval( min, max );

		// now define the interval on the infinite view and display
		ImageJFunctions.show( Views.interval( infinite1, interval ) );
		ImageJFunctions.show( Views.interval( infinite2, interval ) );
		ImageJFunctions.show( Views.interval( infinite3, interval ) );
		ImageJFunctions.show( Views.interval( infinite4, interval ) );
		ImageJFunctions.show( Views.interval( infinite5, interval ) );
		ImageJFunctions.show( Views.interval( infinite6, interval ) );
		ImageJFunctions.show( Views.interval( infinite7, interval ) );
	}

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example5();
	}
}
