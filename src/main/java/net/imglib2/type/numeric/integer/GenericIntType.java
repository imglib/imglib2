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
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.array.IntArray;
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
public abstract class GenericIntType< T extends GenericIntType< T > > extends AbstractIntegerType< T > implements NativeType< T >
{
	final Index i;

	final protected NativeImg< ?, ? extends IntAccess > img;

	// the DataAccess that holds the information
	protected IntAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public GenericIntType( final NativeImg< ?, ? extends IntAccess > intStorage )
	{
		i = new Index();
		img = intStorage;
	}

	// this is the constructor if you want it to be a variable
	public GenericIntType( final int value )
	{
		i = new Index();
		img = null;
		dataAccess = new IntArray( 1 );
		setInt( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public GenericIntType( final IntAccess access )
	{
		i = new Index();
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public GenericIntType()
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
	public abstract NativeTypeFactory< T, IntAccess > getNativeTypeFactory();

	/**
	 * @deprecated Use {@link #getInt()} instead.
	 */
	@Deprecated
	protected int getValue()
	{
		return dataAccess.getValue( i.get() );
	}

	/**
	 * @deprecated Use {@link #setInt(int)} instead.
	 */
	@Deprecated
	protected void setValue( final int f )
	{
		dataAccess.setValue( i.get(), f );
	}

	/**
	 * Returns the primitive int value that is used to store this type.
	 *
	 * @return primitive int value
	 */
	public int getInt()
	{
		return dataAccess.getValue( i.get() );
	}

	/**
	 * Sets the primitive int value that is used to store this type.
	 */
	public void setInt( final int f )
	{
		dataAccess.setValue( i.get(), f );
	}

	@Override
	public void mul( final float c )
	{
		final int a = getInt();
		setInt( Util.round( a * c ) );
	}

	@Override
	public void mul( final double c )
	{
		final int a = getInt();
		setInt( ( int ) Util.round( a * c ) );
	}

	@Override
	public void add( final T c )
	{
		final int a = getInt();
		setInt( a + c.getInt() );
	}

	@Override
	public void div( final T c )
	{
		final int a = getInt();
		setInt( a / c.getInt() );
	}

	@Override
	public void mul( final T c )
	{
		final int a = getInt();
		setInt( a * c.getInt() );
	}

	@Override
	public void sub( final T c )
	{
		final int a = getInt();
		setInt( a - c.getInt() );
	}

	@Override
	public void pow( final T c )
	{
		final int a = getInt();
		setReal( Math.pow( a, c.getInt() ) );
	}

	@Override
	public void pow( final double power )
	{
		final int a = getInt();
		setReal( Math.pow( a, power ) );
	}

	@Override
	public void set( final T c )
	{
		setInt( c.getInt() );
	}

	@Override
	public void setOne()
	{
		setInt( 1 );
	}

	@Override
	public void setZero()
	{
		setInt( 0 );
	}

	@Override
	public void inc()
	{
		int a = getInt();
		setInt( ++a );
	}

	@Override
	public void dec()
	{
		int a = getInt();
		setInt( --a );
	}

	@Override
	public String toString()
	{
		return "" + getInt();
	}

	@Override
	public int getBitsPerPixel()
	{
		return 32;
	}

	@Override
	public int compareTo( final T other )
	{
		return Integer.compare( getInt(), other.getInt() );
	}

	@Override
	public boolean valueEquals( final T t )
	{
		return getInt() == t.getInt();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !getClass().isInstance( obj ) )
			return false;
		@SuppressWarnings( "unchecked" )
		final T t = ( T ) obj;
		return GenericIntType.this.valueEquals( t );
	}

	@Override
	public int hashCode()
	{
		return Integer.hashCode( getInt() );
	}
}
