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

package net.imglib2.neighborhood;

import java.util.Iterator;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.AbstractLocalizable;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;

/**
 * TODO
 * 
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class HyperSphereNeighborhood< T > extends AbstractLocalizable implements Neighborhood< T >
{
	public static < T > HyperSphereNeighborhoodFactory< T > factory()
	{
		return new HyperSphereNeighborhoodFactory< T >()
		{
			@Override
			public Neighborhood< T > create( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
			{
				return new HyperSphereNeighborhood< T >( position, radius, sourceRandomAccess );
			}
		};
	}

	private final RandomAccess< T > sourceRandomAccess;

	private final long radius;

	private final int maxDim;

	private final long size;

	private final Interval structuringElementBoundingBox;

	HyperSphereNeighborhood( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
	{
		super( position );
		this.sourceRandomAccess = sourceRandomAccess;
		this.radius = radius;
		maxDim = n - 1;
		size = computeSize();

		final long[] min = new long[ n ];
		final long[] max = new long[ n ];

		for ( int d = 0; d < n; d++ )
		{
			min[ d ] = -radius;
			max[ d ] = radius;
		}

		structuringElementBoundingBox = new FinalInterval( min, max );
	}

	/**
	 * Compute the number of elements for iteration
	 */
	protected long computeSize()
	{
		final LocalCursor cursor = new LocalCursor( sourceRandomAccess );

		// "compute number of pixels"
		long size = 0;
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			++size;
		}

		return size;
	}

	public final class LocalCursor extends AbstractEuclideanSpace implements Cursor< T >
	{
		private final RandomAccess< T > source;

		// the current radius in each dimension we are at
		private final double[] r;

		// the current radius in each dimension truncated to long
		private final long[] ri;

		// the remaining number of steps in each dimension we still have to go
		private final long[] s;

		public LocalCursor( final RandomAccess< T > source )
		{
			super( source.numDimensions() );
			this.source = source;
			r = new double[ n ];
			ri = new long[ n ];
			s = new long[ n ];
			reset();
		}

		protected LocalCursor( final LocalCursor c )
		{
			super( c.numDimensions() );
			source = c.source.copyRandomAccess();
			r = c.r.clone();
			ri = c.ri.clone();
			s = c.s.clone();
		}

		@Override
		public T get()
		{
			return source.get();
		}

		@Override
		public void fwd()
		{

			if ( --s[ 0 ] >= 0 )
				source.fwd( 0 );
			else
			{
				int d = 1;
				for ( ; d < n; ++d )
				{
					if ( --s[ d ] >= 0 )
					{
						source.fwd( d );
						break;
					}
				}

				for ( ; d > 0; --d )
				{
					final int e = d - 1;
					final double rd = r[ d ];
					final long pd = s[ d ] - ri[ d ];

					final double rad = Math.sqrt( rd * rd - pd * pd );
					final long radi = ( long ) rad;
					r[ e ] = rad;
					ri[ e ] = radi;
					s[ e ] = 2 * radi;

					source.setPosition( position[ e ] - radi, e );
				}
			}
		}

		@Override
		public void jumpFwd( final long steps )
		{
			for ( long i = 0; i < steps; ++i )
				fwd();
		}

		@Override
		public T next()
		{
			fwd();
			return get();
		}

		@Override
		public void remove()
		{
			// NB: no action.
		}

		@Override
		public void reset()
		{
			for ( int d = 0; d < maxDim; ++d )
			{
				r[ d ] = ri[ d ] = s[ d ] = 0;
				source.setPosition( position[ d ], d );
			}

			source.setPosition( position[ maxDim ] - radius - 1, maxDim );

			r[ maxDim ] = radius;
			ri[ maxDim ] = radius;
			s[ maxDim ] = 1 + 2 * radius;
		}

		@Override
		public boolean hasNext()
		{
			return s[ maxDim ] > 0;
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return source.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return source.getDoublePosition( d );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return source.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return source.getLongPosition( d );
		}

		@Override
		public void localize( final long[] position )
		{
			source.localize( position );
		}

		@Override
		public void localize( final float[] position )
		{
			source.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			source.localize( position );
		}

		@Override
		public void localize( final int[] position )
		{
			source.localize( position );
		}

		@Override
		public LocalCursor copy()
		{
			return new LocalCursor( this );
		}

		@Override
		public LocalCursor copyCursor()
		{
			return copy();
		}
	}

	@Override
	public Interval getStructuringElementBoundingBox()
	{
		return structuringElementBoundingBox;
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return this; // iteration order is only compatible with ourselves
	}

	@Override
	public double realMin( final int d )
	{
		return position[ d ] - radius;
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = position[ d ] - radius;
		}
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
		{
			min.setPosition( position[ d ] - radius, d );
		}
	}

	@Override
	public double realMax( final int d )
	{
		return position[ d ] + radius;
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < max.length; d++ )
		{
			max[ d ] = position[ d ] + radius;
		}
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
		{
			max.setPosition( position[ d ] + radius, d );
		}
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public long min( final int d )
	{
		return position[ d ] - radius;
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = position[ d ] - radius;
		}
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
		{
			min.setPosition( position[ d ] - radius, d );
		}
	}

	@Override
	public long max( final int d )
	{
		return position[ d ] + radius;
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < max.length; d++ )
		{
			max[ d ] = position[ d ] + radius;
		}
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
		{
			max.setPosition( position[ d ] + radius, d );
		}
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < dimensions.length; d++ )
		{
			dimensions[ d ] = ( 2 * radius ) + 1;
		}
	}

	@Override
	public long dimension( final int d )
	{
		return ( 2 * radius ) + 1;
	}

	@Override
	public LocalCursor cursor()
	{
		return new LocalCursor( sourceRandomAccess.copyRandomAccess() );
	}

	@Override
	public LocalCursor localizingCursor()
	{
		return cursor();
	}

}
