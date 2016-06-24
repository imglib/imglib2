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

package net.imglib2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * TODO
 * 
 */
public class PointTest
{

	@Test
	public void testPointInt()
	{
		final Point p = new Point( 3 );
		assertEquals( p.numDimensions(), 3 );
	}

	@Test
	public void testPointLongArray()
	{
		final Point p = new Point( new long[] { 5, 3 } );
		assertEquals( p.numDimensions(), 2 );
		assertEquals( p.getLongPosition( 0 ), 5 );
		assertEquals( p.getLongPosition( 1 ), 3 );
	}

	@Test
	public void testPointIntArray()
	{
		final Point p = new Point( new int[] { 5, 3 } );
		assertEquals( p.numDimensions(), 2 );
		assertEquals( p.getLongPosition( 0 ), 5 );
		assertEquals( p.getLongPosition( 1 ), 3 );
	}

	@Test
	public void testPointLocalizable()
	{
		final Point p = new Point( new Point( new int[] { 15, 2, 1 } ) );
		assertEquals( p.numDimensions(), 3 );
		assertEquals( p.getLongPosition( 0 ), 15 );
		assertEquals( p.getLongPosition( 1 ), 2 );
		assertEquals( p.getLongPosition( 2 ), 1 );
	}

	@Test
	public void testLocalizeFloatArray()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final float[] result = new float[ 3 ];
		final Point p = new Point( initial );
		p.localize( result );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( initial[ i ], result[ i ], 0 );
		}
	}

	@Test
	public void testLocalizeDoubleArray()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final double[] result = new double[ 3 ];
		final Point p = new Point( initial );
		p.localize( result );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( initial[ i ], result[ i ], 0 );
		}
	}

	@Test
	public void testGetFloatPosition()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final Point p = new Point( initial );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( initial[ i ], p.getFloatPosition( i ), 0 );
		}
	}

	@Test
	public void testGetDoublePosition()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final Point p = new Point( initial );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( initial[ i ], p.getDoublePosition( i ), 0 );
		}
	}

	@Test
	public void testNumDimensions()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final Point p = new Point( initial );
		assertEquals( p.numDimensions(), 3 );
	}

	@Test
	public void testFwd()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		for ( int j = 0; j < initial.length; j++ )
		{
			final Point p = new Point( initial );
			p.fwd( j );
			for ( int i = 0; i < initial.length; i++ )
			{
				if ( i == j )
				{
					assertEquals( p.getLongPosition( i ), initial[ i ] + 1 );
				}
				else
				{
					assertEquals( initial[ i ], p.getLongPosition( i ), 0 );
				}
			}
		}
	}

	@Test
	public void testBck()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		for ( int j = 0; j < initial.length; j++ )
		{
			final Point p = new Point( initial );
			p.bck( j );
			for ( int i = 0; i < initial.length; i++ )
			{
				if ( i == j )
				{
					assertEquals( p.getLongPosition( i ), initial[ i ] - 1 );
				}
				else
				{
					assertEquals( initial[ i ], p.getLongPosition( i ), 0 );
				}
			}
		}
	}

	@Test
	public void testMoveIntInt()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final int[] displacement = new int[] { 85, 8643, -973 };
		for ( int j = 0; j < initial.length; j++ )
		{
			final Point p = new Point( initial );
			p.move( displacement[ j ], j );
			for ( int i = 0; i < initial.length; i++ )
			{
				if ( i == j )
				{
					assertEquals( p.getLongPosition( i ), initial[ i ] + displacement[ i ] );
				}
				else
				{
					assertEquals( initial[ i ], p.getLongPosition( i ), 0 );
				}
			}
		}
	}

	@Test
	public void testMoveLongInt()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final long[] displacement = new long[] { 85, 8643, -973 };
		for ( int j = 0; j < initial.length; j++ )
		{
			final Point p = new Point( initial );
			p.move( displacement[ j ], j );
			for ( int i = 0; i < initial.length; i++ )
			{
				if ( i == j )
				{
					assertEquals( p.getLongPosition( i ), initial[ i ] + displacement[ i ] );
				}
				else
				{
					assertEquals( initial[ i ], p.getLongPosition( i ), 0 );
				}
			}
		}
	}

	@Test
	public void testMoveLocalizable()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final long[] displacement = new long[] { 85, 8643, -973 };
		final Point p = new Point( initial );
		final Point d = new Point( displacement );
		p.move( d );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( p.getLongPosition( i ), initial[ i ] + displacement[ i ] );
		}
	}

	@Test
	public void testMoveIntArray()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final int[] displacement = new int[] { 85, 8643, -973 };
		final Point p = new Point( initial );
		p.move( displacement );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( p.getLongPosition( i ), initial[ i ] + displacement[ i ] );
		}
	}

	@Test
	public void testMoveLongArray()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final long[] displacement = new long[] { 85, 8643, -973 };
		final Point p = new Point( initial );
		p.move( displacement );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( p.getLongPosition( i ), initial[ i ] + displacement[ i ] );
		}
	}

	@Test
	public void testSetPositionLocalizable()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final long[] displacement = new long[] { 85, 8643, -973 };
		final Point p = new Point( initial );
		final Point d = new Point( displacement );
		p.setPosition( d );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( p.getLongPosition( i ), displacement[ i ] );
		}
	}

	@Test
	public void testSetPositionIntArray()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final int[] displacement = new int[] { 85, 8643, -973 };
		final Point p = new Point( initial );
		p.setPosition( displacement );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( p.getLongPosition( i ), displacement[ i ] );
		}
	}

	@Test
	public void testSetPositionLongArray()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final long[] displacement = new long[] { 85, 8643, -973 };
		final Point p = new Point( initial );
		p.setPosition( displacement );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( p.getLongPosition( i ), displacement[ i ] );
		}
	}

	@Test
	public void testSetPositionIntInt()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final int[] displacement = new int[] { 85, 8643, -973 };
		for ( int j = 0; j < initial.length; j++ )
		{
			final Point p = new Point( initial );
			p.setPosition( displacement[ j ], j );
			for ( int i = 0; i < initial.length; i++ )
			{
				if ( i == j )
				{
					assertEquals( p.getLongPosition( i ), displacement[ i ] );
				}
				else
				{
					assertEquals( initial[ i ], p.getLongPosition( i ) );
				}
			}
		}
	}

	@Test
	public void testSetPositionLongInt()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final long[] displacement = new long[] { 85, 8643, -973 };
		for ( int j = 0; j < initial.length; j++ )
		{
			final Point p = new Point( initial );
			p.setPosition( displacement[ j ], j );
			for ( int i = 0; i < initial.length; i++ )
			{
				if ( i == j )
				{
					assertEquals( p.getLongPosition( i ), displacement[ i ] );
				}
				else
				{
					assertEquals( initial[ i ], p.getLongPosition( i ) );
				}
			}
		}
	}

	@Test
	public void testLocalizeIntArray()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final Point p = new Point( initial );
		final int[] result = new int[ 3 ];
		p.localize( result );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( p.getLongPosition( i ), initial[ i ] );
		}
	}

	@Test
	public void testLocalizeLongArray()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final Point p = new Point( initial );
		final long[] result = new long[ 3 ];
		p.localize( result );
		for ( int i = 0; i < initial.length; i++ )
		{
			assertEquals( p.getLongPosition( i ), initial[ i ] );
		}
	}

	@Test
	public void testGetIntPosition()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final Point p = new Point( initial );
		assertEquals( p.getIntPosition( 1 ), initial[ 1 ] );
	}

	@Test
	public void testGetLongPosition()
	{
		final long[] initial = new long[] { 532, 632, 987421 };
		final Point p = new Point( initial );
		assertEquals( p.getLongPosition( 1 ), initial[ 1 ] );
	}

}
