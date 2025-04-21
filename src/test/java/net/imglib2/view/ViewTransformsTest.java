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

import net.imglib2.Interval;
import net.imglib2.transform.integer.Mixed;
import net.imglib2.util.Intervals;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ViewTransformsTest
{
	@Test
	public void testTranslate()
	{
		final Mixed transform = ViewTransforms.translate( 10, 9, 8 );
		final long[] result = apply( transform, new long[] { 10, 11, 12 } );
		assertArrayEquals( new long[] { 0, 2, 4 }, result );
	}

	@Test
	public void testRotate()
	{
		final Mixed transform = ViewTransforms.rotate( 3, 1, 0 );
		final long[] result = apply( transform, new long[] { 2, -1, 3 } );
		assertArrayEquals( new long[] { 1, 2, 3 }, result );
	}

	@Test
	public void testPermute()
	{
		final Mixed transform = ViewTransforms.permute( 3, 0, 2 );
		final long[] result = apply( transform, new long[] { 3, 2, 1 } );
		assertArrayEquals( new long[] { 1, 2, 3 }, result );
	}

	@Test
	public void testMoveAxis()
	{
		final Mixed transform = ViewTransforms.moveAxis( 3, 0, 2 );
		final long[] result = apply( transform, new long[] { 2, 3, 1 } );
		assertArrayEquals( new long[] { 1, 2, 3 }, result );
	}

	@Test
	public void testInvertAxis()
	{
		final Mixed transform = ViewTransforms.invertAxis( 3, 1 );
		final long[] result = apply( transform, new long[] { 1, -2, 3 } );
		assertArrayEquals( new long[] { 1, 2, 3 }, result );
	}

	@Test
	public void testZeroMin()
	{
		final Interval interval = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Mixed transform = ViewTransforms.zeroMin( interval );
		final long[] result = apply( transform, new long[] { 0, 0, 0 } );
		assertArrayEquals( new long[] { 1, 2, 3 }, result );
	}

	@Test
	public void testAddDimension()
	{
		final Mixed transform = ViewTransforms.addDimension( 3 );
		final long[] result = apply( transform, new long[] { 1, 2, 3, 17 } );
		assertArrayEquals( new long[] { 1, 2, 3 }, result );
	}

	@Test
	public void testHyperSlice()
	{
		final Mixed transform = ViewTransforms.hyperSlice( 3, 2, 3 );
		final long[] result = apply( transform, new long[] { 1, 2 } );
		assertArrayEquals( new long[] { 1, 2, 3 }, result );
	}

	private long[] apply( Mixed transform, long[] source )
	{
		long[] result = new long[ 3 ];
		transform.apply( source, result );
		return result;
	}

}
