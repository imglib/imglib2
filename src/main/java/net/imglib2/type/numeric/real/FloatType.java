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

package net.imglib2.type.numeric.real;

import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import net.imglib2.view.composite.RealComposite;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class FloatType extends AbstractRealType< FloatType > implements NativeType< FloatType >
{
	private final Index i;

	final protected NativeImg< ?, ? extends FloatAccess > img;

	// the DataAccess that holds the information
	protected FloatAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public FloatType( final NativeImg< ?, ? extends FloatAccess > floatStorage )
	{
		i = new Index();
		img = floatStorage;
	}

	// this is the constructor if you want it to be a variable
	public FloatType( final float value )
	{
		i = new Index();
		img = null;
		dataAccess = new FloatArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public FloatType( final FloatAccess access )
	{
		i = new Index();
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public FloatType()
	{
		this( 0 );
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
	public FloatType duplicateTypeOnSameNativeImg()
	{
		return new FloatType( img );
	}

	private static final NativeTypeFactory< FloatType, FloatAccess > typeFactory = NativeTypeFactory.FLOAT( FloatType::new );

	@Override
	public NativeTypeFactory< FloatType, FloatAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	public float get()
	{
		return dataAccess.getValue( i.get() );
	}

	public void set( final float f )
	{
		dataAccess.setValue( i.get(), f );
	}

	@Override
	public float getRealFloat()
	{
		return get();
	}

	@Override
	public double getRealDouble()
	{
		return get();
	}

	@Override
	public void setReal( final float real )
	{
		set( real );
	}

	@Override
	public void setReal( final double real )
	{
		set( ( float ) real );
	}

	@Override
	public double getMaxValue()
	{
		return Float.MAX_VALUE;
	}

	@Override
	public double getMinValue()
	{
		return -Float.MAX_VALUE;
	}

	@Override
	public double getMinIncrement()
	{
		return Float.MIN_VALUE;
	}

	@Override
	public void mul( final float c )
	{
		set( get() * c );
	}

	@Override
	public void mul( final double c )
	{
		set( ( float ) ( get() * c ) );
	}

	@Override
	public void add( final FloatType c )
	{
		set( get() + c.get() );
	}

	@Override
	public void div( final FloatType c )
	{
		set( get() / c.get() );
	}

	@Override
	public void mul( final FloatType c )
	{
		set( get() * c.get() );
	}

	@Override
	public void sub( final FloatType c )
	{
		set( get() - c.get() );
	}

	@Override
	public void set( final FloatType c )
	{
		set( c.get() );
	}

	@Override
	public void setOne()
	{
		set( 1 );
	}

	@Override
	public void setZero()
	{
		set( 0 );
	}

	@Override
	public void inc()
	{
		float a = get();
		set( ++a );
	}

	@Override
	public void dec()
	{
		float a = get();
		set( --a );
	}

	@Override
	public FloatType createVariable()
	{
		return new FloatType( 0 );
	}

	@Override
	public FloatType copy()
	{
		return new FloatType( dataAccess != null ? get() : 0 );
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction();
	}

	@Override
	public int getBitsPerPixel()
	{
		return 32;
	}

	@Override
	public int compareTo( final FloatType other )
	{
		return Float.compare( get(), other.get() );
	}

	@Override
	public boolean valueEquals( final FloatType other )
	{
		return FloatType.equals( get(), other.get() );
	}

	@Override
	public boolean equals( final Object obj )
	{
		return Util.valueEqualsObject( this, obj );
	}

	@Override
	public int hashCode()
	{
		return Float.hashCode( get() );
	}

	public static boolean equals( final float a, final float b )
	{
		return Float.floatToIntBits( a ) == Float.floatToIntBits( b );
	}

	/**
	 * Create an n-dimensional {@link RealComposite} of {@link FloatType}
	 * backed by a 1-dimensional {@link ArrayImg}.
	 *
	 * @param n number of dimensions
	 * @return
	 */
	public static RealComposite< FloatType > createVector( final int n )
	{
		return Views.collapseReal( ArrayImgs.floats( n ) ).randomAccess().get();
	}

	/**
	 * Wrap an array as an n-dimensional {@link RealComposite} of
	 * {@link FloatType}.
	 *
	 * @param array
	 * @return
	 */
	public static RealComposite< FloatType > wrapVector( final float[] array )
	{
		return Views.collapseReal( ArrayImgs.floats( array, array.length ) ).randomAccess().get();
	}

}
