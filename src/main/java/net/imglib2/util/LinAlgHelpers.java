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

package net.imglib2.util;

/**
 * Basic vector and matrix operations implemented on double[] and double[][].
 *
 * @author Tobias Pietzsch
 * @author Stephan Saalfeld
 */
public class LinAlgHelpers
{
	public static int rows( final double[] a )
	{
		return a.length;
	}

	public static int rows( final double[][] A )
	{
		return A.length;
	}

	public static int cols( final double[][] A )
	{
		return A[ 0 ].length;
	}

	/**
	 * get the squared length of a.
	 *
	 * @param a
	 */
	public static double squareLength( final double[] a )
	{
		final int rows = rows( a );
		double squ_len = 0.0;
		for ( int i = 0; i < rows; ++i )
			squ_len += a[ i ] * a[ i ];
		return squ_len;
	}

	/**
	 * get the length of a.
	 *
	 * @param a
	 */
	public static double length( final double[] a )
	{
		return Math.sqrt( squareLength( a ) );
	}

	/**
	 * get the squared length of (a - b).
	 *
	 * @param a
	 * @param b
	 */
	public static double squareDistance( final double[] a, final double[] b )
	{
		assert rows( a ) == rows( b );
		final int rows = rows( a );
		double squ_len = 0.0;
		for ( int i = 0; i < rows; ++i )
			squ_len += ( a[ i ] - b[ i ] ) * ( a[ i ] - b[ i ] );
		return squ_len;
	}

	/**
	 * get the length of (a - b).
	 *
	 * @param a
	 * @param b
	 */
	public static double distance( final double[] a, final double[] b )
	{
		return Math.sqrt( squareDistance( a, b ) );
	}

	/**
	 * set c = ( 1 - t ) * a + t * b, where a, b are vectors and t is scalar.
	 * Dimensions of a, b, and c must match. In place interpolation (c==a or
	 * c==b) is allowed.
	 */
	public static void lerp( final double[] a, final double[] b, final double t, final double[] c )
	{
		assert rows( a ) == rows( b );
		assert rows( a ) == rows( c );

		final int rows = rows( a );

		for ( int i = 0; i < rows; ++i )
			c[ i ] = ( 1.0 - t ) * a[ i ] + t * b[ i ];
	}

	/**
	 * set c = a * b, where a is a vector and b is scalar. Dimensions of a and c
	 * must match. In place scaling (c==a) is permitted.
	 *
	 * @param a
	 * @param b
	 * @param c
	 */
	public static void scale( final double[] a, final double b, final double[] c )
	{
		assert rows( a ) == rows( c );

		final int rows = rows( a );

		for ( int i = 0; i < rows; ++i )
			c[ i ] = a[ i ] * b;
	}

	/**
	 * set C = A * b, where A is a matrix and b is scalar. Dimensions of A and C
	 * must match. In place scaling (C==A) is permitted.
	 *
	 * @param A
	 * @param b
	 * @param C
	 */
	public static void scale( final double[][] A, final double b, final double[][] C )
	{
		assert rows( A ) == rows( C ) && cols( A ) == cols( C );

		final int rows = rows( A );
		final int cols = cols( A );

		for ( int i = 0; i < rows; ++i )
			for ( int j = 0; j < cols; ++j )
				C[ i ][ j ] = A[ i ][ j ] * b;
	}

	/**
	 * set C = A, where A is a matrix. Dimensions of A and C must match.
	 *
	 * @param A
	 * @param C
	 */
	public static void copy( final double[][] A, final double[][] C )
	{
		assert rows( A ) == rows( C ) && cols( A ) == cols( C );

		final int rows = rows( A );
		final int cols = cols( A );

		for ( int i = 0; i < rows; ++i )
			for ( int j = 0; j < cols; ++j )
				C[ i ][ j ] = A[ i ][ j ];
	}

