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

	@Test
	public void testByte()
	{
		final ByteArray source = new ByteArray( new byte[] { 1, 2, 3, 4, 5 } );
		final ByteArray target = new ByteArray( new byte[] { -1, -2, -3, -4, -5 } );
		Accesses.copy( source, 1, target, 3, 2 );
		Assert.assertArrayEquals( new byte[] { -1, -2, -3, 2, 3 }, target.getCurrentStorageArray() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void testChar()
	{
		final CharArray source = new CharArray( new char[] { 'a', 'b', 'c', 'd', 'e' } );
		final CharArray target = new CharArray( new char[] { 'f', 'g', 'h', 'i', 'j' } );
		Accesses.copy( source, 1, target, 3, 2 );
		Assert.assertArrayEquals( new char[] { 'f', 'g', 'h', 'b', 'c' }, target.getCurrentStorageArray() );
	}

	@Test
	public void testDouble()
	{
		final DoubleArray source = new DoubleArray( new double[] { 1, 2, 3, 4, 5 } );
		final DoubleArray target = new DoubleArray( new double[] { -1, -2, -3, -4, -5 } );
		Accesses.copy( source, 1, target, 3, 2 );
		Assert.assertArrayEquals( new double[] { -1, -2, -3, 2, 3 }, target.getCurrentStorageArray(), 0.0 );
	}

	@Test
	public void testFloat()
	{
		final FloatArray source = new FloatArray( new float[] { 1, 2, 3, 4, 5 } );
		final FloatArray target = new FloatArray( new float[] { -1, -2, -3, -4, -5 } );
		Accesses.copy( source, 1, target, 3, 2 );
		Assert.assertArrayEquals( new float[] { -1, -2, -3, 2, 3 }, target.getCurrentStorageArray(), 0.0f );
	}

	@Test
	public void testInt()
	{
		final IntArray source = new IntArray( new int[] { 1, 2, 3, 4, 5 } );
		final IntArray target = new IntArray( new int[] { -1, -2, -3, -4, -5 } );
		Accesses.copy( source, 1, target, 3, 2 );
		Assert.assertArrayEquals( new int[] { -1, -2, -3, 2, 3 }, target.getCurrentStorageArray() );
	}

	@Test
	public void testLong()
	{
		final LongArray source = new LongArray( new long[] { 1, 2, 3, 4, 5 } );
		final LongArray target = new LongArray( new long[] { -1, -2, -3, -4, -5 } );
		Accesses.copy( source, 1, target, 3, 2 );
		Assert.assertArrayEquals( new long[] { -1, -2, -3, 2, 3 }, target.getCurrentStorageArray() );
	}

	@Test
	public void testShort()
	{
		final ShortArray source = new ShortArray( new short[] { 1, 2, 3, 4, 5 } );
		final ShortArray target = new ShortArray( new short[] { -1, -2, -3, -4, -5 } );
		Accesses.copy( source, 1, target, 3, 2 );
		Assert.assertArrayEquals( new short[] { -1, -2, -3, 2, 3 }, target.getCurrentStorageArray() );
	}

	@Test
	public void testCopyInPlace()
	{
		byte[] expected = { 1, 2, 3, 4, 5 };
		final ByteArray access = new ByteArray( expected.clone() );
		Accesses.copy( access, 0, access, 0, access.getArrayLength() );
		Assert.assertArrayEquals( expected, access.getCurrentStorageArray() );
	}

	@Test
	public void testCopyInPlaceWithOffset()
	{
		final ByteArray access = new ByteArray( new byte[] { 1, 2, 3, 4, 5 } );
		Accesses.copy( access, 1, access, 2, 2 );
		Assert.assertArrayEquals( new byte[] { 1, 2, 2, 3, 5 }, access.getCurrentStorageArray() );
	}

	@Test
	public void testCopyInPlaceWithNegativeOffset()
	{
		final ByteArray access = new ByteArray( new byte[] { 1, 2, 3, 4, 5 } );
		Accesses.copy( access, 2, access, 1, 2 );
		Assert.assertArrayEquals( new byte[] { 1, 3, 4, 4, 5 }, access.getCurrentStorageArray() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void testLongSourceByteTargetFail()
	{
		int numEntities = 5;
		final LongArray source = new LongArray( numEntities );
		final ByteArray target = new ByteArray( numEntities );
		Accesses.copy( source, 0, target, 0, numEntities );
	}

	@Test
	public void testByteWithDifferentAccesses()
	{
		final ByteArray source = new ByteArray( new byte[] { 1, 2, 3, 4, 5 } );
		final SomeByteAccess target = new SomeByteAccess( new byte[] { -1, -2, -3, -4, -5 } );
		Accesses.copy( source, 1, target, 3, 2 );
		Assert.assertArrayEquals( new byte[] { -1, -2, -3, 2, 3 }, target.getCurrentStorageArray() );
	}

	private static final class SomeByteAccess implements ByteAccess
	{

		private final byte[] data;

		private SomeByteAccess( byte[] data )
		{
			this.data = data;
		}

		public byte[] getCurrentStorageArray()
		{
			return this.data;
		}

		@Override
		public byte getValue( int index )
		{
			return data[ index ];
		}

		@Override
		public void setValue(int index, byte value)
		{
			data[ index ] = value;
		}
	}

}
