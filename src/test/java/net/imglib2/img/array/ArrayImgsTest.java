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

package net.imglib2.img.array;

import static org.junit.Assert.assertArrayEquals;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.Unsigned128BitType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.Unsigned2BitType;
import net.imglib2.type.numeric.integer.Unsigned4BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.integer.UnsignedVariableBitLengthType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;

/**
 *
 * @author Philipp Hanslovsky
 * @author Gabe Selzer
 *
 */
public class ArrayImgsTest
{

	private final long[] dims = { 4, 5 };

	private final int nElements = ( int ) Intervals.numElements( dims );

	/** Tests {@link ArrayImgs#unsigned2Bits}. */
	@Test
	public void testUnsigned2Bits()
	{
		final long[] size = { 5, 20 };
		final long[] data = new long[ 100 ];
		for ( long d = 0; d < data.length; d++ )
		{
			data[ ( int ) d ] = d;
		}
		final LongArray access = new LongArray( data );

		final ArrayImg< Unsigned2BitType, LongArray > img = ArrayImgs.unsigned2Bits( size );
		int previous = 1, current = 1;
		for ( final Unsigned2BitType t : img )
		{
			t.set( current % 4 );
			final int next = current + previous;
			previous = current;
			current = next;
		}

		final long[] expected = { //
				0b1001010001111001010001111001010001111001010001111001010001111001L, //
				0b0111100101000111100101000111100101000111100101000111100101000111L, //
				0b0100011110010100011110010100011110010100011110010100011110010100L, //
				0b0000000000000000000000000000000000000000000000000000000001111001L //
		};
		final long[] actual = img.update( null ).getCurrentStorageArray();
		assertArrayEquals( expected, actual );
	}

	@Test
	public void testUnsigned4Bits()
	{
		final long[] size = { 5, 10 };

		final ArrayImg< Unsigned4BitType, LongArray > img = ArrayImgs.unsigned4Bits( size );
		int previous = 1, current = 1;
		for ( final Unsigned4BitType t : img )
		{
			t.set( current % 16 );
			final int next = current + previous;
			previous = current;
			current = next;
		}

		final long[] expected = {
				0xdb29909725d85321L,
				0x25d85321101f2d58L,
				0x101f2d58db299097L,
				0x0000000000000021L
		};
		final long[] actual = img.update( null ).getCurrentStorageArray();
		assertArrayEquals( expected, actual );
	}

	@Test
	public void testUnsigned12Bits()
	{
		final long[] size = { 5, 5 };

		final ArrayImg< Unsigned12BitType, LongArray > img = ArrayImgs.unsigned12Bits( size );
		int previous = 1, current = 1, next = 0;
		for ( final Unsigned12BitType t : img )
		{
			t.set( current % 4096 );
			next = current + ( 2 * previous );
			previous = current;
			current = next;
		}

		final long[] expected = {
				0b1011000000010101000000001011000000000101000000000011000000000001L,
				0b0101010100101010101100010101010100001010101100000101010100000010L,
				0b1010101010110101010101011010101010110101010101011010101010110101L,
				0b1011010101010101101010101011010101010101101010101011010101010101L,
				0b0000000000000000000001010101010110101010101101010101010110101010L
		};

		final long[] actual = img.update( null ).getCurrentStorageArray();
		assertArrayEquals( expected, actual );
	}

