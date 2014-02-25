/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * N-dimensional data is often stored in a flat 1-dimensional array.
 * This class provides convenience methods to translate between N-dimensional
 * indices (positions) and 1-dimensional indices.
 *
 *
 * @author Tobias Pietzsch
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
		int i = ( int )position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + ( int )position[ d ];
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
		indexToPositionWithOffset( index, position.length, dimensions, null, position );
	}

	final static public void indexToPosition( long index, final long[] dimensions, final int[] position )
	{
		indexToPositionWithOffset( ( int ) index, position.length, dimensions, null, position );
	}

	final static public void indexToPosition( int index, final int[] dimensions, final long[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, null, position );
	}

	final static public void indexToPosition( long index, final long[] dimensions, final long[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, null, position );
	}

	final static public void indexToPosition( long index, final Dimensions dimensions, final Positionable position )
	{
		final int maxDim = dimensions.numDimensions() - 1;
		if ( index == -1 )
		{
			// -1 index indicates we wan't to go one back from the starting
			// position. So set position to [0,..0]
			position.setPosition( new long[ position.numDimensions() ] );
			// and move back one in the first axis.
			position.bck( 0 );
		}
		else
		{
			for ( int d = 0; d < maxDim; ++d )
			{
				final long j = index / dimensions.dimension( d );
				position.setPosition( index - j * dimensions.dimension( d ), d );
				index = j;
			}
			position.setPosition( index, maxDim );
		}
	}

	final static public void indexToPosition( int index, final int[] dimensions, final float[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, null, position );
	}

	final static public void indexToPosition( long index, final long[] dimensions, final float[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, null, position );
	}

	final static public void indexToPosition( int index, final int[] dimensions, final double[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, null, position );
	}

	final static public void indexToPosition( long index, final long[] dimensions, final double[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, null, position );
	}

	final static public void indexToPositionWithOffset( int index, final int[] dimensions, final int[] offsets, final int[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, offsets, position );
	}

	final static public void indexToPositionWithOffset( long index, final int[] dimensions, final long[] offsets, final long[] position )
	{

		indexToPositionWithOffset( index, position.length, dimensions, offsets, position );

	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final long[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, offsets, position );
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final int[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, offsets, position );
	}

	final static public void indexToPositionWithOffset( long index, final int[] dimensions, final long[] offsets, final float[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, offsets, position );
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final float[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, offsets, position );
	}

	final static public void indexToPositionWithOffset( long index, final int[] dimensions, final long[] offsets, final double[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, offsets, position );
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final double[] position )
	{
		indexToPositionWithOffset( index, position.length, dimensions, offsets, position );
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

	final static public long indexToPositionWithOffset( final long index, final long[] dimensions, final long[] steps, final long[] offsets, final int dimension )
	{
		return indexToPosition( index, dimensions, steps, dimension ) + offsets[ dimension ];
	}

	/**
	 * Create allocation step array from the dimensions of an N-dimensional array.
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
	 * Create allocation step array from the dimensions of an N-dimensional array.
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

	private static void indexToPositionWithOffset( long index, final int size, final Object dimensions, final Object offsets, final Object position )
	{
		final int maxDim = size - 1;
		// The "-1" index has special significance. It indicates a position
		// such that, with one cursor.fwd() call, the array will be in its
		// starting position. Thus we need to circumvent the normal index to
		// position algorithm and create a starting array explicitly.
		if ( index == -1 )
		{
			makeStartingPositionArray( offsets, position, size );
		}
		else
		{
			// In the general case, just compute the position for the given
			// index.
			for ( int d = 0; d < maxDim; ++d )
			{
				final double value = getValue( dimensions, d );
				final double offset = getValue( offsets, d );
				final long j = ( long ) ( index / getValue( dimensions, d ) );
				setValue( position, d, index - j * value + offset );
				index = j;
			}
			setValue( position, maxDim, index + getValue( offsets, maxDim ) );
		}
	}

	// -- Helper methods --

	/**
	 * Helper method to get the value of a primitive array of unknown type.
	 * Casts to a concrete array type.
	 */
	private static double getValue( Object source, int position )
	{
		if ( source == null )
		{
			return 0;
		}
		else if ( source instanceof long[] )
		{
			return ( ( long[] ) source )[ position ];
		}
		else if ( source instanceof int[] )
		{
			return ( ( int[] ) source )[ position ];
		}
		else if ( source instanceof double[] )
		{
			return ( ( double[] ) source )[ position ];
		}
		else if ( source instanceof float[] ) { return ( ( float[] ) source )[ position ]; }
		throw new IllegalArgumentException( "Can only operate on float, int, long or double arrays." );
	}

	/**
	 * Helper method to set the value of a primitive array of unknown type.
	 * Casts to a concrete array type.
	 */
	private static void setValue( Object dest, int position, double value )
	{
		if ( dest instanceof long[] )
		{
			( ( long[] ) dest )[ position ] = ( long ) value;
			return;
		}
		else if ( dest instanceof int[] )
		{
			( ( int[] ) dest )[ position ] = ( int ) value;
			return;
		}
		else if ( dest instanceof double[] )
		{
			( ( double[] ) dest )[ position ] = value;
			return;
		}
		else if ( dest instanceof float[] )
		{
			( ( float[] ) dest )[ position ] = ( float ) value;
			return;
		}
		throw new IllegalArgumentException( "Can only operate on float, int, long or double arrays." );
	}

	/**
	 * Sets a position array to the special "starting" position, which is
	 * equivalent to the given offset array with the first index decremented
	 * once. For example, if the offset array is [0, 0, 0] then the position
	 * array will be [-1, 0, 0]. This ensures that when {@link Cursor#fwd()} is
	 * called, the position will advance to [0, 0, 0].
	 * 
	 * @param offsets
	 *            base position values. Defaults to the origin if null.
	 * @param position
	 *            position array to modify.
	 * @param size
	 *            the position array size
	 */
	private static void makeStartingPositionArray( final Object offsets, final Object position, final int size )
	{
		for ( int i = 0; i < size; i++ )
		{
			if ( offsets == null )
			{
				setValue( position, i, 0 );
			}
			else
			{
				setValue( position, i, getValue( offsets, i ) );
			}
		}
		setValue( position, 0, getValue( position, 0 ) - 1 );
	}
}
