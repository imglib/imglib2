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
import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.type.NativeType;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 */
public class Unsigned12BitType extends AbstractIntegerType< Unsigned12BitType > implements NativeType< Unsigned12BitType >
{
	private int i = 0;

	final protected NativeImg< Unsigned12BitType, ? extends BitAccess > img;

	// the adresses of the bits that we store
	int j1, j2, j3, j4, j5, j6, j7, j8, j9, j10, j11, j12;

	// the DataAccess that holds the information
	protected BitAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public Unsigned12BitType( final NativeImg< Unsigned12BitType, ? extends BitAccess > bitStorage )
	{
		img = bitStorage;
		updateIndex( 0 );
	}

	// this is the constructor if you want it to be a variable
	public Unsigned12BitType( final short value )
	{
		img = null;
		updateIndex( 0 );
		dataAccess = new BitArray( 12 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public Unsigned12BitType( final BitAccess access )
	{
		img = null;
		updateIndex( 0 );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public Unsigned12BitType()
	{
		this( ( short ) 0 );
	}

	@Override
	public NativeImg< Unsigned12BitType, ? extends BitAccess > createSuitableNativeImg( final NativeImgFactory< Unsigned12BitType > storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg< Unsigned12BitType, ? extends BitAccess > container = storageFactory.createBitInstance( dim, 12 );

		// create a Type that is linked to the container
		final Unsigned12BitType linkedType = new Unsigned12BitType( container );

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
	public Unsigned12BitType duplicateTypeOnSameNativeImg()
	{
		return new Unsigned12BitType( img );
	}

	public short get()
	{
		short value = 0;

		if ( dataAccess.getValue( j1 ) )
			++value;
		if ( dataAccess.getValue( j2 ) )
			value += 2;
		if ( dataAccess.getValue( j3 ) )
			value += 4;
		if ( dataAccess.getValue( j4 ) )
			value += 8;
		if ( dataAccess.getValue( j5 ) )
			value += 16;
		if ( dataAccess.getValue( j6 ) )
			value += 32;
		if ( dataAccess.getValue( j7 ) )
			value += 64;
		if ( dataAccess.getValue( j8 ) )
			value += 128;
		if ( dataAccess.getValue( j9 ) )
			value += 256;
		if ( dataAccess.getValue( j10 ) )
			value += 512;
		if ( dataAccess.getValue( j11 ) )
			value += 1024;
		if ( dataAccess.getValue( j12 ) )
			value += 2048;

		return value;
	}

	public void set( final short value )
	{
		dataAccess.setValue( j1, ( value & 1 ) == 1 );
		dataAccess.setValue( j2, ( value & 2 ) == 2 );
		dataAccess.setValue( j3, ( value & 4 ) == 4 );
		dataAccess.setValue( j4, ( value & 8 ) == 8 );
		dataAccess.setValue( j5, ( value & 16 ) == 16 );
		dataAccess.setValue( j6, ( value & 32 ) == 32 );
		dataAccess.setValue( j7, ( value & 64 ) == 64 );
		dataAccess.setValue( j8, ( value & 128 ) == 128 );
		dataAccess.setValue( j9, ( value & 256 ) == 256 );
		dataAccess.setValue( j10, ( value & 512 ) == 512 );
		dataAccess.setValue( j11, ( value & 1024 ) == 1024 );
		dataAccess.setValue( j12, ( value & 2048 ) == 2048 );
	}

	@Override
	public int getInteger()
	{
		return get();
	}

	@Override
	public long getIntegerLong()
	{
		return get();
	}

	@Override
	public void setInteger( final int f )
	{
		set( ( short ) f );
	}

	@Override
	public void setInteger( final long f )
	{
		set( ( short ) f );
	}

	@Override
	public double getMaxValue()
	{
		return 4095;
	}

	@Override
	public double getMinValue()
	{
		return 0;
	}

	@Override
	public int getIndex()
	{
		return i;
	}

	@Override
	public void updateIndex( final int index )
	{
		i = index;
		j1 = index * 12;
		j2 = j1 + 1;
		j3 = j1 + 2;
		j4 = j1 + 3;
		j5 = j1 + 4;
		j6 = j1 + 5;
		j7 = j1 + 6;
		j8 = j1 + 7;
		j9 = j1 + 8;
		j10 = j1 + 9;
		j11 = j1 + 10;
		j12 = j1 + 11;
	}

	@Override
	public void incIndex()
	{
		++i;
		j1 += 12;
		j2 += 12;
		j3 += 12;
		j4 += 12;
		j5 += 12;
		j6 += 12;
		j7 += 12;
		j8 += 12;
		j9 += 12;
		j10 += 12;
		j11 += 12;
		j12 += 12;
	}

	@Override
	public void incIndex( final int increment )
	{
		i += increment;

		final int inc12 = 12 * increment;
		j1 += inc12;
		j2 += inc12;
		j3 += inc12;
		j4 += inc12;
		j5 += inc12;
		j6 += inc12;
		j7 += inc12;
		j8 += inc12;
		j9 += inc12;
		j10 += inc12;
		j11 += inc12;
		j12 += inc12;
	}

	@Override
	public void decIndex()
	{
		--i;
		j1 -= 12;
		j2 -= 12;
		j3 -= 12;
		j4 -= 12;
		j5 -= 12;
		j6 -= 12;
		j7 -= 12;
		j8 -= 12;
		j9 -= 12;
		j10 -= 12;
		j11 -= 12;
		j12 -= 12;
	}

	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;

		final int dec12 = 12 * decrement;
		j1 -= dec12;
		j2 -= dec12;
		j3 -= dec12;
		j4 -= dec12;
		j5 -= dec12;
		j6 -= dec12;
		j7 -= dec12;
		j8 -= dec12;
		j9 -= dec12;
		j10 -= dec12;
		j11 -= dec12;
		j12 -= dec12;
	}

	@Override
	public Unsigned12BitType createVariable()
	{
		return new Unsigned12BitType();
	}

	@Override
	public Unsigned12BitType copy()
	{
		return new Unsigned12BitType( get() );
	}

	@Override
	public int getEntitiesPerPixel()
	{
		return 1;
	}

	@Override
	public int getBitsPerPixel()
	{
		return 12;
	}
}
