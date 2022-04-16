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

package net.imglib2.view;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.View;

/**
 * {@link RandomAccessible} on a {@link RealRandomAccessible}. For optimal
 * performance, no integer coordinates are stored in the {@link RandomAccess}
 * but only method calls passed through to an actual {@link RealRandomAccess}.
 * Therefore, localization into integer fields performs a Math.round operation
 * per field and is thus not very efficient. Localization into real fields,
 * however, is passed through and thus performs optimally.
 *
 * @author Stephan Saalfeld
 */
public class RandomAccessibleOnRealRandomAccessible< T > extends AbstractEuclideanSpace implements RandomAccessible< T >, View
{
	final protected RealRandomAccessible< T > source;

	final protected class RandomAccessOnRealRandomAccessible implements RandomAccess< T >
	{
		final protected RealRandomAccess< T > sourceAccess;

		public RandomAccessOnRealRandomAccessible( final RealRandomAccess< T > sourceAccess )
		{
			this.sourceAccess = sourceAccess;
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = ( int ) Math.round( sourceAccess.getDoublePosition( d ) );
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = Math.round( sourceAccess.getDoublePosition( d ) );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( int ) Math.round( sourceAccess.getDoublePosition( d ) );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return Math.round( sourceAccess.getDoublePosition( d ) );
		}

		@Override
		public void localize( final float[] position )
		{
			sourceAccess.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			sourceAccess.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return sourceAccess.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return sourceAccess.getDoublePosition( d );
		}

		@Override
		public void fwd( final int d )
		{
			sourceAccess.fwd( d );
		}

		@Override
		public void bck( final int d )
		{
			sourceAccess.bck( d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			sourceAccess.move( distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			sourceAccess.move( distance, d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			sourceAccess.move( localizable );
		}

		@Override
		public void move( final int[] distance )
		{
			sourceAccess.move( distance );
		}

		@Override
		public void move( final long[] distance )
		{
			sourceAccess.move( distance );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			sourceAccess.setPosition( localizable );
		}

		@Override
		public void setPosition( final int[] position )
		{
			sourceAccess.setPosition( position );
		}

		@Override
		public void setPosition( final long[] position )
		{
			sourceAccess.setPosition( position );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			sourceAccess.setPosition( position, d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			sourceAccess.setPosition( position, d );
		}

		@Override
		public T get()
		{
			return sourceAccess.get();
		}

		@Override
		public RandomAccessOnRealRandomAccessible copy()
		{
			return new RandomAccessOnRealRandomAccessible( sourceAccess.copy() );
		}

		@Override
		public int numDimensions()
		{
			return n;
		}
	}

	public RealRandomAccessible< T > getSource()
	{
	    return source;
	}

	public RandomAccessibleOnRealRandomAccessible( final RealRandomAccessible< T > source )
	{
		super( source.numDimensions() );
		this.source = source;
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return new RandomAccessOnRealRandomAccessible( source.realRandomAccess() );
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return new RandomAccessOnRealRandomAccessible( source.realRandomAccess( interval ) );
	}
}
