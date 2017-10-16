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

package net.imglib2.type.numeric.integer;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.type.Type;

/**
 * A {@link Type} with a bit depth of 4.
 *
 * The performance of this type is traded off for the gain in memory storage.
 *
 * @author Albert Cardona
 */
public class Unsigned4BitType extends AbstractIntegerBitType< Unsigned4BitType >
{
	// A mask for bit and, containing nBits of 1
	private final static long mask = 15; // 1111 in binary

	// this is the constructor if you want it to read from an array
	public Unsigned4BitType( final NativeImg< ?, ? extends LongAccess > bitStorage )
	{
		super( bitStorage, 4 );
	}

	// this is the constructor if you want it to be a variable
	public Unsigned4BitType( final long value )
	{
		this( ( NativeImg< ?, ? extends LongAccess > ) null );
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public Unsigned4BitType( final LongAccess access )
	{
		this( ( NativeImg< ?, ? extends LongAccess > ) null );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public Unsigned4BitType()
	{
		this( 0 );
	}

	@Override
	public Unsigned4BitType duplicateTypeOnSameNativeImg()
	{
		return new Unsigned4BitType( img );
	}

	private static final PrimitiveTypeInfo< Unsigned4BitType, LongAccess > info = PrimitiveTypeInfo.LONG( img -> new Unsigned4BitType( img ) );

	@Override
	public PrimitiveTypeInfo< Unsigned4BitType, LongAccess > getPrimitiveTypeInfo()
	{
		return info;
	}

	@Override
	public long get()
	{
		return ( dataAccess.getValue( ( int ) ( i >>> 4 ) ) >>> ( ( i & 15 ) << 2 ) ) & mask;
	}

	// Crops value to within mask
	@Override
	public void set( final long value )
	{
		/*
		final int k = i * 4;
		final int i1 = k >>> 6; // k / 64;
		final long shift = k % 64;
		*/
		// Same as above minus one multiplication, plus one shift (to multiply by 4)
		final int i1 = ( int ) ( i >>> 4 ); // Same as (i * 4) / 64 = ((i << 2) >>> 6)
		final long shift = ( i << 2 ) & 63; // Same as (i * 4) % 64
		// Clear the bits first, then or the masked value

		final long bitsToRetain = ~( mask << shift );
		final long bitsToSet = ( value & mask ) << shift;
		synchronized ( dataAccess )
		{
			dataAccess.setValue( i1, ( dataAccess.getValue( i1 ) & bitsToRetain ) | bitsToSet );
		}
	}

	@Override
	public Unsigned4BitType createVariable()
	{
		return new Unsigned4BitType( 0 );
	}

	@Override
	public Unsigned4BitType copy()
	{
		return new Unsigned4BitType( get() );
	}
}
