/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.view;

import static org.junit.Assert.assertEquals;

import java.util.stream.LongStream;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.LongType;

import org.junit.Test;

/**
 * Tests for {@link Views#rotate(RandomAccessibleInterval, int, int)}
 * functionality.
 *
 * @author Gabe Selzer
 *
 */
public class RotationViewTest
{

	@Test
	public void test()
	{

		final long[] sizes = { 3, 4 };

		final long n = LongStream.of( sizes ).reduce( 1, ( x, y ) -> x * y );

		final long[] data = new long[ ( int ) n ];

		for ( int i = 0; i < n; i++ )
		{
			data[ i ] = i;
		}

		final RandomAccessibleInterval< LongType > source = ArrayImgs.longs( data, sizes );
		final RandomAccess< LongType > sourceRA = source.randomAccess();
		final RandomAccessibleInterval< LongType > actual = Views.rotate( source, 0, 1 );
		final RandomAccess< LongType > actualRA = actual.randomAccess();

		// check each value matches with their rotated counterparts
		for ( int i = 0; i < sizes[ 0 ]; i++ )
		{
			for ( int j = 0; j < sizes[ 1 ]; j++ )
			{
				sourceRA.setPosition( new long[] { i, j } );
				actualRA.setPosition( new long[] { -j, i } );

				assertEquals( sourceRA.get().get(), actualRA.get().get() );
			}
		}

		// check to make sure the bounds are the same
		assertEquals( source.min( 0 ), actual.min( 1 ) );
		assertEquals( source.max( 0 ), actual.max( 1 ) );
		assertEquals( source.min( 1 ), -actual.max( 0 ) );
		assertEquals( source.max( 1 ), -actual.min( 0 ) );

	}

}
