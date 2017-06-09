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
	private final Supplier< T > supplier;
	private T t;

	public ImgFactory( final T type )
	{
		t = type;
		supplier = null;
	}

	public ImgFactory( final Supplier< T > supplier )
	{
		t = null;
		this.supplier = supplier;
	}

	public T type()
	{
		if ( t == null ) t = supplier.get();
		return t;
	}

	/**
	 * The {@link ImgFactory} can decide how to create the {@link Img}. A
	 * {@link NativeImgFactory} will ask the {@link Type} to create a suitable
	 * {@link NativeImg}.
	 *
	 * @return {@link Img}
	 */
	public abstract Img< T > create( final long[] dim );

	/**
	 * The {@link ImgFactory} can decide how to create the {@link Img}. A
	 * {@link NativeImgFactory} will ask the {@link Type} to create a suitable
	 * {@link NativeImg}.
	 *
	 * @return {@link Img}
	 */
	public Img< T > create( final Dimensions dim )
	{
		final long[] size = new long[ dim.numDimensions() ];
		dim.dimensions( size );

		return create( size );
	}

	/**
	 * The {@link ImgFactory} can decide how to create the {@link Img}. A
	 * {@link NativeImgFactory} will ask the {@link Type} to create a suitable
	 * {@link NativeImg}.
	 *
	 * @return {@link Img}
	 */
	public Img< T > create( final int[] dim )
	{
		return create( Util.int2long( dim ) );
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

	/**
	 * Makes a best effort to return a non-null type instance, caching the given
	 * type if a cached type is not already in place.
	 * <p>
	 * Furthermore, if the cached type instance was previously null, the given
	 * {@code type} becomes the cached instance. In this way, if the factory was
	 * created using one of the deprecated typeless constructor signatures, but
	 * then one of the deprecated {@code create} methods is called (i.e.: a
	 * method which provides a type instance as an argument), the provided type
	 * becomes the cached type instance so that subsequent invocations of the
	 * typeless {@code create} methods will work as desired.
	 * </p>
	 * 
	 * @param type
	 *            The type to return, and cache if needed.
	 * @return A type instance matching the type of the factory. If the given
	 *         {@code type} argument is non-null, it is returned. Otherwise, the
	 *         cached type instance {@link #t} is returned.
	 */
	protected T cache( final T type )
	{
		if ( t == null ) t = type;
		return type == null ? t : type;
	}

	@Deprecated
	public ImgFactory() {
		t = null;
		supplier = null;
	}

	@Deprecated
	public abstract Img< T > create( final long[] dim, final T type );

	@Deprecated
	public Img< T > create( final Dimensions dim, final T type )
	{
		final long[] size = new long[ dim.numDimensions() ];
		dim.dimensions( size );

		return create( size, cache( type ) );
	}

	@Deprecated
	public Img< T > create( final int[] dim, final T type )
	{
		return create( Util.int2long( dim ), cache( type ) );
	}

	@Deprecated
	public Img< T > create( final Supplier< T > typeSupplier, final long... dim )
	{
		return create( dim, cache( typeSupplier.get() ) );
	}

	@Deprecated
	public Img< T > create( final Supplier< T > typeSupplier, final Dimensions dim )
	{
		return create( dim, cache( typeSupplier.get() ) );
	}

	public Img< T > create( final Supplier< T > typeSupplier, final int[] dim )
	{
		return create( dim, cache( typeSupplier.get() ) );
	}

}
