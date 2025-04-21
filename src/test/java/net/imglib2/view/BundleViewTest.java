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

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;

public class BundleViewTest
{
	protected Img< IntType > img;

	protected long[][] positions;

	@Before
	public void setUp()
	{
		final Random rnd = new Random();
		final long[] dimensions = new long[] { 10, 6, 5 };
		final int[] data = new int[ 10 * 6 * 5 ];
		Arrays.setAll( data, i -> rnd.nextInt());
		img = ArrayImgs.ints(data, dimensions);

		positions = new long[ 100 ][];
		Arrays.setAll( positions, i -> new long[]{ rnd.nextInt( 11 ), rnd.nextInt( 7 ), rnd.nextInt( 6 ) } );
	}

	@Test
	public void test()
	{
		final ExtendedRandomAccessibleInterval< IntType, Img< IntType > > extended = Views.extendZero( img );
		final BundleView< IntType > bundled = Views.bundle( extended );

		final RandomAccess< IntType > raT = extended.randomAccess();
		final RandomAccess< RandomAccess< IntType > > raRaT = bundled.randomAccess();

		for ( final long[] position : positions )
		{
			raT.setPosition( position );
			raRaT.setPosition( position );

			Assert.assertTrue( raT.get().valueEquals( raRaT.get().get() ) );
			Assert.assertArrayEquals( position, raRaT.get().positionAsLongArray() );
			Assert.assertArrayEquals( raRaT.positionAsLongArray(), raRaT.get().positionAsLongArray() );
		}
	}
}