	/**
	 * set c = a - b. Dimensions of a, b, and c must match. In place subtraction
	 * (c==a) is permitted.
	 *
	 * @param a
	 * @param b
	 * @param c
	 */
	public static void subtract( final double[] a, final double[] b, final double[] c )
	{
		assert ( rows( a ) == rows( b ) ) && ( rows( a ) == rows( c ) );

		final int rows = rows( a );

		for ( int i = 0; i < rows; ++i )
			c[ i ] = a[ i ] - b[ i ];
	}

	/**
	 * set c = a + b. Dimensions of a, b, and c must match. In place addition
	 * (c==a) is permitted.
	 *
	 * @param a
	 * @param b
	 * @param c
	 */
	public static void add( final double[] a, final double[] b, final double[] c )
	{
		assert ( rows( a ) == rows( b ) ) && ( rows( a ) == rows( c ) );

		final int rows = rows( a );

		for ( int i = 0; i < rows; ++i )
			c[ i ] = a[ i ] + b[ i ];
	}

	/**
	 * set c = A * b.
	 *
	 * Dimensions of A, b, and c must match. That is, cols(A) == rows(b), and
	 * rows(c) == rows(A).
	 *
	 * @param A
	 * @param b
	 * @param c
	 */
	public static void mult( final double[][] A, final double[] b, final double[] c )
	{
		assert cols( A ) == rows( b );
		assert rows( c ) == rows( A );

		final int rows = rows( c );
		final int Acols = cols( A );

		for ( int i = 0; i < rows; ++i )
		{
			double sum = 0;
			for ( int k = 0; k < Acols; ++k )
				sum += A[ i ][ k ] * b[ k ];
			c[ i ] = sum;
		}
	}

	/**
	 * set c = A^T * b.
	 *
	 * Dimensions of A, b, and c must match. That is, rows(A) == rows(b), and
	 * rows(c) == cols(A).
	 *
	 * @param A
	 * @param b
	 * @param c
	 */
	public static void multT( final double[][] A, final double[] b, final double[] c )
	{
		assert rows( A ) == rows( b );
		assert rows( c ) == cols( A );

		final int rows = rows( c );
		final int Arows = rows( A );

		for ( int i = 0; i < rows; ++i )
		{
			double sum = 0;
			for ( int k = 0; k < Arows; ++k )
				sum += A[ k ][ i ] * b[ k ];
			c[ i ] = sum;
		}
	}

	/**
	 * set C = A * B.
	 *
	 * Dimensions of A, B, and C must match. That is, cols(A) == rows(B),
	 * rows(C) == rows(A), and cols(C) == cols(B).
	 *
	 * @param A
	 * @param B
	 * @param C
	 */
	public static void mult( final double[][] A, final double[][] B, final double[][] C )
	{
		assert cols( A ) == rows( B );
		assert ( rows( C ) == rows( A ) ) && ( cols( C ) == cols( B ) );

		final int cols = cols( C );
		final int rows = rows( C );
		final int Acols = cols( A );

		for ( int i = 0; i < rows; ++i )
		{
			for ( int j = 0; j < cols; ++j )
			{
				double sum = 0;
				for ( int k = 0; k < Acols; ++k )
					sum += A[ i ][ k ] * B[ k ][ j ];
				C[ i ][ j ] = sum;
			}
		}
	}

	/**
	 * set C = A * B^T.
	 *
	 * Dimensions of A, B, and C must match. That is, cols(A) == cols(B),
	 * rows(C) == rows(A), and cols(C) == rows(B).
	 *
	 * @param A
	 * @param B
	 * @param C
	 */
	public static void multABT( final double[][] A, final double[][] B, final double[][] C )
	{
		assert cols( A ) == cols( B );
		assert ( rows( C ) == rows( A ) ) && ( cols( C ) == rows( B ) );

		final int cols = cols( C );
		final int rows = rows( C );
		final int Acols = cols( A );

		for ( int i = 0; i < rows; ++i )
		{
			for ( int j = 0; j < cols; ++j )
			{
				double sum = 0;
				for ( int k = 0; k < Acols; ++k )
					sum += A[ i ][ k ] * B[ j ][ k ];
				C[ i ][ j ] = sum;
			}
		}
	}

