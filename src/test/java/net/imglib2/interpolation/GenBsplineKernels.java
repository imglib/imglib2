package net.imglib2.interpolation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import net.imglib2.interpolation.randomaccess.BSplineInterpolator;

public class GenBsplineKernels {

	public static void main(String[] args) throws IOException
	{

		int order = 5;
		ArrayList<double[]> pieces = splinePieces( order );
		ArrayList<double[]> cpieces = centeredSplinePieces( order );

//		System.out.println( "spline pieces: ");
//		for( int i = 0; i < pieces.size(); i++ )
//			System.out.println(  "  " + Arrays.toString( pieces.get( i )));
//
//		System.out.println( " ");
//		System.out.println( "center: " + kernelCenter( order ));
//		System.out.println( " ");
//
//		System.out.println( "centered spline pieces: ");
//		for( int i = 0; i < cpieces.size(); i++ )
//			System.out.println(  "  " + Arrays.toString( cpieces.get( i )));


		/*
		 * Good - uncentered splines sum to one
		 */
//		testKnotValueSums( pieces );


		centeredPolysToCsv();		

//		tests();
//		validateForCubicKernel();
	}
	
	public static void testKnotValueSums( final ArrayList<double[]> polys )
	{
		double[] knotValues = valuesAtKnots( polys );
		double sum = Arrays.stream( knotValues ).sum();
		
		System.out.println("values: "  + Arrays.toString( knotValues ));
		System.out.println("   sum: "  + sum );
	}
	
	public static double[] valuesAtKnots( final ArrayList<double[]> polys )
	{
		int N = polys.size() + 1;
		double[] knotValues = new double[ N + 1 ];
		for( int i = 0; i <= N; i++ )
		{
			knotValues[ i ] = apply( polys, (double)i );
		}
		return knotValues;
	}
	
	public static double bsplineKernel( double x, int order )
	{
		return apply(
				splinePieces( order ),
				x + kernelCenter( order ));
	}

	public static void validateForCubicKernel()
	{
		double start = -3;
		double end = 3.0001;
		double step = 0.1;

		ArrayList<double[]> cubK = centeredSplinePieces( 3 );

		double y = BSplineInterpolator.evaluate3Normalized( 0 );

//		double z = apply( cubK, 2 );
		double z = bsplineKernel( 0, 3 );

		System.out.println( "y : " + y );
		System.out.println( "z : " + z );

		for( double x = start; x <= end; x += step)
		{
			y = BSplineInterpolator.evaluate3Normalized( x );
			z = bsplineKernel( x, 3 );
			System.out.println(  String.format("%f  :  %f  %f  #  %f ", x, y, z, (Math.abs(y-z)) ));
			//System.out.println( "abs diff: " + (Math.abs(y-z)));
		}
	}

	public static ArrayList<double[]> centeredSplinePieces( int i )
	{
		double c = kernelCenter( i );

		ArrayList<double[]> ppolysUncentered = splinePieces( i );
		ArrayList<double[]> ppolys = new ArrayList<>();

		for( double[] p : ppolysUncentered )
			ppolys.add( shift( p, -c ));

		return ppolys;
	}
	
	public static void writeCenteredSplinePieces()
	{
		for( int i = 0; i <= 5; i++ )
		{
			ArrayList<double[]> ppolys = centeredSplinePieces( i );

			for( int j = 0; j < ppolys.size(); j++ )
				System.out.println( "p " + Arrays.toString( ppolys.get( j )) );

			System.out.println( " " );
		}
	}

	public static void testShift()
	{
		double[] p = new double[]{ 0, 0, 1 };
		double[] q = shift( p , -2 );
		System.out.println( "q : " + Arrays.toString( q ));
	}
	
	/**
	 * spline pieces directly from the formula below, resulting in
	 * kernels not centered at the origin.
	 */
	public static void shiftedSplinePieces()
	{
		for( int i = 0; i <= 5; i++ )
		{
			ArrayList<double[]> ppolys = splinePieces( i );
			for( int j = 0; j < ppolys.size(); j++ )
				System.out.println( "p " + Arrays.toString( ppolys.get( j )) );

			System.out.println( " " );
		}
	}
	
	public static double kernelCenter( int n )
	{
		return (double)( n + 1 ) / 2.0;
	}
	
