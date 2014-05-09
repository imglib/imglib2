/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.LongType;

import org.junit.Test;

/**
 * TODO
 * 
 */
public class IterableIntervalSubsetTest
{
	final static private Random rnd = new Random( 1234567 );

	final static private long[][] sizes =
			new long[][] {
					{ 127 },
					{ 288 },
					{ 135, 111 },
					{ 172, 131 },
					{ 15, 13, 33 },
					{ 110, 38, 30 },
					{ 109, 34, 111 },
					{ 12, 43, 92, 10 },
					{ 21, 34, 29, 13 },
					{ 5, 12, 30, 4, 21 },
					{ 14, 21, 13, 9, 12 }
			};

	final static private void print( final Object str )
	{
		System.out.println( str );
	}

	@Test
	public void testIntervalIterator()
	{
		for ( int i = 0; i < sizes.length; ++i )
		{
			print( "testcase " + i + " ( n=" + sizes[ i ].length + " )" );

			/* create and fill interval */
			long a = 0;
			final IterableInterval< LongType > iterable = new ArrayImgFactory< LongType >().create( sizes[ i ], new LongType() );
			for ( final LongType t : iterable )
				t.set( a++ );

			assertTrue( test( iterable ) );
		}
	}

	final static private boolean test( final IterableInterval< LongType > iterable )
	{
		/* split into iterable interval subsets */
		long b = 0;
		long firstIndex = 0;
		long lastIndex = 0;
		while ( lastIndex < iterable.size() )
		{
			final int size = rnd.nextInt( ( int ) iterable.size() / 5 );
			firstIndex = lastIndex + 1;
			lastIndex = firstIndex + size - 1;

			print( "testing subset [ " + firstIndex + ", " + lastIndex + " ] size=" + size );

			final IterableIntervalSubset< LongType > iterableIntervalSubset = new IterableIntervalSubset< LongType >( iterable, firstIndex, size );
			for ( final LongType t : iterableIntervalSubset )
				if ( ++b != t.get() )
					return false;
		}
		return true;
	}
}
