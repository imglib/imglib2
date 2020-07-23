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

package net.imglib2;


/**
 * Abstract base class for nodes in a KDTree. A KDTreeNode has coordinates and a
 * value. It provides the coordinates via the {@link RealLocalizable} interface.
 * It provides the value via {@link Sampler#get()}.
 *
 * @param <T>
 *            value type.
 *
 *
 * @author Tobias Pietzsch
 */
public abstract class KDTreeNode< T > implements RealLocalizable, Sampler< T >
{
	/**
	 * number of dimensions of the space (that is, k).
	 */
	protected final int n;

	/**
	 * coordinates of the node.
	 */
	protected final double[] pos;

	/**
	 * dimension along which this node divides the space.
	 */
	protected final int splitDimension;

	/**
	 * Left child of this node. All nodes x in the left subtree have
	 * {@code x.pos[splitDimension] <= this.pos[splitDimension]}.
	 */
	public final KDTreeNode< T > left;

	/**
	 * Right child of this node. All nodes x in the right subtree have
	 * {@code x.pos[splitDimension] >= this.pos[splitDimension]}.
	 */
	public final KDTreeNode< T > right;

	/**
	 * @param position
	 *            coordinates of this node
	 * @param dimension
	 *            dimension along which this node divides the space
	 * @param left
	 *            left child node
	 * @param right
	 *            right child node
	 */
	public KDTreeNode( final RealLocalizable position, final int dimension, final KDTreeNode< T > left, final KDTreeNode< T > right )
	{
		this.n = position.numDimensions();
		this.pos = new double[ n ];
		position.localize( pos );
		this.splitDimension = dimension;
		this.left = left;
		this.right = right;
	}

	protected KDTreeNode( final KDTreeNode< T > node )
	{
		this.n = node.n;
		this.pos = node.pos.clone();
		this.splitDimension = node.splitDimension;
		this.left = node.left;
		this.right = node.right;
	}

	/**
	 * Get the dimension along which this node divides the space.
	 *
	 * @return splitting dimension.
	 */
	public final int getSplitDimension()
	{
		return splitDimension;
	}

	/**
	 * Get the position along {@link KDTreeNode#getSplitDimension()} where this
	 * node divides the space.
	 *
	 * @return splitting position.
	 */
	public final double getSplitCoordinate()
	{
		return pos[ splitDimension ];
	}

	@Override
	public final int numDimensions()
	{
		return n;
	}

	@Override
	public final void localize( final float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = ( float ) pos[ d ];
	}

	@Override
	public final void localize( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
	}

	@Override
	public final float getFloatPosition( final int d )
	{
		return ( float ) pos[ d ];
	}

	@Override
	public final double getDoublePosition( final int d )
	{
		return pos[ d ];
	}

	@Override
	public abstract KDTreeNode< T > copy();

	/**
	 * Compute the squared distance from p to this node.
	 */
	public final float squDistanceTo( final float[] p )
	{
		float sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			sum += ( pos[ d ] - p[ d ] ) * ( pos[ d ] - p[ d ] );
		}
		return sum;
	}

	/**
	 * Compute the squared distance from p to this node.
	 */
	public final double squDistanceTo( final double[] p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			sum += ( pos[ d ] - p[ d ] ) * ( pos[ d ] - p[ d ] );
		}
		return sum;
	}

	/**
	 * Compute the squared distance from p to this node.
	 */
	public final double squDistanceTo( final RealLocalizable p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			sum += ( pos[ d ] - p.getDoublePosition( d ) ) * ( pos[ d ] - p.getDoublePosition( d ) );
		}
		return sum;
	}
}
