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

package net.imglib2.img.basictypeaccess;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;

public class AccessesTest
{

	private final int numEntities = 10;

	private final int start = 3;

	private final int stop = 6;

	@Test
	public void testByte()
	{

		final ByteArray source = new ByteArray( numEntities );

		{
			final ByteArray target = new ByteArray( numEntities );
			Accesses.copy( source, target, numEntities );
			Assert.assertArrayEquals( source.getCurrentStorageArray(), target.getCurrentStorageArray() );
		}

		{
			final ByteArray target = new ByteArray( numEntities );
			Accesses.copy( source, target, start, stop );
			for ( int i = 0; i < numEntities; ++i )
			{
				Assert.assertEquals( i < start || i >= stop ? 0 : source.getValue( i ), target.getValue( i ) );
			}
		}

	}

	@Test
	public void testChar()
	{

		final CharArray source = new CharArray( numEntities );

		{
			final CharArray target = new CharArray( numEntities );
			Accesses.copy( source, target, numEntities );
			Assert.assertArrayEquals( source.getCurrentStorageArray(), target.getCurrentStorageArray() );
		}

		{
			final CharArray target = new CharArray( numEntities );
			Accesses.copy( source, target, start, stop );
			for ( int i = 0; i < numEntities; ++i )
			{
				Assert.assertEquals( i < start || i >= stop ? 0 : source.getValue( i ), target.getValue( i ) );
			}
		}

	}

	@Test
	public void testDouble()
	{

		final DoubleArray source = new DoubleArray( numEntities );

		{
			final DoubleArray target = new DoubleArray( numEntities );
			Accesses.copy( source, target, numEntities );
			Assert.assertArrayEquals( source.getCurrentStorageArray(), target.getCurrentStorageArray(), 0.0 );
		}

		{
			final DoubleArray target = new DoubleArray( numEntities );
			Accesses.copy( source, target, start, stop );
			for ( int i = 0; i < numEntities; ++i )
			{
				Assert.assertEquals( i < start || i >= stop ? 0 : source.getValue( i ), target.getValue( i ), 0.0 );
			}
		}

	}

	@Test
	public void testFloat()
	{

		final FloatArray source = new FloatArray( numEntities );

		{
			final FloatArray target = new FloatArray( numEntities );
			Accesses.copy( source, target, numEntities );
			Assert.assertArrayEquals( source.getCurrentStorageArray(), target.getCurrentStorageArray(), 0.0f );
		}

		{
			final FloatArray target = new FloatArray( numEntities );
			Accesses.copy( source, target, start, stop );
			for ( int i = 0; i < numEntities; ++i )
			{
				Assert.assertEquals( i < start || i >= stop ? 0 : source.getValue( i ), target.getValue( i ), 0.0f );
			}
		}

	}

	@Test
	public void testInt()
	{

		final IntArray source = new IntArray( numEntities );

		{
			final IntArray target = new IntArray( numEntities );
			Accesses.copy( source, target, numEntities );
			Assert.assertArrayEquals( source.getCurrentStorageArray(), target.getCurrentStorageArray() );
		}

		{
			final IntArray target = new IntArray( numEntities );
			Accesses.copy( source, target, start, stop );
			for ( int i = 0; i < numEntities; ++i )
			{
				Assert.assertEquals( i < start || i >= stop ? 0 : source.getValue( i ), target.getValue( i ) );
			}
		}

	}

	@Test
	public void testLong()
	{

		final LongArray source = new LongArray( numEntities );

		{
			final LongArray target = new LongArray( numEntities );
			Accesses.copy( source, target, numEntities );
			Assert.assertArrayEquals( source.getCurrentStorageArray(), target.getCurrentStorageArray() );
		}

		{
			final LongArray target = new LongArray( numEntities );
			Accesses.copy( source, target, start, stop );
			for ( int i = 0; i < numEntities; ++i )
			{
				Assert.assertEquals( i < start || i >= stop ? 0 : source.getValue( i ), target.getValue( i ) );
			}
		}

	}

	@Test
	public void testShort()
	{

		final ShortArray source = new ShortArray( numEntities );

		{
			final ShortArray target = new ShortArray( numEntities );
			Accesses.copy( source, target, numEntities );
			Assert.assertArrayEquals( source.getCurrentStorageArray(), target.getCurrentStorageArray() );
		}

		{
			final ShortArray target = new ShortArray( numEntities );
			Accesses.copy( source, target, start, stop );
			for ( int i = 0; i < numEntities; ++i )
			{
				Assert.assertEquals( i < start || i >= stop ? 0 : source.getValue( i ), target.getValue( i ) );
			}
		}

	}

}
