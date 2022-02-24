/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.img;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import org.junit.Test;

/**
 * Tests {@link ImgView}.
 *
 * @author Curtis Rueden
 */
public class ImgViewTest
{
	/**
	 * Tests {@link ImgView#wrap(RandomAccessibleInterval)}.
	 */
	@Test
	public void testDefaultWrapping()
	{
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 7, 11, 5 );
		final RandomAccessible< UnsignedByteType > ra = Views.extendBorder( img );
		final RandomAccessibleInterval< UnsignedByteType > rai = Views.interval( ra, FinalInterval.createMinSize( -3, 4, -2, 29, 37, 31 ) );
		final Img< UnsignedByteType > result = ImgView.wrap( rai );
		assertSame( ImgView.class, result.getClass() );
		assertTrue( Intervals.equals( rai, result ) );
		assertSame( img.factory().getClass(), result.factory().getClass() );
	}

	/**
	 * Tests {@link ImgView#wrap(RandomAccessibleInterval, ImgFactory)}.
	 */
	@Test
	public void testWrapWithFactory()
	{
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 5, 7, 11 );
		final RandomAccessible< UnsignedByteType > ra = Views.extendBorder( img );
		final RandomAccessibleInterval< UnsignedByteType > rai = Views.interval( ra, FinalInterval.createMinSize( -2, 1, 3, 19, 17, 13 ) );
		final Img< UnsignedByteType > result = ImgView.wrap( rai, img.factory() );
		assertSame( ImgView.class, result.getClass() );
		assertTrue( Intervals.equals( rai, result ) );
		assertSame( img.factory().getClass(), result.factory().getClass() );
	}

	/**
	 * Tests {@link ImgView#wrap(RandomAccessibleInterval, ImgFactory)} with {@link Img} argument.
	 */
	public void testAvoidUnnecessaryWrapping()
	{
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 5, 7, 11 );
		assertSame( img, ImgView.wrap( img ) );
		final Img< UnsignedByteType > result = ImgView.wrap( img, new PlanarImgFactory<>( img.firstElement() ) );
		assertSame( img, result );
		assertNotSame( PlanarImgFactory.class, img.factory().getClass() );
	}
}
