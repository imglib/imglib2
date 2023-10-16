/*
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
import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

/**
 * A {@link RandomAccessible} over two independent
 * {@link RandomAccessible RandomAccessibles} whose type is the {@link Pair} of
 * corresponding values at the same coordinates in either of the two sources.
 *
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class RandomAccessiblePair< A, B > implements RandomAccessible< Pair< A, B > >
{
	final protected RandomAccessible< A > sourceA;
	final protected RandomAccessible< B > sourceB;

	public class RandomAccess implements Pair< A, B >, net.imglib2.RandomAccess< Pair< A, B > >
	{
		final protected net.imglib2.RandomAccess< A > a;
		final protected net.imglib2.RandomAccess< B > b;

		public RandomAccess()
		{
			a = sourceA.randomAccess();
			b = sourceB.randomAccess();
		}

		@Override
		public A getA()
		{
			return a.get();
		}

		@Override
		public B getB()
		{
			return b.get();
		}

		@Override
		public void localize( final int[] position )
		{
			a.localize( position );
		}

		@Override
		public void localize( final long[] position )
		{
			a.localize( position );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return a.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return a.getLongPosition( d );
		}

		@Override
		public void localize( final float[] position )
		{
			a.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			a.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return a.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return a.getDoublePosition( d );
		}

		@Override
		public int numDimensions()
		{
			return RandomAccessiblePair.this.numDimensions();
		}

		@Override
		public void fwd( final int d )
		{
			a.fwd( d );
			b.fwd( d );
		}

		@Override
		public void bck( final int d )
		{
			a.bck( d );
			b.bck( d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			a.move( distance, d );
			b.move( distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			a.move( distance, d );
			b.move( distance, d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			a.move( localizable );
			b.move( localizable );
		}

		@Override
		public void move( final int[] distance )
		{
			a.move( distance );
			b.move( distance );
		}

		@Override
		public void move( final long[] distance )
		{
			a.move( distance );
			b.move( distance );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			a.setPosition( localizable );
			b.setPosition( localizable );
		}

		@Override
		public void setPosition( final int[] position )
		{
			a.setPosition( position );
			b.setPosition( position );
		}

		@Override
		public void setPosition( final long[] position )
		{
			a.setPosition( position );
			b.setPosition( position );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			a.setPosition( position, d );
			b.setPosition( position, d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			a.setPosition( position, d );
			b.setPosition( position, d );
		}

		@Override
		public RandomAccess get()
		{
			return this;
		}

		@Override
		public RandomAccess getType()
		{
			return this;
		}

		@Override
		public RandomAccess copy()
		{
			final RandomAccess copy = new RandomAccess();
			copy.setPosition( this );
			return copy;
		}
	}

	public RandomAccessiblePair(
			final RandomAccessible< A > sourceA,
			final RandomAccessible< B > sourceB )
	{
		this.sourceA = sourceA;
		this.sourceB = sourceB;
	}

	@Override
	public int numDimensions()
	{
		return sourceA.numDimensions();
	}

	@Override
	public RandomAccess randomAccess()
	{
		return new RandomAccess();
	}

	@Override
	public RandomAccess randomAccess( final Interval interval )
	{
		return new RandomAccess();
	}

	@Override
	public Pair<A, B> getType() {
		// sources may have an optimized implementation for getType
		return new ValuePair<>( sourceA.getType(), sourceB.getType() );
	}
}