	/**
	 * set C = A^T * B.
	 *
	 * Dimensions of A, B, and C must match. That is, rows(A) == rows(B),
	 * rows(C) == cols(A), and cols(C) == cols(B).
	 *
	 * @param A
	 * @param B
	 * @param C
	 */
	public static void multATB( final double[][] A, final double[][] B, final double[][] C )
	{
		assert rows( A ) == rows( B );
		assert ( rows( C ) == cols( A ) ) && ( cols( C ) == cols( B ) );

		final int cols = cols( C );
		final int rows = rows( C );
		final int Arows = rows( A );

		for ( int i = 0; i < rows; ++i )
		{
			for ( int j = 0; j < cols; ++j )
			{
				double sum = 0;
				for ( int k = 0; k < Arows; ++k )
					sum += A[ k ][ i ] * B[ k ][ j ];
				C[ i ][ j ] = sum;
			}
		}
	}

	/**
	 * set C = A + B.
	 *
	 * Dimensions of A, B, and C must match. In place addition (C==A or C==B) is
	 * permitted.
	 *
	 * @param A
	 * @param B
	 * @param C
	 */
	public static void add( final double[][] A, final double[][] B, final double[][] C )
	{
		assert rows( A ) == rows( B ) && rows( A ) == rows( C );
		assert cols( A ) == cols( B ) && cols( A ) == cols( C );

		final int rows = rows( A );
		final int cols = cols( A );

		for ( int i = 0; i < rows; ++i )
			for ( int j = 0; j < cols; ++j )
				C[ i ][ j ] = A[ i ][ j ] + B[ i ][ j ];
	}

	/**
	 * extract column c of A into vector b.
	 *
	 * Dimensions of A and b must match. That is, rows(A) == rows(b).
	 *
	 * @param c
	 * @param A
	 * @param b
	 */
	public static void getCol( final int c, final double[][] A, final double[] b )
	{
		assert rows( A ) == rows( b );
		assert cols( A ) > c && c >= 0;

		final int rows = rows( A );

		for ( int i = 0; i < rows; ++i )
			b[ i ] = A[ i ][ c ];
	}

	/**
	 * set column c of B to vector a.
	 *
	 * Dimensions of a and B must match. That is, rows(a) == rows(B).
	 *
	 * @param c
	 * @param a
	 * @param B
	 */
	public static void setCol( final int c, final double[] a, final double[][] B )
	{
		assert rows( B ) == rows( a );
		assert cols( B ) > c && c >= 0;

		final int rows = rows( B );

		for ( int i = 0; i < rows; ++i )
			B[ i ][ c ] = a[ i ];
	}

	/**
	 * extract row r of A into vector b.
	 *
	 * Dimensions of A and b must match. That is, cols(A) == rows(b).
	 *
	 * @param r
	 * @param A
	 * @param b
	 */
	public static void getRow( final int r, final double[][] A, final double[] b )
	{
		assert cols( A ) == rows( b );
		assert rows( A ) > r && r >= 0;

		final int cols = cols( A );

		for ( int i = 0; i < cols; ++i )
			b[ i ] = A[ r ][ i ];
	}

	/**
	 * set row r of B to vector a.
	 *
	 * Dimensions of a and B must match. That is, rows(a) == cols(B).
	 *
	 * @param r
	 * @param a
	 * @param B
	 */
	public static void setRow( final int r, final double[] a, final double[][] B )
	{
		assert cols( B ) == rows( a );
		assert rows( B ) > r && r >= 0;

		final int cols = cols( B );

		for ( int i = 0; i < cols; ++i )
			B[ r ][ i ] = a[ i ];
	}

	/**
	 * normalize a, i.e., scale to unit length.
	 *
	 * @param a
	 */
	public static void normalize( final double[] a )
	{
		final int rows = rows( a );
		final double len = length( a );
		for ( int i = 0; i < rows; ++i )
			a[ i ] /= len;
	}

