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

package net.imglib2.img.cell;

import java.io.Serializable;

import net.imglib2.Interval;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;

/**
 * A cell of an {@link AbstractCellImg}.
 *
 * @author Tobias Pietzsch
 */
public class Cell< A > implements Interval, Serializable
{
	private static final long serialVersionUID = 1L;

	protected final int n;

	protected final int[] dimensions;

	protected final int[] steps;

	protected final long[] min;

	protected final long[] max;

	private final int numPixels;

	private final A data;

	public Cell( final int[] dimensions, final long[] min, final A data )
	{
		this.n = dimensions.length;
		this.dimensions = dimensions.clone();
		this.steps = new int[ n ];
		IntervalIndexer.createAllocationSteps( dimensions, steps );
		this.min = min.clone();

		max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = min[ d ] + dimensions[ d ] - 1;

		numPixels = ( int ) Intervals.numElements( dimensions );

		this.data = data;
	}

	/**
	 * Get the basic type array that stores this cells pixels.
	 *
	 * @return underlying basic type array.
	 */
	public A getData()
	{
		return data;
	}

	public long size()
	{
		return numPixels;
	}

	public long indexToGlobalPosition( final int index, final int d )
	{
		return IntervalIndexer.indexToPosition( index, dimensions, steps, d ) + min[ d ];
	}

	public void indexToGlobalPosition( final int index, final long[] position )
	{
		IntervalIndexer.indexToPosition( index, dimensions, position );
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += min[ d ];
	}

	/**
	 * Compute the index in the underlying flat array of this cell which
	 * corresponds to the specified global {@code position}.
	 *
	 * @param position
	 *            a global position
	 * @return corresponding index
	 */
	public int globalPositionToIndex( final long[] position )
	{
		return IntervalIndexer.positionWithOffsetToIndex( position, dimensions, min );
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	/**
	 * Get the minimum in dimension d.
	 *
	 * @param d
	 *            dimension
	 * @return minimum in dimension d.
	 */
	@Override
	public long min( final int d )
	{
		return min[ d ];
	}

	/**
	 * Get the maximum in dimension d.
	 *
	 * @param d
	 *            dimension
	 * @return maximum in dimension d.
	 */
	@Override
	public long max( final int d )
	{
		return max[ d ];
	}

	/**
	 * Get the number of pixels in a given dimension <em>d</em>.
	 *
	 * @param d
	 */
	@Override
	public long dimension( final int d )
	{
		return dimensions[ d ];
	}

	/**
	 * Write the number of pixels in each dimension into int[].
	 *
	 * @param dim
	 */
	public void dimensions( final int[] dim )
	{
		for ( int d = 0; d < n; ++d )
			dim[ d ] = dimensions[ d ];
	}
}
