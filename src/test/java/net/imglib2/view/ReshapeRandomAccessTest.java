/*
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

package net.imglib2.view;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.transform.integer.ReshapeTransform;
import net.imglib2.util.Localizables;

import org.junit.Test;

public class ReshapeRandomAccessTest
{
	private static final long[][][] SHAPES = {
			{ { 6, 4 }, { 24 } },
			{ { 24 }, { 6, 4 } },
			{ { 4, 6 }, { 6, 4 } },
			{ { 2, 3, 4 }, { 4, 6 } },
			{ { 30 }, { 2, 3, 5 } },
			{ { 3, 10 }, { 3, 2, 5 } },
			{ { 1, 6, 1 }, { 2, 1, 3 } },
			{ { 4, 3, 5, 2 }, { 12, 2, 5 } },
			{ { 300, 300, 8, 5 }, { 300, 300, 40 } },
			{ { 90000, 40 }, { 300, 300, 40 } },
			{ { 3600000 }, { 300, 300, 40 } },
	};

	private static ReshapeRandomAccess< Localizable > randomAccess( final ReshapeTransform t )
	{
		final RandomAccessible< Localizable > source = Localizables.randomAccessible( t.numTargetDimensions() );
		return new ReshapeRandomAccess<>( source.randomAccess(), t );
	}

	private static void check( final ReshapeTransform t, final long[] expectedPosition, final RandomAccess< Localizable > ra )
	{
		assertArrayEquals( expectedPosition, ra.positionAsLongArray() );
		final long[] expectedTarget = new long[ t.numTargetDimensions() ];
		t.apply( expectedPosition, expectedTarget );
		assertArrayEquals( t + " at " + new Point( expectedPosition ), expectedTarget, ra.get().positionAsLongArray() );
	}

	@Test
	public void testInitialPosition()
	{
		for ( final long[][] shape : SHAPES )
		{
			final ReshapeTransform t = new ReshapeTransform( shape[ 0 ], shape[ 1 ] );
			check( t, new long[ t.numSourceDimensions() ], randomAccess( t ) );
		}
	}

	@Test
	public void testRandomWalk()
	{
		final Random random = new Random( 3 );
		for ( final long[][] shape : SHAPES )
		{
			final ReshapeTransform t = new ReshapeTransform( shape[ 0 ], shape[ 1 ] );
			final long[] dims = shape[ 0 ];
			final int n = dims.length;
			RandomAccess< Localizable > ra = randomAccess( t );
			final long[] expected = new long[ n ];
			for ( int k = 0; k < 20000; ++k )
			{
				final int d = random.nextInt( n );
				// mostly small steps, sometimes large jumps that may leave the domain
				final long range = random.nextInt( 10 ) == 0 ? 3 * dims[ d ] : Math.min( dims[ d ], 5 );
				final long distance = ( long ) ( ( random.nextDouble() * 2 - 1 ) * range );
				final long[] distances = new long[ n ];
				final long[] positions = new long[ n ];
				for ( int e = 0; e < n; ++e )
				{
					distances[ e ] = random.nextInt( ( int ) Math.min( dims[ e ], 7 ) ) - 3;
					positions[ e ] = random.nextInt( ( int ) Math.min( 3 * dims[ e ], 1000000 ) ) - Math.min( dims[ e ], 300000 );
				}
				switch ( random.nextInt( 14 ) )
				{
				case 0:
					ra.fwd( d );
					++expected[ d ];
					break;
				case 1:
					ra.bck( d );
					--expected[ d ];
					break;
				case 2:
					ra.move( ( int ) distance, d );
					expected[ d ] += distance;
					break;
				case 3:
					ra.move( distance, d );
					expected[ d ] += distance;
					break;
				case 4:
					ra.setPosition( ( int ) positions[ d ], d );
					expected[ d ] = positions[ d ];
					break;
				case 5:
					ra.setPosition( positions[ d ], d );
					expected[ d ] = positions[ d ];
					break;
				case 6:
					ra.move( distances );
					for ( int e = 0; e < n; ++e )
						expected[ e ] += distances[ e ];
					break;
				case 7:
					ra.move( toInt( distances ) );
					for ( int e = 0; e < n; ++e )
						expected[ e ] += distances[ e ];
					break;
				case 8:
					ra.move( Point.wrap( distances ) );
					for ( int e = 0; e < n; ++e )
						expected[ e ] += distances[ e ];
					break;
				case 9:
					ra.setPosition( positions );
					System.arraycopy( positions, 0, expected, 0, n );
					break;
				case 10:
					ra.setPosition( toInt( positions ) );
					System.arraycopy( positions, 0, expected, 0, n );
					break;
				case 11:
					ra.setPosition( Point.wrap( positions ) );
					System.arraycopy( positions, 0, expected, 0, n );
					break;
				case 12:
					ra = ra.copy();
					break;
				case 13:
					// back into the domain
					for ( int e = 0; e < n; ++e )
						ra.setPosition( Math.floorMod( expected[ e ], dims[ e ] ), e );
					for ( int e = 0; e < n; ++e )
						expected[ e ] = Math.floorMod( expected[ e ], dims[ e ] );
					break;
				}
				check( t, expected, ra );
			}
		}
	}

	@Test
	public void testSetPositionToItself()
	{
		final ReshapeTransform t = new ReshapeTransform( new long[] { 4, 6 }, new long[] { 6, 4 } );
		final RandomAccess< Localizable > ra = randomAccess( t );
		ra.setPosition( new long[] { 3, 5 } );
		ra.setPosition( ra );
		check( t, new long[] { 3, 5 }, ra );
		ra.move( ra );
		check( t, new long[] { 6, 10 }, ra );
	}

	@Test
	public void testCopyIsIndependent()
	{
		final ReshapeTransform t = new ReshapeTransform( new long[] { 90000, 40 }, new long[] { 300, 300, 40 } );
		final RandomAccess< Localizable > ra = randomAccess( t );
		ra.setPosition( new long[] { 299, 3 } );
		final RandomAccess< Localizable > copy = ra.copy();
		copy.fwd( 0 );
		copy.fwd( 1 );
		check( t, new long[] { 299, 3 }, ra );
		check( t, new long[] { 300, 4 }, copy );
		ra.bck( 0 );
		check( t, new long[] { 298, 3 }, ra );
		check( t, new long[] { 300, 4 }, copy );
	}

	@Test
	public void testCursorLikeTraversal()
	{
		// A cursor parks at min - 1 in dimension 0, and wraps lines by moving
		// back and fwd in higher dimensions.
		final ReshapeTransform t = new ReshapeTransform( new long[] { 4, 3, 5, 2 }, new long[] { 12, 2, 5 } );
		final RandomAccess< Localizable > ra = randomAccess( t );
		final long[] dims = t.getSourceDimensions();
		final long[] position = new long[ 4 ];
		position[ 0 ] = -1;
		ra.setPosition( position );
		final long size = 4 * 3 * 5 * 2;
		for ( long i = 0; i < size; ++i )
		{
			ra.fwd( 0 );
			++position[ 0 ];
			for ( int d = 0; d < 3 && position[ d ] == dims[ d ]; ++d )
			{
				ra.move( -dims[ d ], d );
				position[ d ] = 0;
				ra.fwd( d + 1 );
				++position[ d + 1 ];
			}
			check( t, position, ra );
		}
	}

	@Test
	public void testNoIntOverflow()
	{
		// a split: moving by 60000 in dimension 1 moves the source by 60000 * 70000
		final ReshapeTransform split = new ReshapeTransform( new long[] { 70000, 70000 }, new long[] { 4900000000L } );
		final RandomAccess< Localizable > ra = randomAccess( split );
		ra.move( 60000, 1 );
		assertEquals( 60000L * 70000, ra.get().getLongPosition( 0 ) );
		ra.setPosition( 65000, 1 );
		assertEquals( 65000L * 70000, ra.get().getLongPosition( 0 ) );
		ra.setPosition( new int[] { 1, 69999 } );
		assertEquals( 69999L * 70000 + 1, ra.get().getLongPosition( 0 ) );

		// a merge
		final ReshapeTransform merge = split.inverse();
		final RandomAccess< Localizable > mra = randomAccess( merge );
		mra.setPosition( 4899999999L, 0 );
		check( merge, new long[] { 4899999999L }, mra );
		mra.move( -2000000000, 0 );
		check( merge, new long[] { 2899999999L }, mra );
		mra.fwd( 0 );
		check( merge, new long[] { 2900000000L }, mra );
		assertTrue( mra.get().getLongPosition( 1 ) < 70000 );
	}

	private static int[] toInt( final long[] a )
	{
		final int[] b = new int[ a.length ];
		for ( int i = 0; i < a.length; ++i )
			b[ i ] = ( int ) a[ i ];
		return b;
	}
}
