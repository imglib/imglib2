/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */

package net.imglib2.util;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;

/**
 * Helper class for {@link Img} subclass unit tests.
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Curtis Rueden
 */
public class ImgTestHelper
{
	// which dimensions to test
	private static final long[][] DIM =
			new long[][] {
					{ 127 },
					{ 288 },
					{ 135, 111 },
					{ 172, 131 },
					{ 15, 13, 33 },
					{ 110, 38, 30 },
					{ 109, 34, 111 },
					{ 12, 43, 92, 10 },
					{ 21, 34, 29, 13 },
					{ 5, 12, 30, 4, 21 },
					{ 14, 21, 13, 9, 12 }
			};

	public static long[][] dims()
	{
		return DIM.clone();
	}

	public static boolean testImg( final long[] size, final ImgFactory< FloatType > factory1, final ImgFactory< FloatType > factory2 )
	{
		// create the image
		final Img< FloatType > img1 = factory1.create( size, new FloatType() );
		final Img< FloatType > img2 = factory2.create( size, new FloatType() );

		final int numDimensions = img1.numDimensions();

		// get a reference to compare to
		final float[] reference = createReference( img1 );

		// copy into a second image using simple cursors
		final Cursor< FloatType > cursor1 = img1.cursor();
		final Cursor< FloatType > cursor2 = img2.cursor();

		while ( cursor1.hasNext() )
		{
			cursor1.fwd();
			cursor2.fwd();

			cursor2.get().set( cursor1.get() );
		}

		cursor1.reset();
		cursor2.reset();

		// and copy right back
		while ( cursor2.hasNext() )
		{
			cursor1.fwd();
			cursor2.fwd();

			cursor1.get().set( cursor2.get() );
		}

		// copy back into a second image using localizable and positionable
		// cursors
		final Cursor< FloatType > localizableCursor1 = img1.localizingCursor();
		final RandomAccess< FloatType > positionable2 = img2.randomAccess();

		int i = 0;

		while ( localizableCursor1.hasNext() )
		{
			localizableCursor1.fwd();
			++i;

			if ( i % 2 == 0 )
				positionable2.setPosition( localizableCursor1 );
			else
				positionable2.setPosition( localizableCursor1 );

			final FloatType t2 = positionable2.get();
			final FloatType t1 = localizableCursor1.get();
//			float f1 = t1.getRealFloat();
//			float f2 = t2.getRealFloat();
			t2.set( t1 );
//			positionable2.get().set( localizableCursor1.get() );
		}

		// copy again to the first image using a LocalizableByDimOutsideCursor
		// and a LocalizableByDimCursor
		final ExtendedRandomAccessibleInterval< FloatType, Img< FloatType > > extendedImg2 = new ExtendedRandomAccessibleInterval< FloatType, Img< FloatType > >( img2, new OutOfBoundsPeriodicFactory< FloatType, Img< FloatType > >() );
		final RandomAccess< FloatType > outsideCursor2 = extendedImg2.randomAccess();
		localizableCursor1.reset();

		final int[] pos = new int[ numDimensions ];
		i = 0;
		int direction = 1;

		try
		{
			while ( localizableCursor1.hasNext() )
			{
				localizableCursor1.fwd();
				localizableCursor1.localize( pos );
				++i;

				// how many times far away from the original image do we grab
				// the pixel
				final int distance = i % 5;
				direction *= -1;

				pos[ i % numDimensions ] += img1.dimension( i % numDimensions ) * distance * direction;

				if ( i % 7 == 0 )
					outsideCursor2.setPosition( pos );
				else
					outsideCursor2.setPosition( pos );

				final FloatType t1 = localizableCursor1.get();
				final FloatType t2 = outsideCursor2.get();

//				final float f1 = t1.getRealFloat();
//				final float f2 = t2.getRealFloat();

				t1.set( t2 );

			}
		}
		catch ( final ArrayIndexOutOfBoundsException e )
		{
			System.err.println( ( i % 7 == 0 ? "setPosition() " : "moveTo() " ) + Util.printCoordinates( pos ) );
			e.printStackTrace();
			System.exit( 1 );
		}

		final boolean success = test( img1, reference );

		return success;
	}

	private static float[] createReference( final Img< FloatType > img )
	{
		// use a random number generator
		final Random rnd = new Random( 1241234 );

		// create reference array
		final float[] reference = new float[ ( int ) img.size() ];

		// iterate over image and reference array and fill with data
		final Cursor< FloatType > cursor = img.cursor();
		int i = 0;

		while ( cursor.hasNext() )
		{
			cursor.fwd();

			final float value = rnd.nextFloat();
			reference[ i++ ] = value;
			cursor.get().set( value );
		}

		return reference;
	}

	private static boolean test( final Img< FloatType > img, final float[] reference )
	{
		boolean allEqual = true;

		final Cursor< FloatType > cursor = img.cursor();
		int i = 0;

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			allEqual &= cursor.get().get() == reference[ i++ ];
		}

		return allEqual;
	}
}
