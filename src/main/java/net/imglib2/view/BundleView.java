/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.AbstractConvertedRandomAccess;

/**
 * A {@link RandomAccessible} that bundles the coordinates and value domain
 * of a {@link RandomAccessible RandomAccessible&lt;T&gt;} as a
 * {@link RandomAccessible RandomAccessible&lt;RandomAccess&lt;T&gt;&gt;}.
 * This is useful for code that need access to both coordinates and values but
 * is implemented as a consumer of values (e.g. in a
 * <code>for (A t : iterable)...</code> loop).
 *
 * <p>If you move the returned {@link RandomAccess RandomAccess&lt;T&gt;},
 * follow up relative moves of the
 * {@link RandomAccess RandomAccess&lt;RandomAccess&lt;T&gt;&gt;} will preserve
 * this relative offset while absolute positioning will reset it, so, you can
 * do that, but you should know why :).</p>
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 *
 * @param <T>
 */
class BundleView< T > implements RandomAccessible< RandomAccess< T > >
{
	final protected RandomAccessible< T > source;

	class BundleRandomAccess extends AbstractConvertedRandomAccess< T, RandomAccess< T > >
	{
		BundleRandomAccess( final RandomAccess< T > source )
		{
			super( source );
		}

		@Override
		public RandomAccess< T > get()
		{
			return source;
		}

		@Override
		public BundleRandomAccess copy()
		{
			return new BundleRandomAccess( source.copy() );
		}
	}

	public BundleView( final RandomAccessible< T > source )
	{
		this.source = source;
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public BundleRandomAccess randomAccess()
	{
		return new BundleRandomAccess( source.randomAccess() );
	}

	@Override
	public BundleRandomAccess randomAccess( final Interval interval )
	{
		return new BundleRandomAccess( source.randomAccess( interval ) );
	}

	@Override
	public RandomAccess< T > getType()
	{
		return source.randomAccess();
	}
}
