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

package net.imglib2.algorithm.gauss3;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.imglib2.Dimensions;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * Convolution with a separable symmetric kernel.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public final class SeparableSymmetricConvolution
{
	/**
	 * Convolve source with a separable symmetric kernel and write the result to
	 * output. In-place operation (source==target) is supported.
	 * 
	 * <p>
	 * If the target type T is {@link DoubleType}, all calculations are done in
	 * double precision. For all other target {@link RealType RealTypes} float
	 * precision is used. General {@link NumericType NumericTypes} are computed
	 * in their own precision. The source type S and target type T are either
	 * both {@link RealType RealTypes} or both the same type.
	 * 
	 * @param halfkernels
	 *            an array containing half-kernels for every dimension. A
	 *            half-kernel is the upper half (starting at the center pixel)
	 *            of the symmetric convolution kernel for a given dimension.
	 * @param source
	 *            source image, must be sufficiently padded (e.g.
	 *            {@link Views#extendMirrorSingle(RandomAccessibleInterval)}) to
	 *            provide values for the target interval plus a border of half
	 *            the kernel size.
	 * @param target
	 *            target image.
	 * @param service
	 *            service providing threads for multi-threading
	 * @param <S>
	 *            source type
	 * @param <T>
	 *            target type
	 * @throws IncompatibleTypeException
	 *             if source and target type are not compatible (they must be
	 *             either both {@link RealType RealTypes} or the same type).
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static < S extends NumericType< S >, T extends NumericType< T > > void convolve( final double[][] halfkernels, final RandomAccessible< S > source, final RandomAccessibleInterval< T > target, final ExecutorService service ) throws IncompatibleTypeException
	{
		final T targetType = Util.getTypeFromInterval( target );
		final S sourceType = getType( source, target );
		if ( targetType instanceof RealType )
		{
			if ( !( sourceType instanceof RealType ) )
				throw new IncompatibleTypeException( sourceType, "RealType source required for convolving into a RealType target" );
			// NB: Casting madness thanks to a long standing javac bug;
			// see e.g. http://bugs.sun.com/view_bug.do?bug_id=6548436
			// TODO: remove casting madness as soon as the bug is fixed
			final Object oTargetType = targetType;
			if ( oTargetType instanceof DoubleType )
				convolveRealTypeDouble( halfkernels, ( RandomAccessible ) source, ( RandomAccessibleInterval ) target, service );
			else
				convolveRealTypeFloat( halfkernels, ( RandomAccessible ) source, ( RandomAccessibleInterval ) target, service );
		}
		else
		{
			if ( !targetType.getClass().isInstance( sourceType ) )
				throw new IncompatibleTypeException( sourceType, targetType.getClass().getCanonicalName() + " source required for convolving into a " + targetType.getClass().getCanonicalName() + " target" );
			if ( targetType instanceof NativeType )
				convolveNativeType( halfkernels, ( RandomAccessible ) source, ( RandomAccessibleInterval ) target, service );
			else
				convolveNumericType( halfkernels, ( RandomAccessible ) source, ( RandomAccessibleInterval ) target, service );
		}
	}

	private static < S extends RealType< S >, T extends RealType< T > > void convolveRealTypeFloat( final double[][] halfkernels, final RandomAccessible< S > source, final RandomAccessibleInterval< T > target, final ExecutorService service )
	{
		final FloatType type = new FloatType();
		final ImgFactory< FloatType > imgfac = getImgFactory( target, halfkernels, type );
		if ( canUseBufferedConvolver( target, halfkernels ) )
			convolve( halfkernels, source, target, FloatConvolverRealTypeBuffered.< S, FloatType >factory(), FloatConvolverRealTypeBuffered.< FloatType, FloatType >factory(), FloatConvolverRealTypeBuffered.< FloatType, T >factory(), FloatConvolverRealTypeBuffered.< S, T >factory(), imgfac, type, service );
		else
			convolve( halfkernels, source, target, FloatConvolverRealType.< S, FloatType >factory(), FloatConvolverRealType.< FloatType, FloatType >factory(), FloatConvolverRealType.< FloatType, T >factory(), FloatConvolverRealType.< S, T >factory(), imgfac, type, service );
	}

	private static < S extends RealType< S >, T extends RealType< T > > void convolveRealTypeDouble( final double[][] halfkernels, final RandomAccessible< S > source, final RandomAccessibleInterval< T > target, final ExecutorService service )
	{
		final DoubleType type = new DoubleType();
		final ImgFactory< DoubleType > imgfac = getImgFactory( target, halfkernels, type );
		if ( canUseBufferedConvolver( target, halfkernels ) )
			convolve( halfkernels, source, target, DoubleConvolverRealTypeBuffered.< S, DoubleType >factory(), DoubleConvolverRealTypeBuffered.< DoubleType, DoubleType >factory(), DoubleConvolverRealTypeBuffered.< DoubleType, T >factory(), DoubleConvolverRealTypeBuffered.< S, T >factory(), imgfac, type, service );
		else
			convolve( halfkernels, source, target, DoubleConvolverRealType.< S, DoubleType >factory(), DoubleConvolverRealType.< DoubleType, DoubleType >factory(), DoubleConvolverRealType.< DoubleType, T >factory(), DoubleConvolverRealType.< S, T >factory(), imgfac, type, service );
	}

	private static < T extends NumericType< T > & NativeType< T > > void convolveNativeType( final double[][] halfkernels, final RandomAccessible< T > source, final RandomAccessibleInterval< T > target, final ExecutorService service )
	{
		final T type = Util.getTypeFromInterval( target );
		final ConvolverFactory< T, T > convfac;
		if ( canUseBufferedConvolver( target, halfkernels ) )
			convfac = ConvolverNativeTypeBuffered.factory( type );
		else
			convfac = ConvolverNativeType.factory( type );
		final ImgFactory< T > imgfac = getImgFactory( target, halfkernels, type );
		convolve( halfkernels, source, target, convfac, convfac, convfac, convfac, imgfac, type, service );
	}

	private static < T extends NumericType< T > > void convolveNumericType( final double[][] halfkernels, final RandomAccessible< T > source, final RandomAccessibleInterval< T > target, final ExecutorService service )
	{
		final T type = Util.getTypeFromInterval( target );
		final ConvolverFactory< T, T > convfac = ConvolverNumericType.factory( type );
		convolve( halfkernels, source, target, convfac, convfac, convfac, convfac, new ListImgFactory< T >(), type, service );
	}

	/**
	 * Get an instance of type T from a {@link RandomAccess} on accessible that
	 * is positioned at the min of interval.
	 * 
	 * @param accessible
	 * @param interval
	 * @return type instance
	 */
	private static < T extends NumericType< T > > T getType( final RandomAccessible< T > accessible, final Interval interval )
	{
		final RandomAccess< T > a = accessible.randomAccess();
		interval.min( a );
		return a.get();
	}

	public static < S, T > void convolve1d( final double[] halfkernel, final RandomAccessible< S > source, final RandomAccessibleInterval< T > target, final ConvolverFactory< S, T > convolverFactoryST, final ExecutorService service )
	{
		final long[] sourceOffset = new long[] { 1 - halfkernel.length };
		convolveOffset( halfkernel, source, sourceOffset, target, target, 0, convolverFactoryST, service, 1 );
	}

	/**
	 * Convolve source with a separable symmetric kernel and write the result to
	 * output. In-place operation (source==target) is supported. Calculations
	 * are done in the intermediate type determined by the
	 * {@link ConvolverFactory ConvolverFactories}.
	 * 
	 * @param halfkernels
	 *            an array containing half-kernels for every dimension. A
	 *            half-kernel is the upper half (starting at the center pixel)
	 *            of the symmetric convolution kernel for a given dimension.
	 * @param source
	 *            source image, must be sufficiently padded (e.g.
	 *            {@link Views#extendMirrorSingle(RandomAccessibleInterval)}) to
	 *            provide values for the target interval plus a border of half
	 *            the kernel size.
	 * @param target
	 *            target image.
	 * @param convolverFactorySI
	 *            produces line convolvers reading source type and writing
	 *            temporary type.
	 * @param convolverFactoryII
	 *            produces line convolvers reading temporary type and writing
	 *            temporary type.
	 * @param convolverFactoryIT
	 *            produces line convolvers reading temporary type and writing
	 *            target type.
	 * @param convolverFactoryST
	 *            produces line convolvers reading source type and writing
	 *            target type.
	 * @param imgFactory
	 *            factory to create temporary images.
	 * @param type
	 *            instance of the temporary image type.
	 * @param service
	 *            service providing threads for multi-threading
	 */
	public static < S, I, T > void convolve( final double[][] halfkernels, final RandomAccessible< S > source, final RandomAccessibleInterval< T > target, final ConvolverFactory< S, I > convolverFactorySI, final ConvolverFactory< I, I > convolverFactoryII, final ConvolverFactory< I, T > convolverFactoryIT, final ConvolverFactory< S, T > convolverFactoryST, final ImgFactory< I > imgFactory, final I type, final ExecutorService service )
	{
		final int n = source.numDimensions();
		if ( n == 1 )
		{
			convolve1d( halfkernels[ 0 ], source, target, convolverFactoryST, service );
		}
		else
		{
			// FIXME: is there a better way to determine the number of threads
			final int numThreads = Runtime.getRuntime().availableProcessors();
			final int numTasks = numThreads > 1 ? numThreads * 4 : 1;
			final long[] sourceOffset = new long[ n ];
			final long[] targetOffset = new long[ n ];
			target.min( sourceOffset );
			for ( int d = 0; d < n; ++d )
			{
				targetOffset[ d ] = -sourceOffset[ d ];
				sourceOffset[ d ] += 1 - halfkernels[ d ].length;
			}

			final long[][] tmpdims = getTempImageDimensions( target, halfkernels );
			Img< I > tmp1 = imgFactory.create( tmpdims[ 0 ], type );
			if ( n == 2 )
			{
				convolveOffset( halfkernels[ 0 ], source, sourceOffset, tmp1, tmp1, 0, convolverFactorySI, service, numTasks );
				convolveOffset( halfkernels[ 1 ], tmp1, targetOffset, target, target, 1, convolverFactoryIT, service, numTasks );
			}
			else
			{
				Img< I > tmp2 = imgFactory.create( tmpdims[ 1 ], type );
				final long[] zeroOffset = new long[ n ];
				convolveOffset( halfkernels[ 0 ], source, sourceOffset, tmp1, new FinalInterval( tmpdims[ 0 ] ), 0, convolverFactorySI, service, numTasks );
				for ( int d = 1; d < n - 1; ++d )
				{
					convolveOffset( halfkernels[ d ], tmp1, zeroOffset, tmp2, new FinalInterval( tmpdims[ d ] ), d, convolverFactoryII, service, numTasks );
					final Img< I > tmp = tmp2;
					tmp2 = tmp1;
					tmp1 = tmp;
				}
				convolveOffset( halfkernels[ n - 1 ], tmp1, targetOffset, target, target, n - 1, convolverFactoryIT, service, numTasks );
			}
		}
	}

	/**
	 * 1D convolution in dimension d.
	 */
	static < S, T > void convolveOffset( final double[] halfkernel, final RandomAccessible< S > source, final long[] sourceOffset, final RandomAccessible< T > target, final Interval targetInterval, final int d, final ConvolverFactory< S, T > factory, final ExecutorService service, final int numTasks )
	{
		final int n = source.numDimensions();
		final int k1 = halfkernel.length - 1;
		long tmp = 1;
		for ( int i = 0; i < n; ++i )
			if ( i != d )
				tmp *= targetInterval.dimension( i );
		final long endIndex = tmp;

		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		final long[] dim = new long[ n ];
		targetInterval.min( min );
		targetInterval.max( max );
		targetInterval.dimensions( dim );
		dim[ d ] = 1;

		final long[] srcmin = new long[ n ];
		final long[] srcmax = new long[ n ];
		for ( int i = 0; i < n; ++i )
		{
			srcmin[ i ] = min[ i ] + sourceOffset[ i ];
			srcmax[ i ] = max[ i ] + sourceOffset[ i ];
		}
		srcmax[ d ] += 2 * k1;

		final ArrayList< Future< Void > > futures = new ArrayList< Future< Void > >();

		for ( int taskNum = 0; taskNum < numTasks; ++taskNum )
		{
			final long myStartIndex = taskNum * ( ( endIndex + 1 ) / numTasks );
			final long myEndIndex = ( taskNum == numTasks - 1 ) ? endIndex : ( taskNum + 1 ) * ( ( endIndex + 1 ) / numTasks );
			final Callable< Void > r = new Callable< Void >()
			{
				@Override
				public Void call()
				{
					final RandomAccess< S > in = source.randomAccess( new FinalInterval( srcmin, srcmax ) );
					final RandomAccess< T > out = target.randomAccess( targetInterval );
					final Runnable convolver = factory.create( halfkernel, in, out, d, targetInterval.dimension( d ) );

					out.setPosition( min );
					in.setPosition( srcmin );

					final long[] moveToStart = new long[ n ];
					IntervalIndexer.indexToPosition( myStartIndex, dim, moveToStart );
					out.move( moveToStart );
					in.move( moveToStart );

					for ( long index = myStartIndex; index < myEndIndex; ++index )
					{
						convolver.run();
						out.setPosition( min[ d ], d );
						in.setPosition( srcmin[ d ], d );
						for ( int i = 0; i < n; ++i )
						{
							if ( i != d )
							{
								out.fwd( i );
								if ( out.getLongPosition( i ) > max[ i ] )
								{
									out.setPosition( min[ i ], i );
									in.setPosition( srcmin[ i ], i );
								}
								else
								{
									in.fwd( i );
									break;
								}
							}
						}
					}
					return null;
				}
			};
			futures.add( service.submit( r ) );
		}

		for ( Future< Void > future : futures )
		{
			try
			{
				future.get();
			}
			catch ( InterruptedException e )
			{
				e.printStackTrace();
			}
			catch ( ExecutionException e )
			{
				e.printStackTrace();
			}
		}

	}

	static long[][] getTempImageDimensions( final Dimensions targetsize, final double[][] halfkernels )
	{
		final int n = targetsize.numDimensions();
		final long[][] tmpdims = new long[ n ][];
		tmpdims[ n - 1 ] = new long[ n ];
		targetsize.dimensions( tmpdims[ n - 1 ] );
		for ( int d = n - 2; d >= 0; --d )
		{
			tmpdims[ d ] = tmpdims[ d + 1 ].clone();
			tmpdims[ d ][ d + 1 ] += 2 * halfkernels[ d + 1 ].length - 2;
		}
		return tmpdims;
	}

	static boolean canUseBufferedConvolver( final Dimensions targetsize, final double[][] halfkernels )
	{
		final int n = targetsize.numDimensions();
		for ( int d = 0; d < n; ++d )
			if ( targetsize.dimension( d ) + 4 * halfkernels[ d ].length - 4 > Integer.MAX_VALUE )
				return false;
		return true;
	}

	static boolean canUseArrayImgFactory( final Dimensions targetsize, final double[][] halfkernels )
	{
		final int n = targetsize.numDimensions();
		long size = targetsize.dimension( 0 );
		for ( int d = 1; d < n; ++d )
			size *= targetsize.dimension( d ) + 2 * halfkernels[ d ].length;
		return size <= Integer.MAX_VALUE;
	}

	static < T extends NativeType< T > > ImgFactory< T > getImgFactory( final Dimensions targetsize, final double[][] halfkernels, final T type )
	{
		if ( canUseArrayImgFactory( targetsize, halfkernels ) )
			return new ArrayImgFactory< T >();
		final int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / type.getEntitiesPerPixel(), 1.0 / targetsize.numDimensions() );
		return new CellImgFactory< T >( cellSize );
	}
}