	public static void tests() throws IOException
	{
//		System.out.println( fact( 5 ));
//		System.out.println( nCk( 10, 3 ));

//		double[] a = new double[]{ -1, 1 };
//		double[] b = new double[]{ 1, 0, 1 };
//		double[] c = polyMult( a, b );
//		System.out.println( "c: " + Arrays.toString( c ));

//		double[] c = shiftedPoly( 3, -1 );
//		System.out.println( "c: " + Arrays.toString( c ));


//		long j = 3;
//		long sng = j % 2 == 0 ? 1 : -1;
//		System.out.println( sng );

//		double[] p0 = bsplinePolyPiece( 0, 0 );
//		System.out.println( "p0: " + Arrays.toString( p0 ));
//
//		double[] p1 = bsplinePolyPiece( 3, 0 );
//		System.out.println( "p1: " + Arrays.toString( p1 ));
		
//		bsplineAll( 0 );
//		bsplineAll( 1 );
//		bsplineAll( 2 );
//		bsplineAll( 3 );


//		ArrayList<double[]> ppolys = bsplineAll( 1 );
//		double x = 3;
//		double y = apply( ppolys.get( 0 ), x );
//		System.out.println( " f(" + x + ") = " + y  );
		double start = -1.0;
		double end = 7.00001;

		toCsv( 	"/home/john/tmp/bsplineKernelTests/ker1.csv", 
				splinePieces( 0 ),
				start, end, 0.1 );

		toCsv( 	"/home/john/tmp/bsplineKernelTests/ker1.csv", 
				splinePieces( 1 ),
				start, end, 0.1 );

		toCsv( 	"/home/john/tmp/bsplineKernelTests/ker2.csv", 
				splinePieces( 2 ),
				start, end, 0.1 );

		toCsv( 	"/home/john/tmp/bsplineKernelTests/ker3.csv", 
				splinePieces( 3 ),
				start, end, 0.1 );

		toCsv( 	"/home/john/tmp/bsplineKernelTests/ker4.csv", 
				splinePieces( 4 ),
				start, end, 0.1 );
//
		toCsv( 	"/home/john/tmp/bsplineKernelTests/ker5.csv", 
				splinePieces( 5 ),
				start, end, 0.1 );
		
	}
	
	public static void centeredPolysToCsv() throws IOException
	{
		double start = -7.0;
		double end = 7.00001;

		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker0.csv", 
				splinePieces( 0 ),
				start, end, 0.1, 0.5 );

		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker1.csv", 
				splinePieces( 1 ),
				start, end, 0.1, 1 );

		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker2.csv", 
				splinePieces( 2 ),
				start, end, 0.1, 1.5 );

		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker3.csv", 
				splinePieces( 3 ),
				start, end, 0.1, 2.0 );

		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker4.csv", 
				splinePieces( 4 ),
				start, end, 0.1, 2.5 );

		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker5.csv", 
				splinePieces( 5 ),
				start, end, 0.1, 3.0 );


