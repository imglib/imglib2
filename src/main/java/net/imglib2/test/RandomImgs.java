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

package net.imglib2.test;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.AbstractIntegerBitType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.integer.UnsignedVariableBitLengthType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import java.util.Random;
import java.util.function.Consumer;

public class RandomImgs
{

	private final Random random;

	public static RandomImgs seed( int seed )
	{
		return new RandomImgs( seed );
	}

	private RandomImgs( int seed )
	{
		this.random = new Random( seed );
	}

	/**
	 * Creates an image with randomized content.
	 * @param type Pixel type
	 * @param interval Interval
	 */
	public < T extends NativeType< T > > RandomAccessibleInterval< T > nextImage( final T type, Interval interval )
	{
		long[] sizes = Intervals.dimensionsAsLongArray( interval );
		long[] min = Intervals.minAsLongArray( interval );
		return Views.translate( nextImage( type, sizes ), min );
	}

	/**
	 * Creates an image with randomized content
	 * @param type Pixel type
	 * @param dims Dimensions
	 */
	public < T extends NativeType< T > > Img< T > nextImage( final T type, final long... dims )
	{
		final Img< T > result = new ArrayImgFactory<>( type ).create( dims );
		return randomize( result );
	}

	/**
	 * Randomizes the content of the given image.
	 * @return Reference to the given image
	 */
	public < I extends RandomAccessibleInterval< T >, T >
	I randomize( final I image )
	{
		final T type = image.getType();
		Views.iterable( image ).forEach( randomSetter( type ) );
		return image;
	}

	private < T > Consumer< T > randomSetter( final T type )
	{
		if ( ( type instanceof UnsignedByteType )
				|| ( type instanceof ByteType )
				|| ( type instanceof ShortType )
				|| ( type instanceof UnsignedShortType )
				|| ( type instanceof IntType ) )
			return b -> ( ( IntegerType< ? > ) b ).setInteger( random.nextInt() );
		if ( ( type instanceof UnsignedLongType )
				|| ( type instanceof UnsignedIntType )
				|| ( type instanceof AbstractIntegerBitType )
				|| ( type instanceof UnsignedVariableBitLengthType )
				|| ( type instanceof LongType ) )
			return b -> ( ( IntegerType< ? > ) b ).setInteger( random.nextLong() );
		if ( type instanceof ARGBType )
			return b -> ( ( ARGBType ) b ).set( random.nextInt() );
		if ( type instanceof FloatType )
			return b -> ( ( FloatType ) b ).setReal( random.nextFloat() );
		if ( type instanceof DoubleType )
			return b -> ( ( DoubleType ) b ).setReal( random.nextDouble() );
		throw new UnsupportedOperationException( "Randomization of type: " + type.getClass() + " is not supported." );
	}
}
