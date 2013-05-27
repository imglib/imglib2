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
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Here we want to copy an Image into another with a different Container one using a generic method,
 * using a LocalizingCursor and a RandomAccess
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example2c
{
	public Example2c() throws ImgIOException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > img = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// copy & display an image
		Img< FloatType > duplicate = img.factory().create( img, img.firstElement() );
		copy( img, duplicate );
		ImageJFunctions.show( duplicate );

		// use a View to define an interval as source for copying
		//
		// Views.offsetInterval() does not only define where it is, but also adds a translation
		// so that the minimal coordinate (upper left) of the view maps to (0,0)
		RandomAccessibleInterval< FloatType > viewSource = Views.offsetInterval( img,
			new long[] { 100, 100 }, new long[]{ 250, 150 } );

		// and as target
		RandomAccessibleInterval< FloatType > viewTarget = Views.offsetInterval( img,
			new long[] { 500, 200 }, new long[]{ 250, 150 } );

		// now we make the target iterable
		// (which is possible because it is a RandomAccessibleInterval)
		IterableInterval< FloatType > iterableTarget = Views.iterable( viewTarget );

		// copy it into the original image (overwriting part of img)
		copy( viewSource, iterableTarget );

		// show the original image
		ImageJFunctions.show( img );
	}

	/**
	 * Copy from a source that is just RandomAccessible to an IterableInterval. Latter one defines
	 * size and location of the copy operation. It will query the same pixel locations of the
	 * IterableInterval in the RandomAccessible. It is up to the developer to ensure that these
	 * coordinates match.
	 *
	 * Note that both, input and output could be Views, Img or anything that implements
	 * those interfaces.
	 *
	 * @param source - a RandomAccess as source that can be infinite
	 * @param target - an IterableInterval as target
	 */
	public < T extends Type< T > > void copy( final RandomAccessible< T > source,
		final IterableInterval< T > target )
	{
		// create a cursor that automatically localizes itself on every move
		Cursor< T > targetCursor = target.localizingCursor();
		RandomAccess< T > sourceRandomAccess = source.randomAccess();

		// iterate over the input cursor
		while ( targetCursor.hasNext())
		{
			// move input cursor forward
			targetCursor.fwd();

			// set the output cursor to the position of the input cursor
			sourceRandomAccess.setPosition( targetCursor );

			// set the value of this pixel of the output image, every Type supports T.set( T type )
			targetCursor.get().set( sourceRandomAccess.get() );
		}
	}

	public static void main( String[] args ) throws ImgIOException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example2c();
	}
}
