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

package net.imglib2.img;

import java.util.function.Supplier;

import net.imglib2.Dimensions;
import net.imglib2.type.NativeType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

/**
 * TODO: get rid of {@link NativeImgFactory}???
 */
public abstract class NativeImgFactory< T extends NativeType< T > > extends ImgFactory< T >
{
	public NativeImgFactory( final T type )
	{
		super( type );
	}

	public NativeImgFactory( final Supplier< T > supplier )
	{
		super( supplier );
	}

	/**
	 * Create a {@link NativeImg} with the specified {@code dimensions}.
	 *
	 * @param dimension
	 *            the dimensions of the image.
	 *
	 * @return new {@link NativeImg} with the specified {@code dimensions}.
	 */
	@Override
	public abstract NativeImg< T, ? > create( final long... dimensions );

	/**
	 * Create an {@code Img<T>} with the specified {@code dimensions}.
	 *
	 * @return new image with the specified {@code dimensions}.
	 */
	@Override
	public NativeImg< T, ? > create( final Dimensions dimensions )
	{
		return create( Intervals.dimensionsAsLongArray( dimensions ) );
	}

	/**
	 * Create an {@code Img<T>} with the specified {@code dimensions}.
	 *
	 * <p>
	 * Note: This is not a vararg function because the underlying {@code int[]}
	 * based methods already copies the {@code int[]} dimensions into a
	 * disposable {@code long[]} anyways. This would be an unnecessary copy for
	 * {@code int...} varargs.
	 * </p>
	 *
	 * @return new image with the specified {@code dimensions}.
	 */
	@Override
	public NativeImg< T, ? > create( final int[] dimensions )
	{
		return create( Util.int2long( dimensions ) );
	}


	/*
	 * -----------------------------------------------------------------------
	 *
	 * Deprecated API.
	 *
	 * Supports backwards compatibility with ImgFactories that are constructed
	 * without a type instance or supplier.
	 *
	 * -----------------------------------------------------------------------
	 */

	@Override
	@Deprecated
	public abstract NativeImg< T, ? > create( final long[] dimension, final T type );

	@Deprecated
	public NativeImgFactory( )
	{
		super();
	}
}
