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

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Util;

/**
 * Abstract base class for {@link NativeType native} {@link IntegerType}s that
 * encode their value into a 16bit short.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public abstract class GenericShortType< T extends GenericShortType< T > >
		extends AbstractIntegerType< T >
		implements NativeType< T >
{
	int i = 0;

	final protected NativeImg< ?, ? extends ShortAccess > img;

	// the DataAccess that holds the information
	protected ShortAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public GenericShortType( final NativeImg< ?, ? extends ShortAccess > shortStorage )
	{
		img = shortStorage;
	}

	// this is the constructor if you want it to be a variable
	public GenericShortType( final short value )
	{
		img = null;
		dataAccess = new ShortArray( 1 );
		setShort( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public GenericShortType( final ShortAccess access )
	{
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public GenericShortType()
	{
		this( ( short ) 0 );
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
	public abstract NativeTypeFactory< T, ShortAccess > getNativeTypeFactory();

	/**
	 * @deprecated Use {@link #getShort()} instead.
	 */
	@Deprecated
	protected short getValue()
	{
		return dataAccess.getValue( i );
	}

	/**
	 * @deprecated Use {@link #setShort(short)} instead.
	 */
	@Deprecated
	protected void setValue( final short f )
	{
		dataAccess.setValue( i, f );
	}

	/**
	 * Returns the primitive short value that is used to store this type.
	 *
	 * @return primitive short value
	 */
	public short getShort()
	{
		return dataAccess.getValue( i );
	}

	/**
	 * Sets the primitive short value that is used to store this type.
	 */
	public void setShort( final short f )
	{
		dataAccess.setValue( i, f );
	}

	@Override
	public void mul( final float c )
	{
		final short a = getShort();
		setShort( ( short ) Util.round( a * c ) );
	}

	@Override
	public void mul( final double c )
	{
		final short a = getShort();
		setShort( ( short ) Util.round( a * c ) );
	}

	@Override
	public void add( final T c )
	{
		final short a = getShort();
		setShort( ( short ) ( a + c.getShort() ) );
	}

	@Override
	public void div( final T c )
	{
		final short a = getShort();
		setShort( ( short ) ( a / c.getShort() ) );
	}

	@Override
	public void mul( final T c )
	{
		final short a = getShort();
		setShort( ( short ) ( a * c.getShort() ) );
	}

	@Override
	public void sub( final T c )
	{
		final short a = getShort();
		setShort( ( short ) ( a - c.getShort() ) );
	}

	@Override
	public void set( final T c )
	{
		setShort( c.getShort() );
	}

	@Override
	public void setOne()
	{
		setShort( ( short ) 1 );
	}

	@Override
	public void setZero()
	{
		setShort( ( short ) 0 );
	}

	@Override
	public void inc()
	{
		short a = getShort();
		setShort( ++a );
	}

	@Override
	public void dec()
	{
		short a = getShort();
		setShort( --a );
	}

	@Override
	public String toString()
	{
		return "" + getShort();
	}

	@Override
	public void updateIndex( final int index )
	{
		i = index;
	}

	@Override
	public int getIndex()
	{
		return i;
	}

	@Override
	public void incIndex()
	{
		++i;
	}

	@Override
	public void incIndex( final int increment )
	{
		i += increment;
	}

	@Override
	public void decIndex()
	{
		--i;
	}

	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
	}

	@Override
	public int getBitsPerPixel()
	{
		return 16;
	}

	@Override
	public int compareTo( final T other )
	{
		// NB: Use Integer.compare because Short.compare returns values different from -1, 0, 1.
		return Integer.compare( getShort(), other.getShort() );
	}

	@Override
	public boolean valueEquals( final T t )
	{
		return getShort() == t.getShort();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( ! getClass().isInstance( obj ) )
			return false;
		@SuppressWarnings( "unchecked" )
		T t = ( T ) obj;
		return GenericShortType.this.valueEquals( t );
	}

	@Override
	public int hashCode()
	{
		return Short.hashCode( getShort() );
	}
}
