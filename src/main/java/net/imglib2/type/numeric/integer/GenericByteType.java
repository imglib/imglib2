/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public abstract class GenericByteType< T extends GenericByteType< T > > extends AbstractIntegerType< T > implements NativeType< T >
{
	final Index i;

	final protected NativeImg< ?, ? extends ByteAccess > img;

	// the DataAccess that holds the information
	protected ByteAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public GenericByteType( final NativeImg< ?, ? extends ByteAccess > byteStorage )
	{
		i = new Index();
		img = byteStorage;
	}

	// this is the constructor if you want it to be a variable
	public GenericByteType( final byte value )
	{
		i = new Index();
		img = null;
		dataAccess = new ByteArray( 1 );
		setByte( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public GenericByteType( final ByteAccess access )
	{
		i = new Index();
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public GenericByteType()
	{
		this( ( byte ) 0 );
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction();
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
	public abstract NativeTypeFactory< T, ByteAccess > getNativeTypeFactory();

	/**
	 * @deprecated Use {@link #getByte()} instead.
	 */
	@Deprecated
	protected byte getValue()
	{
		return dataAccess.getValue( i.get() );
	}

	/**
	 * @deprecated Use {@link #setByte(byte)} instead.
	 */
	@Deprecated
	protected void setValue( final byte f )
	{
		dataAccess.setValue( i.get(), f );
	}

	/**
	 * Returns the primitive byte value that is used to store this type.
	 *
	 * @return primitive byte value
	 */
	public byte getByte()
	{
		return dataAccess.getValue( i.get() );
	}

	/**
	 * Sets the primitive byte value that is used to store this type.
	 */
	public void setByte( final byte f )
	{
		dataAccess.setValue( i.get(), f );
	}

	@Override
	public void mul( final float c )
	{
		final byte a = getByte();
		setByte( ( byte ) Util.round( a * c ) );
	}

	@Override
	public void mul( final double c )
	{
		final byte a = getByte();
		setByte( ( byte ) Util.round( a * c ) );
	}

	@Override
	public void add( final T c )
	{
		final byte a = getByte();
		setByte( ( byte ) ( a + c.getByte() ) );
	}

	@Override
	public void div( final T c )
	{
		final byte a = getByte();
		setByte( ( byte ) ( a / c.getByte() ) );
	}

	@Override
	public void mul( final T c )
	{
		final byte a = getByte();
		setByte( ( byte ) ( a * c.getByte() ) );
	}

	@Override
	public void sub( final T c )
	{
		final byte a = getByte();
		setByte( ( byte ) ( a - c.getByte() ) );
	}

	@Override
	public void set( final T c )
	{
		setByte( c.getByte() );
	}

	@Override
	public void setOne()
	{
		setByte( ( byte ) 1 );
	}

	@Override
	public void setZero()
	{
		setByte( ( byte ) 0 );
	}

	@Override
	public void inc()
	{
		byte a = getByte();
		setByte( ++a );
	}

	@Override
	public void dec()
	{
		byte a = getByte();
		setByte( --a );
	}

	@Override
	public String toString()
	{
		return "" + getByte();
	}

	@Override
	public int getBitsPerPixel()
	{
		return 8;
	}

	@Override
	public int compareTo( final T other )
	{
		return Byte.compare( getByte(), other.getByte() );
	}

	@Override
	public boolean valueEquals( final T t )
	{
		return getByte() == t.getByte();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !getClass().isInstance( obj ) )
			return false;
		@SuppressWarnings( "unchecked" )
		final T t = ( T ) obj;
		return GenericByteType.this.valueEquals( t );
	}

	@Override
	public int hashCode()
	{
		return Byte.hashCode( getByte() );
	}
}
