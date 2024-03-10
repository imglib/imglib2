package net.imglib2;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

public class CosLookupExample
{


	static class OldLoopkup
	{
		// static lookup table for the blending function
		final static private double[] lookUp;

		private static int indexFor( final double d ) { return (int)( d * 1000.0 + 0.5 ); }

		static
		{
			lookUp = new double[ 1001 ];

			for ( double d = 0; d <= 1.0001; d = d + 0.001 )
				lookUp[ indexFor( d ) ] = ( Math.cos( ( 1 - d ) * Math.PI ) + 1 ) / 2;
		}

		static double fn( final double d )
		{
			return lookUp[ indexFor( d ) ];
		}
	}

	static class SmallLookup
	{
		// [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ]
		//   0                             1
		//   n = 10
		//   d = (double) i / n

		private static final int n = 100;

		// static lookup table for the blending function
		private static final double[] lookUp = createLookup( n );

		private static double[] createLookup( final int n )
		{
			final double[] lookup = new double[ n + 1 ];
			for ( int i = 0; i <= n; i++ )
			{
				final double d = ( double ) i / n;
				lookup[ i ] = ( Math.cos( ( 1 - d ) * Math.PI ) + 1 ) / 2;
			}
			return lookup;
		}

		static double fn( final double d )
		{
			return lookUp[ ( int ) Math.round( d * n ) ];
		}
	}

	static class SmallLookup2
	{
		// [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ]
		//   0                             1
		//   n = 10
		//   d = (double) i / n

		private static final int n = 30;

		// static lookup table for the blending function
		private static final double[] lookUp = createLookup( n );

		private static double[] createLookup( final int n )
		{
			final double[] lookup = new double[ n + 2 ];
			for ( int i = 0; i <= n; i++ )
			{
				final double d = ( double ) i / n;
				lookup[ i ] = ( Math.cos( ( 1 - d ) * Math.PI ) + 1 ) / 2;
			}
			lookup[ n + 1 ] = lookup[ n ];
			return lookup;
		}

		static double fn( final double d )
		{
			final int i = ( int ) ( d * n );
			final double s = ( d * n ) - i;
			return lookUp[ i ] * (1. - s) + lookUp[ i + 1 ] * s;
		}
	}


	static double fn( final double d )
	{
		return ( Math.cos( ( 1 - d ) * Math.PI ) + 1 ) / 2;
	}


	public static void main( String[] args )
	{
		final int nSamples = 10000;
		System.out.println( String.format( "OldLoopkup avg error = %f, max error = %f", avgError( nSamples, OldLoopkup::fn ), maxError( nSamples, OldLoopkup::fn ) ) );
		System.out.println( String.format( "SmallLookup avg error = %f, max error = %f", avgError( nSamples, SmallLookup::fn ), maxError( nSamples, SmallLookup::fn ) ) );
		System.out.println( String.format( "SmallLookup2 avg error = %f, max error = %f", avgError( nSamples, SmallLookup2::fn ), maxError( nSamples, SmallLookup2::fn ) ) );
	}

	private static double avgError( final int nSamples, DoubleUnaryOperator fn )
	{
		double sum = 0;
		for ( int i = 0; i < nSamples; ++i )
		{
			final double d = ( double ) i / nSamples;
			double diff = Math.abs( fn.applyAsDouble( d ) - fn( d ) );
			sum += diff;
		}
		return sum / nSamples;
	}

	private static double maxError( final int nSamples, DoubleUnaryOperator fn )
	{
		double max = 0;
		for ( int i = 0; i < nSamples; ++i )
		{
			final double d = ( double ) i / nSamples;
			double diff = Math.abs( fn.applyAsDouble( d ) - fn( d ) );
			max = Math.max( max, diff );
		}
		return max;
	}
}
