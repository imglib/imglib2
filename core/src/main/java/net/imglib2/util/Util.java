/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

import java.util.List;

import net.imglib2.Dimensions;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ExponentialMathType;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class Util
{
	@SuppressWarnings( "unchecked" )
	public static < T > T[] genericArray( final int length )
	{
		return ( T[] ) ( new Object[ length ] );
	}

	public static double log2( final double value )
	{
		return Math.log( value ) / Math.log( 2.0 );
	}

	// TODO: move to ArrayUtil?
	public static double[] getArrayFromValue( final double value, final int numDimensions )
	{
		final double[] values = new double[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
			values[ d ] = value;

		return values;
	}

	// TODO: move to ArrayUtil?
	public static float[] getArrayFromValue( final float value, final int numDimensions )
	{
		final float[] values = new float[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
			values[ d ] = value;

		return values;
	}

	// TODO: move to ArrayUtil?
	public static int[] getArrayFromValue( final int value, final int numDimensions )
	{
		final int[] values = new int[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
			values[ d ] = value;

		return values;
	}

	// TODO: move to ArrayUtil?
	public static long[] getArrayFromValue( final long value, final int numDimensions )
	{
		final long[] values = new long[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
			values[ d ] = value;

		return values;
	}

	final public static float computeDistance( final RealLocalizable position1, final RealLocalizable position2 )
	{
		float dist = 0;

		final int n = position1.numDimensions();
		for ( int d = 0; d < n; ++d )
		{
			final float pos = position2.getFloatPosition( d ) - position1.getFloatPosition( d );

			dist += pos * pos;
		}

		return ( float ) Math.sqrt( dist );
	}

	final public static float computeDistance( final int[] position1, final int[] position2 )
	{
		float dist = 0;

		for ( int d = 0; d < position1.length; ++d )
		{
			final int pos = position2[ d ] - position1[ d ];

			dist += pos * pos;
		}

		return ( float ) Math.sqrt( dist );
	}

	final public static float computeDistance( final long[] position1, final long[] position2 )
	{
		float dist = 0;

		for ( int d = 0; d < position1.length; ++d )
		{
			final long pos = position2[ d ] - position1[ d ];

			dist += pos * pos;
		}

		return ( float ) Math.sqrt( dist );
	}

	final public static float computeLength( final int[] position )
	{
		float dist = 0;

		for ( int d = 0; d < position.length; ++d )
		{
			final int pos = position[ d ];

			dist += pos * pos;
		}

		return ( float ) Math.sqrt( dist );
	}

	final public static float computeLength( final long[] position )
	{
		float dist = 0;

		for ( int d = 0; d < position.length; ++d )
		{
			final long pos = position[ d ];

			dist += pos * pos;
		}

		return ( float ) Math.sqrt( dist );
	}

	public static long computeMedian( final long[] values )
	{
		final long temp[] = values.clone();
		long median;

		final int length = temp.length;

		quicksort( temp, 0, length - 1 );

		if ( length % 2 == 1 ) // odd length
			median = temp[ length / 2 ];
		else
			// even length
			median = ( temp[ length / 2 ] + temp[ ( length / 2 ) - 1 ] ) / 2;

		return median;
	}

	public static double computeMedian( final double[] values )
	{
		final double temp[] = values.clone();
		double median;

		final int length = temp.length;

		quicksort( temp, 0, length - 1 );

		if ( length % 2 == 1 ) // odd length
			median = temp[ length / 2 ];
		else
			// even length
			median = ( temp[ length / 2 ] + temp[ ( length / 2 ) - 1 ] ) / 2;

		return median;
	}

	/**
	 * Computes the percentile of a collection of doubles (percentile 0.5
	 * roughly corresponds to median)
	 * 
	 * @param values
	 *            - the values
	 * @param percentile
	 *            - the percentile [0...1]
	 * @return the corresponding value
	 */
	public static double computePercentile( final double[] values, final double percentile )
	{
		final double temp[] = values.clone();
		final int length = temp.length;

		quicksort( temp );

		return temp[ Math.min( length - 1, Math.max( 0, ( int ) Math.round( ( length - 1 ) * percentile ) ) ) ];
	}

	public static double computeAverageDouble( final List< Double > values )
	{
		final double size = values.size();
		double avg = 0;

		for ( final double v : values )
			avg += v / size;

		return avg;
	}

	public static float computeAverageFloat( final List< Float > values )
	{
		final double size = values.size();
		double avg = 0;

		for ( final double v : values )
			avg += v / size;

		return ( float ) avg;
	}

	public static float computeMinimum( final List< Float > values )
	{
		float min = Float.MAX_VALUE;

		for ( final float v : values )
			if ( v < min )
				min = v;

		return min;
	}

	public static float computeMaximum( final List< Float > values )
	{
		float max = -Float.MAX_VALUE;

		for ( final float v : values )
			if ( v > max )
				max = v;

		return max;
	}

	public static float computeAverage( final float[] values )
	{
		final double size = values.length;
		double avg = 0;

		for ( final float v : values )
			avg += v / size;

		return ( float ) avg;
	}

	public static double computeAverage( final double[] values )
	{
		final double size = values.length;
		double avg = 0;

		for ( final double v : values )
			avg += v / size;

		return avg;
	}

	public static double computeMin( final double[] values )
	{
		double min = values[ 0 ];

		for ( final double v : values )
			if ( v < min )
				min = v;

		return min;
	}

	public static double computeMax( final double[] values )
	{
		double max = values[ 0 ];

		for ( final double v : values )
			if ( v > max )
				max = v;

		return max;
	}

	public static float computeMedian( final float[] values )
	{
		final float temp[] = values.clone();
		float median;

		final int length = temp.length;

		quicksort( temp, 0, length - 1 );

		if ( length % 2 == 1 ) // odd length
			median = temp[ length / 2 ];
		else
			// even length
			median = ( temp[ length / 2 ] + temp[ ( length / 2 ) - 1 ] ) / 2;

		return median;
	}

	public static void quicksort( final long[] data, final int left, final int right )
	{
		if ( data == null || data.length < 2 )
			return;
		int i = left, j = right;
		final long x = data[ ( left + right ) / 2 ];
		do
		{
			while ( data[ i ] < x )
				i++;
			while ( x < data[ j ] )
				j--;
			if ( i <= j )
			{
				final long temp = data[ i ];
				data[ i ] = data[ j ];
				data[ j ] = temp;
				i++;
				j--;
			}
		}
		while ( i <= j );
		if ( left < j )
			quicksort( data, left, j );
		if ( i < right )
			quicksort( data, i, right );
	}

	public static void quicksort( final double[] data )
	{
		quicksort( data, 0, data.length - 1 );
	}

	public static void quicksort( final double[] data, final int left, final int right )
	{
		if ( data == null || data.length < 2 )
			return;
		int i = left, j = right;
		final double x = data[ ( left + right ) / 2 ];
		do
		{
			while ( data[ i ] < x )
				i++;
			while ( x < data[ j ] )
				j--;
			if ( i <= j )
			{
				final double temp = data[ i ];
				data[ i ] = data[ j ];
				data[ j ] = temp;
				i++;
				j--;
			}
		}
		while ( i <= j );
		if ( left < j )
			quicksort( data, left, j );
		if ( i < right )
			quicksort( data, i, right );
	}

	public static void quicksort( final float[] data )
	{
		quicksort( data, 0, data.length - 1 );
	}

	public static void quicksort( final float[] data, final int left, final int right )
	{
		if ( data == null || data.length < 2 )
			return;
		int i = left, j = right;
		final float x = data[ ( left + right ) / 2 ];
		do
		{
			while ( data[ i ] < x )
				i++;
			while ( x < data[ j ] )
				j--;
			if ( i <= j )
			{
				final float temp = data[ i ];
				data[ i ] = data[ j ];
				data[ j ] = temp;
				i++;
				j--;
			}
		}
		while ( i <= j );
		if ( left < j )
			quicksort( data, left, j );
		if ( i < right )
			quicksort( data, i, right );
	}

	public static void quicksort( final double[] data, final int[] sortAlso, final int left, final int right )
	{
		if ( data == null || data.length < 2 )
			return;
		int i = left, j = right;
		final double x = data[ ( left + right ) / 2 ];
		do
		{
			while ( data[ i ] < x )
				i++;
			while ( x < data[ j ] )
				j--;
			if ( i <= j )
			{
				final double temp = data[ i ];
				data[ i ] = data[ j ];
				data[ j ] = temp;

				final int temp2 = sortAlso[ i ];
				sortAlso[ i ] = sortAlso[ j ];
				sortAlso[ j ] = temp2;

				i++;
				j--;
			}
		}
		while ( i <= j );
		if ( left < j )
			quicksort( data, sortAlso, left, j );
		if ( i < right )
			quicksort( data, sortAlso, i, right );
	}

	public static double gLog( final double z, final double c )
	{
		if ( c == 0 )
			return z;
		return Math.log10( ( z + Math.sqrt( z * z + c * c ) ) / 2.0 );
	}

	public static float gLog( final float z, final float c )
	{
		if ( c == 0 )
			return z;
		return ( float ) Math.log10( ( z + Math.sqrt( z * z + c * c ) ) / 2.0 );
	}

	public static double gLogInv( final double w, final double c )
	{
		if ( c == 0 )
			return w;
		return Math.pow( 10, w ) - ( ( ( c * c ) * Math.pow( 10, -w ) ) / 4.0 );
	}

	public static double gLogInv( final float w, final float c )
	{
		if ( c == 0 )
			return w;
		return Math.pow( 10, w ) - ( ( ( c * c ) * Math.pow( 10, -w ) ) / 4.0 );
	}

	public static boolean isApproxEqual( final float a, final float b, final float threshold )
	{
		if ( a == b )
			return true;
		else if ( a + threshold > b && a - threshold < b )
			return true;
		else
			return false;
	}

	public static boolean isApproxEqual( final double a, final double b, final double threshold )
	{
		if ( a == b )
			return true;
		else if ( a + threshold > b && a - threshold < b )
			return true;
		else
			return false;
	}

	public static int round( final float value )
	{
		return ( int ) ( value + ( 0.5f * Math.signum( value ) ) );
	}

	public static long round( final double value )
	{
		return ( long ) ( value + ( 0.5d * Math.signum( value ) ) );
	}

	/**
	 * This method creates a gaussian kernel
	 * 
	 * @param sigma
	 *            Standard Derivation of the gaussian function
	 * @param normalize
	 *            Normalize integral of gaussian function to 1 or not...
	 * @return double[] The gaussian kernel
	 * 
	 */
	public static double[] createGaussianKernel1DDouble( final double sigma, final boolean normalize )
	{
		int size = 3;
		final double[] gaussianKernel;

		if ( sigma <= 0 )
		{
			gaussianKernel = new double[ 3 ];
			gaussianKernel[ 1 ] = 1;
		}
		else
		{
			size = Math.max( 3, ( 2 * ( int ) ( 3 * sigma + 0.5 ) + 1 ) );

			final double two_sq_sigma = 2 * sigma * sigma;
			gaussianKernel = new double[ size ];

			for ( int x = size / 2; x >= 0; --x )
			{
				final double val = Math.exp( -( x * x ) / two_sq_sigma );

				gaussianKernel[ size / 2 - x ] = val;
				gaussianKernel[ size / 2 + x ] = val;
			}
		}

		if ( normalize )
		{
			double sum = 0;
			for ( final double value : gaussianKernel )
				sum += value;

			for ( int i = 0; i < gaussianKernel.length; ++i )
				gaussianKernel[ i ] /= sum;
		}

		return gaussianKernel;
	}

	/**
	 * This method creates a gaussian kernel
	 * 
	 * @param sigma
	 *            Standard Derivation of the gaussian function in the desired
	 *            {@link Type}
	 * @param normalize
	 *            Normalize integral of gaussian function to 1 or not...
	 * @return T[] The gaussian kernel
	 * 
	 */
	@SuppressWarnings( "unchecked" )
	public static < T extends ExponentialMathType< T > > T[] createGaussianKernel1D( final T sigma, final boolean normalize )
	{
		final T[] gaussianKernel;
		int kernelSize;

		final T zero = sigma.createVariable();
		final T two = sigma.createVariable();
		final T one = sigma.createVariable();
		final T minusOne = sigma.createVariable();
		final T two_sq_sigma = zero.createVariable();
		final T sum = sigma.createVariable();
		final T value = sigma.createVariable();
		final T xPos = sigma.createVariable();
		final T cs = sigma.createVariable();

		zero.setZero();
		one.setOne();

		two.setOne();
		two.add( one );

		minusOne.setZero();
		minusOne.sub( one );

		if ( sigma.compareTo( zero ) <= 0 )
		{
			kernelSize = 3;
			// NB: Need explicit cast to T[] to satisfy javac;
			// See: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
			gaussianKernel = ( T[] ) genericArray( 3 ); // zero.createArray1D( 3
														// );
			gaussianKernel[ 1 ].set( one );
		}
		else
		{
			// size = Math.max(3, (int) (2 * (int) (3 * sigma + 0.5) + 1));
			cs.set( sigma );
			cs.mul( 3.0 );
			cs.round();
			cs.mul( 2.0 );
			cs.add( one );

			kernelSize = Util.round( cs.getRealFloat() );

			// kernelsize has to be at least 3
			kernelSize = Math.max( 3, kernelSize );

			// kernelsize has to be odd
			if ( kernelSize % 2 == 0 )
				++kernelSize;

			// two_sq_sigma = 2 * sigma * sigma;
			two_sq_sigma.set( two );
			two_sq_sigma.mul( sigma );
			two_sq_sigma.mul( sigma );

			// NB: Need explicit cast to T[] to satisfy javac;
			// See: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
			gaussianKernel = ( T[] ) genericArray( kernelSize ); // zero.createArray1D(
																	// kernelSize
																	// );

			for ( int i = 0; i < gaussianKernel.length; ++i )
				gaussianKernel[ i ] = zero.createVariable();

			// set the xPos to kernelSize/2
			xPos.setZero();
			for ( int x = 1; x <= kernelSize / 2; ++x )
				xPos.add( one );

			for ( int x = kernelSize / 2; x >= 0; --x )
			{
				// final double val = Math.exp( -(x * x) / two_sq_sigma );
				value.set( xPos );
				value.mul( xPos );
				value.mul( minusOne );
				value.div( two_sq_sigma );
				value.exp();

				gaussianKernel[ kernelSize / 2 - x ].set( value );
				gaussianKernel[ kernelSize / 2 + x ].set( value );

				xPos.sub( one );
			}
		}

		if ( normalize )
		{
			sum.setZero();

			for ( final T val : gaussianKernel )
				sum.add( val );

			for ( int i = 0; i < gaussianKernel.length; ++i )
				gaussianKernel[ i ].div( sum );
		}

		for ( int i = 0; i < gaussianKernel.length; ++i )
			System.out.println( gaussianKernel[ i ] );

		return gaussianKernel;
	}

	public static int getSuggestedKernelDiameter( final double sigma )
	{
		int size = 3;

		if ( sigma > 0 )
			size = Math.max( 3, ( 2 * ( int ) ( 3 * sigma + 0.5 ) + 1 ) );

		return size;
	}

	public static String printCoordinates( final float[] value )
	{
		String out = "(Array empty)";

		if ( value == null || value.length == 0 )
			return out;
		out = "(" + value[ 0 ];

		for ( int i = 1; i < value.length; i++ )
			out += ", " + value[ i ];

		out += ")";

		return out;
	}

	public static String printCoordinates( final double[] value )
	{
		String out = "(Array empty)";

		if ( value == null || value.length == 0 )
			return out;
		out = "(" + value[ 0 ];

		for ( int i = 1; i < value.length; i++ )
			out += ", " + value[ i ];

		out += ")";

		return out;
	}

	public static String printCoordinates( final RealLocalizable localizable )
	{
		String out = "(RealLocalizable empty)";

		if ( localizable == null || localizable.numDimensions() == 0 )
			return out;
		out = "(" + localizable.getFloatPosition( 0 );

		for ( int i = 1; i < localizable.numDimensions(); i++ )
			out += ", " + localizable.getFloatPosition( i );

		out += ")";

		return out;
	}

	public static String printInterval( final Interval interval )
	{
		String out = "(Interval empty)";

		if ( interval == null || interval.numDimensions() == 0 )
			return out;

		out = "[" + interval.min( 0 );

		for ( int i = 1; i < interval.numDimensions(); i++ )
			out += ", " + interval.min( i );

		out += "] -> [" + interval.max( 0 );

		for ( int i = 1; i < interval.numDimensions(); i++ )
			out += ", " + interval.max( i );

		out += "], dimensions (" + interval.dimension( 0 );

		for ( int i = 1; i < interval.numDimensions(); i++ )
			out += ", " + interval.dimension( i );

		out += ")";

		return out;
	}

	public static String printCoordinates( final int[] value )
	{
		String out = "(Array empty)";

		if ( value == null || value.length == 0 )
			return out;
		out = "(" + value[ 0 ];

		for ( int i = 1; i < value.length; i++ )
			out += ", " + value[ i ];

		out += ")";

		return out;
	}

	public static String printCoordinates( final long[] value )
	{
		String out = "(Array empty)";

		if ( value == null || value.length == 0 )
			return out;
		out = "(" + value[ 0 ];

		for ( int i = 1; i < value.length; i++ )
			out += ", " + value[ i ];

		out += ")";

		return out;
	}

	public static String printCoordinates( final boolean[] value )
	{
		String out = "(Array empty)";

		if ( value == null || value.length == 0 )
			return out;
		out = "(";

		if ( value[ 0 ] )
			out += "1";
		else
			out += "0";

		for ( int i = 1; i < value.length; i++ )
		{
			out += ", ";
			if ( value[ i ] )
				out += "1";
			else
				out += "0";
		}

		out += ")";

		return out;
	}

	public static int pow( final int a, final int b )
	{
		if ( b == 0 )
			return 1;
		else if ( b == 1 )
			return a;
		else
		{
			int result = a;

			for ( int i = 1; i < b; i++ )
				result *= a;

			return result;
		}
	}

	public static < T extends Type< T > & Comparable< T >> T max( final T value1, final T value2 )
	{
		if ( value1.compareTo( value2 ) >= 0 )
			return value1;
		return value2;
	}

	public static < T extends Type< T > & Comparable< T >> T min( final T value1, final T value2 )
	{
		if ( value1.compareTo( value2 ) <= 0 )
			return value1;
		return value2;
	}

	public static boolean[][] getRecursiveCoordinates( final int numDimensions )
	{
		final boolean[][] positions = new boolean[ Util.pow( 2, numDimensions ) ][ numDimensions ];

		setCoordinateRecursive( numDimensions - 1, numDimensions, new int[ numDimensions ], positions );

		return positions;
	}

	/**
	 * recursively get coordinates covering all binary combinations for the
	 * given dimensionality
	 * 
	 * example for 3d:
	 * 
	 * x y z index 0 0 0 [0] 1 0 0 [1] 0 1 0 [2] 1 1 0 [3] 0 0 1 [4] 1 0 1 [5] 0
	 * 1 1 [6] 1 1 1 [7]
	 * 
	 * All typical call will look like that:
	 * 
	 * boolean[][] positions = new boolean[ MathLib.pow( 2, numDimensions ) ][
	 * numDimensions ]; MathLib.setCoordinateRecursive( numDimensions - 1,
	 * numDimensions, new int[ numDimensions ], positions );
	 * 
	 * @param dimension
	 *            - recusively changed current dimension, init with
	 *            numDimensions - 1
	 * @param numDimensions
	 *            - the number of dimensions
	 * @param location
	 *            - recursively changed current state, init with new int[
	 *            numDimensions ]
	 * @param result
	 *            - where the result will be stored when finished, needes a
	 *            boolean[ MathLib.pow( 2, numDimensions ) ][ numDimensions ]
	 */
	public static void setCoordinateRecursive( final int dimension, final int numDimensions, final int[] location, final boolean[][] result )
	{
		final int[] newLocation0 = new int[ numDimensions ];
		final int[] newLocation1 = new int[ numDimensions ];

		for ( int d = 0; d < numDimensions; d++ )
		{
			newLocation0[ d ] = location[ d ];
			newLocation1[ d ] = location[ d ];
		}

		newLocation0[ dimension ] = 0;
		newLocation1[ dimension ] = 1;

		if ( dimension == 0 )
		{
			// compute the index in the result array ( binary to decimal
			// conversion )
			int index0 = 0, index1 = 0;

			for ( int d = 0; d < numDimensions; d++ )
			{
				index0 += newLocation0[ d ] * pow( 2, d );
				index1 += newLocation1[ d ] * pow( 2, d );
			}

			// fill the result array
			for ( int d = 0; d < numDimensions; d++ )
			{
				result[ index0 ][ d ] = ( newLocation0[ d ] == 1 );
				result[ index1 ][ d ] = ( newLocation1[ d ] == 1 );
			}
		}
		else
		{
			setCoordinateRecursive( dimension - 1, numDimensions, newLocation0, result );
			setCoordinateRecursive( dimension - 1, numDimensions, newLocation1, result );
		}

	}

	/**
	 * Deprecated, use {@link Intervals#dimensionsAsLongArray(Dimensions)}
	 * instead.
	 * 
	 * <p>
	 * Create a long[] with the dimensions of an {@link Interval}.
	 * </p>
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly.
	 * </p>
	 * 
	 * @param interval
	 * 
	 * @return dimensions of the interval as a new long[]
	 */
	@Deprecated
	final static public long[] intervalDimensions( final Interval interval )
	{
		final long[] dimensions = new long[ interval.numDimensions() ];
		interval.dimensions( dimensions );
		return dimensions;
	}

	final static public int[] long2int( final long[] a )
	{
		final int[] i = new int[ a.length ];

		for ( int d = 0; d < a.length; ++d )
			i[ d ] = ( int ) a[ d ];

		return i;
	}

	final static public long[] int2long( final int[] i )
	{
		final long[] l = new long[ i.length ];

		for ( int d = 0; d < l.length; ++d )
			l[ d ] = i[ d ];

		return l;
	}

	/**
	 * Deprecated, use {@link Intervals#maxAsLongArray(Interval)} instead.
	 * 
	 * <p>
	 * Create a long[] with the max coordinates of an {@link Interval}.
	 * </p>
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly.
	 * </p>
	 * 
	 * @param interval
	 * 
	 * @return dimensions of the interval as a new long[]
	 */
	@Deprecated
	final static public long[] intervalMax( final Interval interval )
	{
		final long[] max = new long[ interval.numDimensions() ];
		interval.max( max );
		return max;
	}

	/**
	 * Deprecated, use {@link Intervals#minAsLongArray(Interval)} instead.
	 * 
	 * <p>
	 * Create a long[] with the min coordinates of an {@link Interval}.
	 * </p>
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly.
	 * </p>
	 * 
	 * @param interval
	 * 
	 * @return dimensions of the interval as a new long[]
	 */
	@Deprecated
	final static public long[] intervalMin( final Interval interval )
	{
		final long[] min = new long[ interval.numDimensions() ];
		interval.min( min );
		return min;
	}

	/**
	 * Deprecated, no replacement.
	 * 
	 * <p>
	 * Create a double[] with the dimensions of a {@link RealInterval}.
	 * Dimensions are returned as <em>max</em> - <em>min</em>.
	 * </p>
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly.
	 * </p>
	 * 
	 * @param interval
	 * 
	 * @return dimensions of the interval as a new double[]
	 */
	@Deprecated
	final static public double[] realIntervalDimensions( final RealInterval interval )
	{
		final int n = interval.numDimensions();
		final double[] dimensions = new double[ interval.numDimensions() ];

		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = interval.realMax( d ) - interval.realMin( d );

		return dimensions;
	}

	/**
	 * Deprecated, use {@link Intervals#maxAsDoubleArray(RealInterval)} instead.
	 * 
	 * <p>
	 * Create a double[] with the max coordinates of a {@link RealInterval}.
	 * Dimensions are returned as <em>max</em> - <em>min</em>.
	 * </p>
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly.
	 * </p>
	 * 
	 * @param interval
	 * 
	 * @return dimensions of the interval as a new double[]
	 */
	@Deprecated
	final static public double[] realIntervalMax( final RealInterval interval )
	{
		final int n = interval.numDimensions();
		final double[] max = new double[ interval.numDimensions() ];

		for ( int d = 0; d < n; ++d )
			max[ d ] = interval.realMax( d );

		return max;
	}

	/**
	 * Deprecated, use {@link Intervals#minAsDoubleArray(RealInterval)} instead.
	 * 
	 * <p>
	 * Create a double[] with the min coordinates of a {@link RealInterval}.
	 * Dimensions are returned as <em>max</em> - <em>min</em>.
	 * </p>
	 * 
	 * <p>
	 * Keep in mind that creating arrays wildly is not good practice and
	 * consider using the interval directly.
	 * </p>
	 * 
	 * @param interval
	 * 
	 * @return dimensions of the interval as a new double[]
	 */
	@Deprecated
	final static public double[] realIntervalMin( final RealInterval interval )
	{
		final int n = interval.numDimensions();
		final double[] min = new double[ interval.numDimensions() ];

		for ( int d = 0; d < n; ++d )
			min[ d ] = interval.realMin( d );

		return min;
	}

	/**
	 * Gets an instance of T from the {@link RandomAccessibleInterval} by
	 * querying the value at the min coordinate
	 * 
	 * @param <T>
	 *            - the T
	 * @param rai
	 *            - the {@link RandomAccessibleInterval}
	 * @return - an instance of T
	 */
	final public static < T, F extends Interval & RandomAccessible< T >> T getTypeFromInterval( final F rai )
	{
		// create RandomAccess
		final RandomAccess< T > randomAccess = rai.randomAccess();

		// place it at the first pixel
		rai.min( randomAccess );

		return randomAccess.get();
	}

	/**
	 * Gets an instance of T from the {@link RandomAccessible}
	 * 
	 * @param <T>
	 *            - the T
	 * @param rai
	 *            - the {@link RandomAccessible}
	 * @return - an instance of T
	 */
	final public static < T > T getTypeFromRandomAccess( final RandomAccessible< T > ra )
	{
		// test that it is not an interval, because in this case a simple get()
		// at the position of creation will fail
		if ( RandomAccessibleInterval.class.isInstance( ra ) )
			return getTypeFromInterval( ( RandomAccessibleInterval< T > ) ra );
		return ra.randomAccess().get();
	}

	/**
	 * Gets an instance of T from the {@link RandomAccessibleInterval} by
	 * querying the value at the min coordinate
	 * 
	 * @param <T>
	 *            - the T
	 * @param rai
	 *            - the {@link RandomAccessibleInterval}
	 * @return - an instance of T
	 */
	final public static < T, F extends RealInterval & RealRandomAccessible< T >> T getTypeFromRealInterval( final F rai )
	{
		// create RealRandomAccess
		final RealRandomAccess< T > realRandomAccess = rai.realRandomAccess();

		// place it at the first pixel
		rai.realMin( realRandomAccess );

		return realRandomAccess.get();
	}

	/**
	 * Gets an instance of T from the {@link RealRandomAccessible}
	 * 
	 * @param <T>
	 *            - the T
	 * @param rai
	 *            - the {@link RealRandomAccessible}
	 * @return - an instance of T
	 */
	final public static < T > T getTypeFromRealRandomAccess( final RealRandomAccessible< T > ra )
	{
		// test that it is not an interval, because in this case a simple get()
		// at the position of creation will fail
		if ( RealRandomAccessibleRealInterval.class.isInstance( ra ) )
			return getTypeFromRealInterval( ( RealRandomAccessibleRealInterval< T > ) ra );
		return ra.realRandomAccess().get();
	}

	/**
	 * Create an {@link ArrayImgFactory} if an image of the requested
	 * <code>targetSize</code> could be held in an {@link ArrayImg}. Otherwise
	 * return a {@link CellImgFactory} with as large as possible cell size.
	 * 
	 * @param targetSize
	 *            size of image that the factory should be able to create.
	 * @param type
	 *            type of the factory.
	 * @return an {@link ArrayImgFactory} or a {@link CellImgFactory}.
	 */
	public static < T extends NativeType< T > > ImgFactory< T > getArrayOrCellImgFactory( final Dimensions targetSize, final T type )
	{
		if ( Intervals.numElements( targetSize ) <= Integer.MAX_VALUE )
			return new ArrayImgFactory< T >();
		final int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / type.getEntitiesPerPixel().getRatio(), 1.0 / targetSize.numDimensions() );
		return new CellImgFactory< T >( cellSize );
	}

	/**
	 * Create an {@link ArrayImgFactory} if an image of the requested
	 * <code>targetSize</code> could be held in an {@link ArrayImg}. Otherwise
	 * return a {@link CellImgFactory} with cell size
	 * <code>targetCellSize</code> (or as large as possible if
	 * <code>targetCellSize</code> is too large).
	 * 
	 * @param targetSize
	 *            size of image that the factory should be able to create.
	 * @param targetCellSize
	 *            if a {@link CellImgFactory} is created, what should be the
	 *            cell size.
	 * @param type
	 *            type of the factory.
	 * @return an {@link ArrayImgFactory} or a {@link CellImgFactory}.
	 */
	public static < T extends NativeType< T > > ImgFactory< T > getArrayOrCellImgFactory( final Dimensions targetSize, final int targetCellSize, final T type )
	{
		if ( Intervals.numElements( targetSize ) <= Integer.MAX_VALUE )
			return new ArrayImgFactory< T >();
		final int cellSize;
		if ( Math.pow( targetCellSize, targetSize.numDimensions() ) <= Integer.MAX_VALUE )
			cellSize = targetCellSize;
		else
			cellSize = ( int ) Math.pow( Integer.MAX_VALUE / type.getEntitiesPerPixel().getRatio(), 1.0 / targetSize.numDimensions() );
		return new CellImgFactory< T >( cellSize );
	}

	/**
	 * (Hopefully) fast floor log<sub>2</sub> of an unsigned(!) integer value.
	 * 
	 * @param v
	 *            unsigned integer
	 * @return floor log<sub>2</sub>
	 */
	final static public int ldu( int v )
	{
		int c = 0;
		do
		{
			v >>= 1;
			++c;
		}
		while ( v > 1 );
		return c;
	}

	/**
	 * Checks whether n {@link IterableInterval} have the same iteration order.
	 */
	public static boolean equalIterationOrder( final IterableInterval< ? >... intervals )
	{
		final Object order = intervals[ 0 ].iterationOrder();
		for ( int i = 1; i < intervals.length; i++ )
		{
			if ( !order.equals( intervals[ i ].iterationOrder() ) )
				return false;
		}

		return true;
	}

	/**
	 * Writes min(a,b) into a
	 * 
	 * @param a
	 * @param b
	 */
	final static public void min( final double[] a, final double[] b )
	{
		for ( int i = 0; i < a.length; ++i )
			if ( b[ i ] < a[ i ] )
				a[ i ] = b[ i ];
	}

	/**
	 * Writes max(a,b) into a
	 * 
	 * @param a
	 * @param b
	 */
	final static public void max( final double[] a, final double[] b )
	{
		for ( int i = 0; i < a.length; ++i )
			if ( b[ i ] > a[ i ] )
				a[ i ] = b[ i ];
	}
}
