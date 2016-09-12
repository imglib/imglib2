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
package net.imglib2.view.linear;

import net.imglib2.LinearAccess;
import net.imglib2.LinearAccessible;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.IntervalIndexer;

/**
 *
 * View arbitrary {@link RandomAccessibleInterval} as {@link LinearAccessible}.
 * This is the inefficient fallback for any {@link RandomAccessibleInterval}
 * that does not provide an efficient {@link LinearAccess}.
 *
 * @author Philipp Hanslovsky
 *
 * @param <T>
 */
public class LinearAccessibleViewOnRandomAccessibleInterval< T > extends AbstractLinearAccessibleViewOnRandomAccessibleInterval< T >
{
	public LinearAccessibleViewOnRandomAccessibleInterval( final RandomAccessibleInterval< T > source )
	{
		super( source );
	}

	@Override
	public LinearAccess< T > linearAccess()
	{
		return new RandomAccessibleIntervalLinearAccess( source );
	}

	public class RandomAccessibleIntervalLinearAccess implements LinearAccess< T >
	{

		private final RandomAccess< T > access;

		private final long position;

		public RandomAccessibleIntervalLinearAccess( final RandomAccessible< T > source )
		{
			this( source.randomAccess(), 0 );
		}

		public RandomAccessibleIntervalLinearAccess( final RandomAccess< T > access, final long position )
		{
			super();
			this.access = access;
			this.position = position;
		}

		@Override
		public T get()
		{
			return access.get();
		}

		@Override
		public LinearAccess< T > copy()
		{
			return new RandomAccessibleIntervalLinearAccess( access.copyRandomAccess(), position );
		}

		@Override
		public void set( final int position )
		{
			set( ( long ) position );
		}

		@Override
		public void set( final long position )
		{
			IntervalIndexer.indexToPosition( position, source, access );
		}

		@Override
		public long getPosition()
		{
			return position;
		}

	}

}
