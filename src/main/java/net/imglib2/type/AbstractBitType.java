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

package net.imglib2.type;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.util.Fraction;

/**
 * Provides basic infrastructure to implement Types based on single bits stored
 * in a LongAccess
 *
 * @author Albert Cardona
 * @author Stephan Preibisch
 */
public abstract class AbstractBitType< T extends AbstractBitType< T > > implements NativeType< T >
{
	// Maximum count is Integer.MAX_VALUE * (64 / getBitsPerPixel())
	protected long i = 0;

	// the number of bits per pixel
	protected final int nBits;

	// the NativeImg that can update the DataAccess (if this instance is not a variable)
	final protected NativeImg< ?, ? extends LongAccess > img;

	// the DataAccess that holds the information
	protected LongAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public AbstractBitType(
			final NativeImg< ?, ? extends LongAccess > bitStorage,
			final int nBits )
	{
		img = bitStorage;
		this.nBits = nBits;
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public abstract PrimitiveTypeInfo< T, LongAccess > getPrimitiveTypeInfo();

	@Override
	public int getIndex()
	{
		return ( int ) i;
	}

	@Override
	public void updateIndex( final int index )
	{
		i = index;
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
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction( nBits, 64 );
	}
}
