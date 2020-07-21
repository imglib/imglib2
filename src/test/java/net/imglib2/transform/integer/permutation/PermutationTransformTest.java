/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
/**
 *
 */
package net.imglib2.transform.integer.permutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.TransformView;
import net.imglib2.view.Views;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 */
public class PermutationTransformTest
{
	final static Random rnd = new Random();

	static long[] values;

	static int[] lut;

	static final int width = 5;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		PermutationTransformTest.values = new long[ PermutationTransformTest.width * PermutationTransformTest.width ];
		for ( int i = 0; i < PermutationTransformTest.values.length; ++i )
			PermutationTransformTest.values[ i ] = PermutationTransformTest.rnd.nextLong();

		PermutationTransformTest.lut = new int[ PermutationTransformTest.width ];

		final ArrayList< Integer > indices = new ArrayList< Integer >();
		for ( int i = 0; i < PermutationTransformTest.width; ++i )
			indices.add( i );

		Collections.shuffle( indices );

		for ( int i = 0; i < PermutationTransformTest.width; ++i )
			PermutationTransformTest.lut[ i ] = indices.get( i );
	}

	@Test
	public void test()
	{
		final ArrayImg< LongType, LongArray > img = ArrayImgs.longs( PermutationTransformTest.values, PermutationTransformTest.width, PermutationTransformTest.width );
		final PermutationTransform t = new PermutationTransform( PermutationTransformTest.lut, 2, 2 );
		final TransformView< LongType > bijectivePermutation = new TransformView< LongType >( img, t );
		final TransformView< LongType > inverseBijectivePermutation = new TransformView< LongType >( bijectivePermutation, t.inverse() );
		final IntervalView< LongType > viewTransformed = Views.permuteCoordinatesInverse( img, PermutationTransformTest.lut );
		final IntervalView< LongType > identity = Views.permuteCoordinates( viewTransformed, PermutationTransformTest.lut );

		final RandomAccess< LongType > reference = img.randomAccess();
		final RandomAccess< LongType > result = Views.interval( inverseBijectivePermutation, img ).randomAccess();

		for ( int i = 0; i < 1000; ++i )
		{
			final long[] x = new long[] { PermutationTransformTest.rnd.nextInt( PermutationTransformTest.width ), PermutationTransformTest.rnd.nextInt( PermutationTransformTest.width ) };
			reference.setPosition( x );
			result.setPosition( x );
			Assert.assertEquals( reference.get().get(), result.get().get() );
		}

		{
			final Cursor< LongType > v = Views.flatIterable( viewTransformed ).cursor();
			final Cursor< LongType > b = Views.flatIterable( Views.interval( bijectivePermutation, img ) ).cursor();
			final Cursor< LongType > i = Views.flatIterable( identity ).cursor();
			final ArrayRandomAccess< LongType > r = img.randomAccess();

			while ( v.hasNext() )
			{
				b.fwd();
				v.fwd();
				for ( int d = 0; d < r.numDimensions(); ++d )
					r.setPosition( PermutationTransformTest.lut[ b.getIntPosition( d ) ], d ); // manually
				// permute
				// source
				// image
				// coordinate

				Assert.assertEquals( r.get().get(), b.get().get() );
				Assert.assertEquals( b.get().get(), v.get().get() );

				i.fwd();
				r.setPosition( i );

				Assert.assertEquals( r.get().get(), i.get().get() );

			}
		}

		{
			int i = 0;
			for ( final LongType l : Views.flatIterable( Views.interval( inverseBijectivePermutation, img ) ) )
				Assert.assertEquals( PermutationTransformTest.values[ i++ ], l.get() );
		}
	}
}
