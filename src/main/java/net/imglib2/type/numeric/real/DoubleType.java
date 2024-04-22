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
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;
import net.imglib2.view.Views;
import net.imglib2.view.composite.RealComposite;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class DoubleType extends AbstractRealType< DoubleType > implements NativeType< DoubleType >
{
	private final Index i;

	final protected NativeImg< ?, ? extends DoubleAccess > img;

	// the DataAccess that holds the information
	protected DoubleAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public DoubleType( final NativeImg< ?, ? extends DoubleAccess > doubleStorage )
	{
		i = new Index();
		img = doubleStorage;
	}

	// this is the constructor if you want it to be a variable
	public DoubleType( final double value )
	{
		i = new Index();
		img = null;
		dataAccess = new DoubleArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public DoubleType( final DoubleAccess access )
	{
		i = new Index();
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public DoubleType()
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
	public DoubleType duplicateTypeOnSameNativeImg()
	{
		return new DoubleType( img );
	}

	private static final NativeTypeFactory< DoubleType, DoubleAccess > typeFactory = NativeTypeFactory.DOUBLE( DoubleType::new );

	@Override
	public NativeTypeFactory< DoubleType, DoubleAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	public double get()
	{
		return dataAccess.getValue( i.get() );
	}

	public void set( final double f )
	{
		dataAccess.setValue( i.get(), f );
	}

	@Override
	public float getRealFloat()
	{
		return ( float ) get();
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
		set( real );
	}

	@Override
	public double getMaxValue()
	{
		return Double.MAX_VALUE;
	}

	@Override
	public double getMinValue()
	{
		return -Double.MAX_VALUE;
	}

	@Override
	public double getMinIncrement()
	{
		return Double.MIN_VALUE;
	}

	@Override
	public DoubleType createVariable()
	{
		return new DoubleType( 0 );
	}

	@Override
	public DoubleType copy()
	{
		return new DoubleType( dataAccess != null ? get() : 0 );
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction();
	}

	@Override
	public int getBitsPerPixel()
	{
		return 64;
	}

	public static boolean equals( final double a, final double b )
	{
		return Double.doubleToLongBits( a ) == Double.doubleToLongBits( b );
	}

	/**
	 * Create an n-dimensional {@link RealComposite} of {@link DoubleType}
	 * backed by a 1-dimensional {@link ArrayImg}.
	 *
	 * @param n number of dimensions
	 * @return
	 */
	public static RealComposite< DoubleType > createVector( final int n )
	{
		return Views.collapseReal( ArrayImgs.doubles( n ) ).randomAccess().get();
	}

	/**
	 * Wrap an array as an n-dimensional {@link RealComposite} of
	 * {@link DoubleType}.
	 *
	 * @param array
	 * @return
	 */
	public static RealComposite< DoubleType > wrapVector( final double[] array )
	{
		return Views.collapseReal( ArrayImgs.doubles( array, array.length ) ).randomAccess().get();
	}
}
