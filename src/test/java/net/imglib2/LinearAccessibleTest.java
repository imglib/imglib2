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
package net.imglib2;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.operators.ValueEquals;
import net.imglib2.view.Views;
import net.imglib2.view.linear.LinearAccessibleViewOnArrayImg;
import net.imglib2.view.linear.LinearAccessibleViewOnPlanarImg;
import net.imglib2.view.linear.LinearAccessibleViewOnRandomAccessibleInterval;

/**
 *
 * Test for various {@link LinearAccessible} implementations. These
 * implementations are tested: {@link ArrayImg}, {@link PlanarImg},
 * {@link RandomAccessibleInterval} (general).
 *
 * @author Philipp Hanslovsky
 *
 */
public class LinearAccessibleTest
{

	private final long[] dimensions = { 20, 30, 40 };

	private final long size = dimensions[ 0 ] * dimensions[ 1 ] * dimensions[ 2 ];

	private final Random rng = new Random( 100 );

	@Test
	public void testRandomAccessibleInterval()
	{
		final CellImg< DoubleType, ?, ? > img = new CellImgFactory< DoubleType >( new int[] { 10, 11, 12 } ).create( dimensions, new DoubleType() );
		for ( final DoubleType i : Views.iterable( img ) )
		{
			i.set( rng.nextDouble() );
		}
		testLinearAccessible( new LinearAccessibleViewOnRandomAccessibleInterval<>( img ) );

	}

	@Test
	public void testPlanarImg()
	{
		final PlanarImg< FloatType, FloatArray > img = PlanarImgs.floats( dimensions );
		for ( final FloatType i : img )
		{
			i.set( rng.nextFloat() );
		}
		testLinearAccessible( new LinearAccessibleViewOnPlanarImg<>( img ) );
	}

	@Test
	public void testArrayImg()
	{
		final ArrayImg< LongType, LongArray > img = ArrayImgs.longs( dimensions );
		for ( final LongType i : img )
		{
			i.set( rng.nextLong() );
		}
		testLinearAccessible( new LinearAccessibleViewOnArrayImg<>( img ) );
	}

	public < T extends ValueEquals< T >, A extends RandomAccessibleInterval< T > & LinearAccessible< T > > void testLinearAccessible( final A a )
	{
		Assert.assertEquals( size, a.size() );
		final Cursor< T > c = Views.iterable( a ).cursor();
		final LinearAccess< T > l = a.linearAccess();
		for ( long i = 0; c.hasNext(); ++i )
		{
			Assert.assertTrue( c.next().valueEquals( l.get( i ) ) );
		}
	}

}
