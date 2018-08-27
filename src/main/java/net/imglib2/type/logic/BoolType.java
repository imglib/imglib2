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
package net.imglib2.type.logic;

import java.math.BigInteger;

import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.integer.AbstractIntegerType;

/**
 * A {@link BooleanType} wrapping a single primitive {@code boolean} variable.
 *
 * @author Tobias Pietzsch
 */
public class BoolType extends AbstractIntegerType< BoolType > implements BooleanType< BoolType >
{
	boolean value;

	public BoolType()
	{
		this( false );
	}

	public BoolType( final boolean value )
	{
		this.value = value;
	}

	public < T extends BooleanType<T> >BoolType( final T type )
	{
		this( type.get() );
	}

	@Override
	public BoolType createVariable()
	{
		return new BoolType();
	}

	@Override
	public BoolType copy()
	{
		return new BoolType( this );
	}

	@Override
	public void set( final BoolType c )
	{
		value = c.get();
	}

	@Override
	public int compareTo( final BoolType o )
	{
		return Boolean.compare( value, o.value );
	}

	@Override
	public boolean get()
	{
		return value;
	}

	@Override
	public void set( final boolean value )
	{
		this.value = value;
	}

	public void and( final boolean b )
	{
		value &= b;
	}

	public void or( final boolean b )
	{
		value |= b;
	}

	public void xor( final boolean b )
	{
		value ^= b;
	}

	@Override
	public void and( final BoolType c )
	{
		and( c.value );
	}

	@Override
	public void or( final BoolType c )
	{
		or( c.value );
	}

	@Override
	public void xor( final BoolType c )
	{
		xor( c.value );
	}

	@Override
	public void not()
	{
		value = !value;
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
	public int getBitsPerPixel()
	{
		return 1;
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
		set( f >= 1 );
	}

	@Override
	public void setInteger( final long f )
	{
		set( f >= 1 );
	}

	@Override
	public void setBigInteger( final BigInteger b )
	{
		set( b.compareTo(BigInteger.ZERO) > 0 );
	}

	@Override
	public boolean valueEquals( BoolType t )
	{
		return get() == t.get();
	}

	@Override
	public int hashCode()
	{
		return Boolean.hashCode( get() );
	}
}
