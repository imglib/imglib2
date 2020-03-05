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

/**
 * A {@link Neighborhood} that iterates through the <b>tips</b> of a
 * multi-dimensional diamond.
 * <p>
 * Though it has very few direct applications, it is used in structuring element
 * decomposition for mathematical morphology.
 * 
 * @author Jean-Yves Tinevez Nov 6, 2013
 * @param <T>
 */
public class DiamondTipsNeighborhood< T > extends AbstractLocalizable implements Neighborhood< T >
{

	public static < T > DiamondTipsNeighborhoodFactory< T > factory()
	{
		return new DiamondTipsNeighborhoodFactory< T >()
		{
			@Override
			public DiamondTipsNeighborhood< T > create( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
			{
				return new DiamondTipsNeighborhood< T >( position, radius, sourceRandomAccess );
			}
		};
	}

	private final long radius;

	private final RandomAccess< T > sourceRandomAccess;

	private final Interval structuringElementBoundingBox;

	/**
	 * Creates a new diamond tip neighborhood.
	 * 
	 * @param position
	 *            the central position of the diamond.
	 * @param radius
	 *            the diamond radius in all dimensions.
	 * @param sourceRandomAccess
	 *            a {@link RandomAccess} on the target source.
	 */
	public DiamondTipsNeighborhood( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
	{
		super( position );
		this.radius = radius;
		this.sourceRandomAccess = sourceRandomAccess;
		this.structuringElementBoundingBox = createInterval();
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

	@Override
	public long size()
	{
		return sourceRandomAccess.numDimensions() * 2;
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return this;
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
		for ( int d = 0; d < position.length; d++ )
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
		for ( int d = 0; d < position.length; d++ )
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
		for ( int d = 0; d < position.length; d++ )
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
		for ( int d = 0; d < position.length; d++ )
		{
			max.setPosition( position[ d ] + radius, d );
		}
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < dimensions.length; d++ )
		{
			dimensions[ d ] = 2 * radius + 1;
		}
	}

	@Override
	public long dimension( final int d )
	{
		return 2 * radius + 1;
	}

	@Override
	public Interval getStructuringElementBoundingBox()
	{
		return structuringElementBoundingBox;
	}

	public final class LocalCursor extends AbstractEuclideanSpace implements Cursor< T >
	{
		private final RandomAccess< T > source;

		private int currentDim;

		private boolean parity;

		public LocalCursor( final RandomAccess< T > source )
		{
			super( source.numDimensions() );
			this.source = source;
			reset();
		}

		protected LocalCursor( final LocalCursor c )
		{
			super( c.numDimensions() );
			this.source = c.source.copyRandomAccess();
			this.currentDim = c.currentDim;
			this.parity = c.parity;
		}

		@Override
		public T get()
		{
			return source.get();
		}

		@Override
		public void fwd()
		{
			if ( !parity )
			{
				if ( currentDim >= 0 )
				{
					source.setPosition( position[ currentDim ], currentDim );
				}
				currentDim++;
				source.move( -radius, currentDim );
				parity = true;
			}
			else
			{
				source.move( 2 * radius, currentDim );
				parity = false;

			}
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
			currentDim = -1;
			parity = false;
		}

		@Override
		public boolean hasNext()
		{
			return currentDim < source.numDimensions() - 1 || parity;
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
		final long[] minmax = new long[ 2 * position.length ];
		for ( int i = 0; i < position.length; i++ )
		{
			minmax[ i ] = position[ i ] - radius;
		}
		for ( int i = position.length; i < minmax.length; i++ )
		{
			minmax[ i ] = position[ i - position.length ] + radius;
		}
		return Intervals.createMinMax( minmax );
	}
}
