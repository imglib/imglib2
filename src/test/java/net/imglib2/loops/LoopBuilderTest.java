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
package net.imglib2.loops;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class LoopBuilderTest
{

	private RandomAccessibleInterval< IntType > imageA = randomImage( 1 );

	private RandomAccessibleInterval< IntType > imageB = randomImage( 42 );

	@Test
	public void testLoopBuilderRun()
	{
		RandomAccessibleInterval< IntType > sum = ArrayImgs.ints(
				Intervals.dimensionsAsLongArray( imageA )
		);
		LoopBuilder.setImages( imageA, imageB, sum ).forEachPixel(
				( a, b, s ) -> {
					s.set( a.get() + b.get() );
				}
		);
		assertSum( sum );
	}

	private RandomAccessibleInterval< IntType > randomImage( int randomSeed )
	{
		Img< IntType > result = ArrayImgs.ints( 3, 2, 5 );
		Random random = new Random( randomSeed );
		result.forEach( x -> x.set( random.nextInt() ) );
		return Views.translate( result, random.nextInt(), random.nextInt(), random.nextInt() );
	}

	private void assertSum( RandomAccessibleInterval< IntType > sum )
	{
		Cursor< IntType > a = Views.iterable( imageA ).cursor();
		Cursor< IntType > b = Views.iterable( imageB ).cursor();
		Cursor< IntType > s = Views.iterable( sum ).cursor();
		while ( s.hasNext() )
			assertEquals( s.next().get(), a.next().get() + b.next().get() );
	}
}
