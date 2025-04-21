/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.kdtree;

import net.imglib2.RealLocalizable;

import static net.imglib2.kdtree.KDTreeUtils.leftChildIndex;
import static net.imglib2.kdtree.KDTreeUtils.parentIndex;
import static net.imglib2.kdtree.KDTreeUtils.rightChildIndex;

/**
 * Represents the tree structure, and provides access to node coordinates (but
 * not values).
 * <p>
 * The nodes in the tree are arranged in Eytzinger layout (children of i are at
 * 2i and 2i+1). Additionally, pivot indices are chosen such that "leaf layers"
 * are filled from the left.
 * <p>
 * For example 10 nodes will always be arranged like this:
 * <pre>
 *            0
 *         /     \
 *       1         2
 *     /   \     /   \
 *    3     4   5     6
 *   / \   /
 *  7   8 9
 * </pre>
 * <p>
 * never like this:
 * <pre>
 *            0
 *         /     \
 *       1         2
 *     /   \     /   \
 *    3     4   5     6
 *   /         /     /
 *  7         8     9
 * </pre>
 * <p>
 * By choosing pivots in this way, the tree structure is fully
 * determined. For every node index, the child indices can be calculated
 * without dependent reads. And iff the calculated child index is less
 * than the number of nodes, the child exists.
 */
public class KDTreeImpl
{
	private final int numDimensions;

	private final int numPoints;

	private final KDTreePositions positions;

	public KDTreeImpl( final KDTreePositions positions )
	{
		this.numDimensions = positions.numDimensions;
		this.numPoints = positions.numPoints;
		this.positions = positions;
	}

	/**
	 * Get the root node of the tree.
	 *
	 * @return index of the root node
	 */
	public int root()
	{
		return 0;
	}

	/**
	 * Get the left child of node {@code i}.
	 *
	 * @param i
	 * 		node index
	 *
	 * @return index of left child or {@code -1} if no left child exists
	 */
	public int left( final int i )
	{
		return ifExists( leftChildIndex( i ) );
	}

	/**
	 * Get the right child of node {@code i}.
	 *
	 * @param i
	 * 		node index
	 *
	 * @return index of right child or {@code -1} if no right child exists
	 */
	public int right( final int i )
	{
		return ifExists( rightChildIndex( i ) );
	}

	/**
	 * Get the parent of node {@code i}.
	 *
	 * @param i
	 * 		node index
	 *
	 * @return index of parent
	 */
	public int parent( final int i )
	{
		return i == root() ? -1 : parentIndex( i );
	}

	/**
	 * If a node with index {@code i} exists, returns {@code i}.
	 * Otherwise, returns {@code -1}.
	 */
	private int ifExists( final int i )
	{
		return i < numPoints ? i : -1;
	}

	/**
	 * Get the dimension along which node {@code i} divides the space.
	 *
	 * @param i
	 * 		node index
	 *
	 * @return splitting dimension.
	 */
	public int splitDimension( final int i )
	{
		return ( 31 - Integer.numberOfLeadingZeros( i + 1 ) ) % numDimensions;
	}

	public double getDoublePosition( final int i, final int d )
	{
		return positions.get( i, d );
	}

	/**
	 * Compute the squared distance from node {@code i} to {@code pos}.
	 */
	public float squDistance( final int i, final float[] pos )
	{
		float sum = 0;
		for ( int d = 0; d < numDimensions; ++d )
		{
			final float diff = pos[ d ] - ( float ) positions.get( i, d );
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Compute the squared distance from node {@code i} to {@code pos}.
	 */
	public double squDistance( final int i, final double[] pos )
	{
		double sum = 0;
		for ( int d = 0; d < numDimensions; ++d )
		{
			final double diff = pos[ d ] - positions.get( i, d );
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Compute the squared distance from node {@code i} to {@code pos}.
	 */
	public double squDistance( final int i, final RealLocalizable pos )
	{
		double sum = 0;
		for ( int d = 0; d < numDimensions; ++d )
		{
			final double diff = pos.getDoublePosition( d ) - positions.get( i, d );
			sum += diff * diff;
		}
		return sum;
	}

	public int numDimensions()
	{
		return numDimensions;
	}

	public int size()
	{
		return numPoints;
	}

	public int depth()
	{
		return 32 - Integer.numberOfLeadingZeros( numPoints );
	}
}