	@Test
	public void testUnsigned128Bits()
	{
		final long[] size = { 5, 5 };

		final ArrayImg< Unsigned128BitType, LongArray > img = ArrayImgs.unsigned128Bits( size );
		BigInteger previous = BigInteger.ONE, current = BigInteger.ONE, next = BigInteger.ZERO;
		BigInteger modulus = BigInteger.valueOf( 2 );
		modulus = modulus.pow( 128 );
		for ( final Unsigned128BitType t : img )
		{
			t.set( current.mod( modulus ) );
			next = current.add( previous.multiply( BigInteger.valueOf( 579 ) ) );
			previous = current;
			current = next;
		}

		final long[] expected = {
				0x1L, 0x0L, 0x244L, 0x0L, 0x487L, 0x0L, 0x52453L, 0x0L, 0xf61a8L,
				0x0L, 0xbb08961L, 0x0L, 0x2e7a6859L, 0x0L, 0x1a9ec11ebcL, 0x0L,
				0x83bd9b2007L, 0x0L, 0x3cb8cc63a33bL, 0x0L, 0x166aea23d1310L, 0x0L,
				0x8abca4e7974181L, 0x0L, 0x3b5f99dd7b95eb1L, 0x0L, 0x3d7ea295a2d28574L,
				0x1L, 0xa21232948b13afc7L, 0x9L, 0xb77beb03cd378523L, 0x2d7L,
				0x46a450fa5abe1438L, 0x18a1L, 0x43e8da937f503261L, 0x68685L,
				0x98c00cebb37ed09L, 0x3e3b48L, 0xa1325c67ad99de6cL, 0xf007ab0L,
				0x38d82ff91d16f9c7L, 0x9bc08e9eL, 0xcdbf2e76c019080bL, 0x2289d60c1aL,
				0x5eb3aee38b0ff520L, 0x182ce589bf5L, 0xb619c57803ad2601L,
				0x4fa08d75fc94L, 0xe67e521b88c48d61L, 0x3ba793fdeb889L
		};

		final long[] actual = img.update( null ).getCurrentStorageArray();
		assertArrayEquals( expected, actual );
	}

	@Test
	public void testUnsignedVariableBits7()
	{
		final long[] size = { 5, 5 };

		final ArrayImg< UnsignedVariableBitLengthType, LongArray > img = ArrayImgs.unsignedVariableBitLengths( 7, size );
		long previous = 1, current = 1, next = 0;
		for ( final UnsignedVariableBitLengthType t : img )
		{
			t.set( current % 128 );
			next = current + previous;
			previous = current;
			current = next;
		}

		final long[] expected = {
				0xb744546880a0c101L,
				0x6a987b6f179d242cL,
				0x00003122838af85bL
		};

		final long[] actual = img.update( null ).getCurrentStorageArray();
		assertArrayEquals( expected, actual );
	}

	@Test
	public void testUnsignedVariableBits33()
	{
		final long[] size = { 5, 5 };

		final ArrayImg< UnsignedVariableBitLengthType, LongArray > img = ArrayImgs.unsignedVariableBitLengths( 33, size );
		long previous = 1, current = 1, next = 0;
		for ( final UnsignedVariableBitLengthType t : img )
		{
			t.set( current % 8589934592L );
			next = current + previous;
			previous = current;
			current = next;
		}

		final long[] expected = {
				0x0000000400000001L,
				0x000000280000000cL,
				0x000001a000000080L,
				0x0000110000000540L,
				0x0000b20000003700L,
				0x0007480000024000L,
				0x004c400000179000L,
				0x031e800000f6c000L,
				0x20aa00000a180000L,
				0x5610000069b40000L,
				0xfe20000452f00001L,
				0x8880002d4800000dL,
				0x000001da31000092L
		};

		final long[] actual = img.update( null ).getCurrentStorageArray();
		assertArrayEquals( expected, actual );
	}

