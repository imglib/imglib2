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

package net.imglib2.transform.integer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;
import net.imglib2.util.Localizables;

import org.junit.Test;

public class ReshapeTransformTest
{
	/**
	 * Pairs of (view, source) shapes that are small enough to test
	 * exhaustively.
	 */
	private static final long[][][] SHAPES = {
			{ { 6, 4 }, { 24 } },
			{ { 24 }, { 6, 4 } },
			{ { 2, 3 }, { 3, 2 } },
			{ { 4, 6 }, { 6, 4 } },
			{ { 2, 3, 4 }, { 6, 4 } },
			{ { 2, 3, 4 }, { 4, 6 } },
			{ { 2, 3, 5 }, { 6, 5 } },
			{ { 30 }, { 2, 3, 5 } },
			{ { 3, 10 }, { 3, 2, 5 } },
			{ { 1, 6, 1 }, { 2, 1, 3 } },
			{ { 3, 1, 4 }, { 3, 4 } },
			{ { 3, 4 }, { 1, 3, 1, 4, 1 } },
			{ { 1, 1 }, { 1 } },
			{ { 5 }, { 5 } },
			{ { 4, 3, 5, 2 }, { 12, 2, 5 } },
	};

	@Test
	public void testGroups()
	{
		assertEquals( "ReshapeTransform{[90000, 40]->[300, 300, 40]: [90000]<->[300, 300] | [40]<->[40]}",
				new ReshapeTransform( new long[] { 90000, 40 }, new long[] { 300, 300, 40 } ).toString() );
		assertEquals( "ReshapeTransform{[3600000]->[300, 300, 40]: [3600000]<->[300, 300, 40]}",
				new ReshapeTransform( new long[] { 3600000 }, new long[] { 300, 300, 40 } ).toString() );
		assertEquals( "ReshapeTransform{[300, 300, 8, 5]->[300, 300, 40]: [300]<->[300] | [300]<->[300] | [8, 5]<->[40]}",
				new ReshapeTransform( new long[] { 300, 300, 8, 5 }, new long[] { 300, 300, 40 } ).toString() );
		assertEquals( "ReshapeTransform{[2, 3]->[3, 2]: [2, 3]<->[3, 2]}",
				new ReshapeTransform( new long[] { 2, 3 }, new long[] { 3, 2 } ).toString() );
		assertEquals( "ReshapeTransform{[1, 6, 1]->[2, 1, 3]: [6]<->[2, 3]}",
				new ReshapeTransform( new long[] { 1, 6, 1 }, new long[] { 2, 1, 3 } ).toString() );
		assertEquals( "ReshapeTransform{[1, 1]->[1]:}",
				new ReshapeTransform( new long[] { 1, 1 }, new long[] { 1 } ).toString() );

		final ReshapeTransform t = new ReshapeTransform( new long[] { 3, 1, 10 }, new long[] { 3, 2, 5 } );
		assertEquals( 2, t.numGroups() );
		assertArrayEquals( new int[] { 0 }, t.getGroupSourceAxes( 0 ) );
		assertArrayEquals( new int[] { 0 }, t.getGroupTargetAxes( 0 ) );
		assertArrayEquals( new int[] { 2 }, t.getGroupSourceAxes( 1 ) );
		assertArrayEquals( new int[] { 1, 2 }, t.getGroupTargetAxes( 1 ) );
		assertEquals( -1, t.getSourceGroup( 1 ) );
		assertEquals( 0, t.getSourceStride( 1 ) );
		assertEquals( 1, t.getTargetStride( 1 ) );
		assertEquals( 2, t.getTargetStride( 2 ) );
	}

