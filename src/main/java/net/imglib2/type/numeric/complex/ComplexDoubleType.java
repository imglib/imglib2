/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ComplexDoubleType extends AbstractComplexType< ComplexDoubleType > implements NativeType< ComplexDoubleType >
{
	private final Index i;

	// the indices for real and imaginary value
	private int realI = 0, imaginaryI = 1;

	final protected NativeImg< ?, ? extends DoubleAccess > img;

	// the DataAccess that holds the information
	protected DoubleAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public ComplexDoubleType( final NativeImg< ?, ? extends DoubleAccess > complexfloatStorage )
	{
		i = new Index();
		img = complexfloatStorage;
	}

	// this is the constructor if you want it to be a variable
	public ComplexDoubleType( final double r, final double i )
	{
		this.i = new Index();
		img = null;
		dataAccess = new DoubleArray( 2 );
		set( r, i );
	}

	// this is the constructor if you want to specify the dataAccess
	public ComplexDoubleType( final DoubleAccess access )
	{
		i = new Index();
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public ComplexDoubleType()
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
	public ComplexDoubleType duplicateTypeOnSameNativeImg()
	{
		return new ComplexDoubleType( img );
	}

	private static final NativeTypeFactory< ComplexDoubleType, DoubleAccess > typeFactory = NativeTypeFactory.DOUBLE( ComplexDoubleType::new );

	@Override
	public NativeTypeFactory< ComplexDoubleType, DoubleAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	@Override
	public float getRealFloat()
	{
		return ( float ) dataAccess.getValue( i.get() << 1 );
	}

	@Override
	public double getRealDouble()
	{
		return dataAccess.getValue( i.get() << 1 );
	}

	@Override
	public float getImaginaryFloat()
	{
		return ( float ) dataAccess.getValue( ( i.get() << 1 ) + 1 );
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
		dataAccess.setValue( i.get() << 1, r );
	}

	@Override
	public void setImaginary( final float i )
	{
		dataAccess.setValue( ( this.i.get() << 1 ) + 1, i );
	}

	@Override
	public void setImaginary( final double i )
	{
		dataAccess.setValue( ( this.i.get() << 1 ) + 1, i );
	}

	public void set( final double r, final double i )
	{
		final int j = this.i.get() << 1;
		dataAccess.setValue( j, r );
		dataAccess.setValue( j + 1, i );
	}

	@Override
	public void set( final ComplexDoubleType c )
	{
		setReal( c.getRealDouble() );
		setImaginary( c.getImaginaryDouble() );
	}

	@Override
	public ComplexDoubleType createVariable()
	{
		return new ComplexDoubleType( 0, 0 );
	}

	@Override
	public ComplexDoubleType copy()
	{
		if ( dataAccess != null )
			return new ComplexDoubleType( getRealFloat(), getImaginaryFloat() );
		else
			return createVariable();
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction( 2, 1 );
	}
}
