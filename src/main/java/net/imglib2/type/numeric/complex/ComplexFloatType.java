/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.type.numeric.complex;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ComplexFloatType extends AbstractComplexType< ComplexFloatType > implements NativeType< ComplexFloatType >
{
	private final Index i;

	final protected NativeImg< ?, ? extends FloatAccess > img;

	// the DataAccess that holds the information
	protected FloatAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public ComplexFloatType( final NativeImg< ?, ? extends FloatAccess > complexfloatStorage )
	{
		i = new Index();
		img = complexfloatStorage;
	}

	// this is the constructor if you want it to be a variable
	public ComplexFloatType( final float r, final float i )
	{
		this.i = new Index();
		img = null;
		dataAccess = new FloatArray( 2 );
		set( r, i );
	}

	// this is the constructor if you want to specify the dataAccess
	public ComplexFloatType( final FloatAccess access )
	{
		this.i = new Index();
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public ComplexFloatType()
	{
		this( 0, 0 );
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
	public ComplexFloatType duplicateTypeOnSameNativeImg()
	{
		return new ComplexFloatType( img );
	}

	private static final NativeTypeFactory< ComplexFloatType, FloatAccess > typeFactory = NativeTypeFactory.FLOAT( ComplexFloatType::new );

	@Override
	public NativeTypeFactory< ComplexFloatType, FloatAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	@Override
	public float getRealFloat()
	{
		return dataAccess.getValue( i.get() << 1 );
	}

	@Override
	public double getRealDouble()
	{
		return dataAccess.getValue( i.get() << 1 );
	}

	@Override
	public float getImaginaryFloat()
	{
		return dataAccess.getValue( ( i.get() << 1 ) + 1 );
	}

	@Override
	public double getImaginaryDouble()
	{
		return dataAccess.getValue( ( i.get() << 1 ) + 1 );
	}

	@Override
	public void setReal( final float r )
	{
		dataAccess.setValue( i.get() << 1, r );
	}

	@Override
	public void setReal( final double r )
	{
		dataAccess.setValue( i.get() << 1, ( float ) r );
	}

	@Override
	public void setImaginary( final float i )
	{
		dataAccess.setValue( ( this.i.get() << 1 ) + 1, i );
	}

	@Override
	public void setImaginary( final double i )
	{
		dataAccess.setValue( ( this.i.get() << 1 ) + 1, ( float ) i );
	}

	public void set( final float r, final float i )
	{
		final int j = this.i.get() << 1;
		dataAccess.setValue( j, r );
		dataAccess.setValue( j + 1, i );
	}

	@Override
	public void add( final ComplexFloatType c )
	{
		setReal( getRealFloat() + c.getRealFloat() );
		setImaginary( getImaginaryFloat() + c.getImaginaryFloat() );
	}

	@Override
	public void div( final ComplexFloatType c )
	{
		final float a1 = getRealFloat();
		final float b1 = getImaginaryFloat();
		final float c1 = c.getRealFloat();
		final float d1 = c.getImaginaryFloat();

		setReal( ( a1 * c1 + b1 * d1 ) / ( c1 * c1 + d1 * d1 ) );
		setImaginary( ( b1 * c1 - a1 * d1 ) / ( c1 * c1 + d1 * d1 ) );
	}

	@Override
	public void mul( final ComplexFloatType t )
	{
		// a + bi
		final float a = getRealFloat();
		final float b = getImaginaryFloat();

		// c + di
		final float c = t.getRealFloat();
		final float d = t.getImaginaryFloat();

		setReal( a * c - b * d );
		setImaginary( a * d + b * c );
	}

	@Override
	public void sub( final ComplexFloatType c )
	{
		setReal( getRealFloat() - c.getRealFloat() );
		setImaginary( getImaginaryFloat() - c.getImaginaryFloat() );
	}

	@Override
	public void complexConjugate()
	{
		setImaginary( -getImaginaryFloat() );
	}

	public void switchRealComplex()
	{
		final float a = getRealFloat();
		setReal( getImaginaryFloat() );
		setImaginary( a );
	}

	@Override
	public void set( final ComplexFloatType c )
	{
		setReal( c.getRealFloat() );
		setImaginary( c.getImaginaryFloat() );
	}

	@Override
	public ComplexFloatType createVariable()
	{
		return new ComplexFloatType( 0, 0 );
	}

	@Override
	public ComplexFloatType copy()
	{
		return new ComplexFloatType( getRealFloat(), getImaginaryFloat() );
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction( 2, 1 );
	}

	@Override
	public boolean valueEquals( final ComplexFloatType t )
	{
		return FloatType.equals( getRealFloat(), t.getRealFloat() ) &&
				FloatType.equals( getImaginaryFloat(), t.getImaginaryFloat() );
	}

	@Override
	public boolean equals( final Object obj )
	{
		return Util.valueEqualsObject( this, obj );
	}

	@Override
	public int hashCode()
	{
		final int rHash = Float.hashCode( getRealFloat() );
		final int iHash = Float.hashCode( getImaginaryFloat() );
		return Util.combineHash( rHash, iHash );
	}
}