	@Test
	public void testApplyInDomain()
	{
		for ( final long[][] shape : SHAPES )
		{
			final long[] viewDims = shape[ 0 ];
			final long[] sourceDims = shape[ 1 ];
			final ReshapeTransform t = new ReshapeTransform( viewDims, sourceDims );
			final long[] view = new long[ viewDims.length ];
			final long[] expected = new long[ sourceDims.length ];
			final long[] actual = new long[ sourceDims.length ];
			final int[] viewInt = new int[ viewDims.length ];
			final int[] actualInt = new int[ sourceDims.length ];
			final Point actualPoint = new Point( sourceDims.length );
			final long size = Intervals.numElements( viewDims );
			for ( long i = 0; i < size; ++i )
			{
				IntervalIndexer.indexToPosition( i, viewDims, view );
				IntervalIndexer.indexToPosition( i, sourceDims, expected );
				t.apply( view, actual );
				assertArrayEquals( t + " at " + Arrays.toString( view ), expected, actual );

				for ( int d = 0; d < view.length; ++d )
					viewInt[ d ] = ( int ) view[ d ];
				t.apply( viewInt, actualInt );
				assertArrayEquals( expected, Arrays.stream( actualInt ).asLongStream().toArray() );

				t.apply( Point.wrap( view ), actualPoint );
				assertArrayEquals( expected, actualPoint.positionAsLongArray() );

				// inverse
				final long[] back = new long[ viewDims.length ];
				t.applyInverse( back, expected );
				assertArrayEquals( view, back );
			}
		}
	}

	/**
	 * Outside the domain, within every group the flat index is preserved and
	 * all but the outermost target dimension stay inside their bounds. This
	 * characterizes the result uniquely.
	 */
	@Test
	public void testApplyOffDomain()
	{
		final Random random = new Random( 42 );
		for ( final long[][] shape : SHAPES )
		{
			final long[] viewDims = shape[ 0 ];
			final long[] sourceDims = shape[ 1 ];
			final ReshapeTransform t = new ReshapeTransform( viewDims, sourceDims );
			final long[] view = new long[ viewDims.length ];
			final long[] target = new long[ sourceDims.length ];
			for ( int k = 0; k < 1000; ++k )
			{
				for ( int d = 0; d < view.length; ++d )
					view[ d ] = random.nextInt( ( int ) ( 5 * viewDims[ d ] ) ) - 2 * viewDims[ d ];
				t.apply( view, target );
				checkOffDomain( t, view, target );
			}
		}
	}

	private static void checkOffDomain( final ReshapeTransform t, final long[] view, final long[] target )
	{
		for ( int d = 0; d < t.numTargetDimensions(); ++d )
			if ( t.getTargetDimension( d ) == 1 )
				assertEquals( 0, target[ d ] );
		for ( int g = 0; g < t.numGroups(); ++g )
		{
			long fv = 0;
			for ( final int d : t.getGroupSourceAxes( g ) )
				fv += t.getSourceStride( d ) * view[ d ];
			long ft = 0;
			final int[] tAxes = t.getGroupTargetAxes( g );
			for ( int i = 0; i < tAxes.length; ++i )
			{
				final int d = tAxes[ i ];
				ft += t.getTargetStride( d ) * target[ d ];
				if ( i < tAxes.length - 1 )
					assertTrue( target[ d ] >= 0 && target[ d ] < t.getTargetDimension( d ) );
			}
			assertEquals( fv, ft );
		}
	}

	@Test
	public void testConcatenate()
	{
		final ReshapeTransform ab = new ReshapeTransform( new long[] { 2, 3, 4 }, new long[] { 6, 4 } );
		final ReshapeTransform bc = new ReshapeTransform( new long[] { 6, 4 }, new long[] { 24 } );
		assertTrue( bc.isConcatenable( ab ) );
		assertFalse( ab.isConcatenable( bc ) );
		assertEquals( new ReshapeTransform( new long[] { 2, 3, 4 }, new long[] { 24 } ), bc.concatenate( ab ) );
		assertThrows( IllegalArgumentException.class, () -> ab.concatenate( bc ) );

		// same number of elements, but different dimensions
		final ReshapeTransform other = new ReshapeTransform( new long[] { 24 }, new long[] { 4, 6 } );
		assertFalse( other.isConcatenable( ab ) );
		assertThrows( IllegalArgumentException.class, () -> other.concatenate( ab ) );
	}

	@Test
	public void testInverse()
	{
		final ReshapeTransform t = new ReshapeTransform( new long[] { 2, 3, 4 }, new long[] { 6, 4 } );
		final ReshapeTransform inverse = t.inverse();
		assertEquals( new ReshapeTransform( new long[] { 6, 4 }, new long[] { 2, 3, 4 } ), inverse );
		assertSame( t, inverse.inverse() );
		assertSame( inverse, t.inverse() );
	}

