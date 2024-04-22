/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Mark Hiner
 */
public class LongType extends GenericLongType< LongType >
{
	// this is the constructor if you want it to read from an array
	public LongType( final NativeImg< ?, ? extends LongAccess > longStorage )
	{
		super( longStorage );
	}

	// this is the constructor if you want to specify the dataAccess
	public LongType( final LongAccess access )
	{
		super( access );
	}

	// this is the constructor if you want it to be a variable
	public LongType( final long value )
	{
		super( value );
	}

	// this is the constructor if you want it to be a variable
	public LongType()
	{
		super( 0 );
	}

	@Override
	public LongType duplicateTypeOnSameNativeImg()
	{
		return new LongType( img );
	}

	private static final NativeTypeFactory< LongType, LongAccess > typeFactory = NativeTypeFactory.LONG( LongType::new );

	@Override
	public NativeTypeFactory< LongType, LongAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	public long get()
	{
		return getLong();
	}

	public void set( final long f )
	{
		dataAccess.setValue( i.get(), f );
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
	public void setReal( float real )
	{
		set( Util.roundToLong( real ) );
	}

	@Override
	public double getMaxValue()
	{
		return Long.MAX_VALUE;
	} // imprecise

	@Override
	public double getMinValue()
	{
		return Long.MIN_VALUE;
	}

	@Override
	public LongType createVariable()
	{
		return new LongType( 0 );
	}

	@Override
	public LongType copy()
	{
		return new LongType( dataAccess != null ? get() : 0 );
	}
}
