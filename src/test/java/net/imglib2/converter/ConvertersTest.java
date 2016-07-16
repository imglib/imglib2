/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.converter;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

/**
 *
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 */
public class ConvertersTest
{
	final Random rnd = new Random();

	final static byte[] testValues = new byte[ 20 * 30 * 4 ];
	{
		rnd.nextBytes( testValues );
	}

	final static int[] data = new int[ 20 * 30 ];
	{
		for ( int i = 0; i < data.length; ++i )
			data[ i ] = rnd.nextInt();
	}

	@Test
	public void testArgbChannels()
	{
		final ArrayImg< ARGBType, ? > test = ArrayImgs.argbs( data, 20, 30 );
		final ArrayImg< ARGBType, ? > source = ArrayImgs.argbs( data.clone(), 20, 30 );
		final RandomAccessibleInterval< UnsignedByteType > composite = Converters.argbChannels( source );

		final Cursor< UnsignedByteType > compositeCursorA = Views.flatIterable( Views.hyperSlice( composite, 2, 0 ) ).cursor();
		final Cursor< UnsignedByteType > compositeCursorR = Views.flatIterable( Views.hyperSlice( composite, 2, 1 ) ).cursor();
		final Cursor< UnsignedByteType > compositeCursorG = Views.flatIterable( Views.hyperSlice( composite, 2, 2 ) ).cursor();
		final Cursor< UnsignedByteType > compositeCursorB = Views.flatIterable( Views.hyperSlice( composite, 2, 3 ) ).cursor();
		final Cursor< ARGBType > testCursor = test.cursor();
		final Cursor< ARGBType > sourceCursor = source.cursor();

		int i = 0;
		while ( compositeCursorA.hasNext() )
		{
			final ARGBType argbSource = sourceCursor.next();
			final ARGBType argbTest = testCursor.next();
			final UnsignedByteType a = compositeCursorA.next();
			final UnsignedByteType r = compositeCursorR.next();
			final UnsignedByteType g = compositeCursorG.next();
			final UnsignedByteType b = compositeCursorB.next();

			/* read */
			Assert.assertTrue( argbSource.valueEquals( argbTest ) );

			Assert.assertEquals( ARGBType.alpha( argbTest.get() ), a.get() );
			Assert.assertEquals( ARGBType.red( argbTest.get() ), r.get() );
			Assert.assertEquals( ARGBType.green( argbTest.get() ), g.get() );
			Assert.assertEquals( ARGBType.blue( argbTest.get() ), b.get() );

			/* write */
			a.set( testValues[ i ] & 0xff );
			Assert.assertEquals( ARGBType.alpha( argbSource.get() ), testValues[ i ] & 0xff );
			Assert.assertEquals( a.get(), testValues[ i ] & 0xff );
			++i;

			r.set( testValues[ i ] & 0xff );
			Assert.assertEquals( ARGBType.red( argbSource.get() ), testValues[ i ] & 0xff );
			Assert.assertEquals( r.get(), testValues[ i ] & 0xff );
			++i;

			g.set( testValues[ i ] & 0xff );
			Assert.assertEquals( ARGBType.green( argbSource.get() ), testValues[ i ] & 0xff );
			Assert.assertEquals( g.get(), testValues[ i ] & 0xff );
			++i;

			b.set( testValues[ i ] & 0xff );
			Assert.assertEquals( ARGBType.blue( argbSource.get() ), testValues[ i ] & 0xff );
			Assert.assertEquals( b.get(), testValues[ i ] & 0xff );
			++i;
		}
	}
}
