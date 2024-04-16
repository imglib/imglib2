/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.Arrays;
import net.imglib2.RealLocalizable;

/**
 * <em>k</em>-nearest-neighbor search on {@link KDTreeImpl}.
 * Results are node indices.
 */
public class KNearestNeighborSearchImpl
{
	private final KDTreeImpl tree;
	private final int numDimensions;
	private final int numPoints;
	private final double[] pos;
	private final int k;
	private final double[] bestSquDistance;
	private final int[] bestIndex;
	private final double[] axisDiffs;
	private final int[] awayChilds;

	public KNearestNeighborSearchImpl( final KDTreeImpl tree, final int k )
	{
		this.tree = tree;
		numDimensions = tree.numDimensions();
		numPoints = tree.size();
		this.k = k;
		pos = new double[ numDimensions ];
		bestSquDistance = new double[ k ];
		bestIndex = new int[ k ];
		final int depth = tree.depth();
		axisDiffs = new double[ depth + 1 ];
		awayChilds = new int[ depth + 1 ];
	}

	/**
	 * Insert index into list of best nodes.
	 * Also checks whether index will be inserted at all, that is,
	 * whether squDistance < bestSquDistance[k-1]
	 */
	private void insert( final double squDistance, final int index )
	{
		// first check whether index will be inserted at all
		if ( squDistance < bestSquDistance[ k - 1 ] )
		{

			// find insertion point, shifting existing elements to make room
			int i;
			for ( i = k - 1; i > 0 && squDistance < bestSquDistance[ i - 1 ]; --i )
			{
				bestSquDistance[ i ] = bestSquDistance[ i - 1 ];
				bestIndex[ i ] = bestIndex[ i - 1 ];
			}

			// insert index at i,
			bestSquDistance[ i ] = squDistance;
			bestIndex[ i ] = index;
		}
	}

	public void search( final RealLocalizable p )
	{
		p.localize( pos );
		int current = tree.root();
		int depth = 0;
		Arrays.fill( bestSquDistance, Double.POSITIVE_INFINITY );
		Arrays.fill( bestIndex, -1 );
		while ( true )
		{
			insert( tree.squDistance( current, pos ), current );

			final int d = depth % numDimensions;
			final double axisDiff = pos[ d ] - tree.getDoublePosition( current, d );
			final boolean leftIsNearBranch = axisDiff < 0;

			// search the near branch
			final int nearChild = ( 2 * current ) + ( leftIsNearBranch ? 1 : 2 );
			final int awayChild = ( 2 * current ) + ( leftIsNearBranch ? 2 : 1 );
			++depth;
			awayChilds[ depth ] = awayChild;
			axisDiffs[ depth ] = axisDiff * axisDiff;
			if ( nearChild >= numPoints )
			{
				while ( awayChilds[ depth ] >= numPoints || axisDiffs[ depth ] > bestSquDistance[ k - 1 ] )
				{
					if ( --depth == 0 )
					{
						return;
					}
				}
				current = awayChilds[ depth ];
				awayChilds[ depth ] = numPoints;
			}
			else
			{
				current = nearChild;
			}
		}
	}

	public int k()
	{
		return k;
	}

	public int bestIndex( final int i )
	{
		return bestIndex[ i ];
	}

	public double bestSquDistance( final int i )
	{
		return bestSquDistance[ i ];
	}

	public KNearestNeighborSearchImpl copy()
	{
		final KNearestNeighborSearchImpl copy = new KNearestNeighborSearchImpl( tree, k );
		System.arraycopy( pos, 0, copy.pos, 0, pos.length );
		System.arraycopy( bestIndex, 0, copy.bestIndex, 0, bestIndex.length );
		System.arraycopy( bestSquDistance, 0, copy.bestSquDistance, 0, bestSquDistance.length );
		return copy;
	}
}
