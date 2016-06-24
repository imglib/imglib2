/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import net.imglib2.util.IntervalIndexer;

/**
 * A cell of an {@link CellImg}.
 * 
 * @author Tobias Pietzsch (tobias.pietzsch@gmail.com)
 */
public abstract class AbstractCell< A > implements Serializable
{
	private static final long serialVersionUID = 1L;

	protected int n;

	protected int[] dimensions;

	protected int[] steps;

	protected long[] min;

	protected long[] max;

	protected int numPixels;

	public AbstractCell()
	{}

	public AbstractCell( final int[] dimensions, final long[] min )
	{
		this.n = dimensions.length;
		this.dimensions = dimensions.clone();
		this.steps = new int[ n ];
		IntervalIndexer.createAllocationSteps( dimensions, steps );
		this.min = min.clone();

		max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = min[ d ] + dimensions[ d ] - 1;

		int nPixels = dimensions[ 0 ];
		for ( int d = 1; d < n; ++d )
			nPixels *= dimensions[ d ];
		numPixels = nPixels;
	}

	/**
	 * Get the basic type array that stores this cells pixels.
	 * 
	 * @return underlying basic type array.
	 */
	public abstract A getData();

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
	 * compute the index in the underlying flat array of this cell which
	 * corresponds to a local position (i.e., relative to the origin of this
	 * cell).
	 * 
	 * @param position
	 *            a local position
	 * @return corresponding index
	 */
	public int localPositionToIndex( final long[] position )
	{
		return IntervalIndexer.positionToIndex( position, dimensions );
	}

	/**
	 * 
	 * @param d
	 *            dimension
	 * @return minimum
	 */
	public long min( final int d )
	{
		return min[ d ];
	}

	/**
	 * Write the minimum of each dimension into long[].
	 * 
	 * @param minValue
	 */
	public void min( final long[] minimum )
	{
		for ( int d = 0; d < n; ++d )
			minimum[ d ] = min[ d ];
	}

	/**
	 * Get the number of pixels in a given dimension <em>d</em>.
	 * 
	 * @param d
	 */
	public int dimension( final int d )
	{
		return dimensions[ d ];
	}

	/**
	 * Write the number of pixels in each dimension into long[].
	 * 
	 * @param dimensions
	 */
	public void dimensions( final int[] dim )
	{
		for ( int d = 0; d < n; ++d )
			dim[ d ] = dimensions[ d ];
	}
}