	@Test
	public void testHelperMethods()
	{
		// bytes
		{
			final byte[] data = new byte[ nElements ];
			for ( int d = 0; d < nElements; ++d )
				data[ d ] = ( byte ) d;
			final ByteArray access = new ByteArray( data );

			final ArrayImg< UnsignedByteType, ByteArray > unsignedRef = ArrayImgs.unsignedBytes( data, dims );
			final ArrayImg< UnsignedByteType, ByteArray > unsignedComp = ArrayImgs.unsignedBytes( access, dims );
			testRange( unsignedComp, unsignedRef, 0, nElements );

			final ArrayImg< ByteType, ByteArray > ref = ArrayImgs.bytes( data, dims );
			final ArrayImg< ByteType, ByteArray > comp = ArrayImgs.bytes( access, dims );
			testRange( comp, ref, 0, nElements );
		}

		// shorts
		{
			final short[] data = new short[ nElements ];
			for ( int d = 0; d < nElements; ++d )
				data[ d ] = ( short ) d;
			final ShortArray access = new ShortArray( data );

			final ArrayImg< UnsignedShortType, ShortArray > unsignedRef = ArrayImgs.unsignedShorts( data, dims );
			final ArrayImg< UnsignedShortType, ShortArray > unsignedComp = ArrayImgs.unsignedShorts( access, dims );
			testRange( unsignedComp, unsignedRef, 0, nElements );

			final ArrayImg< ShortType, ShortArray > ref = ArrayImgs.shorts( data, dims );
			final ArrayImg< ShortType, ShortArray > comp = ArrayImgs.shorts( access, dims );
			testRange( comp, ref, 0, nElements );
		}

		// ints & arbgs
		{
			final int[] data = new int[ nElements ];
			for ( int d = 0; d < nElements; ++d )
				data[ d ] = d;
			final IntArray access = new IntArray( data );

			final ArrayImg< UnsignedIntType, IntArray > unsignedRef = ArrayImgs.unsignedInts( data, dims );
			final ArrayImg< UnsignedIntType, IntArray > unsignedComp = ArrayImgs.unsignedInts( access, dims );
			testRange( unsignedComp, unsignedRef, 0, nElements );

			final ArrayImg< IntType, IntArray > ref = ArrayImgs.ints( data, dims );
			final ArrayImg< IntType, IntArray > comp = ArrayImgs.ints( access, dims );
			testRange( comp, ref, 0, nElements );

			final ArrayImg< ARGBType, IntArray > argbRef = ArrayImgs.argbs( data, dims );
			final ArrayImg< ARGBType, IntArray > argbComp = ArrayImgs.argbs( access, dims );
			testEquality( argbComp, argbRef );
		}

		// longs
		{
			final long[] data = new long[ nElements ];
			for ( int d = 0; d < nElements; ++d )
				data[ d ] = d;
			final LongArray access = new LongArray( data );

			final ArrayImg< UnsignedLongType, LongArray > unsignedComp = ArrayImgs.unsignedLongs( access, dims );
			final ArrayCursor< UnsignedLongType > u = unsignedComp.cursor();
			for ( int i = 0; i < nElements; ++i )
				Assert.assertEquals( i, u.next().get() );

			final ArrayImg< LongType, LongArray > ref = ArrayImgs.longs( data, dims );
			final ArrayImg< LongType, LongArray > comp = ArrayImgs.longs( access, dims );
			testRange( comp, ref, 0, nElements );
		}

		// floats
		{
			final Random rng = new Random();
			final float[] data = new float[ nElements ];
			for ( int d = 0; d < data.length; ++d )
				data[ d ] = rng.nextFloat();
			final FloatArray access = new FloatArray( data );
			final ArrayImg< FloatType, FloatArray > comp = ArrayImgs.floats( access, dims );
			final ArrayImg< FloatType, FloatArray > ref = ArrayImgs.floats( data, dims );
			testEquality( comp, ref );
			testEquality( comp, data );

			final float[] d0 = new float[ nElements ];
			final float[] d1 = new float[ nElements ];
			final ArrayCursor< FloatType > c = ArrayImgs.floats( d1, dims ).cursor();
			for ( int i = 0; c.hasNext(); ++i )
			{
				final float v = rng.nextFloat();
				d0[ i ] = v;
				c.next().set( v );
			}
			Assert.assertArrayEquals( d0, d1, 0.0f );
			testEquality( ArrayImgs.floats( d1, dims ), d0 );

		}

		// complex floats
		{
			final Random rng = new Random();
			final float[] data = new float[ 2 * nElements ];
			for ( int d = 0; d < data.length; ++d )
				data[ d ] = rng.nextFloat();
			final FloatArray access = new FloatArray( data );

			final ArrayImg< ComplexFloatType, FloatArray > complexComp = ArrayImgs.complexFloats( access, dims );
			final ArrayImg< ComplexFloatType, FloatArray > complexRef = ArrayImgs.complexFloats( data, dims );
			testEquality( complexComp, complexRef );

			final float[] d0 = new float[ 2 * nElements ];
			final float[] d1 = new float[ 2 * nElements ];
			final ArrayCursor< ComplexFloatType > c = ArrayImgs.complexFloats( d1, dims ).cursor();
			for ( int i = 0; c.hasNext(); i += 2 )
			{
				final float v1 = rng.nextFloat();
				final float v2 = rng.nextFloat();
				d0[ i ] = v1;
				d0[ i + 1 ] = v2;
				c.next().setReal( v1 );
				c.get().setImaginary( v2 );
			}
			Assert.assertArrayEquals( d0, d1, 0.0f );
		}

		// doubles
		{
			final Random rng = new Random();
			final double[] data = new double[ nElements ];
			for ( int d = 0; d < data.length; ++d )
				data[ d ] = rng.nextDouble();
			final DoubleArray access = new DoubleArray( data );
			final ArrayImg< DoubleType, DoubleArray > comp = ArrayImgs.doubles( access, dims );
			final ArrayImg< DoubleType, DoubleArray > ref = ArrayImgs.doubles( data, dims );
			testEquality( comp, ref );
			testEquality( comp, data );

			final double[] d0 = new double[ nElements ];
			final double[] d1 = new double[ nElements ];
			final ArrayCursor< DoubleType > c = ArrayImgs.doubles( d1, dims ).cursor();
			for ( int i = 0; c.hasNext(); ++i )
			{
				final double v = rng.nextDouble();
				d0[ i ] = v;
				c.next().set( v );
			}
			Assert.assertArrayEquals( d0, d1, 0.0f );
			testEquality( ArrayImgs.doubles( d1, dims ), d0 );
		}

		// complex doubles
		{
			final Random rng = new Random();
			final double[] data = new double[ 2 * nElements ];
			for ( int d = 0; d < data.length; ++d )
				data[ d ] = rng.nextDouble();
			final DoubleArray access = new DoubleArray( data );

			final ArrayImg< ComplexDoubleType, DoubleArray > complexComp = ArrayImgs.complexDoubles( access, dims );
			final ArrayImg< ComplexDoubleType, DoubleArray > complexRef = ArrayImgs.complexDoubles( data, dims );
			testEquality( complexComp, complexRef );

			final double[] d0 = new double[ 2 * nElements ];
			final double[] d1 = new double[ 2 * nElements ];
			final ArrayCursor< ComplexDoubleType > c = ArrayImgs.complexDoubles( d1, dims ).cursor();
			for ( int i = 0; c.hasNext(); i += 2 )
			{
				final double v1 = rng.nextDouble();
				final double v2 = rng.nextDouble();
				d0[ i ] = v1;
				d0[ i + 1 ] = v2;
				c.next().setReal( v1 );
				c.get().setImaginary( v2 );
			}
			Assert.assertArrayEquals( d0, d1, 0.0 );
		}

		// bits
		{
			final long[] data = new long[] { 0 };
			final int step = 3;
			for ( long start = 0; start < Long.SIZE; start += step )
				data[ 0 ] = data[ 0 ] | 1l << start;
			final ArrayImg< BitType, LongArray > a = ArrayImgs.bits( new LongArray( data ), 8, 4, 2 );
			final ArrayCursor< BitType > c = a.cursor();
			for ( int i = 0; c.hasNext(); ++i )
				Assert.assertEquals( i % 3 == 0, c.next().get() );
		}

	}

