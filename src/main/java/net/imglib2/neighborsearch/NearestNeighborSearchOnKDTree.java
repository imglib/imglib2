/*
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

package net.imglib2.neighborsearch;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;
import net.imglib2.KDTree;
import net.imglib2.KDTreeNode;
import net.imglib2.kdtree.NearestNeighborSearchImpl;

/**
 * Implementation of {@link NearestNeighborSearch} search for kd-trees.
 * 
 * 
 * @author Tobias Pietzsch
 */
public class NearestNeighborSearchOnKDTree< T > implements NearestNeighborSearch< T >
{
	private final KDTree< T > tree;

	private final NearestNeighborSearchImpl impl;

	private final KDTreeNode< T > bestPoint;

	public NearestNeighborSearchOnKDTree( final KDTree< T > tree )
	{
		this.tree = tree;
		impl = new NearestNeighborSearchImpl( tree.impl() );
		bestPoint = tree.createNode();
	}

	private NearestNeighborSearchOnKDTree( final NearestNeighborSearchOnKDTree< T > nn )
	{
		tree = nn.tree;
		impl = nn.impl.copy();
		bestPoint = tree.createNode();
		bestPoint.setNodeIndex( nn.impl.bestIndex() );
	}

	@Override
	public int numDimensions()
	{
		return tree.numDimensions();
	}

	@Override
	public void search( final RealLocalizable p )
	{
		impl.search( p );
		bestPoint.setNodeIndex( impl.bestIndex() );
	}

	@Override
	public Sampler< T > getSampler()
	{
		return bestPoint;
	}

	@Override
	public RealLocalizable getPosition()
	{
		return bestPoint;
	}

	@Override
	public double getSquareDistance()
	{
		return impl.bestSquDistance();
	}

	@Override
	public NearestNeighborSearchOnKDTree< T > copy()
	{
		return new NearestNeighborSearchOnKDTree<>( this );
	}
}
