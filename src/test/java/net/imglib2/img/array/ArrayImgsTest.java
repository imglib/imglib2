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
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class ArrayImgsTest
{

	private final long[] dims = { 4, 5 };

	private final int nElements = ( int ) Intervals.numElements( dims );

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
