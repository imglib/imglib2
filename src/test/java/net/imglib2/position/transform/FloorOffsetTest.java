/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.position.transform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import net.imglib2.Point;
import net.imglib2.RealPoint;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author Stephan Saalfeld
 */
public class FloorOffsetTest
{
	final private double[] singleRealLocations = new double[] { 0.1, 0.7, -0.1, -0.7, 0.2, 0.3, 0.3, 20.1, -13.5, 1, 1, -2.4 };

	final private long[] singleLocations = new long[] { 1, 7, -1, -7, 2, 3, 3, 20, -135, 1, -1, -4 };

	final private double[][] realLocations = new double[][] {
			{ 0.1, 0.7, -0.1 },
			{ -0.7, 0.2, 0.3 },
			{ 0.3, 20.1, -13.5 },
			{ 1, 1, -2.4 } };

	final private long[][] locations = new long[][] {
			{ 1, 7, -1 },
			{ -7, 2, 3 },
			{ 3, 20, -135 },
			{ 1, -1, -4 } };

	private double[] r;

	private RealPoint reference;

	private long[] t;

	private Point target;

	private long[] o;

	private Point offset;

	private double[] realLocation;

	private long[] location;

	private long[] referenceFloorOffset;

	private FloorOffset< Point > fo;

	@Before
	public void init()
	{
		r = new double[] { 1.54, -20.3, 100.4 };
		reference = RealPoint.wrap( r );
		t = new long[] { 3, 4, 5 };
		target = Point.wrap( t );
		o = new long[] { 1, -2, 3 };
		offset = Point.wrap( o );
		realLocation = new double[ 3 ];
		location = new long[ 3 ];
		referenceFloorOffset = new long[] { 2, -23, 103 };
		fo = new FloorOffset< Point >( reference, target, offset );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#FloorOffset(net.imglib2.Localizable, long[])}
	 * .
	 */
	@Test
	public void testFloorOffsetLocalizablePositionableLongArray()
	{
		new FloorOffset< Point >( target, o );
		target.localize( location );
		assertArrayEquals( location, o );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#FloorOffset(net.imglib2.Localizable, net.imglib2.Localizable)}
	 * .
	 */
	@Test
	public void testFloorOffsetLocalizablePositionableLocalizable()
	{
		new FloorOffset< Point >( target, offset );
		target.localize( location );
		assertArrayEquals( location, o );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#FloorOffset(net.imglib2.RealLocalizable, net.imglib2.Localizable, long[])}
	 * .
	 */
	@Test
	public void testFloorOffsetRealLocalizableLocalizablePositionableLongArray()
	{
		new FloorOffset< Point >( reference, target, o );
		target.localize( location );
		assertArrayEquals( location, referenceFloorOffset );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#FloorOffset(net.imglib2.RealLocalizable, net.imglib2.Localizable, net.imglib2.Localizable)}
	 * .
	 */
	@Test
	public void testFloorOffsetRealLocalizableLocalizablePositionableLocalizable()
	{
		new FloorOffset< Point >( reference, target, offset );
		target.localize( location );
		assertArrayEquals( location, referenceFloorOffset );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#f(double, long)}.
	 */
	@Test
	public void testFDoubleLong()
	{
		assertEquals( FloorOffset.f( 1.5, 3 ), 4 );
		assertEquals( FloorOffset.f( -1.5, 3 ), 1 );
		assertEquals( FloorOffset.f( 1.5, -3 ), -2 );
		assertEquals( FloorOffset.f( -1.5, -3 ), -5 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#f(float, long)}.
	 */
	@Test
	public void testFFloatLong()
	{
		assertEquals( FloorOffset.f( 1.5f, 3 ), 4 );
		assertEquals( FloorOffset.f( -1.5f, 3 ), 1 );
		assertEquals( FloorOffset.f( 1.5f, -3 ), -2 );
		assertEquals( FloorOffset.f( -1.5f, -3 ), -5 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#move(float, int)}.
	 */
	@Test
	public void testMoveFloatInt()
	{
		for ( int d = 0; d < 3; ++d )
		{
			double real = r[ d ];
			long discrete = FloorOffset.f( real, o[ d ] );
			for ( final double move : singleRealLocations )
			{
				final float f = ( float ) move;
				real += f;
				discrete = FloorOffset.f( real, o[ d ] );
				fo.move( ( float ) move, d );
				assertEquals( fo.getDoublePosition( d ), real, 0.00001 );
				assertEquals( target.getLongPosition( d ), discrete );
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#move(double, int)}.
	 */
	@Test
	public void testMoveDoubleInt()
	{
		for ( int d = 0; d < 3; ++d )
		{
			double real = r[ d ];
			long discrete = FloorOffset.f( real, o[ d ] );
			for ( final double move : singleRealLocations )
			{
				real += move;
				discrete = FloorOffset.f( real, o[ d ] );
				fo.move( move, d );
				assertEquals( fo.getDoublePosition( d ), real, 0.00001 );
				assertEquals( target.getLongPosition( d ), discrete );
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#move(net.imglib2.RealLocalizable)}
	 * .
	 */
	@Test
	public void testMoveRealLocalizable()
	{
		final double[] real = r.clone();
		final long[] discrete = referenceFloorOffset.clone();
		for ( final double[] move : realLocations )
		{
			for ( int d = 0; d < move.length; ++d )
			{
				real[ d ] += move[ d ];
				discrete[ d ] = FloorOffset.f( real[ d ], o[ d ] );
			}
			fo.move( RealPoint.wrap( move ) );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( discrete, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#move(float[])}.
	 */
	@Test
	public void testMoveFloatArray()
	{
		final double[] real = r.clone();
		final long[] discrete = referenceFloorOffset.clone();
		for ( final double[] move : realLocations )
		{
			final float[] f = new float[ move.length ];
			for ( int d = 0; d < move.length; ++d )
			{
				f[ d ] = ( float ) move[ d ];
				real[ d ] += f[ d ];
				discrete[ d ] = FloorOffset.f( real[ d ], o[ d ] );
			}
			fo.move( f );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( discrete, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#move(double[])}.
	 */
	@Test
	public void testMoveDoubleArray()
	{
		final double[] real = r.clone();
		final long[] discrete = referenceFloorOffset.clone();
		for ( final double[] move : realLocations )
		{
			for ( int d = 0; d < move.length; ++d )
			{
				real[ d ] += move[ d ];
				discrete[ d ] = FloorOffset.f( real[ d ], o[ d ] );
			}
			fo.move( move );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( discrete, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#setPosition(net.imglib2.RealLocalizable)}
	 * .
	 */
	@Test
	public void testSetPositionRealLocalizable()
	{
		final long[] discrete = referenceFloorOffset.clone();
		for ( final double[] real : realLocations )
		{
			for ( int d = 0; d < real.length; ++d )
				discrete[ d ] = FloorOffset.f( real[ d ], o[ d ] );

			fo.setPosition( RealPoint.wrap( real ) );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( discrete, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#setPosition(float[])}.
	 */
	@Test
	public void testSetPositionFloatArray()
	{
		final long[] discrete = referenceFloorOffset.clone();
		for ( final double[] real : realLocations )
		{
			final float[] f = new float[ real.length ];
			for ( int d = 0; d < real.length; ++d )
			{
				f[ d ] = ( float ) real[ d ];
				discrete[ d ] = FloorOffset.f( real[ d ], o[ d ] );
			}
			fo.setPosition( f );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( discrete, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#setPosition(double[])}.
	 */
	@Test
	public void testSetPositionDoubleArray()
	{
		final long[] discrete = referenceFloorOffset.clone();
		for ( final double[] real : realLocations )
		{
			for ( int d = 0; d < real.length; ++d )
				discrete[ d ] = FloorOffset.f( real[ d ], o[ d ] );

			fo.setPosition( real );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( discrete, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#setPosition(float, int)}
	 * .
	 */
	@Test
	public void testSetPositionFloatInt()
	{
		for ( int d = 0; d < 3; ++d )
		{
			for ( final double real : singleRealLocations )
			{
				final long discrete = FloorOffset.f( real, o[ d ] );
				fo.setPosition( ( float ) real, d );
				assertEquals( fo.getDoublePosition( d ), real, 0.00001 );
				assertEquals( target.getLongPosition( d ), discrete );
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.FloorOffset#setPosition(double, int)}
	 * .
	 */
	@Test
	public void testSetPositionDoubleInt()
	{
		for ( int d = 0; d < 3; ++d )
		{
			for ( final double real : singleRealLocations )
			{
				final long discrete = FloorOffset.f( real, o[ d ] );
				fo.setPosition( real, d );
				assertEquals( fo.getDoublePosition( d ), real, 0.00001 );
				assertEquals( target.getLongPosition( d ), discrete );
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#bck(int)}
	 * .
	 */
	@Test
	public void testBck()
	{
		for ( int d = 0; d < 3; ++d )
		{
			fo.bck( d );
			assertEquals( r[ d ] - 1, fo.getDoublePosition( d ), 0.00001 );
			assertEquals( FloorOffset.f( r[ d ], o[ d ] ) - 1, target.getLongPosition( d ) );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#fwd(int)}
	 * .
	 */
	@Test
	public void testFwd()
	{
		for ( int d = 0; d < 3; ++d )
		{
			fo.fwd( d );
			assertEquals( r[ d ] + 1, fo.getDoublePosition( d ), 0.00001 );
			assertEquals( FloorOffset.f( r[ d ], o[ d ] ) + 1, target.getLongPosition( d ) );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#move(int, int)}
	 * .
	 */
	@Test
	public void testMoveIntInt()
	{
		for ( int d = 0; d < 3; ++d )
		{
			double real = r[ d ];
			long discrete = FloorOffset.f( real, o[ d ] );
			for ( final long move : singleLocations )
			{
				final int f = ( int ) move;
				real += f;
				discrete = FloorOffset.f( real, o[ d ] );
				fo.move( f, d );
				assertEquals( fo.getDoublePosition( d ), real, 0.00001 );
				assertEquals( target.getLongPosition( d ), discrete );
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#move(long, int)}
	 * .
	 */
	@Test
	public void testMoveLongInt()
	{
		for ( int d = 0; d < 3; ++d )
		{
			double real = r[ d ];
			long discrete = FloorOffset.f( real, o[ d ] );
			for ( final long move : singleLocations )
			{
				real += move;
				discrete = FloorOffset.f( real, o[ d ] );
				fo.move( move, d );
				assertEquals( fo.getDoublePosition( d ), real, 0.00001 );
				assertEquals( target.getLongPosition( d ), discrete );
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#move(net.imglib2.Localizable)}
	 * .
	 */
	@Test
	public void testMoveLocalizable()
	{
		final double[] real = r.clone();
		final long[] discrete = referenceFloorOffset.clone();
		for ( final long[] move : locations )
		{
			for ( int d = 0; d < move.length; ++d )
			{
				real[ d ] += move[ d ];
				discrete[ d ] = FloorOffset.f( real[ d ], o[ d ] );
			}
			fo.move( Point.wrap( move ) );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( discrete, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#move(int[])}
	 * .
	 */
	@Test
	public void testMoveIntArray()
	{
		final double[] real = r.clone();
		final long[] discrete = referenceFloorOffset.clone();
		for ( final long[] move : locations )
		{
			final int[] f = new int[ move.length ];
			for ( int d = 0; d < move.length; ++d )
			{
				f[ d ] = ( int ) move[ d ];
				real[ d ] += f[ d ];
				discrete[ d ] = FloorOffset.f( real[ d ], o[ d ] );
			}
			fo.move( f );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( discrete, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#move(long[])}
	 * .
	 */
	@Test
	public void testMoveLongArray()
	{
		final double[] real = r.clone();
		final long[] discrete = referenceFloorOffset.clone();
		for ( final long[] move : locations )
		{
			for ( int d = 0; d < move.length; ++d )
			{
				real[ d ] += move[ d ];
				discrete[ d ] = FloorOffset.f( real[ d ], o[ d ] );
			}
			fo.move( move );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( discrete, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#setPosition(net.imglib2.Localizable)}
	 * .
	 */
	@Test
	public void testSetPositionLocalizable()
	{
		final double[] real = r.clone();
		for ( final long[] l : locations )
		{
			for ( int d = 0; d < l.length; ++d )
				real[ d ] = l[ d ];

			fo.setPosition( Point.wrap( l ) );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( l, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#setPosition(int[])}
	 * .
	 */
	@Test
	public void testSetPositionIntArray()
	{
		final double[] real = r.clone();
		for ( final long[] l : locations )
		{
			final int[] li = new int[ l.length ];
			for ( int d = 0; d < l.length; ++d )
			{
				real[ d ] = l[ d ];
				li[ d ] = ( int ) l[ d ];
			}

			fo.setPosition( li );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( l, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#setPosition(long[])}
	 * .
	 */
	@Test
	public void testSetPositionLongArray()
	{
		final double[] real = r.clone();
		for ( final long[] l : locations )
		{
			for ( int d = 0; d < l.length; ++d )
				real[ d ] = l[ d ];

			fo.setPosition( l );
			fo.localize( realLocation );
			target.localize( location );
			assertArrayEquals( real, realLocation, 0.00001 );
			assertArrayEquals( l, location );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#setPosition(int, int)}
	 * .
	 */
	@Test
	public void testSetPositionIntInt()
	{
		for ( int d = 0; d < 3; ++d )
		{
			for ( final long l : singleLocations )
			{
				fo.setPosition( ( int ) l, d );
				assertEquals( fo.getDoublePosition( d ), l, 0.00001 );
				assertEquals( target.getLongPosition( d ), l );
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.position.transform.AbstractPositionableTransform#setPosition(long, int)}
	 * .
	 */
	@Test
	public void testSetPositionLongInt()
	{
		for ( int d = 0; d < 3; ++d )
		{
			for ( final long l : singleLocations )
			{
				fo.setPosition( l, d );
				assertEquals( fo.getDoublePosition( d ), l, 0.00001 );
				assertEquals( target.getLongPosition( d ), l );
			}
		}
	}
}