	@Test
	public void testEqualsHashCode()
	{
		final ReshapeTransform a = new ReshapeTransform( new long[] { 2, 3 }, new long[] { 6 } );
		final ReshapeTransform b = new ReshapeTransform( new long[] { 2, 3 }, new long[] { 6 } );
		assertEquals( a, b );
		assertEquals( a.hashCode(), b.hashCode() );
		assertNotEquals( a, new ReshapeTransform( new long[] { 3, 2 }, new long[] { 6 } ) );
	}

	@Test
	public void testIdentityAndSingletonOnly()
	{
		assertTrue( new ReshapeTransform( new long[] { 5, 4 }, new long[] { 5, 4 } ).isIdentity() );
		assertTrue( new ReshapeTransform( new long[] { 5, 4 }, new long[] { 5, 4 } ).isSingletonOnly() );
		assertFalse( new ReshapeTransform( new long[] { 5, 4 }, new long[] { 5, 1, 4 } ).isIdentity() );
		assertTrue( new ReshapeTransform( new long[] { 5, 4 }, new long[] { 5, 1, 4 } ).isSingletonOnly() );
		assertTrue( new ReshapeTransform( new long[] { 1, 1 }, new long[] { 1 } ).isSingletonOnly() );
		assertFalse( new ReshapeTransform( new long[] { 20 }, new long[] { 5, 4 } ).isSingletonOnly() );
		assertFalse( new ReshapeTransform( new long[] { 4, 5 }, new long[] { 5, 4 } ).isSingletonOnly() );
		assertThrows( IllegalStateException.class, () -> new ReshapeTransform( new long[] { 20 }, new long[] { 5, 4 } ).toMixed() );
	}

	@Test
	public void testToMixed()
	{
		final Random random = new Random( 1 );
		final long[][][] shapes = {
				{ { 3, 1, 4 }, { 3, 4 } },
				{ { 3, 4 }, { 1, 3, 1, 4, 1 } },
				{ { 1, 5, 1 }, { 5 } },
				{ { 1, 1 }, { 1 } },
		};
		for ( final long[][] shape : shapes )
		{
			final ReshapeTransform t = new ReshapeTransform( shape[ 0 ], shape[ 1 ] );
			final MixedTransform mixed = t.toMixed();
			final long[] view = new long[ shape[ 0 ].length ];
			final long[] expected = new long[ shape[ 1 ].length ];
			final long[] actual = new long[ shape[ 1 ].length ];
			for ( int k = 0; k < 200; ++k )
			{
				for ( int d = 0; d < view.length; ++d )
					view[ d ] = random.nextInt( 20 ) - 10;
				t.apply( view, expected );
				mixed.apply( view, actual );
				assertArrayEquals( expected, actual );
			}
		}
	}

