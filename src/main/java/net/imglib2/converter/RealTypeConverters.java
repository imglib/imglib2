/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.converter;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public final class RealTypeConverters
{

	private RealTypeConverters()
	{
		// prevent from instantiation
	}

	/**
	 * Copy the image content from a source image to a destination image.
	 * The area to be copied is defined by the destination.
	 * <p>
	 * Both images need to be of type {@link RealType}. So for example
	 * {@link UnsignedByteType}, {@link IntType}, {@link FloatType}
	 * and many more types are supported. A type conversion is done
	 * if needed.
	 *
	 * @param source Image that is source of the copy operation.
	 * @param destination Image that is destination of the copy operation.
	 */
	public static void copyFromTo(
			RandomAccessible< ? extends RealType< ? > > source,
			RandomAccessibleInterval< ? extends RealType< ? > > destination
	)
	{
		IntervalView< ? extends RealType< ? > > sourceInterval = Views.interval( source, destination );
		RealType< ? > s = sourceInterval.getType();
		RealType< ? > d = destination.getType();
		Converter< RealType< ? >, RealType< ? > > copy = getConverter( s, d );
		boolean useMultiThreading = Intervals.numElements(destination) >= 20_000;
		LoopBuilder.setImages( sourceInterval, destination ).multiThreaded( useMultiThreading ).forEachPixel( copy::convert );
	}

	/**
	 * Convert the pixel type of the given image to a given output pixel type.
	 * <p>
	 * Example, convert and image from {@link UnsignedByteType} to {@link IntType}:
	 * <pre>
	 * {@code
	 *
	 * RandomAccessibleInterval<UnsignedByteType> image = ...;
	 * RandomAccessibleInterval<IntType> intImage =
	 *     RealTypeConverters.convert( image, new IntType() );
	 * }
	 * </pre>
	 *
	 * The conversion is done on-the-fly when a pixel value is red.
	 * @param image image to convert
	 * @param pixelType pixel type of the result image
	 */
	public static < T extends RealType< T > > RandomAccessibleInterval< T > convert(
			RandomAccessibleInterval< ? extends RealType< ? > > image,
			T pixelType )
	{
		RealType< ? > in = image.getType();
		Converter< RealType< ? >, T > converter = getConverter( in, pixelType );
		return Converters.convert( image, converter, pixelType );
	}

	/**
	 * Returns a converter that converts from input to output type.
	 * <p>
	 * To get a converter from {@link UnsignedByteType} to {@link LongType} use:
	 * <p>
	 * {@code
	 * Converter< UnsignedByteType, LongType > converter =
	 * RealConverters.getConverter(new UnsignedByteType(), new LongType());
	 * }
	 * <p>
	 * The functionality is equivalent to {@link RealDoubleConverter}.
	 * But the returned converter is faster and has higher numerical precision
	 * than {@link RealDoubleConverter}.
	 * This is because it's optimal for the given pair of types.
	 * A converter from {@link UnsignedByteType} to {@link ByteType}
	 * will for example copy the byte directly and never convert to double.
	 */
	public static < S extends RealType< ? >, T extends RealType< ? > > Converter< S, T > getConverter( S inputType, T outputType )
	{
		return RealTypeConverterInternals.getConverter( inputType, outputType );
	}

}