	/**
	 * compute dot product a * b.
	 *
	 * Dimensions of a and b must match.
	 *
	 * @param a
	 * @param b
	 */
	public static double dot( final double[] a, final double[] b )
	{
		assert rows( a ) == rows( b );

		final int rows = rows( a );

		double sum = 0;
		for ( int i = 0; i < rows; ++i )
			sum += a[ i ] * b[ i ];

		return sum;
	}

	/**
	 * compute cross product, set c = a ^ b.
	 *
	 * Dimensions of a, b, and c must equal 3.
	 *
	 * @param a
	 * @param b
	 */
	public static void cross( final double[] a, final double[] b, final double[] c )
	{
		c[ 0 ] = a[ 1 ] * b[ 2 ] - a[ 2 ] * b[ 1 ];
		c[ 1 ] = a[ 2 ] * b[ 0 ] - a[ 0 ] * b[ 2 ];
		c[ 2 ] = a[ 0 ] * b[ 1 ] - a[ 1 ] * b[ 0 ];
	}

	/**
	 * compute outer product, set C = a * b^T.
	 *
	 * Dimensions of a, b, and C must match. That is, rows(a) == rows(C), and
	 * rows(b) == cols(C).
	 *
	 * @param a
	 * @param b
	 * @param C
	 */
	public static void outer( final double[] a, final double[] b, final double[][] C )
	{
		assert rows( a ) == rows( C ) && rows( b ) == cols( C );

		final int rows = rows( a );
		final int cols = rows( b );

		for ( int i = 0; i < rows; ++i )
			for ( int j = 0; j < cols; ++j )
				C[ i ][ j ] = a[ i ] * b[ j ];
	}

	/**
	 * compute the angle of rotation from a rotation matrix. The returned value
	 * is in the range [0, PI].
	 *
	 * @param R
	 *            rotation matrix
	 */
	public static double angleFromR( final double[][] R )
	{
		assert cols( R ) >= 3;
		assert rows( R ) >= 3;

		final double tr = R[ 0 ][ 0 ] + R[ 1 ][ 1 ] + R[ 2 ][ 2 ];
		final double theta = Math.acos( ( tr - 1.0 ) / 2.0 );
		return theta;
	}

	/**
	 * compute the axis of rotation from a rotation matrix.
	 *
	 * @param R
	 *            rotation matrix
	 * @param a
	 *            rotation axis is stored here
	 */
	public static void axisFromR( final double[][] R, final double[] a )
	{
		assert cols( R ) >= 3;
		assert rows( R ) >= 3;
		assert rows( a ) >= 3;

		final double s = 1.0 / ( 2.0 * Math.sin( angleFromR( R ) ) );
		a[ 0 ] = s * ( R[ 2 ][ 1 ] - R[ 1 ][ 2 ] );
		a[ 1 ] = s * ( R[ 0 ][ 2 ] - R[ 2 ][ 0 ] );
		a[ 2 ] = s * ( R[ 1 ][ 0 ] - R[ 0 ][ 1 ] );
	}

