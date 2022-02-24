/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

/**
 * 
 * 
 * @author Stephan Saalfeld
 */
public class ARGBDoubleType extends AbstractARGBDoubleType< ARGBDoubleType >
{
	protected double a, r, g, b;

	public ARGBDoubleType()
	{}

	public ARGBDoubleType( final double a, final double r, final double g, final double b )
	{
		set( a, r, g, b );
	}

	@Override
	public ARGBDoubleType createVariable()
	{
		return new ARGBDoubleType();
	}

	@Override
	public ARGBDoubleType copy()
	{
		return new ARGBDoubleType( a, r, g, b );
	}

	@Override
	public void set( final double a, final double r, final double g, final double b )
	{
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void set( final NativeARGBDoubleType c )
	{
		set( c.getA(), c.getR(), c.getG(), c.getB() );
	}

	@Override
	public double getA()
	{
		return a;
	}

	@Override
	public double getR()
	{
		return r;
	}

	@Override
	public double getG()
	{
		return g;
	}

	@Override
	public double getB()
	{
		return b;
	}

	@Override
	public void setA( final double a )
	{
		this.a = a;
	}

	@Override
	public void setR( final double r )
	{
		this.r = r;
	}

	@Override
	public void setG( final double g )
	{
		this.g = g;
	}

	@Override
	public void setB( final double b )
	{
		this.b = b;
	}
}
