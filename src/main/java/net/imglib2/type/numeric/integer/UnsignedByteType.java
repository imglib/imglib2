/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class UnsignedByteType extends GenericByteType< UnsignedByteType >
{
	// this is the constructor if you want it to read from an array
	public UnsignedByteType( final NativeImg< ?, ? extends ByteAccess > img )
	{
		super( img );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedByteType( final int value )
	{
		super( getCodedSignedByte( value ) );
	}

	// this is the constructor if you want to specify the dataAccess
	public UnsignedByteType( final ByteAccess access )
	{
		super( access );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedByteType()
	{
		this( 0 );
	}

	public static byte getCodedSignedByteChecked( int unsignedByte )
	{
		if ( unsignedByte < 0 )
			unsignedByte = 0;
		else if ( unsignedByte > 255 )
			unsignedByte = 255;

		return getCodedSignedByte( unsignedByte );
	}

	public static byte getCodedSignedByte( final int unsignedByte )
	{
		return ( byte ) ( unsignedByte & 0xff );
	}

	public static int getUnsignedByte( final byte signedByte )
	{
		return signedByte & 0xff;
	}

	@Override
	public UnsignedByteType duplicateTypeOnSameNativeImg()
	{
		return new UnsignedByteType( img );
	}

	private final PrimitiveTypeInfo< UnsignedByteType, ByteAccess > info = PrimitiveTypeInfo.BYTE( img -> new UnsignedByteType( img ) );

	@Override
	public PrimitiveTypeInfo< UnsignedByteType, ByteAccess > getPrimitiveTypeInfo()
	{
		return info;
	}

	@Override
	public void mul( final float c )
	{

		final int a = getUnsignedByte( getByte() );
		setByte( getCodedSignedByte( Util.round( a * c ) ) );
	}

	@Override
	public void mul( final double c )
	{
		final int a = getUnsignedByte( getByte() );
		setByte( getCodedSignedByte( ( int ) Util.round( a * c ) ) );
	}

	@Override
	public void add( final UnsignedByteType c )
	{
		set( get() + c.get() );
	}

	@Override
	public void div( final UnsignedByteType c )
	{
		set( get() / c.get() );
	}

	@Override
	public void mul( final UnsignedByteType c )
	{
		set( get() * c.get() );
	}

	@Override
	public void sub( final UnsignedByteType c )
	{
		set( get() - c.get() );
	}

	public int get()
	{
		return getUnsignedByte( getByte() );
	}

	public void set( final int f )
	{
		setByte( getCodedSignedByte( f ) );
	}

	@Override
	public int getInteger()
	{
		return get();
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
		set( ( int ) f );
	}

	@Override
	public void setBigInteger( final BigInteger b )
	{
		set( b.intValue() );
	}

	@Override
	public double getMaxValue()
	{
		return -Byte.MIN_VALUE + Byte.MAX_VALUE;
	}

	@Override
	public double getMinValue()
	{
		return 0;
	}

	@Override
	public int hashCode()
	{
		// NB: Use the same hash code as java.lang.Integer#hashCode().
		return get();
	}

	@Override
	public int compareTo( final UnsignedByteType c )
	{
		final int a = get();
		final int b = c.get();

		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else
			return 0;
	}

	@Override
	public UnsignedByteType createVariable()
	{
		return new UnsignedByteType( 0 );
	}

	@Override
	public UnsignedByteType copy()
	{
		return new UnsignedByteType( get() );
	}

	@Override
	public String toString()
	{
		return "" + get();
	}
}
