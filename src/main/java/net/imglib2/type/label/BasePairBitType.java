/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.type.label;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.type.AbstractBit64Type;
import net.imglib2.type.BasePairType;
import net.imglib2.type.NativeTypeFactory;

/**
 * Representation of base pairs using 3 bits per entry, supported characters: gap, N, A, T, G, C, U
 * Bases are handled using the {@link Base} enumeration.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class BasePairBitType extends AbstractBit64Type< BasePairBitType > implements BasePairType< BasePairBitType >
{
	// hom many bits a BasePairBitType contains
	private static final int NBITS = 3;

	public static enum Base { gap, N, A, T, G, C, U; }

	// this is the constructor if you want it to read from an array
	public BasePairBitType( final NativeImg< ?, ? extends LongAccess > bitStorage )
	{
		super( bitStorage, NBITS );
	}

	// this is the constructor if you want it to be a variable
	public BasePairBitType( final Base value )
	{
		super( value.ordinal(), NBITS );
	}

	// this is the constructor if you want it to be a variable
	public BasePairBitType()
	{
		this( Base.N );
	}

	@Override
	public BasePairBitType duplicateTypeOnSameNativeImg() { return new BasePairBitType( img ); }

	private static final NativeTypeFactory< BasePairBitType, LongAccess > typeFactory = NativeTypeFactory.LONG( img -> new BasePairBitType( img ) );

	@Override
	public NativeTypeFactory< BasePairBitType, LongAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	@Override
	public void set( final Base base )
	{
		setBits( base.ordinal() );
	}

	@Override
	public Base get()
	{
		return Base.values()[ ( int ) getBits() ];
	}

	@Override
	public int compareTo( final BasePairBitType c )
	{
		return get().compareTo( c.get() );
	}

	@Override
	public void complement()
	{
		final Base base = get();
		switch ( base )
		{
			case A: set( Base.T ); break;
			case T: set( Base.A ); break;
			case G: set( Base.C ); break;
			case C: set( Base.G ); break;
			case U: set( Base.A ); break;
			default: break;
		}
	}

	@Override
	public byte baseToValue()
	{
		final Base base = get();

		switch ( base )
		{
		case N:
			return 1;
		case A:
			return 2;
		case T:
			return 3;
		case G:
			return 4;
		case C:
			return 5;
		case U:
			return 6;
		default:
			return 0;
		}
	}

	@Override
	public void set( final BasePairBitType c )
	{
		set( c.get() );
	}

	@Override
	public BasePairBitType createVariable()
	{
		return new BasePairBitType();
	}

	@Override
	public BasePairBitType copy()
	{
		return new BasePairBitType( this.get() );
	}

	@Override
	public String toString()
	{
		return this.get().toString();
	}

	@Override
	public boolean valueEquals( final BasePairBitType t )
	{
		return get() == t.get();
	}
}
