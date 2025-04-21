/*-
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
package net.imglib2.blocks;

import java.util.function.Supplier;
import net.imglib2.converter.Converter;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/*
TODO: Performance of Convert is very unstable.
      Different Java versions can optimize ConvertGeneric (and modified versions) to different degrees.
      Although actual type in the convert loop are identical, specialized versions (e.g.,
      ConvertImpl.Convert_UnsignedByteType_FloatType) are often twice as fast.
      Also, the only experiments I have run so far are isolated instances with a single converter.
      For real work loads, probably it is required to additionally use ClassCopyProvider, switching on the Converter (supplier) type.
      More experiments are needed.
      See ConvertBenchmark.

TODO: Implement more special case converters.
*/
interface Convert
{
	void convert( Object src, Object dest, final int length );

	Convert newInstance();

	static < A extends NativeType< A >, B extends NativeType< B > > Convert create(
			final A srcType,
			final B destType,
			final Supplier< Converter< A, B > > converterSupplier )
	{
		if ( srcType instanceof UnsignedByteType )
		{
			if ( destType instanceof FloatType )
			{
				return new ConvertImpl.Convert_UnsignedByteType_FloatType( ( Supplier ) converterSupplier );
			}
		}
		else if ( srcType instanceof UnsignedShortType )
		{
			if ( destType instanceof FloatType )
			{
				return new ConvertImpl.Convert_UnsignedShortType_FloatType( ( Supplier ) converterSupplier );
			}
		}

		return new ConvertImpl.ConvertGeneric<>( srcType, destType, converterSupplier );
	}
}
