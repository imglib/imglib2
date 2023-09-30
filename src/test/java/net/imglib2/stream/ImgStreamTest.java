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
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BitType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith( Parameterized.class )
public class ImgStreamTest
{
	private Img< BitType > img;

	private int expectedCount;

	@Parameterized.Parameters( name = "{0}" )
	public static Collection< ImgFactory< BitType > > data()
	{
		final List< ImgFactory< BitType > > list = new ArrayList<>();
		list.add( new ArrayImgFactory<>( new BitType() ) );
		list.add( new CellImgFactory<>( new BitType() ) );
		list.add( new PlanarImgFactory<>( new BitType() ) );
		return list;
	}

	public ImgStreamTest( ImgFactory< BitType > factory )
	{
		img = factory.create( 100, 100, 100 );

		final Random rand = new Random( 12 );
		expectedCount = 0;
		for ( BitType t : img )
		{
			final boolean b = rand.nextBoolean();
			t.set( b );
			if ( b )
				++expectedCount;
		}
	}

//	@Test
	public void testForLoop()
	{
		long actualCount = 0;
		for ( BitType t : img )
		{
			if ( t.get() )
				++actualCount;
		}
		Assert.assertEquals( expectedCount, actualCount );
	}

	@Test
	public void testStream()
	{
		final long actualCount = img.stream().filter( BooleanType::get ).count();
		Assert.assertEquals( expectedCount, actualCount );
	}

	@Test
	public void testParallelStream()
	{
		final long actualCount = img.parallelStream().filter( BooleanType::get ).count();
		Assert.assertEquals( expectedCount, actualCount );
	}

	@Test
	public void testEstimateSize()
	{
		final LocalizableSpliterator< BitType > spliterator = img.spliterator();
		final long actualSize = spliterator.estimateSize();
		final long expectedSize = img.size();
		Assert.assertEquals( expectedSize, actualSize );
	}
}
