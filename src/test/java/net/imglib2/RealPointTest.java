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

package net.imglib2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author Lee Kamentsky
 */
public class RealPointTest
{

	/**
	 * Test method for {@link net.imglib2.RealPoint#RealPoint(int)}.
	 */
	@Test
	public void testRealPointInt()
	{
		final RealPoint p = new RealPoint( 3 );
		assertEquals( p.numDimensions(), 3 );
		for ( int i = 0; i < 3; i++ )
		{
			assertEquals( p.getDoublePosition( i ), 0, 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#RealPoint(double[])}.
	 */
	@Test
	public void testRealPointDoubleArray()
	{
		final double[] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
		final RealPoint p = new RealPoint( expected );
		assertEquals( p.numDimensions(), 4 );
		for ( int i = 0; i < 4; i++ )
		{
			assertEquals( p.getDoublePosition( i ), expected[ i ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#RealPoint(float[])}.
	 */
	@Test
	public void testRealPointFloatArray()
	{
		final float[] expected = new float[] { 1.5f, 2.5f, 4.5f, 6.5f };
		final RealPoint p = new RealPoint( expected );
		assertEquals( p.numDimensions(), 4 );
		for ( int i = 0; i < 4; i++ )
		{
			assertEquals( p.getFloatPosition( i ), expected[ i ], 0 );
		}
	}

	@Test
	public void testRealPointRealLocalizable()
	{
		final RealPoint p = new RealPoint( new RealPoint( new double[] { 15.3, 2.1, 1.2 } ) );
		assertEquals( p.numDimensions(), 3 );
		assertEquals( p.getDoublePosition( 0 ), 15.3, 0 );
		assertEquals( p.getDoublePosition( 1 ), 2.1, 0 );
		assertEquals( p.getDoublePosition( 2 ), 1.2, 0 );
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#fwd(int)}.
	 */
	@Test
	public void testFwd()
	{
		for ( int i = 0; i < 3; i++ )
		{
			final double[] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
			final RealPoint p = new RealPoint( expected );
			p.fwd( i );
			expected[ i ] += 1;
			for ( int j = 0; j < 4; j++ )
			{
				assertEquals( p.getDoublePosition( j ), expected[ j ], 0 );
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#bck(int)}.
	 */
	@Test
	public void testBck()
	{
		for ( int i = 0; i < 3; i++ )
		{
			final double[] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
			final RealPoint p = new RealPoint( expected );
			p.bck( i );
			expected[ i ] -= 1;
			for ( int j = 0; j < 4; j++ )
			{
				assertEquals( p.getDoublePosition( j ), expected[ j ], 0 );
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#move(int, int)}.
	 */
	@Test
	public void testMoveIntInt()
	{
		final int[] move = { 1, 5, -3, 16 };
		for ( int i = 0; i < move.length; i++ )
		{
			final double[] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
			final RealPoint p = new RealPoint( expected );
			p.move( move[ i ], i );
			expected[ i ] += move[ i ];
			for ( int j = 0; j < 4; j++ )
			{
				assertEquals( p.getDoublePosition( j ), expected[ j ], 0 );
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#move(long, int)}.
	 */
	@Test
	public void testMoveLongInt()
	{
		final long[] move = { 1, 5, -3, 16 };
		for ( int i = 0; i < move.length; i++ )
		{
			final double[] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
			final RealPoint p = new RealPoint( expected );
			p.move( move[ i ], i );
			expected[ i ] += move[ i ];
			for ( int j = 0; j < 4; j++ )
			{
				assertEquals( p.getDoublePosition( j ), expected[ j ], 0 );
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.RealPoint#move(net.imglib2.Localizable)}.
	 */
	@Test
	public void testMoveLocalizable()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final double[] displacement = { 98.2, -16, 44.2, 0 };
		final RealPoint p1 = new RealPoint( initial );
		final RealPoint p2 = new RealPoint( displacement );
		p1.move( p2 );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), initial[ j ] + displacement[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#move(int[])}.
	 */
	@Test
	public void testMoveIntArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final int[] displacement = { 98, -16, 44, 0 };
		final RealPoint p1 = new RealPoint( initial );
		p1.move( displacement );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), initial[ j ] + displacement[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#move(long[])}.
	 */
	@Test
	public void testMoveLongArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final long[] displacement = { 98, -16, 44, 0 };
		final RealPoint p1 = new RealPoint( initial );
		p1.move( displacement );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), initial[ j ] + displacement[ j ], 0 );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.RealPoint#setPosition(net.imglib2.Localizable)}.
	 */
	@Test
	public void testSetPositionLocalizable()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final long[] fynal = { 98, -16, 44, 0 };
		final RealPoint p1 = new RealPoint( initial );
		final Point p2 = new Point( fynal );
		p1.setPosition( p2 );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), fynal[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#setPosition(int[])}.
	 */
	@Test
	public void testSetPositionIntArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final int[] fynal = { 98, -16, 44, 0 };
		final RealPoint p1 = new RealPoint( initial );
		p1.setPosition( fynal );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), fynal[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#setPosition(long[])}.
	 */
	@Test
	public void testSetPositionLongArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final long[] fynal = { 98, -16, 44, 0 };
		final RealPoint p1 = new RealPoint( initial );
		p1.setPosition( fynal );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), fynal[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#setPosition(int, int)}.
	 */
	@Test
	public void testSetPositionIntInt()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final int[] fynal = { 98, -16, 44, 0 };
		for ( int i = 0; i < initial.length; i++ )
		{
			final RealPoint p1 = new RealPoint( initial );
			p1.setPosition( fynal[ i ], i );
			assertEquals( p1.getDoublePosition( i ), fynal[ i ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#setPosition(long, int)}.
	 */
	@Test
	public void testSetPositionLongInt()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final long[] fynal = { 98, -16, 44, 0 };
		for ( int i = 0; i < initial.length; i++ )
		{
			final RealPoint p1 = new RealPoint( initial );
			p1.setPosition( fynal[ i ], i );
			assertEquals( p1.getDoublePosition( i ), fynal[ i ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#numDimensions()}.
	 */
	@Test
	public void testNumDimensions()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final RealPoint p1 = new RealPoint( initial );
		assertEquals( 4, p1.numDimensions() );
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#move(float, int)}.
	 */
	@Test
	public void testMoveFloatInt()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final float[] displacement = { 4.2f, 77.1f, -2f, 51.4f };
		for ( int i = 0; i < initial.length; i++ )
		{
			final RealPoint p1 = new RealPoint( initial );
			p1.move( displacement[ i ], i );
			for ( int j = 0; j < 4; j++ )
			{
				double expected = initial[ j ];
				if ( i == j )
					expected += displacement[ j ];
				assertEquals( p1.getDoublePosition( j ), expected, 0 );
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#move(double, int)}.
	 */
	@Test
	public void testMoveDoubleInt()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final double[] displacement = { 4.2, 77.1, -2, 51.4 };
		for ( int i = 0; i < initial.length; i++ )
		{
			final RealPoint p1 = new RealPoint( initial );
			p1.move( displacement[ i ], i );
			for ( int j = 0; j < 4; j++ )
			{
				double expected = initial[ j ];
				if ( i == j )
					expected += displacement[ j ];
				assertEquals( p1.getDoublePosition( j ), expected, 0 );
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.RealPoint#move(net.imglib2.RealLocalizable)}.
	 */
	@Test
	public void testMoveRealLocalizable()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final double[] displacement = { 4.2, 77.1, -2, 51.4 };
		final RealPoint p1 = new RealPoint( initial );
		final RealPoint p2 = new RealPoint( displacement );
		p1.move( p2 );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), initial[ j ] + displacement[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#move(float[])}.
	 */
	@Test
	public void testMoveFloatArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final float[] displacement = { 4.2f, 77.1f, -2f, 51.4f };
		final RealPoint p1 = new RealPoint( initial );
		p1.move( displacement );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), initial[ j ] + displacement[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#move(double[])}.
	 */
	@Test
	public void testMoveDoubleArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final double[] displacement = { 4.2, 77.1, -2, 51.4 };
		final RealPoint p1 = new RealPoint( initial );
		p1.move( displacement );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), initial[ j ] + displacement[ j ], 0 );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.RealPoint#setPosition(net.imglib2.RealLocalizable)}.
	 */
	@Test
	public void testSetPositionRealLocalizable()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final double[] fynal = { 98.2, -16.1, 44.7, 0 };
		final RealPoint p1 = new RealPoint( initial );
		final RealPoint p2 = new RealPoint( fynal );
		p1.setPosition( p2 );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), fynal[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#setPosition(float[])}.
	 */
	@Test
	public void testSetPositionFloatArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final float[] fynal = { 98.2f, -16.1f, 44.7f, 0f };
		final RealPoint p1 = new RealPoint( initial );
		p1.setPosition( fynal );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), fynal[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#setPosition(double[])}.
	 */
	@Test
	public void testSetPositionDoubleArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final double[] fynal = { 98.2, -16.1, 44.7, 0 };
		final RealPoint p1 = new RealPoint( initial );
		p1.setPosition( fynal );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), fynal[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#setPosition(float, int)}.
	 */
	@Test
	public void testSetPositionFloatInt()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final float[] fynal = { 98.2f, -16.1f, 44.7f, 0f };
		for ( int i = 0; i < initial.length; i++ )
		{
			final RealPoint p1 = new RealPoint( initial );
			p1.setPosition( fynal[ i ], i );
			for ( int j = 0; j < 4; j++ )
			{
				if ( i == j )
					assertEquals( p1.getDoublePosition( j ), fynal[ j ], 0 );
				else
					assertEquals( p1.getDoublePosition( j ), initial[ j ], 0 );
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#setPosition(double, int)}.
	 */
	@Test
	public void testSetPositionDoubleInt()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final double[] fynal = { 98.2, -16.1, 44.7, 0 };
		for ( int i = 0; i < initial.length; i++ )
		{
			final RealPoint p1 = new RealPoint( initial );
			p1.setPosition( fynal[ i ], i );
			for ( int j = 0; j < 4; j++ )
			{
				if ( i == j )
					assertEquals( p1.getDoublePosition( j ), fynal[ j ], 0 );
				else
					assertEquals( p1.getDoublePosition( j ), initial[ j ], 0 );
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#localize(float[])}.
	 */
	@Test
	public void testLocalizeFloatArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final float[] result = new float[ initial.length ];
		final RealPoint p1 = new RealPoint( initial );
		p1.localize( result );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( result[ j ], ( float ) ( initial[ j ] ), 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#localize(double[])}.
	 */
	@Test
	public void testLocalizeDoubleArray()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final double[] result = new double[ initial.length ];
		final RealPoint p1 = new RealPoint( initial );
		p1.localize( result );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( result[ j ], initial[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#getFloatPosition(int)}.
	 */
	@Test
	public void testGetFloatPosition()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final RealPoint p1 = new RealPoint( initial );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getFloatPosition( j ), ( float ) ( initial[ j ] ), 0 );
		}
	}

	/**
	 * Test method for {@link net.imglib2.RealPoint#getDoublePosition(int)}.
	 */
	@Test
	public void testGetDoublePosition()
	{
		final double[] initial = { 5.3, 2.6, 3.1, -852.1 };
		final RealPoint p1 = new RealPoint( initial );
		for ( int j = 0; j < 4; j++ )
		{
			assertEquals( p1.getDoublePosition( j ), initial[ j ], 0 );
		}
	}

	/**
	 * Test method for {@link RealPoint#equals(Object)}
	 */
	@Test
	public void testEquals()
	{
		final RealPoint point = new RealPoint( 1.0, 2.5 );
		final RealPoint same = new RealPoint( 1.0, 2.5 );
		final RealPoint different = new RealPoint( 1.0, 2.501 );
		assertTrue( point.equals( same ) );
		assertFalse( point.equals( different ) );
	}

	/**
	 * Test method for {@link RealPoint#hashCode()}
	 */
	@Test
	public void testHashCode()
	{
		final RealPoint point = new RealPoint( 1.0, 2.5 );
		final RealPoint same = new RealPoint( 1.0, 2.5 );
		assertEquals( point.hashCode(), same.hashCode() );
	}

	@Test
	public void testToString()
	{
		final RealPoint point = new RealPoint( 1.0, 2.0 );
		assertEquals( "(1.0,2.0)", point.toString() );
	}
}
