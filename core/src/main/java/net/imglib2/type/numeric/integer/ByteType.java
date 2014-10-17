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
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.util.Fraction;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ByteType extends GenericByteType< ByteType >
{
	// this is the constructor if you want it to read from an array
	public ByteType( final NativeImg< ?, ? extends ByteAccess > img )
	{
		super( img );
	}

	// this is the constructor if you want it to be a variable
	public ByteType( final byte value )
	{
		super( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public ByteType( final ByteAccess access )
	{
		super( access );
	}

	// this is the constructor if you want it to be a variable
	public ByteType()
	{
		super( ( byte ) 0 );
	}

	@Override
	public NativeImg< ByteType, ? extends ByteAccess > createSuitableNativeImg( final NativeImgFactory< ByteType > storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<ByteType, ? extends ByteAccess> container = storageFactory.createByteInstance( dim, new Fraction() );

		// create a Type that is linked to the container
		final ByteType linkedType = new ByteType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public ByteType duplicateTypeOnSameNativeImg()
	{
		return new ByteType( img );
	}

	public byte get()
	{
		return getValue();
	}

	public void set( final byte b )
	{
		setValue( b );
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
		set( ( byte ) f );
	}

	@Override
	public void setInteger( final long f )
	{
		set( ( byte ) f );
	}

	@Override
	public double getMaxValue()
	{
		return Byte.MAX_VALUE;
	}

	@Override
	public double getMinValue()
	{
		return Byte.MIN_VALUE;
	}

	@Override
	public ByteType createVariable()
	{
		return new ByteType( ( byte ) 0 );
	}

	@Override
	public ByteType copy()
	{
		return new ByteType( getValue() );
	}
}
