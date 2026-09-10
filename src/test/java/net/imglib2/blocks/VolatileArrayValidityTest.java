/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.blocks;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import net.imglib2.type.PrimitiveType;

import org.junit.Test;

/**
 * The {@link Validity} field of a {@link VolatileArray}, and how {@link
 * VolatileArray#valid()} and {@link VolatileArray#validForWriting()} resolve it.
 */
public class VolatileArrayValidityTest
{
	/** 2 x 2 ranges of widths {2,3} x {1,2}, so 5 x 3 == 15 pixels. */
	private static final int[][] WIDTHS = { { 2, 3 }, { 1, 2 } };

	private static RangeValidity rangeValidity()
	{
		return new RangeValidity( WIDTHS, new byte[] { 1, 0, 0, 1 } );
	}

	private static byte[] expandedRangeValidity()
	{
		return new byte[] {
				1, 1, 0, 0, 0,
				0, 0, 1, 1, 1,
				0, 0, 1, 1, 1 };
	}

	private static VolatileArray< byte[] > array( final int length )
	{
		return new VolatileArray<>( new byte[ length ], new byte[ length ] );
	}

	/**
	 * {@link VolatileArray#getValidity()} must describe the same thing as
	 * {@link VolatileArray#valid()}. In particular a {@code Validity} that was
	 * set before a write through {@link VolatileArray#validForWriting()} must
	 * not survive that write, because it describes the pre-write state.
	 */
	private static void assertValidityAgreesWithValid( final VolatileArray< byte[] > a )
	{
		final byte[] valid = a.valid();
		final Validity v = a.getValidity();

		final byte[] expanded = new byte[ v.length() ];
		v.expandTo( expanded );
		assertArrayEquals( "getValidity() disagrees with valid()",
				java.util.Arrays.copyOf( valid, v.length() ), expanded );

		boolean all = true;
		for ( int i = 0; i < v.length(); ++i )
			all &= valid[ i ] != 0;
		assertEquals( "allValid() disagrees with valid()", all, v.allValid() );
	}

	@Test
	public void nullValidityReturnsOwnBuffer()
	{
		final VolatileArray< byte[] > a = array( 15 );
		final byte[] own = a.valid();
		assertSame( own, a.valid() );
	}

	@Test
	public void getValidityInstallsDenseOverOwnBuffer()
	{
		final VolatileArray< byte[] > a = array( 15 );
		final byte[] own = a.valid();
		own[ 3 ] = 1;

		final Validity v = a.getValidity();
		assertEquals( 15, v.length() );
		assertFalse( v.allValid() );
		// same object on a second call, and still the own buffer
		assertSame( v, a.getValidity() );
		assertSame( own, a.valid() );
	}

	/**
	 * A {@code VolatileArray} handed out by a {@code TempArray} can have a
	 * validity buffer longer than its pixel count. The installed {@code
	 * DenseValidity} must not look at the slop.
	 */
	@Test
	public void getValidityIgnoresSlopBeyondLength()
	{
		final TempArray< VolatileArray< byte[] > > tempArray =
				TempArray.forPrimitiveType( PrimitiveType.BYTE, true );

		final VolatileArray< byte[] > big = tempArray.get( 15 );
		assertEquals( 15, big.capacity() );
		// mark everything valid, then hand out a shorter block from the same buffer
		final byte[] own = big.valid();
		java.util.Arrays.fill( own, ( byte ) 1 );

		final VolatileArray< byte[] > small = tempArray.get( 4 );
		assertSame( big, small );
		assertEquals( "buffer is reused, not reallocated", 15, small.capacity() );

		// slop (indices 4..14) is all 1 but must not be counted
		small.valid()[ 2 ] = 0;
		final Validity v = small.getValidity();
		assertEquals( 4, v.length() );
		assertFalse( "must not scan past length", v.allValid() );

		final byte[] expanded = new byte[ 4 ];
		v.expandTo( expanded );
		assertArrayEquals( new byte[] { 1, 1, 0, 1 }, expanded );
	}

	@Test
	public void rangeValidityIsExpandedLazilyIntoOwnBuffer()
	{
		final VolatileArray< byte[] > a = array( 15 );
		final byte[] own = a.valid();

		a.setValidity( rangeValidity() );
		final byte[] valid = a.valid();
		assertSame( "expands into the own buffer", own, valid );
		assertArrayEquals( expandedRangeValidity(), valid );

		// second call does not re-expand; scribbling on the buffer survives
		valid[ 0 ] = 7;
		assertEquals( 7, a.valid()[ 0 ] );
	}

