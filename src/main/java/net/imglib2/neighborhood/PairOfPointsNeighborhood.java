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
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.util.Intervals;

public class PairOfPointsNeighborhood< T > extends AbstractLocalizable implements Neighborhood< T >
{
	public static < T > PairOfPointsNeighborhoodFactory< T > factory()
	{
		return new PairOfPointsNeighborhoodFactory< T >()
				{
			@Override
			public Neighborhood< T > create( final long[] position, final long[] offset, final RandomAccess< T > sourceRandomAccess )
			{
				return new PairOfPointsNeighborhood< T >( position, offset, sourceRandomAccess );
			}
				};
	}

	private final long[] offset;

	private final int ndims;

	private final RandomAccess< T > ra;

	PairOfPointsNeighborhood( final long[] position, final long[] offset, final RandomAccess< T > sourceRandomAccess )
	{
		super( position );
		ra = sourceRandomAccess.copyRandomAccess();
		this.offset = offset;
		this.ndims = sourceRandomAccess.numDimensions();

	}

	public class LocalCursor extends AbstractEuclideanSpace implements Cursor< T >
	{
		private int index;

		private RandomAccess< T > source;

		private LocalCursor( final RandomAccess< T > source )
		{
			super( source.numDimensions() );
			this.source = source;
			reset();
		}

		private LocalCursor( final LocalCursor c )
		{
			this( c.source.copyRandomAccess() );
			this.index = c.index;
		}

		@Override
		public T get()
		{
			return source.get();
		}

		@Override
		public void fwd()
		{
			index++;
			if ( index == 1 )
			{
				for ( int d = 0; d < offset.length; d++ )
				{
					source.move( offset[ d ], d );
				}
			}
		}

		@Override
		public void jumpFwd( final long steps )
		{
			for ( long i = 0; i < steps; ++i )
			{
				fwd();
			}
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
			index = -1;
			source.setPosition( position );
		}

		@Override
		public boolean hasNext()
		{
			return index < 1;
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
			for ( int d = 0; d < position.length; d++ )
			{
				position[ d ] = source.getLongPosition( d );
			}
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < position.length; d++ )
			{
				position[ d ] = source.getFloatPosition( d );
			}
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < position.length; d++ )
			{
				position[ d ] = source.getDoublePosition( d );
			}
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < position.length; d++ )
			{
				position[ d ] = source.getIntPosition( d );
			}
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
		final long[] minmax = new long[ ndims * 2 ];
		for ( int d = 0; d < ndims; d++ )
		{
			minmax[ d ] = Math.min( position[ d ], position[ d ] + offset[ d ] );
		}
		for ( int d = ndims; d < 2 * ndims; d++ )
		{
			final int sd = d - ndims;
			minmax[ d ] = Math.max( position[ sd ], position[ sd ] + offset[ sd ] );
		}
		return Intervals.createMinMax( minmax );
	}

	@Override
	public long size()
	{
		return 2l;
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
		return Math.min( position[ d ], position[ d ] + offset[ d ] );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = Math.min( position[ d ], position[ d ] + offset[ d ] );
		}
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
		{
			min.setPosition( Math.min( position[ d ], position[ d ] + offset[ d ] ), d );
		}
	}

	@Override
	public double realMax( final int d )
	{
		return Math.max( position[ d ], position[ d ] + offset[ d ] );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < max.length; d++ )
		{
			max[ d ] = Math.max( position[ d ], position[ d ] + offset[ d ] );
		}
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
		{
			max.setPosition( Math.max( position[ d ], position[ d ] + offset[ d ] ), d );
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
		return Math.min( position[ d ], position[ d ] + offset[ d ] );
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = Math.min( position[ d ], position[ d ] + offset[ d ] );
		}
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
		{
			min.setPosition( Math.min( position[ d ], position[ d ] + offset[ d ] ), d );
		}
	}

	@Override
	public long max( final int d )
	{
		return Math.max( position[ d ], position[ d ] + offset[ d ] );
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < max.length; d++ )
		{
			max[ d ] = Math.max( position[ d ], position[ d ] + offset[ d ] );
		}
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
		{
			max.setPosition( Math.max( position[ d ], position[ d ] + offset[ d ] ), d );
		}
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < dimensions.length; d++ )
		{
			dimensions[ d ] = Math.abs( offset[ d ] ) + 1;
		}
	}

	@Override
	public long dimension( final int d )
	{
		return Math.abs( offset[ d ] ) + 1;
	}

	@Override
	public LocalCursor cursor()
	{
		return new LocalCursor( ra.copyRandomAccess() );
	}

	@Override
	public LocalCursor localizingCursor()
	{
		return cursor();
	}

}
