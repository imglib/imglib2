/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import net.imglib2.type.BooleanType;

/**
 * A {@link BooleanType} wrapping a single primitive {@code boolean} variable.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class BoolType implements BooleanType< BoolType >
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
		if ( value )
			return o.value ? 0 : 1;
		else
			return o.value ? -1 : 0;
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
}
