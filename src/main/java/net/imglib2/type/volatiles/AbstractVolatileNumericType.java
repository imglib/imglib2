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

package net.imglib2.type.volatiles;

import net.imglib2.Volatile;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

/**
 * Abstract base class for {@link VolatileNumericType}s that wrap a
 * {@link NumericType} that is either VALID or INVALID.
 * 
 * @param <N>
 *            wrapped {@link NumericType}.
 * @param <T>
 *            type of derived concrete class.
 * 
 * @author Stephan Saalfeld
 */
abstract public class AbstractVolatileNumericType< N extends NumericType< N >, T extends AbstractVolatileNumericType< N, T > >
		extends Volatile< N >
		implements NumericType< T >
{
	public AbstractVolatileNumericType( final N t, final boolean valid )
	{
		super( t, valid );
	}

	public AbstractVolatileNumericType( final N t )
	{
		this( t, false );
	}

	@Override
	public void set( final T c )
	{
		t.set( c.t );
		valid = c.valid;
	}

	@Override
	public void add( final T c )
	{
		t.add( c.t );
		valid &= c.valid;
	}

	@Override
	public void sub( final T c )
	{
		t.sub( c.t );
		valid &= c.valid;
	}

	@Override
	public void mul( final T c )
	{
		t.mul( c.t );
		valid &= c.valid;
	}

	@Override
	public void div( final T c )
	{
		t.div( c.t );
		valid &= c.valid;
	}

	@Override
	public void setZero()
	{
		t.setZero();
	}

	@Override
	public void setOne()
	{
		t.setOne();
	}

	@Override
	public void mul( final float c )
	{
		t.mul( c );
	}

	@Override
	public void mul( final double c )
	{
		t.mul( c );
	}

	@Override
	public boolean valueEquals( T other )
	{
		return isValid() == other.isValid() && t.valueEquals( other.t );
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( ! getClass().isInstance( obj ) )
			return false;
		@SuppressWarnings( "unchecked" )
		T t = ( T ) obj;
		return AbstractVolatileNumericType.this.valueEquals( t );
	}

	@Override
	public int hashCode()
	{
		return Util.combineHash( Boolean.hashCode( isValid() ), t.hashCode() );
	}
}
