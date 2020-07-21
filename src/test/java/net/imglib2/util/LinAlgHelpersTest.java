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

package net.imglib2.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LinAlgHelpersTest
{
	public double[][] getXRot( final double theta )
	{
		final double s = Math.sin( theta );
		final double c = Math.cos( theta );
		final double[][] R = new double[][] {
				{ 1, 0, 0 },
				{ 0, c, -s },
				{ 0, s, c }
		};
		return R;
	}

	public double[][] getYRot( final double theta )
	{
		final double s = Math.sin( theta );
		final double c = Math.cos( theta );
		final double[][] R = new double[][] {
				{ c, 0, s },
				{ 0, 1, 0 },
				{ -s, 0, c }
		};
		return R;
	}

	public double[][] getZRot( final double theta )
	{
		final double s = Math.sin( theta );
		final double c = Math.cos( theta );
		final double[][] R = new double[][] {
				{ c, -s, 0 },
				{ s, c, 0 },
				{ 0, 0, 1 }
		};
		return R;
	}

	final static double delta = 1e-10;

	@Test
	public void testAngleFromR()
	{
		assertEquals( 0.2, LinAlgHelpers.angleFromR( getXRot( 0.2 ) ), delta );
		assertEquals( 0.5, LinAlgHelpers.angleFromR( getYRot( 0.5 ) ), delta );
		assertEquals( 0.135, LinAlgHelpers.angleFromR( getZRot( 0.135 ) ), delta );
		assertEquals( 3.14, LinAlgHelpers.angleFromR( getZRot( 3.14 ) ), delta );
	}

	@Test
	public void testAxisFromR()
	{
		final double[] X = new double[] { 1, 0, 0 };
		final double[] Y = new double[] { 0, 1, 0 };
		final double[] Z = new double[] { 0, 0, 1 };
		final double[] a = new double[ 3 ];

		LinAlgHelpers.axisFromR( getXRot( 0.2 ), a );
		assertArrayEquals( X, a, delta );

		LinAlgHelpers.axisFromR( getXRot( 3.1 ), a );
		assertArrayEquals( X, a, delta );

		LinAlgHelpers.axisFromR( getYRot( 0.2 ), a );
		assertArrayEquals( Y, a, delta );

		LinAlgHelpers.axisFromR( getYRot( 3.1 ), a );
		assertArrayEquals( Y, a, delta );

		LinAlgHelpers.axisFromR( getZRot( 0.2 ), a );
		assertArrayEquals( Z, a, delta );

		LinAlgHelpers.axisFromR( getZRot( 0.9 ), a );
		assertArrayEquals( Z, a, delta );
	}

	@Test
	public void testR2Q2R()
	{
		double[][] expectedR;
		final double[][] R = new double[ 3 ][ 3 ];
		final double[] q = new double[ 4 ];

		expectedR = getXRot( 0.2 );
		LinAlgHelpers.quaternionFromR( expectedR, q );
		LinAlgHelpers.quaternionToR( q, R );
		for ( int i = 0; i < 3; ++i )
			assertArrayEquals( expectedR[ i ], R[ i ], delta );

		expectedR = getXRot( 3.1 );
		LinAlgHelpers.quaternionFromR( expectedR, q );
		LinAlgHelpers.quaternionToR( q, R );
		for ( int i = 0; i < 3; ++i )
			assertArrayEquals( expectedR[ i ], R[ i ], delta );

		expectedR = getYRot( 0.2 );
		LinAlgHelpers.quaternionFromR( expectedR, q );
		LinAlgHelpers.quaternionToR( q, R );
		for ( int i = 0; i < 3; ++i )
			assertArrayEquals( expectedR[ i ], R[ i ], delta );

		expectedR = getYRot( 3.1 );
		LinAlgHelpers.quaternionFromR( expectedR, q );
		LinAlgHelpers.quaternionToR( q, R );
		for ( int i = 0; i < 3; ++i )
			assertArrayEquals( expectedR[ i ], R[ i ], delta );

		expectedR = getZRot( 0.2 );
		LinAlgHelpers.quaternionFromR( expectedR, q );
		LinAlgHelpers.quaternionToR( q, R );
		for ( int i = 0; i < 3; ++i )
			assertArrayEquals( expectedR[ i ], R[ i ], delta );

		expectedR = getZRot( 0.9 );
		LinAlgHelpers.quaternionFromR( expectedR, q );
		LinAlgHelpers.quaternionToR( q, R );
		for ( int i = 0; i < 3; ++i )
			assertArrayEquals( expectedR[ i ], R[ i ], delta );
	}

	@Test
	public void testQuaternionToR()
	{
		final double[] q = new double[] { 1, 0, 0, 0 };
		final double[][] expectedR = new double[][] {
				{ 1, 0, 0 },
				{ 0, 1, 0 },
				{ 0, 0, 1 }
		};
		final double[][] R = new double[ 3 ][ 3 ];
		LinAlgHelpers.quaternionToR( q, R );
		assertArrayEquals( expectedR[ 0 ], R[ 0 ], delta );
		assertArrayEquals( expectedR[ 1 ], R[ 1 ], delta );
		assertArrayEquals( expectedR[ 2 ], R[ 2 ], delta );
	}

	@Test
	public void testQuaternionFromR()
	{
		final double[][] R = new double[][] {
				{ 1, 0, 0 },
				{ 0, 1, 0 },
				{ 0, 0, 1 }
		};
		final double[] expectedQ = new double[] { 1, 0, 0, 0 };
		final double[] q = new double[ 4 ];
		LinAlgHelpers.quaternionFromR( R, q );
		assertArrayEquals( expectedQ, q, delta );
	}

	@Test
	public void testQuaternionMultiply()
	{
		final double[][] Rp = getZRot( 0.2 );
		final double[][] Rq = getXRot( 4.1 );
		final double[][] Rpq = new double[ 3 ][ 3 ];
		LinAlgHelpers.mult( Rp, Rq, Rpq );
		final double[] p = new double[ 4 ];
		final double[] q = new double[ 4 ];
		final double[] pq = new double[ 4 ];
		final double[] expectedPq = new double[ 4 ];
		LinAlgHelpers.quaternionFromR( Rp, p );
		LinAlgHelpers.quaternionFromR( Rq, q );
		LinAlgHelpers.quaternionFromR( Rpq, expectedPq );
		LinAlgHelpers.quaternionMultiply( p, q, pq );
		assertArrayEquals( expectedPq, pq, delta );
	}

	@Test
	public void testQuaternionApply()
	{
		final double[][] R = getXRot( 4.1 );
		final double[] q = new double[ 4 ];
		LinAlgHelpers.quaternionFromR( R, q );
		final double[] p = new double[] { 100, 0.7, -31 };
		final double[] qp = new double[ 3 ];
		final double[] expectedQp = new double[ 3 ];
		LinAlgHelpers.mult( R, p, expectedQp );
		LinAlgHelpers.quaternionApply( q, p, qp );
		assertArrayEquals( expectedQp, qp, delta );
	}
}
