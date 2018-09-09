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

package net.imglib2.util;

import net.imglib2.Dimensions;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.operators.ValueEquals;
import net.imglib2.view.Views;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.StreamSupport;

/**
 * A collection of general-purpose utility methods for working with ImgLib2 data
 * structures.
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Curtis Rueden
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

	final public static double distance( final RealLocalizable position1, final RealLocalizable position2 )
	{
		double dist = 0;

		final int n = position1.numDimensions();
		for ( int d = 0; d < n; ++d )
		{
			final double pos = position2.getDoublePosition( d ) - position1.getDoublePosition( d );

			dist += pos * pos;
		}

		return Math.sqrt( dist );
	}

	final public static double distance( final long[] position1, final long[] position2 )
	{
		double dist = 0;

		for ( int d = 0; d < position1.length; ++d )
		{
			final long pos = position2[ d ] - position1[ d ];

			dist += pos * pos;
		}

		return Math.sqrt( dist );
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
	public static double percentile( final double[] values, final double percentile )
	{
		final double temp[] = values.clone();
		final int length = temp.length;
		final int pos = Math.min( length - 1, Math.max( 0, ( int ) Math.round( ( length - 1 ) * percentile ) ) );

		KthElement.kthElement( pos, temp );

		return temp[ pos ];
	}

	public static double averageDouble( final List< Double > values )
	{
		final double size = values.size();
		double avg = 0;

		for ( final double v : values )
			avg += v / size;

		return avg;
	}

	public static float averageFloat( final List< Float > values )
	{
		final double size = values.size();
		double avg = 0;

		for ( final double v : values )
			avg += v / size;

		return ( float ) avg;
	}

	public static float min( final List< Float > values )
	{
		float min = Float.MAX_VALUE;

		for ( final float v : values )
			if ( v < min )
				min = v;

		return min;
	}

	public static float max( final List< Float > values )
	{
		float max = -Float.MAX_VALUE;

		for ( final float v : values )
			if ( v > max )
				max = v;

		return max;
	}

	public static float average( final float[] values )
	{
		final double size = values.length;
		double avg = 0;

		for ( final float v : values )
			avg += v / size;

		return ( float ) avg;
	}

	public static double average( final double[] values )
	{
		final double size = values.length;
		double avg = 0;

		for ( final double v : values )
			avg += v / size;

		return avg;
	}

	public static double min( final double[] values )
	{
		double min = values[ 0 ];

		for ( final double v : values )
			if ( v < min )
				min = v;

		return min;
	}

	public static double max( final double[] values )
	{
		double max = values[ 0 ];

		for ( final double v : values )
			if ( v > max )
				max = v;

		return max;
	}

	public static long median( final long[] values )
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

	public static double median( final double[] values )
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

	public static float median( final float[] values )
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
	
	public static void quicksort( final long[] data )
	{
		quicksort( data, 0, data.length - 1 );
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

	public static < T extends Type< T > & Comparable< T > > T max( final T value1, final T value2 )
	{
		if ( value1.compareTo( value2 ) >= 0 )
			return value1;
		else
			return value2;
	}

	public static < T extends Type< T > & Comparable< T > > T min( final T value1, final T value2 )
	{
		if ( value1.compareTo( value2 ) <= 0 )
			return value1;
		else
			return value2;
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
	 * Gets an instance of T from the {@link RandomAccessibleInterval} by
	 * querying the value at the min coordinate
	 * 
	 * @param <T>
	 *            - the T
	 * @param rai
	 *            - the {@link RandomAccessibleInterval}
	 * @return - an instance of T
	 */
	final public static < T, F extends Interval & RandomAccessible< T > > T getTypeFromInterval( final F rai )
	{
		// create RandomAccess
		final RandomAccess< T > randomAccess = rai.randomAccess();

		// place it at the first pixel
		rai.min( randomAccess );

		return randomAccess.get();
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
			return new ArrayImgFactory<>( type );
		final int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / type.getEntitiesPerPixel().getRatio(), 1.0 / targetSize.numDimensions() );
		return new CellImgFactory<>( type, cellSize );
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
			return new ArrayImgFactory<>( type );
		final int cellSize;
		if ( Math.pow( targetCellSize, targetSize.numDimensions() ) <= Integer.MAX_VALUE )
			cellSize = targetCellSize;
		else
			cellSize = ( int ) Math.pow( Integer.MAX_VALUE / type.getEntitiesPerPixel().getRatio(), 1.0 / targetSize.numDimensions() );
		return new CellImgFactory<>( type, cellSize );
	}

	/**
	 * Create an appropriate {@link ImgFactory} for the requested
	 * {@code targetSize} and {@code type}. If the type is a {@link NativeType},
	 * then {@link #getArrayOrCellImgFactory(Dimensions, NativeType)} is used;
	 * if not, a {@link ListImgFactory} is returned.
	 * 
	 * @param targetSize
	 *            size of image that the factory should be able to create.
	 * @param type
	 *            type of the factory.
	 * @return an {@link ArrayImgFactory}, {@link CellImgFactory} or
	 *         {@link ListImgFactory} as appropriate.
	 */
	public static < T > ImgFactory< T > getSuitableImgFactory( final Dimensions targetSize, final T type )
	{
		if ( type instanceof NativeType )
		{
			// NB: Eclipse does not demand the cast to ImgFactory< T >, but javac does.
			@SuppressWarnings( { "cast", "rawtypes", "unchecked" } )
			final ImgFactory< T > arrayOrCellImgFactory = ( ImgFactory< T > ) getArrayOrCellImgFactory( targetSize, ( NativeType ) type );
			return arrayOrCellImgFactory;
		}
		return new ListImgFactory<>( type );
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
	 * Determines whether the two {@link Localizable} objects have the same
	 * position, with {@code long} precision.
	 * <p>
	 * At first glance, this method may appear to be unnecessary, since there is
	 * also {@link #locationsEqual(RealLocalizable, RealLocalizable)}, which is
	 * more general. The difference is that this method compares the positions
	 * using {@link Localizable#getLongPosition(int)}, which has higher
	 * precision in integer space than
	 * {@link RealLocalizable#getDoublePosition(int)} does, which is what the
	 * {@link #locationsEqual(RealLocalizable, RealLocalizable)} method uses.
	 * </p>
	 * 
	 * @param l1
	 *            The first {@link Localizable}.
	 * @param l2
	 *            The second {@link Localizable}.
	 * @return True iff the positions are the same, including dimensionality.
	 * @see Localizable#getLongPosition(int)
	 */
	public static boolean locationsEqual( final Localizable l1, final Localizable l2 )
	{
		final int numDims = l1.numDimensions();
		if ( l2.numDimensions() != numDims )
			return false;
		for ( int d = 0; d < numDims; d++ )
		{
			if ( l1.getLongPosition( d ) != l2.getLongPosition( d ) )
				return false;
		}
		return true;
	}

	/**
	 * Determines whether the two {@link RealLocalizable} objects have the same
	 * position, with {@code double} precision.
	 * 
	 * @param l1
	 *            The first {@link RealLocalizable}.
	 * @param l2
	 *            The second {@link RealLocalizable}.
	 * @return True iff the positions are the same, including dimensionality.
	 * @see RealLocalizable#getDoublePosition(int)
	 */
	public static boolean locationsEqual( final RealLocalizable l1, final RealLocalizable l2 )
	{
		final int numDims = l1.numDimensions();
		if ( l2.numDimensions() != numDims )
			return false;
		for ( int d = 0; d < numDims; d++ )
		{
			if ( l1.getDoublePosition( d ) != l2.getDoublePosition( d ) )
				return false;
		}
		return true;
	}

	/**
	 * Checks if both images have equal intervals and content.
	 */
	public static < T extends ValueEquals< U >, U > boolean imagesEqual( final RandomAccessibleInterval< ? extends T > a, final RandomAccessibleInterval< ? extends U > b )
	{
		return imagesEqual( a, b, ValueEquals::valueEquals );
	}

	/**
	 * Checks if both images have equal intervals and content.
	 * A predicate must be given to check if two pixels are equal.
	 */
	public static < T, U > boolean imagesEqual( final RandomAccessibleInterval< ? extends T > a, final RandomAccessibleInterval< ? extends U > b, final BiPredicate< T, U > pixelEquals )
	{
		if ( !Intervals.equals( a, b ) )
			return false;
		for ( final Pair< ? extends T, ? extends U > pair : Views.interval( Views.pair( a, b ), b ) )
			if ( !pixelEquals.test( pair.getA(), pair.getB() ) )
				return false;
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

	/**
	 * Returns the content of {@code Iterable<RealType>} as array of doubles.
	 */
	public static double[] asDoubleArray( final Iterable< ? extends RealType< ? > > iterable )
	{
		return StreamSupport.stream( iterable.spliterator(), false ).mapToDouble( RealType::getRealDouble ).toArray();
	}

	/**
	 * Returns the pixels of an RandomAccessibleInterval of RealType as array of doubles.
	 * The pixels are sorted in flat iteration order.
	 */
	public static double[] asDoubleArray( final RandomAccessibleInterval< ? extends RealType< ? > > rai )
	{
		return asDoubleArray( Views.flatIterable( rai ) );
	}

	/**
	 * Returns the pixels of an image of RealType as array of doubles.
	 * The pixels are sorted in flat iteration order.
	 */
	public static double[] asDoubleArray( final Img< ? extends RealType< ? > > image )
	{
		return asDoubleArray( ( RandomAccessibleInterval< ? extends RealType< ? > > ) image );
	}

	/**
	 * This method should be used in implementations of {@link ValueEquals}, to
	 * override {@link Object#equals(Object)}.
	 *
	 * @see net.imglib2.type.AbstractNativeType#equals(Object)
	 */
	public static < T extends ValueEquals< T > > boolean valueEqualsObject( final T a, final Object b )
	{
		if ( !a.getClass().isInstance( b ) )
			return false;
		@SuppressWarnings( "unchecked" )
		final T t = ( T ) b;
		return a.valueEquals( t );
	}

	public static int combineHash( final int hash1, final int hash2 )
	{
		return 31 * hash1 + hash2;
	}
}
