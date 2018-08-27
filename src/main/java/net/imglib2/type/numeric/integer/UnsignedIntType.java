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

package net.imglib2.type.numeric.integer;

import java.math.BigInteger;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class UnsignedIntType extends GenericIntType< UnsignedIntType >
{
	// this is the constructor if you want it to read from an array
	public UnsignedIntType( final NativeImg< ?, ? extends IntAccess > img )
	{
		super( img );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedIntType( final long value )
	{
		super( getCodedSignedInt( value ) );
	}

	// this is the constructor if you want to specify the dataAccess
	public UnsignedIntType( final IntAccess access )
	{
		super( access );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedIntType()
	{
		this( 0 );
	}

	public static int getCodedSignedIntChecked( long unsignedInt )
	{
		if ( unsignedInt < 0 )
			unsignedInt = 0;
		else if ( unsignedInt > 0xffffffffL )
			unsignedInt = 0xffffffffL;

		return getCodedSignedInt( unsignedInt );
	}

	public static int getCodedSignedInt( final long unsignedInt )
	{
		return ( int ) ( unsignedInt & 0xffffffff );
	}

	public static long getUnsignedInt( final int signedInt )
	{
		return signedInt & 0xffffffffL;
	}

	@Override
	public UnsignedIntType duplicateTypeOnSameNativeImg()
	{
		return new UnsignedIntType( img );
	}

	private static final NativeTypeFactory< UnsignedIntType, IntAccess > typeFactory = NativeTypeFactory.INT( img -> new UnsignedIntType( img ) );

	@Override
	public NativeTypeFactory< UnsignedIntType, IntAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	@Override
	public void mul( final float c )
	{
		final long a = getUnsignedInt( getInt() );
		setInt( getCodedSignedInt( Util.round( a * c ) ) );
	}

	@Override
	public void mul( final double c )
	{
		final long a = getUnsignedInt( getInt() );
		setInt( getCodedSignedInt( ( int ) Util.round( a * c ) ) );
	}

	@Override
	public void add( final UnsignedIntType c )
	{
		set( get() + c.get() );
	}

	@Override
	public void div( final UnsignedIntType c )
	{
		set( get() / c.get() );
	}

	@Override
	public void mul( final UnsignedIntType c )
	{
		set( get() * c.get() );
	}

	@Override
	public void sub( final UnsignedIntType c )
	{
		set( get() - c.get() );
	}

	@Override
	public void setOne()
	{
		set( 1 );
	}

	@Override
	public void setZero()
	{
		set( 0 );
	}

	@Override
	public void inc()
	{
		set( get() + 1 );
	}

	@Override
	public void dec()
	{
		set( get() - 1 );
	}

	@Override
	public String toString()
	{
		return "" + get();
	}

	public long get()
	{
		return getUnsignedInt( getInt() );
	}

	public void set( final long f )
	{
		setInt( getCodedSignedInt( f ) );
	}

	@Override
	public int getInteger()
	{
		return ( int ) get();
	}

	@Override
	public long getIntegerLong()
	{
		return get();
	}

	@Override
	public BigInteger getBigInteger()
	{
		return BigInteger.valueOf( get() );
	}

	@Override
	public void setInteger( final int f )
	{
		set( f );
	}

	@Override
	public void setInteger( final long f )
	{
		set( f );
	}

	@Override
	public void setBigInteger( final BigInteger b )
	{
		set( b.longValue() );
	}

	@Override
	public double getMaxValue()
	{
		return 0xffffffffL;
	}

	@Override
	public double getMinValue()
	{
		return 0;
	}

	@Override
	public UnsignedIntType createVariable()
	{
		return new UnsignedIntType( 0 );
	}

	@Override
	public UnsignedIntType copy()
	{
		return new UnsignedIntType( get() );
	}

	@Override
	public int compareTo( final UnsignedIntType other )
	{
		return Long.compare( get(), other.get() );
	}
}
