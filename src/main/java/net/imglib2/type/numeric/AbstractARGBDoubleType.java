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

package net.imglib2.type.numeric;

import net.imglib2.util.Util;

import java.util.Objects;

/**
 * 
 * @author Stephan Saalfeld
 */
abstract public class AbstractARGBDoubleType< T extends AbstractARGBDoubleType< T > > implements NumericType< T >
{
	abstract public double getA();

	abstract public double getR();

	abstract public double getG();

	abstract public double getB();

	abstract public void setA( final double a );

	abstract public void setR( final double r );

	abstract public void setG( final double g );

	abstract public void setB( final double b );

	public void set( final double a, final double r, final double g, final double b )
	{
		setA( a );
		setR( r );
		setG( g );
		setB( b );
	}

	@Override
	public void set( final T c )
	{
		set( c.getA(), c.getR(), c.getG(), c.getB() );
	}

	@Override
	public void add( final T c )
	{
		set(
				getA() + c.getA(),
				getR() + c.getR(),
				getG() + c.getG(),
				getB() + c.getB() );
	}

	@Override
	public void sub( final T c )
	{
		set(
				getA() - c.getA(),
				getR() - c.getR(),
				getG() - c.getG(),
				getB() - c.getB() );
	}

	@Override
	public void mul( final T c )
	{
		set(
				getA() * c.getA(),
				getR() * c.getR(),
				getG() * c.getG(),
				getB() * c.getB() );
	}

	@Override
	public void div( final T c )
	{
		set(
				getA() / c.getA(),
				getR() / c.getR(),
				getG() / c.getG(),
				getB() / c.getB() );
	}

	@Override
	public void setZero()
	{
		set( 0, 0, 0, 0 );
	}

	@Override
	public void setOne()
	{
		set( 1, 1, 1, 1 );
	}

	@Override
	public void mul( final float c )
	{
		set(
				getA() * c,
				getR() * c,
				getG() * c,
				getB() * c );
	}

	@Override
	public void mul( final double c )
	{
		set(
				getA() * c,
				getR() * c,
				getG() * c,
				getB() * c );
	}

	/**
	 * Create and integer packed representation of this ARGB value. Crop
	 */
	public int toARGBInt()
	{
		final int a = ( int ) Math.max( 0, Math.min( 255, Util.round( getA() * 255 ) ) );
		final int r = ( int ) Math.max( 0, Math.min( 255, Util.round( getR() * 255 ) ) );
		final int g = ( int ) Math.max( 0, Math.min( 255, Util.round( getG() * 255 ) ) );
		final int b = ( int ) Math.max( 0, Math.min( 255, Util.round( getB() * 255 ) ) );

		return ( ( ( ( ( a << 8 ) | r ) << 8 ) | g ) << 8 ) | b;
	}

	@Override
	public boolean valueEquals( T t )
	{
		return
				( getA() == t.getA() ) &&
				( getR() == t.getR() ) &&
				( getG() == t.getG() ) &&
				( getB() == t.getB() );
	}

	@Override
	public boolean equals( Object obj )
	{
		return Util.valueEqualsObject( this, obj );
	}

	@Override
	public int hashCode()
	{
		// NB: this is the same as Objects.hashCode( getA(), getR(), getG(), getB() ) but faster, no boxing, no memory allocation
		int result = 31 + Double.hashCode( getA() );
		result = 31 * result + Double.hashCode( getR() );
		result = 31 * result + Double.hashCode( getG() );
		result = 31 * result + Double.hashCode( getB() );
		return result;
	}
}
