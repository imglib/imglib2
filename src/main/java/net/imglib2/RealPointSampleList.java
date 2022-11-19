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

package net.imglib2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A list of data samples at explicit {@link RealLocalizable real coordinates}.
 * 
 * @author Stephan Saalfeld
 */
public class RealPointSampleList< T > implements IterableRealInterval< T >
{
	public class RealPointSampleListCursor implements RealCursor< T >
	{
		protected int index = -1;

		protected RealPoint position = null;

		protected T sample = null;

		@Override
		public RealCursor< T > copy()
		{
			final RealPointSampleListCursor copy = new RealPointSampleListCursor();
			copy.index = index;
			copy.position = position;
			copy.sample = sample;

			return copy;
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
			if ( steps == 0 )
				return;

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
	}

	final protected int n;

	final protected ArrayList< RealPoint > coordinates = new ArrayList< RealPoint >();

	final protected ArrayList< T > samples = new ArrayList< T >();

	protected int lastIndex = -1;

	final protected double[] min, max;

	/**
	 * @param n
	 *            - number of dimensions
	 */
	public RealPointSampleList( final int n )
	{
		this.n = n;
		min = new double[ n ];
		max = new double[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Double.MAX_VALUE;
			max[ d ] = -Double.MAX_VALUE;
		}
	}

	public void add( final RealPoint position, final T sample )
	{
		coordinates.add( position );
		samples.add( sample );
		lastIndex = samples.size() - 1;
		for ( int d = 0; d < n; ++d )
		{
			final double x = position.getDoublePosition( d );

			if ( x < min[ d ] )
				min[ d ] = x;
			if ( x > max[ d ] )
				max[ d ] = x;
		}
	}

	@Override
	public RealCursor< T > cursor()
	{
		return new RealPointSampleListCursor();
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
	public RealCursor< T > localizingCursor()
	{
		return new RealPointSampleListCursor();
	}

	@Override
	public long size()
	{
		return samples.size();
	}

	@Override
	public double realMax( final int d )
	{
		return max[ d ];
	}

	@Override
	public void realMax( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public void realMax( final RealPositionable m )
	{
		m.setPosition( max );
	}

	@Override
	public double realMin( final int d )
	{
		return min[ d ];
	}

	@Override
	public void realMin( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = min[ d ];
	}

	@Override
	public void realMin( final RealPositionable m )
	{
		m.setPosition( min );
	}

	@Override
	public int numDimensions()
	{
		return n;
	}
}