	/**
	 * compute a unit quaternion from a rotation matrix.
	 *
	 * @param R
	 *            rotation matrix.
	 * @param q
	 *            unit quaternion (w, x, y, z) is stored here.
	 */
	public static void quaternionFromR( final double[][] R, final double[] q )
	{
		assert cols( R ) >= 3;
		assert rows( R ) >= 3;
		assert rows( q ) >= 4;

		// The trace determines the method of decomposition
		final double d0 = R[ 0 ][ 0 ], d1 = R[ 1 ][ 1 ], d2 = R[ 2 ][ 2 ];
		final double rr = d0 + d1 + d2;
		if ( rr > 0 )
		{
			final double s = 0.5 / Math.sqrt( 1.0 + rr );
			q[ 1 ] = ( R[ 2 ][ 1 ] - R[ 1 ][ 2 ] ) * s;
			q[ 2 ] = ( R[ 0 ][ 2 ] - R[ 2 ][ 0 ] ) * s;
			q[ 3 ] = ( R[ 1 ][ 0 ] - R[ 0 ][ 1 ] ) * s;
			q[ 0 ] = 0.25 / s;
		}
		else
		{
			// Trace is less than zero, so need to determine which
			// major diagonal is largest
			if ( ( d0 > d1 ) && ( d0 > d2 ) )
			{
				final double s2 = Math.sqrt( 1 + d0 - d1 - d2 );
				final double s = 0.5 / s2;
				q[ 1 ] = 0.5 * s2;
				q[ 2 ] = ( R[ 0 ][ 1 ] + R[ 1 ][ 0 ] ) * s;
				q[ 3 ] = ( R[ 2 ][ 0 ] + R[ 0 ][ 2 ] ) * s;
				q[ 0 ] = ( R[ 2 ][ 1 ] - R[ 1 ][ 2 ] ) * s;
			}
			else if ( d1 > d2 )
			{
				final double s2 = Math.sqrt( 1 - d0 + d1 - d2 );
				final double s = 0.5 / s2;
				q[ 1 ] = ( R[ 0 ][ 1 ] + R[ 1 ][ 0 ] ) * s;
				q[ 2 ] = 0.5 * s2;
				q[ 3 ] = ( R[ 1 ][ 2 ] + R[ 2 ][ 1 ] ) * s;
				q[ 0 ] = ( R[ 0 ][ 2 ] - R[ 2 ][ 0 ] ) * s;
			}
			else
			{
				final double s2 = Math.sqrt( 1 - d0 - d1 + d2 );
				final double s = 0.5 / s2;
				q[ 1 ] = ( R[ 2 ][ 0 ] + R[ 0 ][ 2 ] ) * s;
				q[ 2 ] = ( R[ 1 ][ 2 ] + R[ 2 ][ 1 ] ) * s;
				q[ 3 ] = 0.5 * s2;
				q[ 0 ] = ( R[ 1 ][ 0 ] - R[ 0 ][ 1 ] ) * s;
			}
		}
	}

	/**
	 * compute a rotation matrix from a unit quaternion.
	 *
	 * @param q
	 *            unit quaternion (w, x, y, z).
	 * @param R
	 *            rotation matrix is stored here.
	 */
	public static void quaternionToR( final double[] q, final double[][] R )
	{
		assert rows( q ) >= 4;
		assert cols( R ) >= 3;
		assert rows( R ) >= 3;

		final double w = q[ 0 ];
		final double x = q[ 1 ];
		final double y = q[ 2 ];
		final double z = q[ 3 ];

		R[ 0 ][ 0 ] = w * w + x * x - y * y - z * z;
		R[ 0 ][ 1 ] = 2.0 * ( x * y - w * z );
		R[ 0 ][ 2 ] = 2.0 * ( x * z + w * y );

		R[ 1 ][ 0 ] = 2.0 * ( y * x + w * z );
		R[ 1 ][ 1 ] = w * w - x * x + y * y - z * z;
		R[ 1 ][ 2 ] = 2.0 * ( y * z - w * x );

		R[ 2 ][ 0 ] = 2.0 * ( z * x - w * y );
		R[ 2 ][ 1 ] = 2.0 * ( z * y + w * x );
		R[ 2 ][ 2 ] = w * w - x * x - y * y + z * z;
	}

	/**
	 * compute a quaternion from rotation axis and angle.
	 *
	 * @param axis
	 *            rotation axis as a unit vector.
	 * @param angle
	 *            rotation angle [rad].
	 * @param q
	 *            unit quaternion (w, x, y, z) is stored here.
	 */
	public static void quaternionFromAngleAxis( final double[] axis, final double angle, final double[] q )
	{
		assert rows( axis ) >= 3;
		assert rows( q ) >= 4;

		final double s = Math.sin( 0.5 * angle );
		q[ 0 ] = Math.cos( 0.5 * angle );
		q[ 1 ] = s * axis[ 0 ];
		q[ 2 ] = s * axis[ 1 ];
		q[ 3 ] = s * axis[ 2 ];
	}

