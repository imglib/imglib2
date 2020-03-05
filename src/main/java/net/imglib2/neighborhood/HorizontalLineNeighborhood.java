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

public class HorizontalLineNeighborhood< T > extends AbstractLocalizable implements Neighborhood< T >
{
	public static < T > HorizontalLineNeighborhoodFactory< T > factory()
	{
		return new HorizontalLineNeighborhoodFactory< T >()
		{
			@Override
			public Neighborhood< T > create( final long[] position, final long span, final int dim, final boolean skipCenter, final RandomAccess< T > sourceRandomAccess )
			{
				return new HorizontalLineNeighborhood< T >( position, span, dim, skipCenter, sourceRandomAccess );
			}
		};
	}

	/**
	 * The span of the line. The iterated interval will be of size
	 * {@code 2 x span + 1 }.
	 */
	private final long span;

	/**
	 * The dimension along which the line should extend.
	 */
	private final int dim;

	private final RandomAccess< T > sourceRandomAccess;

	private final Interval structuringElementBoundingBox;

	private final long maxIndex;

	/** If {@code true}, the central pixel will be skipped. */
	private final boolean skipCenter;

	HorizontalLineNeighborhood( final long[] position, final long span, final int dim, final boolean skipCenter, final RandomAccess< T > sourceRandomAccess )
	{
		super( position );
		this.skipCenter = skipCenter;
		if ( skipCenter )
		{
			this.maxIndex = 2 * span;
		}
		else
		{
			this.maxIndex = 2 * span + 1;
		}
		this.span = span;
		this.dim = dim;
		this.sourceRandomAccess = sourceRandomAccess;
		this.structuringElementBoundingBox = createInterval();
	}

	@Override
	public Interval getStructuringElementBoundingBox()
	{
		return structuringElementBoundingBox;
	}

	@Override
	public long size()
	{
		return maxIndex;
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
		return structuringElementBoundingBox.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = structuringElementBoundingBox.realMin( d );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( structuringElementBoundingBox.realMin( d ), d );
	}

	@Override
	public double realMax( final int d )
	{
		return structuringElementBoundingBox.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = structuringElementBoundingBox.realMax( d );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( structuringElementBoundingBox.realMax( d ), d );
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public long min( final int d )
	{
		return structuringElementBoundingBox.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = structuringElementBoundingBox.min( d );
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( structuringElementBoundingBox.min( d ), d );
	}

	@Override
	public long max( final int d )
	{
		return structuringElementBoundingBox.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = structuringElementBoundingBox.max( d );
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( structuringElementBoundingBox.max( d ), d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = dimension( d );
	}

	@Override
	public long dimension( final int d )
	{
		if ( d == dim )
		{
			return maxIndex;
		}
		else
		{
			return 1;
		}
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

	public final class LocalCursor extends AbstractEuclideanSpace implements Cursor< T >
	{
		private final RandomAccess< T > source;

		private long index;

		public LocalCursor( final RandomAccess< T > source )
		{
			super( source.numDimensions() );
			this.source = source;
			reset();
		}

		protected LocalCursor( final LocalCursor c )
		{
			super( c.numDimensions() );
			source = c.source.copyRandomAccess();
			index = c.index;
		}

		@Override
		public T get()
		{
			return source.get();
		}

		@Override
		public void fwd()
		{
			source.fwd( dim );
			if ( skipCenter && index == span )
			{
				source.fwd( dim );
			}
			index++;
		}

		@Override
		public void jumpFwd( final long steps )
		{
			for ( int i = 0; i < steps; i++ )
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
			source.setPosition( position );
			source.move( -span - 1, dim );
			index = 0;
		}

		@Override
		public boolean hasNext()
		{
			return index < maxIndex;
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

	private Interval createInterval()
	{
		final long[] minsize = new long[ 2 * position.length ];
		for ( int i = 0; i < position.length; i++ )
		{
			if ( i == dim )
			{
				minsize[ i ] = position[ i ] - span;
			}
			else
			{
				minsize[ i ] = position[ i ];
			}
		}
		for ( int i = position.length; i < minsize.length; i++ )
		{
			if ( i - position.length == dim )
			{
				minsize[ i ] = position[ i - position.length ] + span;
			}
			else
			{
				minsize[ i ] = position[ i - position.length ];
			}
		}
		return Intervals.createMinMax( minsize );
	}
}
