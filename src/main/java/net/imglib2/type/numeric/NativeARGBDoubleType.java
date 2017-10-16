/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveType;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.util.Fraction;

/**
 *
 * @author Stephan Saalfeld
 */
public class NativeARGBDoubleType extends AbstractARGBDoubleType< NativeARGBDoubleType > implements NativeType< NativeARGBDoubleType >
{
	private int i = 0;

	private int ai = 0, ri = 1, gi = 2, bi = 3;

	final protected NativeImg< ?, ? extends DoubleAccess > img;

	protected DoubleAccess dataAccess;

	public NativeARGBDoubleType( final NativeImg< ?, ? extends DoubleAccess > img )
	{
		this.img = img;
	}

	public NativeARGBDoubleType( final double a, final double r, final double g, final double b )
	{
		img = null;
		dataAccess = new DoubleArray( 4 );
		set( a, r, g, b );
	}

	public NativeARGBDoubleType( final DoubleAccess access )
	{
		img = null;
		dataAccess = access;
	}

	public NativeARGBDoubleType()
	{
		this( 0, 0, 0, 0 );
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public NativeARGBDoubleType duplicateTypeOnSameNativeImg()
	{
		return new NativeARGBDoubleType( img );
	}

	private static final PrimitiveTypeInfo< NativeARGBDoubleType, DoubleAccess > info = PrimitiveTypeInfo.DOUBLE( img -> new NativeARGBDoubleType( img ) );

	@Override
	public PrimitiveTypeInfo< NativeARGBDoubleType, DoubleAccess > getPrimitiveTypeInfo()
	{
		return info;
	}

	@Override
	public double getA()
	{
		return dataAccess.getValue( ai );
	}

	@Override
	public double getR()
	{
		return dataAccess.getValue( ri );
	}

	@Override
	public double getG()
	{
		return dataAccess.getValue( gi );
	}

	@Override
	public double getB()
	{
		return dataAccess.getValue( bi );
	}

	@Override
	public void setA( final double a )
	{
		dataAccess.setValue( ai, a );
	}

	@Override
	public void setR( final double r )
	{
		dataAccess.setValue( ri, r );
	}

	@Override
	public void setG( final double g )
	{
		dataAccess.setValue( gi, g );
	}

	@Override
	public void setB( final double b )
	{
		dataAccess.setValue( bi, b );
	}

	@Override
	public void set( final double a, final double r, final double g, final double b )
	{
		dataAccess.setValue( ai, a );
		dataAccess.setValue( ri, r );
		dataAccess.setValue( gi, g );
		dataAccess.setValue( bi, b );
	}

	public void set( final ARGBDoubleType c )
	{
		set( c.getA(), c.getR(), c.getG(), c.getB() );
	}

	@Override
	public NativeARGBDoubleType createVariable()
	{
		return new NativeARGBDoubleType();
	}

	@Override
	public NativeARGBDoubleType copy()
	{
		return new NativeARGBDoubleType( getA(), getR(), getG(), getB() );
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction( 4, 1 );
	}

	@Override
	public void updateIndex( final int index )
	{
		this.i = index;
		ai = i * 4;
		ri = ai + 1;
		gi = ai + 2;
		bi = ai + 3;
	}

	@Override
	public void incIndex()
	{
		++i;
		ai += 4;
		ri += 4;
		gi += 4;
		bi += 4;
	}

	@Override
	public void incIndex( final int increment )
	{
		i += increment;

		final int inc2 = increment * 4;
		ai += inc2;
		ri += inc2;
		gi += inc2;
		bi += inc2;
	}

	@Override
	public void decIndex()
	{
		--i;
		ai -= 4;
		ri -= 4;
		gi -= 4;
		bi -= 4;
	}

	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;

		final int dec2 = decrement * 4;
		ai -= dec2;
		ri -= dec2;
		gi -= dec2;
		bi -= dec2;
	}

	@Override
	public int getIndex()
	{
		return i;
	}
}
