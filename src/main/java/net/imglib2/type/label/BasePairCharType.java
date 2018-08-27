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
import net.imglib2.img.basictypeaccess.CharAccess;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.type.AbstractNativeType;
import net.imglib2.type.BasePairType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.label.BasePairBitType.Base;
import net.imglib2.util.Fraction;

/**
 * Representation of base pairs using one char per entry, supported characters:
 * gap, N, A, T, G, C, U Bases are handled using the {@link Base} enumeration.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class BasePairCharType extends AbstractNativeType< BasePairCharType > implements BasePairType< BasePairCharType >
{
	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction();
	}

	final protected NativeImg< ?, ? extends CharAccess > img;

	// the DataAccess that holds the information
	protected CharAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public BasePairCharType( final NativeImg< ?, ? extends CharAccess > charStorage )
	{
		img = charStorage;
	}

	// this is the constructor if you want it to be a variable
	public BasePairCharType( final Base value )
	{
		img = null;
		dataAccess = new CharArray( 1 );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public BasePairCharType( final char value )
	{
		img = null;
		dataAccess = new CharArray( 1 );
		setChar( value );
	}

	// this is the constructor if you want it to be a variable
	public BasePairCharType()
	{
		this( Base.N );
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public BasePairCharType duplicateTypeOnSameNativeImg()
	{
		return new BasePairCharType( img );
	}

	private static final NativeTypeFactory< BasePairCharType, CharAccess > typeFactory = NativeTypeFactory.CHAR( img -> new BasePairCharType( img ) );

	@Override
	public NativeTypeFactory< BasePairCharType, CharAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	public char getChar()
	{
		return dataAccess.getValue( i );
	}

	public void setChar( final char f )
	{
		dataAccess.setValue( i, f );
	}

	@Override
	public void set( final Base base )
	{
		setChar( base.getChar() );
	}

	@Override
	public Base get()
	{
		return Base.fromChar( getChar() );
	}

	@Override
	public void set( final BasePairCharType c )
	{
		dataAccess.setValue( i, c.getChar() );
	}

	@Override
	public int compareTo( final BasePairCharType c )
	{
		return get().compareTo( c.get() );
	}

	@Override
	public void complement()
	{
		final char base = getChar();
		switch ( base )
		{
		case 'A':
			setChar( 'T' );
			break;
		case 'T':
			setChar( 'A' );
			break;
		case 'G':
			setChar( 'C' );
			break;
		case 'C':
			setChar( 'G' );
			break;
		case 'U':
			setChar( 'A' );
			break;
		}
	}

	@Override
	public byte baseToValue()
	{
		return ( byte ) get().ordinal();
	}

	@Override
	public BasePairCharType createVariable()
	{
		return new BasePairCharType( Base.N );
	}

	@Override
	public BasePairCharType copy()
	{
		return new BasePairCharType( get() );
	}

	@Override
	public String toString()
	{
		return "" + get();
	}

	@Override
	public boolean valueEquals( final BasePairCharType t )
	{
		return get() == t.get();
	}
}