//		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker0.csv", 
//				centeredSplinePieces( 0 ),
//				start, end, 0.1, 0.5 );
//
//		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker1.csv", 
//				centeredSplinePieces( 1 ),
//				start, end, 0.1, 1 );
//
//		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker2.csv", 
//				centeredSplinePieces( 2 ),
//				start, end, 0.1, 1.5 );
//
//		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker3.csv", 
//				centeredSplinePieces( 3 ),
//				start, end, 0.1, 2.0 );
//
//		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker4.csv", 
//				centeredSplinePieces( 4 ),
//				start, end, 0.1, 2.5 );
//
//		toCsv( 	"/home/john/tmp/bsplineKernelTests/cker5.csv", 
//				centeredSplinePieces( 5 ),
//				start, end, 0.1, 3.0 );
	}
	

	/**
	 * Return a new polynomial that is shifted in x by s.  Specifically if p is
	 *   p_0 + p_1 * x + ... + p_n * x^n
	 * 
	 * this method returns
	 * 
	 *   p_0 + p_1 * (x+s)  + ... + p_n * (x+s)^n
	 *   
	 * but in canonical form: 
	 *   = q_0 + q_1 * x + ... + q_n * x^n
	 * 
	 * @param p the polynomial coeficients
	 * @param s the shift
	 * @return the shifted polynomial
	 */
	public static double[] shift( final double[] p, final double s )
	{
		int order = p.length;
		double[] q = new double[ order ];
		q[ 0 ] = p[ 0 ];

		for( int i = 1; i < order; i++ )
		{
			double[] tmp = shiftedPoly( i, s );

			for( int j = 0; j < tmp.length; j++ )
				tmp[ j ] *= p[ i ];

			q = add( q, tmp );
		}

		return q;
	}

	public static void toCsv( String fpath, ArrayList<double[]> piecewisePolys, double start, double end, double step, double offset ) throws IOException
	{
		StringBuffer s = new StringBuffer();

		for( double xx = start; xx <= end; xx += step)
		{
			double x = xx + offset;
			int i = (int)Math.floor( x );
			double y = 0.0;
			if( i >= 0 &&  i < piecewisePolys.size() )
			{
//				System.out.println( " xx " + xx );
//				System.out.println( " xo " + x  );
				y = apply( piecewisePolys.get( i ), x );
			}
//			System.out.println( " y " + y );
//			System.out.println( "  " );

			s.append( String.format("%f,%f\n", xx, y ));
		}
		Files.write( Paths.get( fpath ), s.toString().getBytes(), StandardOpenOption.CREATE);

	}
	
	public static void toCsv( String fpath, ArrayList<double[]> piecewisePolys, double start, double end, double step ) throws IOException
	{
		toCsv( fpath, piecewisePolys, start, end, step, 0.0 );
	}

	public static double apply( final ArrayList<double[]> polys, double x )
	{
		int i = (int)Math.floor( x );
		if( i < 0 )
			return 0.0;
		else if( i >= polys.size())
			return 0.0;
		else
			return apply( polys.get( i ), x );
	}
	
	public static double apply( final double[] poly, double x )
	{
		double y = poly[ 0 ];
		for( int i = 1; i < poly.length; i++ )
		{
			y += ( poly[ i ] * pow( x, i ));
		}
		return y;
	}
	
	public static double pow( final double x, final int n )
	{
		assert n >= 0;

		double y = 1;
		for( int i = 0; i < n; i++ )
		{
			y *= x;
		}
		return y;	
	}
	
	public static ArrayList<double[]> splinePieces( long n )
	{
		ArrayList< double[] > piecewisePolys = new ArrayList<>();

		double[] p;
		double[] last = null;
		for( long j = 0; j < n + 1; j++ )
		{
			p = bsplinePolyPiece( n, j );

//			System.out.println( "    p: " + Arrays.toString( p ));
		
			double[] q;
			if( last != null )
			{
				q = add( last, p );
			}
			else
				q = p;

//			System.out.println( "  q: " + Arrays.toString( q ));
//			System.out.println( "   ");
			piecewisePolys.add( q );

			last = q;
		}

		return piecewisePolys;
	}

	/**
	 * Generate part of the polynomial that is present at x >= j
	 * 
	 * @param n
	 * @param j
	 * @return
	 */
	public static double[] bsplinePolyPiece( long n, long j )
	{
		long a = nCk( n + 1, j );
		long sng = ( j % 2 == 0 ) ? 1 : -1;
		long b = fact( n );
		
		double coef = ((double) a / b ) * sng; 
		double[] p = shiftedPoly( n, (double)-j );

		for( int i = 0; i < p.length; i++ )
			p[ i ] *= coef;

		return p;
	}
	
	/**
	 * returns the polynomial coefficients for (x + j)^n
	 * @param n
	 * @param j
	 * @return
	 */
	public static double[] shiftedPoly( final long n, final double j )
	{
		double[] start = new double[]{ j, 1 };
		double[] out = start;
		for( int i = 0; i < n - 1; i++ )
			out = polyMult( out, start );

		return out;
	}
	
	/**
	 * Multiply polynomial coefficients  
	 * @param a first coefs 
	 * @param b second coefs 
	 * @return coefficients
	 */
	public static double[] polyMult( final double[] a, final double[] b )
	{
		int M = a.length + b.length - 1;

		double[] out = new double[ M ];
		for( int i = 0; i < a.length; i++ )
		{
			for( int j = 0; j < b.length; j++ )
			{
				out[ i + j ] += a[ i ] * b[ j ];
			}
		}

		return out;
	}

	public static double[] add( final double[] a, final double[] b )
	{
		int Na = a.length;
		int Nb = b.length;
		int N = Na > Nb ? Na : Nb;

		double[] c = new double[ N ];
		for( int i = 0; i < N; i++ )
		{
			if( i < Na && i < Nb )
				c[ i ] = a[ i ] + b[ i ];
			else if( i < Na )
				c[ i ] = a[ i ];
			else if( i < Nb )
				c[ i ] = b[ i ];
		}

		return c;
	}

	public static long fact( long i )
	{
		long out = 1;
		while( i > 1 )
		{
			out *= i;
			i--;
		}
		return out;
	}
	
	/**
	 * Binomial coefficient 
	 * n choose k
	 */
	public static long nCk( long n, long k )
	{
//		long out = 1;
//		for( long i = 1; i <= k; i++ )
//		{
//			out *= (( n + 1 - i ) / i );
//		}
//		return out;

		long num = fact( n );
		long den = fact( k ) * fact( n - k );
		return num / den;
	}

}