	/**
	 * compute the quaternion product pq = p * q. applying rotation pq
	 * corresponds to applying first q, then p (i.e. same as multiplication of
	 * rotation matrices).
	 *
	 * @param p
	 *            unit quaternion (w, x, y, z).
	 * @param q
	 *            unit quaternion (w, x, y, z).
	 * @param pq
	 *            quaternion product p * q is stored here.
	 */
	public static void quaternionMultiply( final double[] p, final double[] q, final double[] pq )
	{
		assert rows( p ) >= 4;
		assert rows( q ) >= 4;
		assert rows( pq ) >= 4;

		final double pw = p[ 0 ];
		final double px = p[ 1 ];
		final double py = p[ 2 ];
		final double pz = p[ 3 ];

		final double qw = q[ 0 ];
		final double qx = q[ 1 ];
		final double qy = q[ 2 ];
		final double qz = q[ 3 ];

		pq[ 0 ] = pw * qw - px * qx - py * qy - pz * qz;
		pq[ 1 ] = pw * qx + px * qw + py * qz - pz * qy;
		pq[ 2 ] = pw * qy + py * qw + pz * qx - px * qz;
		pq[ 3 ] = pw * qz + pz * qw + px * qy - py * qx;
	}

	/**
	 * compute the power of a quaternion q raised to the exponent a.
	 *
	 * @param q
	 *            unit quaternion (w, x, y, z).
	 * @param a
	 *            exponent.
	 * @param qa
	 *            q^a is stored here.
	 */
	public static void quaternionPower( final double[] q, final double a, final double[] qa )
	{
		assert rows( q ) >= 4;
		assert rows( qa ) >= 4;

		final double theta2 = Math.acos( q[ 0 ] );
		final double s = Math.sin( a * theta2 ) / Math.sin( theta2 );
		if ( Double.isNaN( s ) )
		{
			qa[ 0 ] = 1;
			qa[ 1 ] = 0;
			qa[ 2 ] = 0;
			qa[ 3 ] = 0;
		}
		else
		{
			qa[ 0 ] = Math.cos( a * theta2 );
			qa[ 1 ] = s * q[ 1 ];
			qa[ 2 ] = s * q[ 2 ];
			qa[ 3 ] = s * q[ 3 ];
		}
	}

	/**
	 * invert quaternion, set q = p^{-1}. In place inversion (p==q) is
	 * permitted.
	 *
	 * @param p
	 *            unit quaternion (w, x, y, z).
	 * @param q
	 *            inverse of p is stored here.
	 */
	public static void quaternionInvert( final double[] p, final double[] q )
	{
		assert rows( p ) >= 4;
		assert rows( q ) >= 4;

		q[ 0 ] = p[ 0 ];
		q[ 1 ] = -p[ 1 ];
		q[ 2 ] = -p[ 2 ];
		q[ 3 ] = -p[ 3 ];
	}

	/**
	 * Apply quaternion rotation q to 3D point p, set qp = q * p. In place
	 * rotation (p==qp) is permitted.
	 *
	 * @param q
	 *            unit quaternion (w, x, y, z).
	 * @param p
	 *            3D point.
	 * @param qp
	 *            rotated 3D point is stored here.
	 */
	public static void quaternionApply( final double[] q, final double[] p, final double[] qp )
	{
		assert rows( q ) >= 4;
		assert rows( p ) >= 3;
		assert rows( qp ) >= 3;

		final double w = q[ 0 ];
		final double x = q[ 1 ];
		final double y = q[ 2 ];
		final double z = q[ 3 ];

		final double q0 = -x * p[ 0 ] - y * p[ 1 ] - z * p[ 2 ];
		final double q1 = w * p[ 0 ] + y * p[ 2 ] - z * p[ 1 ];
		final double q2 = w * p[ 1 ] + z * p[ 0 ] - x * p[ 2 ];
		final double q3 = w * p[ 2 ] + x * p[ 1 ] - y * p[ 0 ];

		qp[ 0 ] = -q0 * x + q1 * w - q2 * z + q3 * y;
		qp[ 1 ] = -q0 * y + q2 * w - q3 * x + q1 * z;
		qp[ 2 ] = -q0 * z + q3 * w - q1 * y + q2 * x;
	}

