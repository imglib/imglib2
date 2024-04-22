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

package net.imglib2.type.logic;

import java.math.BigInteger;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.BooleanType;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.AbstractIntegerType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class BitType extends AbstractIntegerType< BitType > implements BooleanType< BitType >, NativeType< BitType >, IntegerType< BitType >
{
	// Maximum count is Integer.MAX_VALUE * (64 / getBitsPerPixel())
	protected final Index i;

	final protected NativeImg< ?, ? extends LongAccess > img;

	// the DataAccess that holds the information
	protected LongAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public BitType( final NativeImg< ?, ? extends LongAccess > bitStorage )
	{
		i = new Index();
		img = bitStorage;
	}

	// this is the constructor if you want it to be a variable
	public BitType( final boolean value )
	{
		this( ( NativeImg< ?, ? extends LongAccess > ) null );
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public BitType( final LongAccess access )
	{
		this( ( NativeImg< BitType, ? extends LongAccess > ) null );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public BitType()
	{
		this( false );
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public Index index()
	{
		return i;
	}

	@Override
	public BitType duplicateTypeOnSameNativeImg()
	{
		return new BitType( img );
	}

	private static final NativeTypeFactory< BitType, LongAccess > typeFactory = NativeTypeFactory.LONG( BitType::new );

	@Override
	public NativeTypeFactory< BitType, LongAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	@Override
	public boolean get()
	{
		final int j = i.get();
		return 1 == ( ( dataAccess.getValue( j >>> 6 ) >>> ( ( j & 63 ) ) ) & 1l );
	}

	// Crops value to within mask
	@Override
	public void set( final boolean value )
	{
		/*
		final int i1 = k >>> 6; // k / 64;
		final long shift = k % 64;
		*/
		// Same as above, minus one multiplication, plus one shift to multiply the reminder by 2
		final int j = i.get();
		final int i1 = j >>> 6; // Same as i / 64
		final long bit = 1l << (j & 63);
		synchronized ( dataAccess )
		{
			// Clear or set the bit
			if ( value )
				dataAccess.setValue( i1, dataAccess.getValue( i1 ) | bit );
			else
				dataAccess.setValue( i1, dataAccess.getValue( i1 ) & ~bit );
		}
	}

	@Override
	public int getInteger()
	{
		return get() ? 1 : 0;
	}

	@Override
	public long getIntegerLong()
	{
		return get() ? 1 : 0;
	}

	@Override
	public BigInteger getBigInteger()
	{
		return get() ? BigInteger.ONE : BigInteger.ZERO;
	}

	@Override
	public void setInteger( final int f )
	{
		if ( f >= 1 )
			set( true );
		else
			set( false );
	}

	@Override
	public void setInteger( final long f )
	{
		if ( f >= 1 )
			set( true );
		else
			set( false );
	}

	@Override
	public void setBigInteger( final BigInteger b )
	{
		if ( b.compareTo( BigInteger.ZERO ) > 0 )
			set( true );
		else
			set( false );
	}

	@Override
	public double getMaxValue()
	{
		return 1;
	}

	@Override
	public double getMinValue()
	{
		return 0;
	}

	@Override
	public void set( final BitType c )
	{
		set( c.get() );
	}

	@Override
	public void and( final BitType c )
	{
		set( get() && c.get() );
	}

	@Override
	public void or( final BitType c )
	{
		set( get() || c.get() );
	}

	@Override
	public void xor( final BitType c )
	{
		set( get() ^ c.get() );
	}

	@Override
	public void not()
	{
		set( !get() );
	}

	@Override
	public void add( final BitType c )
	{
		xor( c );
	}

	@Override
	public void div( final BitType c )
	{
		and( c );
	}

	@Override
	public void mul( final BitType c )
	{
		and( c );
	}

	@Override
	public void sub( final BitType c )
	{
		xor( c );
	}

	@Override
	public void mul( final float c )
	{
		if ( c >= 0.5f )
			set( get() && true );
		else
			set( false );
	}

	@Override
	public void mul( final double c )
	{
		if ( c >= 0.5f )
			set( get() && true );
		else
			set( false );
	}

	@Override
	public void setOne()
	{
		set( true );
	}

	@Override
	public void setZero()
	{
		set( false );
	}

	@Override
	public void inc()
	{
		not();
	}

	@Override
	public void dec()
	{
		not();
	}

	@Override
	public BitType createVariable()
	{
		return new BitType();
	}

	@Override
	public BitType copy()
	{
		return new BitType( dataAccess != null ? get() : false );
	}

	@Override
	public String toString()
	{
		final boolean value = get();

		return value ? "1" : "0";
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction(1, 64);
	}

	@Override
	public int getBitsPerPixel()
	{
		return 1;
	}

	@Override
	public int compareTo( final BitType other )
	{
		return Boolean.compare( get(), other.get() );
	}

	@Override
	public boolean valueEquals( final BitType t )
	{
		return get() == t.get();
	}

	@Override
	public boolean equals( final Object obj )
	{
		return Util.valueEqualsObject( this, obj );
	}

	@Override
	public int hashCode()
	{
		return Boolean.hashCode( get() );
	}
}
