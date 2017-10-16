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
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public abstract class ImgFactory< T >
{
	/**
	 * Create an {@code Img} of the specified {@code type} with specified
	 * {@code dimensions}.
	 *
	 * @return {@link Img} new image of specified {@code type} and
	 *         {@code dimensions}.
	 */
	public abstract Img< T > create( final long[] dimensions, final T type );

	/**
	 * Create an {@code Img} of the specified {@code type} with specified
	 * {@code dimensions}.
	 *
	 * @return {@link Img} new image of specified {@code type} and
	 *         {@code dimensions}.
	 */
	public Img< T > create( final Dimensions dimensions, final T type )
	{
		final long[] size = new long[ dimensions.numDimensions() ];
		dimensions.dimensions( size );

		return create( size, type );
	}

	/**
	 * Create an {@code Img} of the specified {@code type} with specified
	 * {@code dimensions}.
	 *
	 * @return {@link Img} new image of specified {@code type} and
	 *         {@code dimensions}.
	 */
	public Img< T > create( final int[] dimensions, final T type )
	{
		return create( Util.int2long( dimensions ), type );
	}

	/**
	 * Creates the same {@link ImgFactory} for a different generic parameter if
	 * possible.
	 *
	 * If the type "S" does not suit the needs of the {@link ImgFactory} (for
	 * example implement {@link NativeType} in all {@link NativeImgFactory},
	 * this method will throw an {@link IncompatibleTypeException}.
	 *
	 * @param <S>
	 *            the new type
	 * @param type
	 *            an instance of S
	 * @return {@link ImgFactory} of type S
	 * @throws IncompatibleTypeException
	 *             if type S is not compatible
	 */
	public abstract < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException;

	/**
	 * Create an {@code Img} of the supplied {@code type} with specified
	 * {@code dimensions}.
	 *
	 * @return {@link Img} new image of specified {@code type} and
	 *         {@code dimensions}.
	 */
	public Img< T > create( final Supplier< T > typeSupplier, final long... dimensions ) {
		return create( dimensions, typeSupplier.get() );
	}

	/**
	 * Create an {@code Img} of the supplied {@code type} with specified
	 * {@code dimensions}.
	 *
	 * @return {@link Img} new image of specified {@code type} and
	 *         {@code dimensions}.
	 */
	public Img< T > create( final Supplier< T > typeSupplier, final Dimensions dimensions )
	{
		return create( dimensions, typeSupplier.get() );
	}

	/**
	 * Create an {@code Img} of the supplied {@code type} with specified
	 * {@code dimensions}.
	 *
	 * <p>
	 * Note: This is not a vararg function because the underlying {@code int[]}
	 * based methods already copies the {@code int[]} dimensions into a
	 * disposable {@code long[]} anyways. This would be an unnecessary copy for
	 * {@code int...} varargs.
	 * </p>
	 *
	 * @return {@link Img} new image of specified {@code type} and
	 *         {@code dimensions}.
	 */
	public Img< T > create( final Supplier< T > typeSupplier, final int[] dimensions )
	{
		return create( dimensions, typeSupplier.get() );
	}

	/**
	 * Creates the same {@link ImgFactory} for a different generic parameter if
	 * possible.
	 *
	 * If the supplied type "S" does not suit the needs of the
	 * {@link ImgFactory} (for example implement {@link NativeType} in all
	 * {@link NativeImgFactory}, this method will throw an
	 * {@link IncompatibleTypeException}.
	 *
	 * @param <S>
	 *            the new type
	 * @param typeSupplier
	 *            a supplier of S
	 * @return {@link ImgFactory} of type S
	 * @throws IncompatibleTypeException
	 *             if type S is not compatible
	 */
	public < S > ImgFactory< S > imgFactory( final Supplier< S > typeSupplier ) throws IncompatibleTypeException
	{
		return imgFactory( typeSupplier.get() );
	}
}
