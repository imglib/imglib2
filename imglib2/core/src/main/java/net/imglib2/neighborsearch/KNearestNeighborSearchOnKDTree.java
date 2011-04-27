/**
 * Copyright (c) 2011, Tobias Pietzsch
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.imglib2.neighborsearch;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;
import net.imglib2.collection.KDTree;
import net.imglib2.collection.KDTreeNode;

/**
 * Implementation of {@link KNearestNeighborSearch} search for kd-trees.
 * 
 * @author Tobias Pietzsch
 */
public class KNearestNeighborSearchOnKDTree< T > implements KNearestNeighborSearch< T >
{
	protected KDTree< T > tree;
	
	protected final int n;
	protected final double[] pos;

	protected final int k;
	protected KDTreeNode< T >[] bestPoints;
	protected double[] bestSquDistances;

	@SuppressWarnings( "unchecked" )
	public KNearestNeighborSearchOnKDTree( KDTree< T > tree, final int k )
	{
		this.tree = tree;
		this.n = tree.numDimensions();
		this.pos = new double[ n ];
		this.k = k;
		this.bestPoints = new KDTreeNode[ k ];
		this.bestSquDistances = new double[ k ];
		for ( int i = 0; i < k; ++i )
			bestSquDistances[ i ] = Double.MAX_VALUE;
	}
	
	
	@Override
	public void search( RealLocalizable reference )
	{
		reference.localize( pos );
		for ( int i = 0; i < k; ++i )
			bestSquDistances[ i ] = Double.MAX_VALUE;
		searchNode( tree.getRoot() );
	}

	protected void searchNode( KDTreeNode< T > current )
	{
		// consider the current node
		final double squDistance = current.squDistanceTo( pos );
		if ( squDistance < bestSquDistances[ k - 1 ] )
		{
			int i = k - 1;
            for ( int j = i - 1; i > 0 && squDistance < bestSquDistances[ j ]; --i, --j )
            {
            	bestSquDistances[ i ] = bestSquDistances[ j ];
            	bestPoints[ i ] = bestPoints[ j ];
            }
            bestSquDistances[ i ] = squDistance;
            bestPoints[ i ] = current;
		}
		
		final double axisDiff = pos[ current.getSplitDimension() ] - current.getSplitCoordinate();
		final double axisSquDistance = axisDiff * axisDiff;
		final boolean leftIsNearBranch = axisDiff < 0;

		// search the near branch
		final KDTreeNode< T > nearChild = leftIsNearBranch ? current.left : current.right;
		final KDTreeNode< T > awayChild = leftIsNearBranch ? current.right : current.left;
		if ( nearChild != null )
			searchNode( nearChild );

	    // search the away branch - maybe
		if ( ( axisSquDistance <= bestSquDistances[ k - 1 ] ) && ( awayChild != null ) )
			searchNode( awayChild );
	}

	@Override
	public Sampler< T > getSampler( int i )
	{
		return bestPoints[ i ];
	}

	@Override
	public RealLocalizable getPosition( int i )
	{
		return bestPoints[ i ];
	}

	@Override
	public double getSquareDistance( int i )
	{
		return bestSquDistances[ i ];
	}

	@Override
	public double getDistance( int i )
	{
		return Math.sqrt( bestSquDistances[ i ] );
	}


	@Override
	public Sampler< T > getSampler()
	{
		return getSampler( 0 );
	}


	@Override
	public RealLocalizable getPosition()
	{
		return getPosition( 0 );
	}


	@Override
	public double getSquareDistance()
	{
		return getSquareDistance( 0 );
	}


	@Override
	public double getDistance()
	{
		return getDistance( 0 );
	}
}
