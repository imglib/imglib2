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

package net.imglib2.type.numeric.integer;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeId;
import net.imglib2.util.Util;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
final public class LongType extends AbstractIntegerType< LongType > implements NativeType< LongType >
{
	private int i = 0;

	final protected NativeImg< ?, ? extends LongAccess > img;

	// the DataAccess that holds the information
	protected LongAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public LongType( final NativeImg< ?, ? extends LongAccess > longStorage )
	{
		img = longStorage;
	}

	// this is the constructor if you want to specify the dataAccess
	public LongType( final LongAccess access )
	{
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public LongType( final long value )
	{
		img = null;
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public LongType()
	{
		this( 0 );
	}

	@Override
	public NativeImg< LongType, ? extends LongAccess > createSuitableNativeImg( final NativeImgFactory< LongType > storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg< LongType, ? extends LongAccess > container = storageFactory.createLongInstance( dim, 1 );

		// create a Type that is linked to the container
		final LongType linkedType = new LongType( container );

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
	public LongType duplicateTypeOnSameNativeImg()
	{
		return new LongType( img );
	}

	public long get()
	{
		return dataAccess.getValue( i );
	}

	public void set( final long f )
	{
		dataAccess.setValue( i, f );
	}

	@Override
	public int getInteger()
	{
		return ( int ) get();
	}

	@Override
	public long getIntegerLong()
	{
		return get();
	}

	@Override
	public void setInteger( final int f )
	{
		set( f );
	}

	@Override
	public void setInteger( final long f )
	{
		set( f );
	}

	@Override
	public double getMaxValue()
	{
		return Long.MAX_VALUE;
	}

	@Override
	public double getMinValue()
	{
		return Long.MIN_VALUE;
	}

	@Override
	public void mul( final float c )
	{
		set( Util.round( get() * c ) );
	}

	@Override
	public void mul( final double c )
	{
		set( Util.round( get() * c ) );
	}

	@Override
	public void add( final LongType c )
	{
		set( get() + c.get() );
	}

	@Override
	public void div( final LongType c )
	{
		set( get() / c.get() );
	}

	@Override
	public void mul( final LongType c )
	{
		set( get() * c.get() );
	}

	@Override
	public void sub( final LongType c )
	{
		set( get() - c.get() );
	}

	@Override
	public int compareTo( final LongType c )
	{
		final long a = get();
		final long b = c.get();
		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else
			return 0;
	}

	@Override
	public void set( final LongType c )
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
		long a = get();
		set( ++a );
	}

	@Override
	public void dec()
	{
		long a = get();
		set( --a );
	}

	@Override
	public LongType createVariable()
	{
		return new LongType( 0 );
	}

	@Override
	public LongType copy()
	{
		return new LongType( get() );
	}

	@Override
	public int getEntitiesPerPixel()
	{
		return 1;
	}

	@Override
	public void updateIndex( final int index )
	{
		this.i = index;
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
		return NativeTypeId.Other;
	}
}
