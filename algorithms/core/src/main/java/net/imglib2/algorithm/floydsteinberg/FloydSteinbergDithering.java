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

package net.imglib2.algorithm.floydsteinberg;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 */
public class FloydSteinbergDithering< T extends RealType< T >> implements OutputAlgorithm< Img< BitType >>, Benchmark
{
	Img< BitType > result;

	final RandomAccessibleInterval< T > img;

	final Img< FloatType > errorDiffusionKernel;

	final long[] dim, tmp1, tmp2;

	final float ditheringThreshold;

	long processingTime;

	String errorMessage = "";

	public FloydSteinbergDithering( final RandomAccessibleInterval< T > img, final float ditheringThreshold )
	{
		this.img = img;
		this.dim = Util.intervalDimensions( img );
		this.tmp1 = new long[ img.numDimensions() ];
		this.tmp2 = new long[ img.numDimensions() ];

		this.errorDiffusionKernel = createErrorDiffusionKernel( img.numDimensions() );

		this.ditheringThreshold = ditheringThreshold;
	}

	/** Will estimate the dithering threshold by (max - min) / 2 */
	public FloydSteinbergDithering( final RandomAccessibleInterval< T > img )
	{
		this( img, Float.NEGATIVE_INFINITY );
	}

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		/*
		 * ComputeMinMax<T> cmm = new ComputeMinMax<T>(img); //TODO
		 * cmm.process(); final float minValue = cmm.getMin().getRealFloat();
		 * final float maxValue = cmm.getMax().getRealFloat(); final long
		 * numDimensions = img.numDimensions(); final float ditheringThreshold =
		 * Float.NEGATIVE_INFINITY == this.ditheringThreshold ? (maxValue -
		 * minValue) / 2.0f : this.ditheringThreshold;
		 * 
		 * // creates the output image of BitType using the same Storage
		 * Strategy as the input image try { result = new
		 * ArrayImgFactory<BitType>().imgFactory(new BitType()).create( dim, new
		 * BitType() ); } catch (IncompatibleTypeException e) { throw new
		 * RuntimeException(e); }
		 * 
		 * // we create a Cursor that traverses (top -> bottom) and (left ->
		 * right) in n dimensions, // which is a Cursor on a normal Array,
		 * therefore we use a FakeArray which just gives us position //
		 * information without allocating memory final ZeroMinIntervalIterator
		 * cursor = new ZeroMinIntervalIterator( dim );
		 * 
		 * // we also need a Cursors for the input, the output and the kernel
		 * image final RandomAccess<T> cursorInput = new
		 * ExtendedRandomAccessibleInterval<T,RandomAccessibleInterval<T>> (img,
		 * new OutOfBoundsConstantValueFactory<T, RandomAccessibleInterval<T>> (
		 * Views.iterable(img).firstElement().createVariable() )) .randomAccess(
		 * img );
		 * 
		 * //img.randomAccess( new OutOfBoundsConstantValueFactory<T,Img<T>>(
		 * img.firstElement().createVariable() ) ); final RandomAccess<BitType>
		 * cursorOutput = result.randomAccess(); final Cursor<FloatType>
		 * cursorKernel = errorDiffusionKernel.cursor();
		 * 
		 * while( cursor.hasNext() ) { cursor.fwd();
		 * 
		 * // move input and output cursor to the current location
		 * cursorInput.setPosition( cursor ); cursorOutput.setPosition( cursor
		 * );
		 * 
		 * // set new value and compute error final float error; final float in
		 * = cursorInput.get().getRealFloat(); if ( in < ditheringThreshold ) {
		 * cursorOutput.get().setZero(); error = in - minValue; } else {
		 * cursorOutput.get().setOne(); error = in - maxValue; }
		 * 
		 * if ( error != 0.0f ) { // distribute the error cursorKernel.reset();
		 * cursorKernel.jumpFwd( errorDiffusionKernel.size()/2 );
		 * cursor.localize( tmp1 );
		 * 
		 * while ( cursorKernel.hasNext() ) { cursorKernel.fwd();
		 * 
		 * final float value = error * cursorKernel.get().get();
		 * cursorKernel.localize( tmp2 );
		 * 
		 * for ( int d = 0; d < numDimensions; ++d ) tmp2[ d ] += tmp1[ d ] - 1;
		 * 
		 * cursorInput.move( tmp2 ); cursorInput.get().setReal(
		 * cursorInput.get().getRealFloat() + value ); } } }
		 * 
		 * processingTime = System.currentTimeMillis() - startTime;
		 * 
		 * // successfully computed the dithering
		 */
		return true;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public Img< BitType > getResult()
	{
		return result;
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	public Img< FloatType > createErrorDiffusionKernel( final int numDimensions )
	{
		final ArrayImgFactory< FloatType > factory = new ArrayImgFactory< FloatType >();

		// for 2d we take the values from the literature
		if ( numDimensions == 2 )
		{
			final Img< FloatType > kernel = factory.create( new long[] { 3, 3 }, new FloatType() );

			final RandomAccess< FloatType > cursor = kernel.randomAccess();

			// For the 2d-case as well:
			// |- - -|
			// |- # 7|
			// |3 5 1|
			// ( - means processed already, # means the one we are currently
			// processing)
			cursor.setPosition( 2, 0 );
			cursor.setPosition( 1, 1 );
			cursor.get().setReal( 7.0f / 16.0f );

			cursor.move( 1, 1 );
			cursor.get().setReal( 1.0f / 16.0f );

			cursor.move( -1, 0 );
			cursor.get().setReal( 5.0f / 16.0f );

			cursor.move( -1, 0 );
			cursor.get().setReal( 3.0f / 16.0f );

			return kernel;
		}
		final Img< FloatType > kernel = factory.create( Util.getArrayFromValue( 3L, numDimensions ), new FloatType() );
		final Cursor< FloatType > cursor = kernel.cursor();

		final int numValues = ( int ) ( kernel.size() / 2 );
		final float[] rndValues = new float[ numValues ];
		float sum = 0;
		final Random rnd = new Random( 435345 );

		for ( int i = 0; i < numValues; ++i )
		{
			rndValues[ i ] = rnd.nextFloat();
			sum += rndValues[ i ];
		}

		for ( int i = 0; i < numValues; ++i )
			rndValues[ i ] /= sum;

		int count = 0;
		while ( cursor.hasNext() )
		{
			cursor.fwd();

			if ( count > numValues )
				cursor.get().setReal( rndValues[ count - numValues - 1 ] );

			++count;
		}

		//
		// Optimize
		//
		for ( int i = 0; i < 100; ++i )
			for ( int d = 0; d < numDimensions; ++d )
			{
				cursor.reset();

				float sumD = 0;

				while ( cursor.hasNext() )
				{
					cursor.fwd();
					if ( cursor.getIntPosition( d ) != 1 )
						sumD += cursor.get().get();
				}

				cursor.reset();
				while ( cursor.hasNext() )
				{
					cursor.fwd();

					if ( cursor.getIntPosition( d ) != 1 )
						cursor.get().set( cursor.get().get() / sumD );
				}
			}

		sum = 0;

		cursor.reset();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			sum += cursor.get().get();
		}

		cursor.reset();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.get().set( cursor.get().get() / sum );
		}
		return kernel;
	}
}