	public static < T extends NativeType< T > & IntegerType< T > > void testRange( final ArrayImg< T, ? > comp, final ArrayImg< T, ? > ref, final int start, final int stop )
	{
		final ArrayCursor< T > c = comp.cursor();
		final ArrayCursor< T > r = ref.cursor();
		for ( int s = start; s < stop; ++s )
		{
			Assert.assertEquals( c.next().getInteger(), s );
			Assert.assertEquals( r.next().getInteger(), s );
		}
	}

	public static < T extends NativeType< T > > void testEquality( final ArrayImg< T, ? > comp, final ArrayImg< T, ? > ref )
	{
		for ( ArrayCursor< T > c = comp.cursor(), r = ref.cursor(); c.hasNext(); )
			Assert.assertTrue( c.next().valueEquals( r.next() ) );
	}

	public static void testEquality( final ArrayImg< ? extends RealType< ? >, ? > img, final float[] array )
	{
		Assert.assertEquals( array.length, Intervals.numElements( img ) );

		final ArrayRandomAccess< ? extends RealType< ? > > access = img.randomAccess();
		for ( int i = 0; i < array.length; ++i )
		{
			IntervalIndexer.indexToPosition( i, img, access );
			Assert.assertEquals( array[ i ], access.get().getRealDouble(), 0.0 );
		}
	}

	public static void testEquality( final ArrayImg< ? extends RealType< ? >, ? > img, final double[] array )
	{
		Assert.assertEquals( array.length, Intervals.numElements( img ) );

		final ArrayRandomAccess< ? extends RealType< ? > > access = img.randomAccess();
		for ( int i = 0; i < array.length; ++i )
		{
			IntervalIndexer.indexToPosition( i, img, access );
			Assert.assertEquals( array[ i ], access.get().getRealDouble(), 0.0 );
		}
	}

}
