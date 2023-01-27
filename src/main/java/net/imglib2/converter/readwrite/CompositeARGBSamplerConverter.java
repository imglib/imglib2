/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.converter.ColorChannelOrder;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.composite.Composite;

import java.util.function.Function;

public class CompositeARGBSamplerConverter implements SamplerConverter< Composite< UnsignedByteType >, ARGBType >
{

	private final Function< Sampler< ? extends Composite< UnsignedByteType > >, ? extends IntAccess > factory;

	public CompositeARGBSamplerConverter( ColorChannelOrder order )
	{
		this.factory = getAccessFactory( order );
	}

	private Function< Sampler< ? extends Composite< UnsignedByteType > >, ? extends IntAccess > getAccessFactory( ColorChannelOrder order )
	{
		switch ( order )
		{
		case ARGB:
			return CompositeARGBAccess::new;
		case RGB:
			return CompositeRGBAccess::new;
		}
		throw new IllegalArgumentException( "Converter only supports ARGB and RGB channel order." );
	}

	@Override
	public ARGBType convert( Sampler< ? extends Composite< UnsignedByteType > > sampler )
	{
		return new ARGBType( factory.apply( sampler ) );
	}

	private static final class CompositeARGBAccess implements IntAccess
	{
		private final Sampler< ? extends Composite< UnsignedByteType > > sampler;

		public CompositeARGBAccess( Sampler< ? extends Composite< UnsignedByteType > > sampler )
		{
			this.sampler = sampler;
		}

		@Override
		public int getValue( int index )
		{
			Composite< UnsignedByteType > composite = sampler.get();
			return ( composite.get( 0 ).get() << 24 ) +
					( composite.get( 1 ).get() << 16 ) +
					( composite.get( 2 ).get() << 8 ) +
					composite.get( 3 ).get();
		}

		@Override
		public void setValue( int index, int value )
		{
			Composite< UnsignedByteType > composite = sampler.get();
			composite.get( 0 ).set( value >> 24 );
			composite.get( 1 ).set( value >> 16 );
			composite.get( 2 ).set( value >> 8 );
			composite.get( 3 ).set( value );
		}
	}

	private static final class CompositeRGBAccess implements IntAccess
	{
		private final Sampler< ? extends Composite< UnsignedByteType > > sampler;

		public CompositeRGBAccess( Sampler< ? extends Composite< UnsignedByteType > > sampler )
		{
			this.sampler = sampler;
		}

		@Override
		public int getValue( int index )
		{
			Composite< UnsignedByteType > composite = sampler.get();
			return 0xff000000 +
					( composite.get( 0 ).get() << 16 ) +
					( composite.get( 1 ).get() << 8 ) +
					composite.get( 2 ).get();
		}

		@Override
		public void setValue( int index, int value )
		{
			Composite< UnsignedByteType > composite = sampler.get();
			composite.get( 0 ).set( value >> 16 );
			composite.get( 1 ).set( value >> 8 );
			composite.get( 2 ).set( value );
		}
	}
}
