/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A list of data samples at explicit {@link Localizable integer coordinates}.
 * 
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class PointSampleList< T > extends AbstractInterval implements IterableInterval< T >
{
	public class PointSampleListCursor implements Cursor< T >
	{
		protected int index = -1;

		protected Point position = null;

		protected T sample = null;

		@Override
		public Cursor< T > copy()
		{
			final PointSampleListCursor copy = new PointSampleListCursor();
			copy.index = index;
			copy.position = position;
			copy.sample = sample;

			return copy;
		}

		@Override
		final public Cursor< T > copyCursor()
		{
			return copy();
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return position.getDoublePosition( d );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return position.getFloatPosition( d );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return position.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return position.getLongPosition( d );
		}

		@Override
		public void localize( final float[] pos )
		{
			position.localize( pos );
		}

		@Override
		public void localize( final double[] pos )
		{
			position.localize( pos );
		}

		@Override
		public void localize( final int[] pos )
		{
			position.localize( pos );
		}

		@Override
		public void localize( final long[] pos )
		{
			position.localize( pos );
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public T get()
		{
			return sample;
		}

		@Override
		public void fwd()
		{
			++index;
			position = coordinates.get( index );
			sample = samples.get( index );
		}

		@Override
		public boolean hasNext()
		{
			return index < lastIndex;
		}

		@Override
		public void jumpFwd( final long steps )
		{
			index += steps;
			position = coordinates.get( index );
			sample = samples.get( index );
		}

		@Override
		public void reset()
		{
			index = -1;
			position = null;
			sample = null;
		}

		@Override
		public T next()
		{
			fwd();
			return sample;
		}

		@Override
		public void remove()
		{
			/* Not yet implemented */
		}
	}

	final protected ArrayList< Point > coordinates = new ArrayList< Point >();

	final protected ArrayList< T > samples = new ArrayList< T >();

	protected int lastIndex = -1;

	private static Interval initInterval( final int n )
	{
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Long.MAX_VALUE;
			max[ d ] = Long.MIN_VALUE;
		}
		return new FinalInterval( min, max );
	}

	/**
	 * @param n
	 *            - number of dimensions
	 */
	public PointSampleList( final int n )
	{
		super( initInterval( n ) );
	}

	public void add( final Point position, final T sample )
	{
		coordinates.add( position );
		samples.add( sample );
		lastIndex = samples.size() - 1;
		for ( int d = 0; d < n; ++d )
		{
			final long x = position.getLongPosition( d );

			if ( x < min[ d ] )
				min[ d ] = x;
			if ( x > max[ d ] )
				max[ d ] = x;
		}
	}

	@Override
	public Cursor< T > cursor()
	{
		return new PointSampleListCursor();
	}

	@Override
	public Object iterationOrder()
	{
		return this; // iteration order is only compatible with ourselves
	}

	@Override
	public T firstElement()
	{
		return samples.get( 0 );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return new PointSampleListCursor();
	}

	@Override
	public long size()
	{
		return samples.size();
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}
}
