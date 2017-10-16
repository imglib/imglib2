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
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.type.PrimitiveTypeInfo;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class IntType extends GenericIntType< IntType >
{
	// this is the constructor if you want it to read from an array
	public IntType( final NativeImg< ?, ? extends IntAccess > img )
	{
		super( img );
	}

	// this is the constructor if you want it to be a variable
	public IntType( final int value )
	{
		super( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public IntType( final IntAccess access )
	{
		super( access );
	}

	// this is the constructor if you want it to be a variable
	public IntType()
	{
		super( 0 );
	}

	@Override
	public IntType duplicateTypeOnSameNativeImg()
	{
		return new IntType( img );
	}

	private static final PrimitiveTypeInfo< IntType, IntAccess > info = PrimitiveTypeInfo.INT( img -> new IntType( img ) );

	@Override
	public PrimitiveTypeInfo< IntType, IntAccess > getPrimitiveTypeInfo()
	{
		return info;
	}

	public int get()
	{
		return getInt();
	}

	public void set( final int b )
	{
		setInt( b );
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
		return Integer.MAX_VALUE;
	}

	@Override
	public double getMinValue()
	{
		return Integer.MIN_VALUE;
	}

	@Override
	public IntType createVariable()
	{
		return new IntType( 0 );
	}

	@Override
	public IntType copy()
	{
		return new IntType( getInt() );
	}
}
