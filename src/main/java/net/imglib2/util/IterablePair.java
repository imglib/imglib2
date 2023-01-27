/*-
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
package net.imglib2.util;

import java.util.Iterator;

/**
 * An {@link Iterable} which is backed by two other {@link Iterable}s, whose
 * iteration produces a {@link Pair} of objects corresponding to those given by
 * the two respective iterations of the backing {@link Iterable}s. That is: the
 * two backing {@link Iterable}s are iterated in synchrony, and their elements
 * combined into a single {@link Pair} accessor object.
 * <p>
 * When two {@link Iterable}s are given which produce an unequal number of
 * elements, the iteration of the {@code IterablePair} ends when <em>either</em>
 * of the two backing iterations ends.
 * </p>
 * 
 * @author Curtis Rueden
 * @author Ellen T Arena
 *
 * @param <A>
 * @param <B>
 */
public class IterablePair< A, B > implements Iterable< Pair< A, B > >
{

	private final Iterable< A > iter1;

	private final Iterable< B > iter2;

	public IterablePair( final Iterable< A > iter1, final Iterable< B > iter2 )
	{
		this.iter1 = iter1;
		this.iter2 = iter2;
	}

	@Override
	public Iterator< Pair< A, B > > iterator()
	{
		return new Iterator< Pair< A, B > >()
		{

			private final Iterator< A > i1 = iter1.iterator();

			private final Iterator< B > i2 = iter2.iterator();

			private A e1;

			private B e2;

			private final Pair< A, B > value = new Pair< A, B >()
			{

				@Override
				public A getA()
				{
					return e1;
				}

				@Override
				public B getB()
				{
					return e2;
				}

				@Override
				public String toString()
				{
					return e1 + ", " + e2;
				}
			};

			@Override
			public boolean hasNext()
			{
				return i1.hasNext() && i2.hasNext();
			}

			@Override
			public Pair< A, B > next()
			{
				e1 = i1.next();
				e2 = i2.next();
				return value;
			}
		};
	}
}