	/**
	 * Calculates the determinant of a 3x3 matrix given as a double[] (row major).
	 *
	 * @param a
	 *
	 * @return determinant
	 */
	final static public double det3x3( final double[] a )
	{
		assert a.length >= 9 : "Not enough coordinates.";

		return
			a[ 0 ] * a[ 4 ] * a[ 8 ] +
			a[ 3 ] * a[ 7 ] * a[ 2 ] +
			a[ 6 ] * a[ 1 ] * a[ 5 ] -
			a[ 2 ] * a[ 4 ] * a[ 6 ] -
			a[ 5 ] * a[ 7 ] * a[ 0 ] -
			a[ 8 ] * a[ 1 ] * a[ 3 ];
	}


	/**
	 * Calculate the determinant of a 3x3 matrix.
	 *
	 * @param m00
	 * @param m01
	 * @param m02
	 * @param m10
	 * @param m11
	 * @param m12
	 * @param m20
	 * @param m21
	 * @param m22
	 *
	 * @return
	 */
	final static public double det3x3(
			final double m00, final double m01, final double m02,
			final double m10, final double m11, final double m12,
			final double m20, final double m21, final double m22 )
	{
		return
			m00 * m11 * m22 +
			m10 * m21 * m02 +
			m20 * m01 * m12 -
			m02 * m11 * m20 -
			m12 * m21 * m00 -
			m22 * m01 * m10;
	}

	/**
	 * Invert a 3x3 matrix given as row major double[] in place.
	 *
	 * @param m matrix
	 *
	 * @throws IllegalArgumentException if matrix is not invertible
	 */
	final static public void invert3x3( final double[] m ) throws IllegalArgumentException
	{
		assert m.length >= 9 : "Not enough coordinates.";

		final double det = det3x3( m );
		if ( det == 0 ) throw new IllegalArgumentException( "Matrix not invertible." );

		final double i00 = ( m[ 4 ] * m[ 8 ] - m[ 5 ] * m[ 7 ] ) / det;
		final double i01 = ( m[ 2 ] * m[ 7 ] - m[ 1 ] * m[ 8 ] ) / det;
		final double i02 = ( m[ 1 ] * m[ 5 ] - m[ 2 ] * m[ 4 ] ) / det;

		final double i10 = ( m[ 5 ] * m[ 6 ] - m[ 3 ] * m[ 8 ] ) / det;
		final double i11 = ( m[ 0 ] * m[ 8 ] - m[ 2 ] * m[ 6 ] ) / det;
		final double i12 = ( m[ 2 ] * m[ 3 ] - m[ 0 ] * m[ 5 ] ) / det;

		final double i20 = ( m[ 3 ] * m[ 7 ] - m[ 4 ] * m[ 6 ] ) / det;
		final double i21 = ( m[ 1 ] * m[ 6 ] - m[ 0 ] * m[ 7 ] ) / det;
		final double i22 = ( m[ 0 ] * m[ 4 ] - m[ 1 ] * m[ 3 ] ) / det;

		m[ 0 ] = i00;
		m[ 1 ] = i01;
		m[ 2 ] = i02;

		m[ 3 ] = i10;
		m[ 4 ] = i11;
		m[ 5 ] = i12;

		m[ 6 ] = i20;
		m[ 7 ] = i21;
		m[ 8 ] = i22;
	}

	/**
	 * Invert a 3x3 matrix given as elements in row major order.
	 *
	 * @return inverted matrix as row major double[]
	 *
	 * @throws IllegalArgumentException if matrix is not invertible
	 */
	final static public double[] invert3x3(
			final double m00, final double m01, final double m02,
			final double m10, final double m11, final double m12,
			final double m20, final double m21, final double m22 ) throws IllegalArgumentException
	{
		final double det = det3x3( m00, m01, m02, m10, m11, m12, m20, m21, m22 );
		if ( det == 0 ) throw new IllegalArgumentException( "Matrix not invertible." );

		return new double[]{
				( m11 * m22 - m12 * m21 ) / det, ( m02 * m21 - m01 * m22 ) / det, ( m01 * m12 - m02 * m11 ) / det,
				( m12 * m20 - m10 * m22 ) / det, ( m00 * m22 - m02 * m20 ) / det, ( m02 * m10 - m00 * m12 ) / det,
				( m10 * m21 - m11 * m20 ) / det, ( m01 * m20 - m00 * m21 ) / det, ( m00 * m11 - m01 * m10 ) / det };
	}

