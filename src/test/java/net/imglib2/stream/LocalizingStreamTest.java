/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith( Parameterized.class )
public class LocalizingStreamTest
{
	private Img< IntType > img;

	private long expectedSum;

	@Parameterized.Parameters( name = "{0}" )
	public static Collection< ImgFactory< IntType > > data()
	{
		final List< ImgFactory< IntType > > list = new ArrayList<>();
		list.add( new ArrayImgFactory<>( new IntType() ) );
		list.add( new CellImgFactory<>( new IntType() ) );
		list.add( new PlanarImgFactory<>( new IntType() ) );
		return list;
	}

	public LocalizingStreamTest( ImgFactory< IntType > factory )
	{
		img = factory.create( 10, 10, 10 );

		final Random rand = new Random( 12 );
		final Cursor< IntType > c = img.localizingCursor();
		while( c.hasNext())
		{
			final int value = rand.nextInt(1000 );
			c.next().set( value );
			expectedSum += value * c.getIntPosition( 0 );
			expectedSum += c.getIntPosition( 1 );
			expectedSum += c.getIntPosition( 2 );
		}
	}

	@Test
	public void testStream()
	{
		final long actualSum = Streams.localizable( img )
				.mapToLong( s -> s.get().get()
						* s.getIntPosition( 0 )
						+ s.getIntPosition( 1 )
						+ s.getIntPosition( 2 )
				).sum();
		Assert.assertEquals( expectedSum, actualSum );
	}

	@Test
	public void testLocalizingStream()
	{
		final long actualSum = Streams.localizing( img )
				.mapToLong( s -> s.get().get()
						* s.getIntPosition( 0 )
						+ s.getIntPosition( 1 )
						+ s.getIntPosition( 2 )
				).sum();
		Assert.assertEquals( expectedSum, actualSum );
	}

	@Test
	public void testParallelStream()
	{
		final long actualSum = Streams.localizable( img )
				.parallel()
				.mapToLong( s -> s.get().get()
						* s.getIntPosition( 0 )
						+ s.getIntPosition( 1 )
						+ s.getIntPosition( 2 )
				).sum();
		Assert.assertEquals( expectedSum, actualSum );
	}

	@Test
	public void testLocalizingParallelStream()
	{
		final long actualSum = Streams.localizing( img )
				.parallel()
				.mapToLong( s -> s.get().get()
						* s.getIntPosition( 0 )
						+ s.getIntPosition( 1 )
						+ s.getIntPosition( 2 )
				).sum();
		Assert.assertEquals( expectedSum, actualSum );
	}
}
