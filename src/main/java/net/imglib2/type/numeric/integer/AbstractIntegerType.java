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

import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.real.AbstractRealType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 */
public abstract class AbstractIntegerType< T extends AbstractIntegerType< T > > extends AbstractRealType< T > implements IntegerType< T >
{
	@Override
	public double getMinIncrement()
	{
		return 1;
	}

	@Override
	public float getRealFloat()
	{
		return getIntegerLong();
	}

	@Override
	public double getRealDouble()
	{
		return getIntegerLong();
	}

	@Override
	public void setReal( final float real )
	{
		setInteger( Util.round( real ) );
	}

	@Override
	public void setReal( final double real )
	{
		setInteger( Util.round( real ) );
	}

	@Override
	public void inc()
	{
		setInteger( getIntegerLong() + 1 );
	}

	@Override
	public void dec()
	{
		setInteger( getIntegerLong() - 1 );
	}

	@Override
	public void setZero()
	{
		setInteger( 0 );
	}

	@Override
	public void setOne()
	{
		setInteger( 1 );
	}

	@Override
	public String toString()
	{
		return Long.toString( getIntegerLong() );
	}

	@Override
	public int compareTo( final T other )
	{
		return Long.compare( getIntegerLong(), other.getIntegerLong() );
	}

	@Override
	public boolean valueEquals( final T other )
	{
		return getIntegerLong() == getIntegerLong();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !getClass().isInstance( obj ) )
			return false;
		@SuppressWarnings( "unchecked" )
		final T t = ( T ) obj;
		return AbstractIntegerType.this.valueEquals( t );
	}

	@Override
	public int hashCode()
	{
		return Long.hashCode( getIntegerLong() );
	}
}
