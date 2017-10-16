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
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class UnsignedShortType extends GenericShortType< UnsignedShortType >
{
	// this is the constructor if you want it to read from an array
	public UnsignedShortType( final NativeImg< ?, ? extends ShortAccess > img )
	{
		super( img );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedShortType( final int value )
	{
		super( getCodedSignedShort( value ) );
	}

	// this is the constructor if you want to specify the dataAccess
	public UnsignedShortType( final ShortAccess access )
	{
		super( access );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedShortType()
	{
		this( 0 );
	}

	public static short getCodedSignedShortChecked( int unsignedShort )
	{
		if ( unsignedShort < 0 )
			unsignedShort = 0;
		else if ( unsignedShort > 65535 )
			unsignedShort = 65535;

		return getCodedSignedShort( unsignedShort );
	}

	public static short getCodedSignedShort( final int unsignedShort )
	{
		return ( short ) ( unsignedShort & 0xffff );
	}

	public static int getUnsignedShort( final short signedShort )
	{
		return signedShort & 0xffff;
	}

	@Override
	public UnsignedShortType duplicateTypeOnSameNativeImg()
	{
		return new UnsignedShortType( img );
	}

	private static final PrimitiveTypeInfo< UnsignedShortType, ShortAccess > info = PrimitiveTypeInfo.SHORT( img -> new UnsignedShortType( img ) );

	@Override
	public PrimitiveTypeInfo< UnsignedShortType, ShortAccess > getPrimitiveTypeInfo()
	{
		return info;
	}

	@Override
	public void mul( final float c )
	{
		set( Util.round( get() * c ) );
	}

	@Override
	public void mul( final double c )
	{
		set( ( int ) Util.round( get() * c ) );
	}

	@Override
	public void add( final UnsignedShortType c )
	{
		set( get() + c.get() );
	}

	@Override
	public void div( final UnsignedShortType c )
	{
		set( get() / c.get() );
	}

	@Override
	public void mul( final UnsignedShortType c )
	{
		set( get() * c.get() );
	}

	@Override
	public void sub( final UnsignedShortType c )
	{
		set( get() - c.get() );
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

	public int get()
	{
		return getUnsignedShort( getShort() );
	}

	public void set( final int f )
	{
		setShort( getCodedSignedShort( f ) );
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
		return -Short.MIN_VALUE + Short.MAX_VALUE;
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
	public int compareTo( final UnsignedShortType c )
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
	public UnsignedShortType createVariable()
	{
		return new UnsignedShortType( 0 );
	}

	@Override
	public UnsignedShortType copy()
	{
		return new UnsignedShortType( get() );
	}

	@Override
	public String toString()
	{
		return "" + get();
	}
}
