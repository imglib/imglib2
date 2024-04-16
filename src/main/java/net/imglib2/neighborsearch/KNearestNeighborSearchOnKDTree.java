/*
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

package net.imglib2.neighborsearch;

import java.util.Arrays;
import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;
import net.imglib2.KDTree;
import net.imglib2.KDTreeNode;
import net.imglib2.kdtree.KNearestNeighborSearchImpl;

/**
 * Implementation of {@link KNearestNeighborSearch} search for kd-trees.
 * 
 * @author Tobias Pietzsch
 */
public class KNearestNeighborSearchOnKDTree< T > implements KNearestNeighborSearch< T >
{
	private final KDTree< T > tree;

	private final int k;

	private final KNearestNeighborSearchImpl impl;

	private final KDTreeNode< T >[] matches;

	@SuppressWarnings( "unchecked" )
	public KNearestNeighborSearchOnKDTree( final KDTree< T > tree, final int k )
	{
		this.tree = tree;
		this.k = k;
		impl = new KNearestNeighborSearchImpl( tree.impl(), k );
		matches = new KDTreeNode[ k ];
		Arrays.setAll( matches, i -> tree.createNode() );
	}

	@SuppressWarnings( "unchecked" )
	private KNearestNeighborSearchOnKDTree( final KNearestNeighborSearchOnKDTree< T > knn )
	{
		tree = knn.tree;
		k = knn.k;
		impl = knn.impl.copy();
		matches = new KDTreeNode[ k ];
		Arrays.setAll( matches, i -> tree.createNode().setNodeIndex( impl.bestIndex( i ) ) );
	}

	@Override
	public int numDimensions()
	{
		return tree.numDimensions();
	}

	@Override
	public int getK()
	{
		return k;
	}

	@Override
	public void search( final RealLocalizable p )
	{
		impl.search( p );
		for ( int i = 0; i < k; i++ )
			matches[ i ].setNodeIndex( impl.bestIndex( i ) );
	}

	@Override
	public Sampler< T > getSampler( final int i )
	{
		return matches[ i ];
	}

	@Override
	public RealLocalizable getPosition( final int i )
	{
		return matches[ i ];
	}

	@Override
	public double getSquareDistance( final int i )
	{
		return impl.bestSquDistance( i );
	}

	@Override
	public KNearestNeighborSearchOnKDTree< T > copy()
	{
		return new KNearestNeighborSearchOnKDTree<>( this );
	}

	/* NearestNeighborSearch */

	@Override
	public RealLocalizable getPosition()
	{
		return getPosition( 0 );
	}

	@Override
	public Sampler< T > getSampler()
	{
		return getSampler( 0 );
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
