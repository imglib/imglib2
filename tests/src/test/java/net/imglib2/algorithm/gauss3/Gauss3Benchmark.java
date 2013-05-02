/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.algorithm.gauss3;

import java.util.Random;

import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss.GaussFloat;
import net.imglib2.algorithm.gauss.GaussGeneral;
import net.imglib2.algorithm.gauss.GaussNativeType;
import net.imglib2.converter.readwrite.RealFloatSamplerConverter;
import net.imglib2.converter.readwrite.WriteConvertedRandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Gauss3Benchmark
{

	public static void main( final String[] args ) throws ImgIOException
	{
		final long[] dimensions = new long[] { 1000, 1000 };
//		final long[] dimensions = new long[] { 100, 100, 100 };
		final double sigma = 3;
		final int numRuns = 100;
		final boolean printIndividualTimes = false;

		System.out.print( String.format( "benchmarking Gaussian convolution (sigma = %.1f) of a ", sigma ) );
	    for ( int d = 0; d < dimensions.length; ++d )
			System.out.print( ( d == 0 ? "" : " x " ) + dimensions[ d ] );
	    System.out.println( " FloatType image." );
	    System.out.println( "showing median runtime over " + numRuns + " trials." );
	    System.out.println();

		benchmarkFloat( dimensions, sigma, printIndividualTimes, numRuns );
		System.out.println( " ================================== " );
		benchmarkNative( dimensions, sigma, printIndividualTimes, numRuns );
		System.out.println( " ================================== " );
		benchmarkGeneric( dimensions, sigma, printIndividualTimes, numRuns );
		System.out.println( " ================================== " );
		benchmarkInFloat( dimensions, sigma, printIndividualTimes, numRuns );

//		visualise( dimensions, sigma );
	}

	public static < T > void convolve(
			final double[] sigmas, final RandomAccessible< T > source, final RandomAccessibleInterval< T > target,
			final ConvolverFactory< T, T > convf, final ImgFactory< T > imgf, final T type )
	{
		final double[][] halfkernels = Gauss3.halfkernels( sigmas );
		final int numthreads = Runtime.getRuntime().availableProcessors();
		SeparableSymmetricConvolution.convolve( halfkernels, source, target, convf, convf,convf, convf, imgf, type, numthreads );
	}

	public static void benchmarkFloat( final long[] dimensions, final double sigma, final boolean printIndividualTimes, final int numRuns) throws ImgIOException
	{
		final FloatType type = new FloatType();
		final ArrayImgFactory< FloatType > factory = new ArrayImgFactory< FloatType >();
		final Img< FloatType > img = factory.create( dimensions, type );
	    final Img< FloatType > convolved = factory.create( dimensions, type );
		fillRandom( img );

	    final int n = img.numDimensions();
	    final double[] sigmas = new double[ n ];
	    for ( int d = 0; d < n; ++d )
	    	sigmas[ d ] = sigma;

	    System.out.println( "GaussFloat" );
		final Point min = new Point( n );
		img.min( min );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
				new GaussFloat( sigmas, Views.extendMirrorSingle( img ), img, convolved, min, factory ).call();
			}
	    } );

	    System.out.println( "Gauss3 (should use FloatConvolverRealTypeBuffered)" );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
				try
				{
					Gauss3.gauss( sigmas, Views.extendMirrorSingle( img ), convolved );
				}
				catch ( final IncompatibleTypeException e )
				{
					e.printStackTrace();
				}
			}
	    } );

	    System.out.println( "SeparableSymmetricConvolution with FloatConvolverRealTypeBuffered" );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
			    convolve( sigmas, Views.extendMirrorSingle( img ), convolved, FloatConvolverRealTypeBuffered.< FloatType, FloatType >factory(), factory, type );
			}
	    } );


	    System.out.println( "SeparableSymmetricConvolution with FloatConvolverRealType" );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
			    convolve( sigmas, Views.extendMirrorSingle( img ), convolved, FloatConvolverRealType.< FloatType, FloatType >factory(), factory, type );
			}
	    } );
	}

	public static void benchmarkNative( final long[] dimensions, final double sigma, final boolean printIndividualTimes, final int numRuns) throws ImgIOException
	{
		final FloatType type = new FloatType();
		final ArrayImgFactory< FloatType > factory = new ArrayImgFactory< FloatType >();
		final Img< FloatType > img = factory.create( dimensions, type );
	    final Img< FloatType > convolved = factory.create( dimensions, type );
		fillRandom( img );

	    final int n = img.numDimensions();
	    final double[] sigmas = new double[ n ];
	    for ( int d = 0; d < n; ++d )
	    	sigmas[ d ] = sigma;

	    System.out.println( "GaussNativeType" );
		final Point min = new Point( n );
		img.min( min );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
				new GaussNativeType< FloatType >( sigmas, Views.extendMirrorSingle( img ), img, convolved, min, factory, type ).call();
			}
	    } );

	    System.out.println( "SeparableSymmetricConvolution with ConvolverNativeTypeBuffered" );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
			    convolve( sigmas, Views.extendMirrorSingle( img ), convolved, ConvolverNativeTypeBuffered.factory( type ), factory, type );
			}
	    } );

	    System.out.println( "SeparableSymmetricConvolution with ConvolverNativeType" );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
			    convolve( sigmas, Views.extendMirrorSingle( img ), convolved, ConvolverNativeType.factory( type ), factory, type );
			}
	    } );
	}

	public static void benchmarkGeneric( final long[] dimensions, final double sigma, final boolean printIndividualTimes, final int numRuns) throws ImgIOException
	{
		final FloatType type = new FloatType();
		final ArrayImgFactory< FloatType > factory = new ArrayImgFactory< FloatType >();
		final Img< FloatType > img = factory.create( dimensions, type );
	    final Img< FloatType > convolved = factory.create( dimensions, type );
		fillRandom( img );

	    final int n = img.numDimensions();
	    final double[] sigmas = new double[ n ];
	    for ( int d = 0; d < n; ++d )
	    	sigmas[ d ] = sigma;

	    System.out.println( "GaussGeneral" );
		final Point min = new Point( n );
		img.min( min );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
				new GaussGeneral< FloatType >( sigmas, Views.extendMirrorSingle( img ), img, convolved, min, factory, type ).call();
			}
	    } );

	    System.out.println( "SeparableSymmetricConvolution with ConvolverNumericType" );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
			    convolve( sigmas, Views.extendMirrorSingle( img ), convolved, ConvolverNumericType.factory( type ), factory, type );
			}
	    } );
	}

	static void fillRandom( final Img< FloatType > img )
	{
		final Random random = new Random( 1232324970l );
		for ( final FloatType t : img )
			t.set( random.nextFloat() );
	}

	public static void visualise( final long[] dimensions, final double sigma ) throws ImgIOException
	{
		final FloatType type = new FloatType();
		final ArrayImgFactory< FloatType > factory = new ArrayImgFactory< FloatType >();
		final Img< FloatType > img = factory.create( dimensions, type );
		fillRandom( img );
		ImageJFunctions.show( img, "source image" );

		final int s = Util.createGaussianKernel1DDouble( sigma, true ).length;
		System.out.println( "kernel size = " + s );

	    final int n = img.numDimensions();
	    final double[] sigmas = new double[ n ];
	    for ( int d = 0; d < n; ++d )
	    	sigmas[ d ] = sigma;

	    for ( int d = 0; d < n; ++d )
			System.out.print( ( d == 0 ? "" : " x " ) + img.dimension( d ) );
	    System.out.println();

		final Point min = new Point( n );
		img.min( min );
		final Img< FloatType > convolved2 = factory.create( img, new FloatType() );
		new GaussFloat( sigmas, Views.extendMirrorSingle( img ), img, convolved2, min, factory ).call();
		ImageJFunctions.show( convolved2, "GaussFloat" );

	    final Img< FloatType > convolved5 = factory.create( img, new FloatType() );
	    convolve( sigmas, Views.extendMirrorSingle( img ), convolved5, ConvolverNumericType.factory( type ), factory, type );
		ImageJFunctions.show( convolved5, "SeparableSymmetricConvolution with ConvolverNumericType"  );

	    final Img< FloatType > convolved4 = factory.create( img, new FloatType() );
	    convolve( sigmas, Views.extendMirrorSingle( img ), convolved4, ConvolverNativeType.factory( type ), factory, type );
		ImageJFunctions.show( convolved4, "SeparableSymmetricConvolution with ConvolverNativeType"  );

	    final Img< FloatType > convolved6 = factory.create( img, new FloatType() );
	    convolve( sigmas, Views.extendMirrorSingle( img ), convolved6, ConvolverNativeTypeBuffered.factory( type ), factory, type );
		ImageJFunctions.show( convolved6, "SeparableSymmetricConvolution with ConvolverNativeTypeBuffered"  );

	    final Img< FloatType > convolved7 = factory.create( img, new FloatType() );
	    convolve( sigmas, Views.extendMirrorSingle( img ), convolved7, FloatConvolverRealType.< FloatType, FloatType >factory(), factory, type );
		ImageJFunctions.show( convolved7, "SeparableSymmetricConvolution with"  );

	    final Img< FloatType > convolved3 = factory.create( img, new FloatType() );
	    convolve( sigmas, Views.extendMirrorSingle( img ), convolved3, FloatConvolverRealTypeBuffered.< FloatType, FloatType >factory(), factory, type );
		ImageJFunctions.show( convolved3, "SeparableSymmetricConvolution with FloatConvolverRealTypeBuffered"  );
	}

	static void fillRandomUnsignedByte( final Img< UnsignedByteType > img )
	{
		final Random random = new Random( 1232324970l );
		for ( final UnsignedByteType t : img )
			t.set( random.nextInt( 256 ) );
	}

	public static void benchmarkInFloat( final long[] dimensions, final double sigma, final boolean printIndividualTimes, final int numRuns) throws ImgIOException
	{
		final UnsignedByteType unsignedByteType = new UnsignedByteType();
		final ArrayImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
	    final Img< UnsignedByteType > img = factory.create( dimensions, unsignedByteType );
	    final Img< UnsignedByteType > convolved = factory.create( dimensions, unsignedByteType );
	    fillRandomUnsignedByte( img );

	    final int n = img.numDimensions();
	    final double[] sigmas = new double[ n ];
	    for ( int d = 0; d < n; ++d )
	    	sigmas[ d ] = sigma;

	    final FloatType floatType = new FloatType();
		final ArrayImgFactory< FloatType > floatFactory = new ArrayImgFactory< FloatType >();

	    System.out.println( "convolve UnsignedByteType using Converters on source and target" );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
			    final ConvolverFactory< FloatType, FloatType > cff = FloatConvolverRealTypeBuffered.< FloatType, FloatType >factory();
				final RandomAccessibleInterval< FloatType > rIn = new WriteConvertedRandomAccessibleInterval< UnsignedByteType, FloatType >( img, new RealFloatSamplerConverter< UnsignedByteType >() );
				final RandomAccessibleInterval< FloatType > rOut = new WriteConvertedRandomAccessibleInterval< UnsignedByteType, FloatType >( convolved, new RealFloatSamplerConverter< UnsignedByteType >() );
				final double[][] halfkernels = Gauss3.halfkernels( sigmas );
				final int numthreads = Runtime.getRuntime().availableProcessors();
				SeparableSymmetricConvolution.convolve( halfkernels, Views.extendMirrorSingle( rIn ), rOut, cff, cff, cff, cff, floatFactory, floatType, numthreads );
			}
	    } );

	    System.out.println( "convolve UnsignedByteType using multiple ConvolverFactories" );
	    BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
			@Override
			public void run()
			{
			    final ConvolverFactory< FloatType, FloatType > cff = FloatConvolverRealTypeBuffered.< FloatType, FloatType >factory();
			    final ConvolverFactory< FloatType, UnsignedByteType > cfi = FloatConvolverRealTypeBuffered.< FloatType, UnsignedByteType >factory();
			    final ConvolverFactory< UnsignedByteType, FloatType > cif = FloatConvolverRealTypeBuffered.< UnsignedByteType, FloatType >factory();
			    final ConvolverFactory< UnsignedByteType, UnsignedByteType > cii = FloatConvolverRealTypeBuffered.< UnsignedByteType, UnsignedByteType >factory();
				final double[][] halfkernels = Gauss3.halfkernels( sigmas );
				final int numthreads = Runtime.getRuntime().availableProcessors();
				SeparableSymmetricConvolution.convolve( halfkernels, Views.extendMirrorSingle( img ), convolved, cif, cff, cfi, cii, floatFactory, floatType, numthreads );
			}
	    } );
	}
}