	@Test
	public void denseValidityOverForeignBufferIsReturnedAsIs()
	{
		final VolatileArray< byte[] > a = array( 15 );
		final VolatileArray< byte[] > b = array( 15 );
		final byte[] foreign = b.valid();
		foreign[ 5 ] = 1;

		a.setValidity( b.getValidity() );
		assertSame( "no copy on setValidity", foreign, a.valid() );
	}

	@Test
	public void validForWritingOnNullValidityDoesNotCopy()
	{
		final VolatileArray< byte[] > a = array( 15 );
		final byte[] own = a.valid();
		own[ 1 ] = 1;

		assertSame( own, a.validForWriting() );
		assertEquals( "contents untouched", 1, own[ 1 ] );
	}

	@Test
	public void validForWritingCopiesForeignBuffer()
	{
		final VolatileArray< byte[] > a = array( 15 );
		final VolatileArray< byte[] > b = array( 15 );
		final byte[] foreign = b.valid();
		java.util.Arrays.fill( foreign, ( byte ) 1 );

		a.setValidity( b.getValidity() );
		final byte[] writable = a.validForWriting();

		assertNotSame( "must be our own buffer", foreign, writable );
		assertArrayEquals( "existing validity preserved", foreign, writable );

		// writing must not reach the foreign buffer
		writable[ 0 ] = 0;
		assertEquals( 1, foreign[ 0 ] );
		assertValidityAgreesWithValid( a );
	}

	@Test
	public void validForWritingExpandsRangeValidity()
	{
		final VolatileArray< byte[] > a = array( 15 );
		a.setValidity( rangeValidity() );

		final byte[] writable = a.validForWriting();
		assertArrayEquals( expandedRangeValidity(), writable );

		// the RangeValidity describes the pre-write state, so it must not
		// survive a write through the returned buffer
		writable[ 0 ] = 0;
		assertValidityAgreesWithValid( a );
	}

	/**
	 * A write through {@link VolatileArray#validForWriting()} must still be
	 * there after the validity is forwarded to another {@code VolatileArray},
	 * which is what a pass-through stage does.
	 */
	@Test
	public void writeSurvivesForwardingTheValidity()
	{
		final VolatileArray< byte[] > src = array( 15 );
		src.setValidity( rangeValidity() );
		src.validForWriting()[ 0 ] = 0;

		final VolatileArray< byte[] > dst = array( 15 );
		dst.setValidity( src.getValidity() );

		assertEquals( "forwarded validity lost the write", 0, dst.valid()[ 0 ] );
		assertValidityAgreesWithValid( dst );
	}

	@Test
	public void validForWritingLeavesOwnBufferAsTheValidity()
	{
		final VolatileArray< byte[] > a = array( 15 );
		final VolatileArray< byte[] > b = array( 15 );
		a.setValidity( b.getValidity() );

		final byte[] writable = a.validForWriting();
		java.util.Arrays.fill( writable, ( byte ) 1 );

		assertSame( writable, a.valid() );
		final Validity v = a.getValidity();
		assertTrue( v.allValid() );
		assertEquals( 15, v.length() );
		assertValidityAgreesWithValid( a );
	}

	@Test
	public void tempArrayResetsValidity()
	{
		final TempArray< VolatileArray< byte[] > > tempArray =
				TempArray.forPrimitiveType( PrimitiveType.BYTE, true );

		final VolatileArray< byte[] > a = tempArray.get( 15 );
		a.setValidity( rangeValidity() );
		assertArrayEquals( expandedRangeValidity(), a.valid() );

		final VolatileArray< byte[] > again = tempArray.get( 15 );
		assertSame( a, again );
		// validity forgotten: valid() is the own buffer, not a fresh expansion
		assertSame( a.valid(), again.validForWriting() );
		assertEquals( 15, again.getValidity().length() );
	}

	@Test
	public void denseValidityRejectsTooSmallBackingArray()
	{
		try
		{
			new DenseValidity( new byte[ 3 ], 4 );
			org.junit.Assert.fail( "expected IllegalArgumentException" );
		}
		catch ( final IllegalArgumentException expected )
		{}
	}
}
