/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.type.NativeTypeFactory;

/**
 *
 */
public class LongVectorType extends GenericLongVectorType< LongVectorType >
{
	@SuppressWarnings( "hiding" )
	@Override
	protected NativeTypeFactory< LongVectorType, LongAccess > createTypeFactory() {

		return  NativeTypeFactory.LONG( ( img ) -> new LongVectorType( img, numElements ) );
	}

	// this is the constructor if you want it to read from an array
	public LongVectorType( final NativeImg< ?, ? extends LongAccess > longStorage, final int numElements )
	{
		super(longStorage, numElements);
	}

	// this is the constructor if you want it to be a variable
	public LongVectorType( final long[] value )
	{
		super(value);
	}

	// this is the constructor if you want to specify the dataAccess
	public LongVectorType( final LongAccess access, final int numElements )
	{
		super(access, numElements);
	}

	// this is the constructor if you want it to be a variable
	public LongVectorType( final int numElements )
	{
		this( new long[numElements] );
	}

	@Override
	public LongVectorType createVariable()
	{
		return new LongVectorType(numElements);
	}

	@Override
	public LongVectorType copy()
	{
		final LongVectorType copy = new LongVectorType(numElements);
		copy.set(this);
		return copy;
	}

	@Override
	public LongVectorType duplicateTypeOnSameNativeImg()
	{
		return new LongVectorType(img, numElements);
	}
}
