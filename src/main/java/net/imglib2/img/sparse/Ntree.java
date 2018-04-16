/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.sparse;

/**
 * N-dimensional equivalent of a quad/oct-tree.
 * 
 * @author Tobias Pietzsch
 */
public final class Ntree< T extends Comparable< T >>
{

	public static final class NtreeNode< T >
	{

		private T value;

		private final NtreeNode< T > parent;

		private NtreeNode< T >[] children;

		public NtreeNode( final NtreeNode< T > parent, final T value )
		{
			this.parent = parent;
			this.value = value;
		}

		boolean hasChildren()
		{
			return children != null;
		}

		public T getValue()
		{
			return value;
		}

		public void setValue( final T value )
		{
			this.value = value;
		}

		public NtreeNode< T >[] getChildren()
		{
			return children;
		}

		public void setChildren( final NtreeNode< T >[] children )
		{
			this.children = children;
		}

	}

	/**
	 * number of dimensions.
	 */
	final int n;

	/**
	 * maximum depth of the tree.
	 */
	final int numTreeLevels;

	/**
	 * how many children (if any) each node has.
	 */
	final int numChildren;

	/**
	 * Root of the tree
	 */
	NtreeNode< T > root;

	/**
	 * dimensions of tree
	 */
	final long[] dimensions;

	/**
	 * Create a ntree structure capable of representing an array of the given
	 * dimensions. Initially, the tree contains only a root node and represents
	 * an array of uniform values.
	 * 
	 * @param dimensions
	 *            of the array
	 * @param value
	 *            uniform value of all pixels in the array
	 */
	public Ntree( final long[] dimensions, final T value )
	{
		this.n = dimensions.length;

		this.dimensions = dimensions.clone();

		// set the maximum number of levels in the ntree.
		// This is how many times to split the maximum dimension
		// in half to arrive at a single pixel
		long maxdim = 0;
		for ( int d = 0; d < n; ++d )
			maxdim = Math.max( maxdim, dimensions[ d ] );
		this.numTreeLevels = ( int ) Math.ceil( Math.log( maxdim ) / Math.log( 2 ) ) + 1;

		this.numChildren = 1 << n;

		this.root = new NtreeNode<>( null, value );
	}

	/**
	 * helper method for the copy constructor {@link #Ntree(Ntree)} to create a
	 * deep copy of the tree.
	 */
	@SuppressWarnings( "unchecked" )
	private NtreeNode< T > copyRecursively( final NtreeNode< T > node, final NtreeNode< T > newParent )
	{
		final NtreeNode< T > copy = new NtreeNode<>( newParent, node.getValue() );
		if ( node.hasChildren() )
		{
			copy.children = new NtreeNode[ numChildren ];
			for ( int i = 0; i < numChildren; ++i )
			{
				copy.children[ i ] = copyRecursively( node.children[ i ], copy );
			}
		}
		return copy;
	}

	/**
	 * Copy constructor. Create a deep copy of ntree.
	 */
	Ntree( final Ntree< T > ntree )
	{
		dimensions = ntree.dimensions;
		n = ntree.n;
		numTreeLevels = ntree.numTreeLevels;
		numChildren = ntree.numChildren;
		root = copyRecursively( ntree.root, null );
	}

	/**
	 * Get the lowest-level node containing position. Note that position is not
	 * necessarily the only pixel inside the node. So use this for read-access
	 * to pixel values only.
	 * 
	 * @param position
	 *            a position inside the image.
	 * @return the lowest-level node containing position.
	 */
	synchronized NtreeNode< T > getNode( final long[] position )
	{
		NtreeNode< T > current = root;
		for ( int l = numTreeLevels - 2; l >= 0; --l )
		{
			if ( !current.hasChildren() )
				break;

			final long bitmask = 1 << l;
			int childindex = 0;
			for ( int d = 0; d < n; ++d )
				if ( ( position[ d ] & bitmask ) != 0 )
					childindex |= 1 << d;
			current = current.children[ childindex ];
		}
		return current;
	}

	/**
	 * Create a node containing only position (if it does not exist already).
	 * This may insert nodes at several levels in the tree.
	 * 
	 * @param position
	 *            a position inside the image.
	 * @return node containing exactly position.
	 */
	@SuppressWarnings( "unchecked" )
	synchronized NtreeNode< T > createNode( final long[] position )
	{
		NtreeNode< T > current = root;
		for ( int l = numTreeLevels - 2; l >= 0; --l )
		{
			if ( !current.hasChildren() )
			{
				current.children = new NtreeNode[ numChildren ];
				for ( int i = 0; i < numChildren; ++i )
					current.children[ i ] = new NtreeNode<>( current, current.getValue() );
			}

			final long bitmask = 1 << l;
			int childindex = 0;
			for ( int d = 0; d < n; ++d )
				if ( ( position[ d ] & bitmask ) != 0 )
					childindex |= 1 << d;
			current = current.children[ childindex ];
		}
		return current;
	}

	/**
	 * Set the value at position and get the lowest-level node containing
	 * position. Note that position is not necessarily the only pixel inside the
	 * node, if the value matches neighboring values. If necessary, new nodes
	 * will be created. If possible, nodes will be merged.
	 * 
	 * @param position
	 *            a position inside the image.
	 * @param value
	 *            value to store at position.
	 * @return node containing position.
	 */
	@SuppressWarnings( "unchecked" )
	synchronized NtreeNode< T > createNodeWithValue( final long[] position, final T value )
	{
		NtreeNode< T > current = root;
		for ( int l = numTreeLevels - 2; l >= 0; --l )
		{
			if ( !current.hasChildren() )
			{
				if ( current.getValue().compareTo( value ) == 0 )
					return current;

				current.children = new NtreeNode[ numChildren ];
				for ( int i = 0; i < numChildren; ++i )
					current.children[ i ] = new NtreeNode<>( current, current.getValue() );
			}

			final long bitmask = 1 << l;
			int childindex = 0;
			for ( int d = 0; d < n; ++d )
				if ( ( position[ d ] & bitmask ) != 0 )
					childindex |= 1 << d;
			current = current.children[ childindex ];
		}
		if ( current.getValue().compareTo( value ) == 0 )
			return current;
		current.setValue( value );
		return mergeUpwards( current );
	}

	/**
	 * If all the children of our parent have the same value remove them all.
	 * Call recursively for parent.
	 * 
	 * @param node
	 *            the starting node (whose parents should be tested
	 *            recursively).
	 * @return node that the starting node was ultimately merged into.
	 */
	NtreeNode< T > mergeUpwards( final NtreeNode< T > node )
	{
		final NtreeNode< T > parent = node.parent;
		if ( parent == null )
			return node;
		final NtreeNode< T > child0 = parent.children[ 0 ];
		if ( child0.hasChildren() )
			return node;
		for ( int i = 1; i < numChildren; ++i )
		{
			final NtreeNode< T > child = parent.children[ i ];
			if ( child.hasChildren() || child0.getValue().compareTo( child.getValue() ) != 0 )
				return node;
		}
		parent.setValue( child0.getValue() );
		parent.children = null;
		return mergeUpwards( parent );
	}

	/**
	 * Returns the root node of the ntree
	 * 
	 * @return root node
	 */
	public NtreeNode< T > getRootNode()
	{
		return root;
	}
}
