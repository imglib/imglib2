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

package net.imglib2.view;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.View;

/**
 * Subdivides an <em>n</em>-dimensional {@link RandomAccessibleInterval} into
 * <em>n</em>-dimensional blocks of user defined size. Border blocks may have
 * smaller sizes.
 *
 * @param <T>
 *            the pixel type
 *
 * @author Marcel Wiedenmann (University of Konstanz)
 * @author Christian Dietz (University of Konstanz)
 */
public class TiledView< T > extends AbstractInterval implements RandomAccessibleInterval< RandomAccessibleInterval< T > >, View
{
	public static < T > TiledView< T > createFromBlocksPerDim( final RandomAccessibleInterval< T > source, final long[] blocksPerDim )
	{
		final long[] blockSize = new long[ blocksPerDim.length ];
		for ( int d = 0; d < blockSize.length; ++d )
		{
			blockSize[ d ] = ( source.dimension( d ) - 1 ) / blocksPerDim[ d ] + 1;
		}
		return new TiledView<>( source, blockSize );
	}

	private final RandomAccessibleInterval< T > source;

	private final long[] blockSize;

	private final long[] overlap;

	public TiledView( final RandomAccessibleInterval< T > source, final long... blockSize )
	{
		this( source, blockSize, new long[ blockSize.length ] );
	}

	public TiledView( final RandomAccessibleInterval< T > source, final long[] blockSize, final long[] overlap )
	{
		super( source.numDimensions() );

		// TODO: Fill missing block sizes with singleton dimensions. (Or
		// generalize the entire view to support hyperslicing/hypercubing when
		// #source dimensions > #block dimensions?)
		assert source.numDimensions() == blockSize.length;

		this.source = source;
		this.blockSize = blockSize;
		for ( int d = 0; d < n; ++d )
		{
			max[ d ] = ( source.dimension( d ) - 1 ) / blockSize[ d ];
		}
		this.overlap = overlap;
	}

	public RandomAccessibleInterval< T > getSource()
	{
		return source;
	}

	public long[] getBlockSize()
	{
		return blockSize.clone();
	}

	public long[] getOverlap()
	{
		return overlap.clone();
	}

	@Override
	public TiledViewRandomAccess< T > randomAccess()
	{
		return new TiledViewRandomAccess<>( source, blockSize, max, overlap );
	}

	@Override
	public TiledViewRandomAccess< T > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	public static class TiledViewRandomAccess< T > extends Point implements RandomAccess< RandomAccessibleInterval< T > >
	{
		private final RandomAccessibleInterval< T > source;

		private final long[] blockSize;

		private final long[] overlap;

		private final long[] max;

		private final long[] tempMin;

		private final long[] tempMax;

		public TiledViewRandomAccess( final RandomAccessibleInterval< T > source, final long[] blockSize, final long[] max, final long[] overlap )
		{
			super( source.numDimensions() );
			this.source = source;
			this.blockSize = blockSize;
			this.overlap = overlap;
			this.max = max;
			tempMin = new long[ n ];
			tempMax = new long[ n ];
		}

		private TiledViewRandomAccess( final TiledViewRandomAccess< T > ra )
		{
			super( ra.position, true );
			source = ra.source;
			blockSize = ra.blockSize;
			overlap = ra.overlap;
			max = ra.max;
			tempMin = ra.tempMin.clone();
			tempMax = ra.tempMax.clone();
		}

		@Override
		public RandomAccessibleInterval< T > get()
		{
			for ( int d = 0; d < n; ++d )
			{
				tempMin[ d ] = position[ d ] * blockSize[ d ];
				if ( position[ d ] < max[ d ] )
				{
					tempMax[ d ] = tempMin[ d ] + blockSize[ d ] - 1;
				}
				else
				{
					tempMax[ d ] = source.max( d );
				}
				// Add overlap
				tempMin[ d ] -= overlap[ d ];
				tempMax[ d ] += overlap[ d ];
			}
			// TODO: [Review] Creating multiple views per call probably isn't
			// what we want.
			// - zeroMin: do we want to ensure zeroMin at all? if yes: see
			// remarks at CombinedView.get() (--> RandomAccess that handles
			// translation)
			// - ínterval: maintain an unsafe interval view whose bounds are
			// updated each time this.get() is called?
			return Views.zeroMin( Views.interval( source, tempMin, tempMax ) );
		}

		@Override
		public TiledViewRandomAccess< T > copy()
		{
			return new TiledViewRandomAccess<>( this );
		}

		@Override
		public TiledViewRandomAccess< T > copyRandomAccess()
		{
			return copy();
		}
	}
}
