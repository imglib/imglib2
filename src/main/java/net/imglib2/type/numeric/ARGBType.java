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
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.AbstractNativeType;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.util.Fraction;
import net.imglib2.util.Util;

/**
 * A {@link NativeType native} {@link NumericType} that encodes four channels at
 * unsigned byte precision into one 32bit signed integer which is the format
 * used in most display oriented image processing libraries such as AWT or
 * ImageJ. {@link ARGBType} implements {@link NumericType} as element-wise
 * vector algebra.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ARGBType extends AbstractNativeType< ARGBType > implements NumericType< ARGBType >
{
	final protected NativeImg< ?, ? extends IntAccess > img;

	// the DataAccess that holds the information
	protected IntAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public ARGBType( final NativeImg< ?, ? extends IntAccess > intStorage )
	{
		img = intStorage;
	}

	// this is the constructor if you want it to be a variable
	public ARGBType( final int value )
	{
		img = null;
		dataAccess = new IntArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public ARGBType( final IntAccess access )
	{
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public ARGBType()
	{
		this( 0 );
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public ARGBType duplicateTypeOnSameNativeImg()
	{
		return new ARGBType( img );
	}

	private static final PrimitiveTypeInfo< ARGBType, IntAccess > info = PrimitiveTypeInfo.INT( img -> new ARGBType( img ) );

	@Override
	public PrimitiveTypeInfo< ARGBType, IntAccess > getPrimitiveTypeInfo()
	{
		return info;
	}

	final public static int rgba( final int r, final int g, final int b, final int a )
	{
		return ( ( r & 0xff ) << 16 ) | ( ( g & 0xff ) << 8 ) | ( b & 0xff ) | ( ( a & 0xff ) << 24 );
	}

	final public static int rgba( final float r, final float g, final float b, final float a )
	{
		return rgba( Util.round( r ), Util.round( g ), Util.round( b ), Util.round( a ) );
	}

	final public static int rgba( final double r, final double g, final double b, final double a )
	{
		return rgba( ( int ) Util.round( r ), ( int ) Util.round( g ), ( int ) Util.round( b ), ( int ) Util.round( a ) );
	}

	final public static int red( final int value )
	{
		return ( value >> 16 ) & 0xff;
	}

	final public static int green( final int value )
	{
		return ( value >> 8 ) & 0xff;
	}

	final public static int blue( final int value )
	{
		return value & 0xff;
	}

	final public static int alpha( final int value )
	{
		return ( value >> 24 ) & 0xff;
	}

	public int get()
	{
		return dataAccess.getValue( i );
	}

	public void set( final int f )
	{
		dataAccess.setValue( i, f );
	}

	@Override
	public void mul( final float c )
	{
		final int value = get();
		set( rgba( red( value ) * c, green( value ) * c, blue( value ) * c, alpha( value ) * c ) );
	}

	@Override
	public void mul( final double c )
	{
		final int value = get();
		set( rgba( red( value ) * c, green( value ) * c, blue( value ) * c, alpha( value ) * c ) );
	}

	@Override
	public void add( final ARGBType c )
	{
		final int value1 = get();
		final int value2 = c.get();

		set( rgba( red( value1 ) + red( value2 ), green( value1 ) + green( value2 ), blue( value1 ) + blue( value2 ), alpha( value1 ) + alpha( value2 ) ) );
	}

	@Override
	public void div( final ARGBType c )
	{
		final int value1 = get();
		final int value2 = c.get();

		set( rgba( red( value1 ) / red( value2 ), green( value1 ) / green( value2 ), blue( value1 ) / blue( value2 ), alpha( value1 ) / alpha( value2 ) ) );
	}

	@Override
	public void mul( final ARGBType c )
	{
		final int value1 = get();
		final int value2 = c.get();

		set( rgba( red( value1 ) * red( value2 ), green( value1 ) * green( value2 ), blue( value1 ) * blue( value2 ), alpha( value1 ) * alpha( value2 ) ) );
	}

	@Override
	public void sub( final ARGBType c )
	{
		final int value1 = get();
		final int value2 = c.get();

		set( rgba( red( value1 ) - red( value2 ), green( value1 ) - green( value2 ), blue( value1 ) - blue( value2 ), alpha( value1 ) - alpha( value2 ) ) );
	}

	@Override
	public void set( final ARGBType c )
	{
		set( c.get() );
	}

	@Override
	public void setOne()
	{
		set( rgba( 1, 1, 1, 1 ) );
	}

	@Override
	public void setZero()
	{
		set( 0 );
	}

	@Override
	public ARGBType createVariable()
	{
		return new ARGBType( 0 );
	}

	@Override
	public ARGBType copy()
	{
		return new ARGBType( get() );
	}

	@Override
	public String toString()
	{
		final int rgba = get();
		return "(r=" + red( rgba ) + ",g=" + green( rgba ) + ",b=" + blue( rgba ) + ",a=" + alpha( rgba ) + ")";
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction();
	}

	@Override
	public boolean valueEquals( final ARGBType t )
	{
		return get() == t.get();
	}
}
