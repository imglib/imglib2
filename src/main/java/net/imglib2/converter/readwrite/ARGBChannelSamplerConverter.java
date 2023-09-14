/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.converter.readwrite;

import net.imglib2.Sampler;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Access the on channel of an {@link ARGBType} as an UnsignedByteType.
 *
 */
public final class ARGBChannelSamplerConverter implements SamplerConverter< ARGBType, UnsignedByteType >
{
	static final private int[] masks = new int[]{
			0x00ffffff,
			0xff00ffff,
			0xffff00ff,
			0xffffff00
	};

	static final private int[] shifts = new int[]{
			24,
			16,
			8,
			0
	};

	final private int mask, shift;

	public ARGBChannelSamplerConverter( final int channel )
	{
		mask = masks[ channel ];
		shift = shifts[ channel ];
	}

	@Override
	public UnsignedByteType convert( final Sampler< ? extends ARGBType > sampler )
	{
		return new UnsignedByteType( new ARGBChannelConvertingAccess( sampler ) );
	}

	final private class ARGBChannelConvertingAccess implements ByteAccess
	{
		final private Sampler< ? extends ARGBType > sampler;

		private ARGBChannelConvertingAccess( final Sampler< ? extends ARGBType > sampler )
		{
			this.sampler = sampler;
		}

		/**
		 * This is only intended to work with UnsignedByteType! We ignore index!!!
		 */
		@Override
		public byte getValue( final int index )
		{
			return ( byte )( ( sampler.get().get() >> shift ) & 0xff );
		}

		/**
		 * This is only intended to work with UnsignedByteType! We ignore index!!!
		 */
		@Override
		public void setValue( final int index, final byte value )
		{
			final ARGBType t = sampler.get();
			t.set( ( t.get() & mask ) | ( value << shift ) );
		}
	}
}
