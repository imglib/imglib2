/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.test.ImgLib2Assert;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link RealTypeConverters}.
 *
 * @author Matthias Arzt
 */
public class RealTypeConvertersTest
{

	@Test
	public void testGetConverter() {
		UnsignedByteType input = new UnsignedByteType( 42 );
		DoubleType output = new DoubleType();
		Converter<UnsignedByteType, DoubleType> converter = RealTypeConverters.getConverter( input, output );
		converter.convert( input, output );
		assertEquals( 42, output.getRealDouble(), 0 );
	}

	@Test
	public void testConvert() {
		Img<UnsignedByteType> input = ArrayImgs.unsignedBytes( new byte[] { 42 }, 1 );
		RandomAccessibleInterval< FloatType > result = RealTypeConverters.convert( input, new FloatType() );
		ImgLib2Assert.assertImageEqualsRealType( input, result, 0 );
	}

	@Test
	public void testCopy()
	{
		Img< UnsignedByteType > source = ArrayImgs.unsignedBytes( new byte[] { 42 }, 1, 1 );
		Img< UnsignedByteType > destination = ArrayImgs.unsignedBytes( 1, 1 );
		RealTypeConverters.copyFromTo( source, destination );
		ImgLib2Assert.assertImageEquals( source, destination );
	}

	@Test
	public void testCopyWithTypeConversion()
	{
		RandomAccessibleInterval< UnsignedByteType > source = ArrayImgs.unsignedBytes( new byte[] { 1, 2, 3, 4 }, 2, 2 );
		RandomAccessibleInterval< DoubleType > destination = ArrayImgs.doubles( 2, 2 );
		RealTypeConverters.copyFromTo( source, destination );
		ImgLib2Assert.assertImageEqualsRealType( source, destination, 0 );
	}
}
