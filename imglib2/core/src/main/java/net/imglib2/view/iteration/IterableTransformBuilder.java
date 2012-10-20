/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Tobias Pietzsch
 */
package net.imglib2.view.iteration;

import java.util.Iterator;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.transform.integer.BoundingBoxTransform;
import net.imglib2.util.Intervals;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.TransformBuilder;
import net.imglib2.view.Views;

/**
 * TODO
 *
 * TODO: {@link TransformBuilder} propagates a BoundingBox through
 * {@link BoundingBoxTransform} transforms. Additionally, for iteration, we need
 * to guarantee that the transforms are bijections (at least within the bounding
 * box).
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class IterableTransformBuilder< T > extends TransformBuilder< T >
{
	/**
	 * TODO
	 *
	 * @param interval
	 * @param randomAccessible
	 * @return
	 */
	public static < S > IterableInterval< S > getEfficientIterableInterval( final Interval interval, final RandomAccessible< S > randomAccessible )
	{
		return new IterableTransformBuilder< S >( interval, randomAccessible ).buildIterableInterval();
	}

	/**
	 * The interval in which access is needed. This is propagated through the
	 * transforms down the view hierarchy.
	 */
	protected Interval interval;

	public IterableTransformBuilder( final Interval interval, final RandomAccessible<T> randomAccessible )
	{
		super( interval, randomAccessible );
		this.interval = interval;
	}

	class SubInterval extends AbstractWrappedInterval< Interval > implements IterableInterval< T >
	{
		final long numElements;

		final SubIntervalIterable< T > iterableSource;

		public SubInterval( final SubIntervalIterable< T > iterableSource )
		{
			super( interval );
			numElements = Intervals.numElements( interval );
			this.iterableSource = iterableSource;
		}

		@Override
		public long size()
		{
			return numElements;
		}

		@Override
		public T firstElement()
		{
			return cursor().next();
		}

		@Override
		public Object iterationOrder()
		{
			// TODO Auto-generated method stub
			// ???
			return this;
		}

		@Override
		public boolean equalIterationOrder( final IterableRealInterval< ? > f )
		{
			return iterationOrder().equals( f.iterationOrder() );
		}

		@Override
		public Iterator< T > iterator()
		{
			return cursor();
		}

		@Override
		public Cursor< T > cursor()
		{
			return iterableSource.cursor( interval );
		}

		@Override
		public Cursor< T > localizingCursor()
		{
			return iterableSource.cursor( interval );
		}
	}

	public IterableInterval< T > buildIterableInterval()
	{
		if ( boundingBox != null && SubIntervalIterable.class.isInstance( source ) )
		{
			if ( transforms.isEmpty() )
			{
				final SubIntervalIterable< T > iterableSource = ( SubIntervalIterable< T > ) source;
				return new SubInterval( iterableSource );
			}
		}
		return new IterableRandomAccessibleInterval( Views.interval( build(), interval ) );
	}
}
