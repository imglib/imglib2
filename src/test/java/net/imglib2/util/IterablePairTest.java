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

package net.imglib2.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * Tests {@link IterablePair}.
 *
 * @author Curtis Rueden
 * @author Ellen T Arena
 */
public class IterablePairTest
{
	/**
	 * Tests {@link IterablePair#iterator()}.
	 */
	@Test
	public void testIterator()
	{
		final List< Integer > iter1 = Arrays.asList( 3, 5, 6, 1, 2, 3 );
		final List< String > iter2 = Arrays.asList( "dog", "cat", "mouse", "fox" );
		final IterablePair< Integer, String > pair = new IterablePair< >( iter1, iter2 );
		final Iterator< Pair< Integer, String > > iterPair = pair.iterator();
		assertNotNull( iterPair );
		assertNextItems( iterPair, 3, "dog" );
		assertNextItems( iterPair, 5, "cat" );
		assertNextItems( iterPair, 6, "mouse" );
		assertNextItems( iterPair, 1, "fox" );
		assertFalse( iterPair.hasNext() );
		try
		{
			final Pair< Integer, String > next = iterPair.next();
			fail( "Next element not expected: " + next );
		}
		catch ( final NoSuchElementException exc )
		{
			// expected behavior
		}
	}

	/**
	 * Tests iteration with null inputs.
	 */
	@Test( expected = NullPointerException.class )
	public void testNulls()
	{
		final IterablePair< Integer, Integer > pair = new IterablePair< >( null, null );
		pair.iterator().next();
	}

	/**
	 * Tests iteration with empty inputs.
	 */
	@Test( expected = NoSuchElementException.class )
	public void testEmpty()
	{
		final List< Integer > iter1 = Collections.emptyList();
		final List< Integer > iter2 = Collections.emptyList();
		final IterablePair< Integer, Integer > pair = new IterablePair< >( iter1, iter2 );
		pair.iterator().next();
	}

	/**
	 * Tests iteration when two inputs are the same reference.
	 */
	@Test
	public void testSameIterable()
	{
		final List< Integer > iter = Arrays.asList( 3, 5, 6, 1, 2, 3 );
		final IterablePair< Integer, Integer > pair = new IterablePair< >( iter, iter );
		final Iterator< Pair< Integer, Integer > > iterPair = pair.iterator();
		assertNotNull( iterPair );
		int count = 0;
		for ( final Integer i : iter )
		{
			final Pair< Integer, Integer > p = iterPair.next();
			assertEquals( i, p.getA() );
			assertEquals( i, p.getB() );
			count++;
		}
		assertEquals( iter.size(), count );
	}

	// -- Helper methods --

	private void assertNextItems( final Iterator< Pair< Integer, String > > iter, final Integer i, final String s )
	{
		assertTrue( iter.hasNext() );
		final Pair< Integer, String > pair1 = iter.next();
		assertEquals( i, pair1.getA() );
		assertEquals( s, pair1.getB() );
	}
}
