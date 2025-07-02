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
package net.imglib2.display;

import java.util.Collections;

import net.imglib2.converter.Converter;
import net.imglib2.loops.ClassCopyProvider;
import net.imglib2.type.mask.Masked;
import net.imglib2.type.numeric.ARGBType;

class MaskedToARGBConverterFactory
{
	private static final class ProviderHolder
	{
		@SuppressWarnings( "rawtypes" )
		static final ClassCopyProvider< MaskedToARGBConverter > provider = new ClassCopyProvider<>( Imp.class, MaskedToARGBConverter.class, Converter.class );
	}

	@SuppressWarnings( "unchecked" )
	static < T > MaskedToARGBConverter< T > create( final Converter< T, ARGBType > converter )
	{
		final Object key = Collections.singletonList( converter.getClass() );
		return ProviderHolder.provider.newInstanceForKey( key, converter );
	}

	public static class Imp< T > implements MaskedToARGBConverter< T >
	{
		private final Converter< T, ARGBType > converter;

		public Imp( final Converter< T, ARGBType > converter )
		{
			this.converter = converter;
		}

		@Override
		public void convert( final Masked< T > input, final ARGBType output )
		{
			converter.convert( input.value(), output );
			final double alpha = input.mask();
			final int a0 = ( int ) ( alpha * 255 + 0.5 );
			final int a = Math.min( 255, a0 );
			output.set( ( output.get() & 0x00ffffff ) | ( a << 24 ) );
		}

		@Override
		public Converter< T, ARGBType > delegate()
		{
			return converter;
		}
	}
}
