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

package net.imglib2.type.numeric.complex;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;

import static java.lang.Float.floatToIntBits;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ComplexFloatType extends AbstractComplexType< ComplexFloatType > implements NativeType< ComplexFloatType >
{
	private int i = 0;

	// the indices for real and imaginary number
	private int realI = 0, imaginaryI = 1;

	final protected NativeImg< ?, ? extends FloatAccess > img;

	// the DataAccess that holds the information
	protected FloatAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public ComplexFloatType( final NativeImg< ?, ? extends FloatAccess > complexfloatStorage )
	{
		img = complexfloatStorage;
	}

	// this is the constructor if you want it to be a variable
	public ComplexFloatType( final float r, final float i )
	{
		img = null;
		dataAccess = new FloatArray( 2 );
		set( r, i );
	}

	// this is the constructor if you want to specify the dataAccess
	public ComplexFloatType( final FloatAccess access )
	{
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
	public ComplexFloatType duplicateTypeOnSameNativeImg()
	{
		return new ComplexFloatType( img );
	}

	private static final NativeTypeFactory< ComplexFloatType, FloatAccess > typeFactory = NativeTypeFactory.FLOAT( img -> new ComplexFloatType( img ) );

	@Override
	public NativeTypeFactory< ComplexFloatType, FloatAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	@Override
	public float getRealFloat()
	{
		return dataAccess.getValue( realI );
	}

	@Override
	public double getRealDouble()
	{
		return dataAccess.getValue( realI );
	}

	@Override
	public float getImaginaryFloat()
	{
		return dataAccess.getValue( imaginaryI );
	}

	@Override
	public double getImaginaryDouble()
	{
		return dataAccess.getValue( imaginaryI );
	}

	@Override
	public void setReal( final float r )
	{
		dataAccess.setValue( realI, r );
	}

	@Override
	public void setReal( final double r )
	{
		dataAccess.setValue( realI, ( float ) r );
	}

	@Override
	public void setImaginary( final float i )
	{
		dataAccess.setValue( imaginaryI, i );
	}

	@Override
	public void setImaginary( final double i )
	{
		dataAccess.setValue( imaginaryI, ( float ) i );
	}

	public void set( final float r, final float i )
	{
		dataAccess.setValue( realI, r );
		dataAccess.setValue( imaginaryI, i );
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
	public void updateIndex( final int index )
	{
		this.i = index;
		realI = index * 2;
		imaginaryI = index * 2 + 1;
	}

	@Override
	public void incIndex()
	{
		++i;
		realI += 2;
		imaginaryI += 2;
	}

	@Override
	public void incIndex( final int increment )
	{
		i += increment;

		final int inc2 = 2 * increment;
		realI += inc2;
		imaginaryI += inc2;
	}

	@Override
	public void decIndex()
	{
		--i;
		realI -= 2;
		imaginaryI -= 2;
	}

	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
		final int dec2 = 2 * decrement;
		realI -= dec2;
		imaginaryI -= dec2;
	}

	@Override
	public int getIndex()
	{
		return i;
	}

	@Override
	public boolean valueEquals( final ComplexFloatType t )
	{
		return ( floatToIntBits( getRealFloat() ) == floatToIntBits( t.getRealFloat() ) ) &&
				( floatToIntBits( getImaginaryFloat() ) == floatToIntBits( t.getImaginaryFloat() ) );
	}

	@Override
	public boolean equals( final Object o )
	{
		return ( o instanceof ComplexFloatType )
				&& valueEquals( ( ComplexFloatType ) o );
	}

	@Override
	public int hashCode()
	{
		// NB: Compute similar hash code to java.lang.Float#hashCode().
		final int rBits = floatToIntBits( getRealFloat() );
		final int iBits = floatToIntBits( getImaginaryFloat() );
		return rBits ^ iBits;
	}
}