	@Test
	public void testBoundingBox()
	{
		final Random random = new Random( 7 );
		for ( final long[][] shape : SHAPES )
		{
			final long[] viewDims = shape[ 0 ];
			final long[] sourceDims = shape[ 1 ];
			final ReshapeTransform t = new ReshapeTransform( viewDims, sourceDims );
			final int n = viewDims.length;
			final int m = sourceDims.length;
			for ( int k = 0; k < 300; ++k )
			{
				final long[] min = new long[ n ];
				final long[] max = new long[ n ];
				final boolean inDomain = k % 2 == 0;
				for ( int d = 0; d < n; ++d )
				{
					final long a, b;
					if ( inDomain )
					{
						a = random.nextInt( ( int ) viewDims[ d ] );
						b = random.nextInt( ( int ) viewDims[ d ] );
					}
					else
					{
						a = random.nextInt( ( int ) ( 3 * viewDims[ d ] ) ) - viewDims[ d ];
						b = random.nextInt( ( int ) ( 3 * viewDims[ d ] ) ) - viewDims[ d ];
					}
					min[ d ] = Math.min( a, b );
					max[ d ] = Math.max( a, b );
				}

				final long[] tmin = new long[ m ];
				final long[] tmax = new long[ m ];
				t.boundingBox( min, max, tmin, tmax );

				// brute force bounding box of all target positions
				final long[] bmin = new long[ m ];
				final long[] bmax = new long[ m ];
				Arrays.fill( bmin, Long.MAX_VALUE );
				Arrays.fill( bmax, Long.MIN_VALUE );
				final long[] target = new long[ m ];
				for ( final Localizable p : Localizables.randomAccessibleInterval( new FinalInterval( min, max ) ) )
				{
					t.apply( p.positionAsLongArray(), target );
					for ( int d = 0; d < m; ++d )
					{
						bmin[ d ] = Math.min( bmin[ d ], target[ d ] );
						bmax[ d ] = Math.max( bmax[ d ], target[ d ] );
					}
				}
				for ( int d = 0; d < m; ++d )
				{
					assertTrue( t + " box " + Arrays.toString( min ) + Arrays.toString( max ), tmin[ d ] <= bmin[ d ] );
					assertTrue( t + " box " + Arrays.toString( min ) + Arrays.toString( max ), tmax[ d ] >= bmax[ d ] );
				}

				// exact if all positions map into the box one-to-one
				if ( inDomain && Intervals.numElements( new FinalInterval( tmin, tmax ) ) == Intervals.numElements( new FinalInterval( min, max ) ) )
				{
					assertArrayEquals( bmin, tmin );
					assertArrayEquals( bmax, tmax );
				}
			}

			// the full interval maps to the full interval
			final long[] tmin = new long[ m ];
			final long[] tmax = new long[ m ];
			t.boundingBox( new long[ n ], Arrays.stream( viewDims ).map( x -> x - 1 ).toArray(), tmin, tmax );
			assertArrayEquals( new long[ m ], tmin );
			assertArrayEquals( Arrays.stream( sourceDims ).map( x -> x - 1 ).toArray(), tmax );
		}
	}

	@Test
	public void testBoundingBoxSingleRunIsExact()
	{
		// rows 1..2 of a 6x4 view of 24: flat range [6, 17]
		final ReshapeTransform t = new ReshapeTransform( new long[] { 6, 4 }, new long[] { 24 } );
		final long[] tmin = new long[ 1 ];
		final long[] tmax = new long[ 1 ];
		t.boundingBox( new long[] { 0, 1 }, new long[] { 5, 2 }, tmin, tmax );
		assertArrayEquals( new long[] { 6 }, tmin );
		assertArrayEquals( new long[] { 17 }, tmax );

		// merge: a run of 24 that stays within one row of the 24x5 source
		final ReshapeTransform merge = new ReshapeTransform( new long[] { 120 }, new long[] { 24, 5 } );
		final long[] mmin = new long[ 2 ];
		final long[] mmax = new long[ 2 ];
		merge.boundingBox( new long[] { 48 }, new long[] { 71 }, mmin, mmax );
		assertArrayEquals( new long[] { 0, 2 }, mmin );
		assertArrayEquals( new long[] { 23, 2 }, mmax );

		// straddling a row boundary spans full rows
		merge.boundingBox( new long[] { 20 }, new long[] { 30 }, mmin, mmax );
		assertArrayEquals( new long[] { 0, 0 }, mmin );
		assertArrayEquals( new long[] { 23, 1 }, mmax );
	}

	@Test
	public void testTransformBoundingBoxWithFlippedCorners()
	{
		final ReshapeTransform t = new ReshapeTransform( new long[] { 6, 4 }, new long[] { 24 } );
		final BoundingBox box = new BoundingBox( new long[] { 5, 2 }, new long[] { 0, 1 } );
		final BoundingBox result = t.transform( box );
		assertArrayEquals( new long[] { 6 }, result.corner1 );
		assertArrayEquals( new long[] { 17 }, result.corner2 );
	}

	@Test
	public void testInvalidDimensions()
	{
		assertThrows( IllegalArgumentException.class, () -> new ReshapeTransform( new long[] { 5, 4 }, new long[] { 21 } ) );
		assertThrows( IllegalArgumentException.class, () -> new ReshapeTransform( new long[] { 5, 0 }, new long[] { 0 } ) );
		assertThrows( IllegalArgumentException.class, () -> new ReshapeTransform( new long[] { -2, -3 }, new long[] { 6 } ) );
	}
}
