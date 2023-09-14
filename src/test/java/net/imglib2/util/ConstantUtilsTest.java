/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.util;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ConstantUtilsTest
{

	@Test
	public void constantRandomAccessible()
	{
		final int nDim = 5;
		final int numPositions = 10;
		final Random rng = new Random( 100 );
		final IntType constVal = new IntType( 123 );
		final RandomAccessible< IntType > randomAccessible = ConstantUtils.constantRandomAccessible( constVal, nDim );
		final RandomAccess< IntType > access = randomAccessible.randomAccess();
		for ( int i = 0; i < numPositions; ++i )
		{
			for ( int d = 0; d < nDim; ++d )
				access.setPosition( rng.nextLong(), d );

			Assert.assertTrue( constVal.valueEquals( access.get() ) );
		}
	}

	@Test
	public void constantRandomAccessibleInterval()
	{
		final int nDim = 5;
		final Random rng = new Random( 100 );
		final long[] dims = LongStream.generate( () -> rng.nextInt( 5 ) + 1 ).limit( nDim ).toArray();
		final IntType constVal = new IntType( 123 );
		final RandomAccessibleInterval< IntType > randomAccessibleInterval = ConstantUtils.constantRandomAccessibleInterval( constVal, new FinalInterval( dims ) );

		Assert.assertArrayEquals( dims, Intervals.dimensionsAsLongArray( randomAccessibleInterval ) );
		Assert.assertArrayEquals( new long[ nDim ], randomAccessibleInterval.minAsLongArray() );

		Views.iterable( randomAccessibleInterval ).forEach( p -> Assert.assertTrue( constVal.valueEquals( constVal ) ) );
	}

	@Test
	public void constantRealRandomAccessible()
	{
		final int nDim = 5;
		final int numPositions = 10;
		final Random rng = new Random( 100 );
		final IntType constVal = new IntType( 123 );
		final RealRandomAccessible< IntType > randomAccessible = ConstantUtils.constantRealRandomAccessible( constVal, nDim );
		final RealRandomAccess< IntType > access = randomAccessible.realRandomAccess();
		for ( int i = 0; i < numPositions; ++i )
		{
			for ( int d = 0; d < nDim; ++d )
				access.setPosition( rng.nextDouble(), d );

			Assert.assertTrue( constVal.valueEquals( access.get() ) );
		}
	}
}
