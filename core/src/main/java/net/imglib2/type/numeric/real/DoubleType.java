/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeId;
import net.imglib2.type.numeric.ExponentialMathType;
import net.imglib2.util.Util;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class DoubleType extends AbstractRealType< DoubleType > implements ExponentialMathType< DoubleType >, NativeType< DoubleType >
{
	private int i = 0;

	final protected NativeImg< ?, ? extends DoubleAccess > img;

	// the DataAccess that holds the information
	protected DoubleAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public DoubleType( final NativeImg< ?, ? extends DoubleAccess > doubleStorage )
	{
		img = doubleStorage;
	}

	// this is the constructor if you want it to be a variable
	public DoubleType( final double value )
	{
		img = null;
		dataAccess = new DoubleArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public DoubleType( final DoubleAccess access )
	{
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public DoubleType()
	{
		this( 0 );
	}

	@Override
	public NativeImg< DoubleType, ? extends DoubleAccess > createSuitableNativeImg( final NativeImgFactory< DoubleType > storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg< DoubleType, ? extends DoubleAccess > container = storageFactory.createDoubleInstance( dim, 1 );

		// create a Type that is linked to the container
		final DoubleType linkedType = new DoubleType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public DoubleType duplicateTypeOnSameNativeImg()
	{
		return new DoubleType( img );
	}

	public double get()
	{
		return dataAccess.getValue( i );
	}

	public void set( final double f )
	{
		dataAccess.setValue( i, f );
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
		return new DoubleType( get() );
	}

	@Override
	public void exp()
	{
		set( Math.exp( get() ) );
	}

	@Override
	public void round()
	{
		set( Util.round( get() ) );
	}

	@Override
	public int getEntitiesPerPixel()
	{
		return 1;
	}

	@Override
	public void updateIndex( final int index )
	{
		i = index;
	}

	@Override
	public int getIndex()
	{
		return i;
	}

	@Override
	public void incIndex()
	{
		++i;
	}

	@Override
	public void incIndex( final int increment )
	{
		i += increment;
	}

	@Override
	public void decIndex()
	{
		--i;
	}

	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
	}

	@Override
	public int getBitsPerPixel()
	{
		return 64;
	}

	@Override
	public NativeTypeId getNativeTypeId()
	{
		return NativeTypeId.Double;
	}
}
