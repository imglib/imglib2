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
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;
import net.imglib2.util.Util;

/**
 * Abstract superclass for Long types.
 *
 * @author Mark Hiner
 */
public abstract class GenericLongType< T extends GenericLongType< T > > extends AbstractIntegerType< T > implements NativeType< T >
{
	final Index i;

	final protected NativeImg< ?, ? extends LongAccess > img;

	// the DataAccess that holds the information
	protected LongAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public GenericLongType( final NativeImg< ?, ? extends LongAccess > longStorage )
	{
		i = new Index();
		img = longStorage;
	}

	// this is the constructor if you want it to be a variable
	public GenericLongType( final long value )
	{
		i = new Index();
		img = null;
		dataAccess = new LongArray( 1 );
		setLong( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public GenericLongType( final LongAccess access )
	{
		i = new Index();
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public GenericLongType()
	{
		this( 0 );
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
	public abstract NativeTypeFactory< T, LongAccess > getNativeTypeFactory();

	/**
	 * @deprecated Use {@link #getLong()} instead.
	 */
	@Deprecated
	protected long getValue()
	{
		return dataAccess.getValue( i.get() );
	}

	/**
	 * @deprecated Use {@link #setLong(long)} instead.
	 */
	@Deprecated
	protected void setValue( final long f )
	{
		dataAccess.setValue( i.get(), f );
	}

	/**
	 * Returns the primitive long value that is used to store this type.
	 *
	 * @return primitive long value
	 */
	public long getLong()
	{
		return dataAccess.getValue( i.get() );
	}

	/**
	 * Sets the primitive long value that is used to store this type.
	 */
	public void setLong( final long f )
	{
		dataAccess.setValue( i.get(), f );
	}

	@Override
	public void mul( final float c )
	{
		setLong( Util.round( getLong() * c ) );
	}

	@Override
	public void mul( final double c )
	{
		setLong( Util.round( getLong() * c ) );
	}

	@Override
	public void add( final T c )
	{
		setLong( getLong() + c.getLong() );
	}

	@Override
	public void div( final T c )
	{
		setLong( getLong() / c.getLong() );
	}

	@Override
	public void mul( final T c )
	{
		setLong( getLong() * c.getLong() );
	}

	@Override
	public void sub( final T c )
	{
		setLong( getLong() - c.getLong() );
	}

	@Override
	public void set( final T c )
	{
		setLong( c.getLong() );
	}

	@Override
	public void setOne()
	{
		setLong( 1 );
	}

	@Override
	public void setZero()
	{
		setLong( 0 );
	}

	@Override
	public void inc()
	{
		long a = getLong();
		setLong( ++a );
	}

	@Override
	public void dec()
	{
		long a = getLong();
		setLong( --a );
	}

	@Override
	public String toString()
	{
		return "" + getLong();
	}

	@Override
	public int getBitsPerPixel()
	{
		return 64;
	}

	@Override
	public int compareTo( final T other )
	{
		return Long.compare( getLong(), other.getLong() );
	}

	@Override
	public boolean valueEquals( final T other )
	{
		return getLong() == other.getLong();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !getClass().isInstance( obj ) )
			return false;
		@SuppressWarnings( "unchecked" )
		final T t = ( T ) obj;
		return GenericLongType.this.valueEquals( t );
	}

	@Override
	public int hashCode()
	{
		return Long.hashCode( getLong() );
	}
}
