/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.util;

import net.imglib2.Dimensions;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * N-dimensional data is often stored in a flat 1-dimensional array. This class
 * provides convenience methods to translate between N-dimensional indices
 * (positions) and 1-dimensional indices.
 *
 *
 * @author Tobias Pietzsch
 * @author Philipp Hanslovsky
 */
public class IntervalIndexer
{
	final static public int positionToIndex( final int[] position, final int[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		int i = position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ];
		return i;
	}

	final static public int positionToIndex( final long[] position, final int[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		int i = ( int ) position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + ( int ) position[ d ];
		return i;
	}

	final static public long positionToIndex( final long[] position, final long[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		long i = position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ];
		return i;
	}

	final static public long positionToIndex( final Localizable position, final Dimensions dimensions )
	{
		final int maxDim = dimensions.numDimensions() - 1;
		long i = position.getLongPosition( maxDim );
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions.dimension( d ) + position.getLongPosition( d );
		return i;
	}

	final static public long positionWithOffsetToIndex( final long[] position, final long[] dimensions, final long[] offsets )
	{
		final int maxDim = dimensions.length - 1;
		long i = position[ maxDim ] - offsets[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ] - offsets[ d ];
		return i;
	}

	final static public int positionWithOffsetToIndex( final long[] position, final int[] dimensions, final long[] offsets )
	{
		final int maxDim = dimensions.length - 1;
		int i = ( int ) ( position[ maxDim ] - offsets[ maxDim ] );
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + ( int ) ( position[ d ] - offsets[ d ] );
		return i;
	}

	final static public int positionWithOffsetToIndex( final int[] position, final int[] dimensions, final int[] offsets )
	{
		final int maxDim = dimensions.length - 1;
		int i = position[ maxDim ] - offsets[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ] - offsets[ d ];
		return i;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( long index, final long[] dimensions, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = ( int ) ( index - j * dimensions[ d ] );
			index = j;
		}
		position[ maxDim ] = ( int ) index;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final long[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( long index, final long[] dimensions, final long[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( long index, final Dimensions dimensions, final Positionable position )
	{
		final int maxDim = dimensions.numDimensions() - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions.dimension( d );
			position.setPosition( index - j * dimensions.dimension( d ), d );
			index = j;
		}
		position.setPosition( index, maxDim );
	}

	final static public void indexToPosition( int index, final int[] dimensions, final float[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( long index, final long[] dimensions, final float[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final double[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( long index, final long[] dimensions, final double[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPositionWithOffset( int index, final int[] dimensions, final int[] offsets, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( int index, final int[] dimensions, final long[] offsets, final long[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final long[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = ( int ) ( index - j * dimensions[ d ] + offsets[ d ] );
			index = j;
		}
		position[ maxDim ] = ( int ) ( index + offsets[ maxDim ] );
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final float[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final double[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public int indexToPosition( final int index, final int[] dimensions, final int dimension )
	{
		int step = 1;
		for ( int d = 0; d < dimension; ++d )
			step *= dimensions[ d ];
		return ( index / step ) % dimensions[ dimension ];
	}

	final static public long indexToPosition( final long index, final long[] dimensions, final int dimension )
	{
		int step = 1;
		for ( int d = 0; d < dimension; ++d )
			step *= dimensions[ d ];
		return ( index / step ) % dimensions[ dimension ];
	}

	final static public int indexToPositionWithOffset( final int index, final int[] dimensions, final int[] offsets, final int dimension )
	{
		return indexToPosition( index, dimensions, dimension ) + offsets[ dimension ];
	}

	final static public int indexToPosition( final int index, final int[] dimensions, final int[] steps, final int dimension )
	{
		return ( index / steps[ dimension ] ) % dimensions[ dimension ];
	}

	final static public long indexToPosition( final long index, final long[] dimensions, final long[] steps, final int dimension )
	{
		return ( index / steps[ dimension ] ) % dimensions[ dimension ];
	}

	final static public int indexToPositionWithOffset( final int index, final int[] dimensions, final int[] steps, final int[] offset, final int dimension )
	{
		return indexToPosition( index, dimensions, steps, dimension ) + offset[ dimension ];
	}

	public static long indexToPositionWithOffset( final int index, final int[] dimensions, final int[] steps, final long[] offset, final int dimension )
	{
		return indexToPosition( index, dimensions, steps, dimension ) + offset[ dimension ];
	}

	final static public long indexToPositionWithOffset( final long index, final long[] dimensions, final long[] steps, final long[] offsets, final int dimension )
	{
		return indexToPosition( index, dimensions, steps, dimension ) + offsets[ dimension ];
	}

	public static long positionToIndexForInterval( final Localizable position, final Interval interval )
	{
		final int maxDim = interval.numDimensions() - 1;
		long i = position.getLongPosition( maxDim ) - interval.min( maxDim );
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * interval.dimension( d ) + position.getLongPosition( d ) - interval.min( d );
		return i;
	}

	public static void indexToPositionForInterval( long index, final Interval interval, final Positionable position )
	{
		final int maxDim = interval.numDimensions() - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long dim = interval.dimension( d );
			final long j = index / dim;
			position.setPosition( index - j * dim + interval.min( d ), d );
			index = j;
		}
		position.setPosition( index + interval.min( maxDim ), maxDim );
	}

	/**
	 * Create allocation step array from the dimensions of an N-dimensional
	 * array.
	 * 
	 * @param dimensions
	 * @param steps
	 */
	public static void createAllocationSteps( final long[] dimensions, final long[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dimensions.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dimensions[ d - 1 ];
	}

	/**
	 * Create allocation step array from the dimensions of an N-dimensional
	 * array.
	 * 
	 * @param dimensions
	 * @param steps
	 */
	public static void createAllocationSteps( final int[] dimensions, final int[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dimensions.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dimensions[ d - 1 ];
	}

}