	/**
	 * Inverts a (invertible) symmetric 3x3 matrix.
	 *
	 * @param m
	 *            symmetric matrix to invert.
	 * @param inverse
	 *            inverse of {@code m} is stored here.
	 */
	public static void invertSymmetric3x3( final double[][] m, final double[][] inverse )
	{
		final double a00 = m[ 2 ][ 2 ] * m[ 1 ][ 1 ] - m[ 1 ][ 2 ] * m[ 1 ][ 2 ];
		final double a01 = m[ 0 ][ 2 ] * m[ 1 ][ 2 ] - m[ 2 ][ 2 ] * m[ 0 ][ 1 ];
		final double a02 = m[ 0 ][ 1 ] * m[ 1 ][ 2 ] - m[ 0 ][ 2 ] * m[ 1 ][ 1 ];

		final double a11 = m[ 2 ][ 2 ] * m[ 0 ][ 0 ] - m[ 0 ][ 2 ] * m[ 0 ][ 2 ];
		final double a12 = m[ 0 ][ 1 ] * m[ 0 ][ 2 ] - m[ 0 ][ 0 ] * m[ 1 ][ 2 ];

		final double a22 = m[ 0 ][ 0 ] * m[ 1 ][ 1 ] - m[ 0 ][ 1 ] * m[ 0 ][ 1 ];

		final double Dinv = 1.0 / ( ( m[ 0 ][ 0 ] * a00 ) + ( m[ 1 ][ 0 ] * a01 ) + ( m[ 0 ][ 2 ] * a02 ) );

		inverse[ 0 ][ 0 ] = a00 * Dinv;
		inverse[ 1 ][ 0 ] = inverse[ 0 ][ 1 ] = a01 * Dinv;
		inverse[ 2 ][ 0 ] = inverse[ 0 ][ 2 ] = a02 * Dinv;
		inverse[ 1 ][ 1 ] = a11 * Dinv;
		inverse[ 2 ][ 1 ] = inverse[ 1 ][ 2 ] = a12 * Dinv;
		inverse[ 2 ][ 2 ] = a22 * Dinv;
	}

	/**
	 * Inverts a (invertible) symmetric 2x2 matrix.
	 *
	 * @param m
	 *            symmetric matrix to invert.
	 * @param inverse
	 *            inverse of {@code m} is stored here.
	 */
	public static void invertSymmetric2x2( final double[][] m, final double[][] inverse )
	{
		final double Dinv = 1.0 / ( m[ 0 ][ 0 ] * m[ 1 ][ 1 ] - m[ 1 ][ 0 ] * m[ 1 ][ 0 ] );
		inverse[ 0 ][ 0 ] = m[ 1 ][ 1 ] * Dinv;
		inverse[ 1 ][ 0 ] = inverse[ 0 ][ 1 ] = -m[ 1 ][ 0 ] * Dinv;
		inverse[ 1 ][ 1 ] = m[ 0 ][ 0 ] * Dinv;
	}

	public static String toString( final double[][] A )
	{
		return toString( A, "%6.3f " );
	}

	public static String toString( final double[][] A, final String format )
	{
		final int rows = rows( A );
		final int cols = cols( A );

		String result = "";
		for ( int i = 0; i < rows; ++i )
		{
			for ( int j = 0; j < cols; ++j )
				result += String.format( format, A[ i ][ j ] );
			result += "\n";
		}
		return result;
	}

	public static String toString( final double[] a )
	{
		return toString( a, "%6.3f " );
	}

	public static String toString( final double[] a, final String format )
	{
		final int rows = rows( a );

		String result = "";
		for ( int i = 0; i < rows; ++i )
			result += String.format( format, a[ i ] );
		result += "\n";
		return result;
	}
}
