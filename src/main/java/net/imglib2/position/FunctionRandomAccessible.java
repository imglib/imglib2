/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.position;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

/**
 * A {@link RandomAccessible} that generates a function value for each
 * position in discrete coordinate space by side-effect using a
 * {@link BiConsumer}.
 *
 * @author Stephan Saalfeld
 */
public class FunctionRandomAccessible< T > extends AbstractFunctionEuclideanSpace< Localizable, T > implements RandomAccessible< T >
{
	public FunctionRandomAccessible(
			final int n,
			final BiConsumer< Localizable, ? super T > function,
			final Supplier< T > typeSupplier )
	{
		super(n, function, typeSupplier);
	}

	public FunctionRandomAccessible(
			final int n,
			final Supplier< BiConsumer< Localizable, ? super T > > functionSupplier,
			final Supplier< T > typeSupplier )
	{
		super(n, functionSupplier, typeSupplier);
	}

	public class FunctionRandomAccess extends Point implements RandomAccess< T >
	{
		private final T t = typeSupplier.get();
		private final BiConsumer< Localizable, ? super T > function = functionSupplier.get();

		public FunctionRandomAccess()
		{
			super( FunctionRandomAccessible.this.n );
		}

		@Override
		public T get()
		{
			function.accept( this, t );
			return t;
		}

		@Override
		public T getType()
		{
			return t;
		}

		@Override
		public FunctionRandomAccess copy()
		{
			return new FunctionRandomAccess();
		}
	}

	@Override
	public FunctionRandomAccess randomAccess()
	{
		return new FunctionRandomAccess();
	}

	@Override
	public FunctionRandomAccess randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	@Override
	public T getType() {
		return super.typeSupplier.get();
	}
}
