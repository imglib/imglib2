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

import net.imglib2.Dimensions;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
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
	 * The {@link ImgFactory} can decide how to create the {@link Img}. A
	 * {@link NativeImgFactory} will ask the {@link Type} to create a suitable
	 * {@link NativeImg}.
	 *
	 * @return {@link Img}
	 */
	public abstract Img< T > create( final long[] dim, final T type );

	/**
	 * The {@link ImgFactory} can decide how to create the {@link Img}. A
	 * {@link NativeImgFactory} will ask the {@link Type} to create a suitable
	 * {@link NativeImg}.
	 *
	 * @return {@link Img}
	 */
	public Img< T > create( final Dimensions dim, final T type )
	{
		final long[] size = new long[ dim.numDimensions() ];
		dim.dimensions( size );

		return create( size, type );
	}

	/**
	 * The {@link ImgFactory} can decide how to create the {@link Img}. A
	 * {@link NativeImgFactory} will ask the {@link Type} to create a suitable
	 * {@link NativeImg}.
	 *
	 * @return {@link Img}
	 */
	public Img< T > create( final int[] dim, final T type )
	{
		return create( Util.int2long( dim ), type );
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
}
