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
package net.imglib2.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class TreeUtils
{
	/**
	 * Find all leaf nodes of a forest.
	 * 
	 * @param forest
	 *            the forest
	 * @return set of leaf nodes.
	 */
	public static < T extends TreeNode< T > > ArrayList< T > getLeafs( final Forest< T > forest )
	{
		final ArrayList< T > leafs = new ArrayList< T >();
		final ArrayDeque< T > nodes = new ArrayDeque< T >( forest.roots() );
		while ( !nodes.isEmpty() )
		{
			final T node = nodes.remove();
			final List< T > children = node.getChildren();
			if ( children.isEmpty() )
				leafs.add( node );
			else
				nodes.addAll( children );
		}
		return leafs;
	}

	public static interface Consumer< T >
	{
		void accept( T t );
	}

	/**
	 * Call {@link Consumer#accept(Object)} on op for every node in the forest.
	 * 
	 * @param forest
	 *            the forest
	 * @param op
	 *            the consumer
	 */
	public static < T extends TreeNode< T > > void forEach( final Forest< T > forest, final Consumer< T > op )
	{
		final ArrayDeque< T > nodes = new ArrayDeque< T >( forest.roots() );
		while ( !nodes.isEmpty() )
		{
			final T node = nodes.remove();
			op.accept( node );
			final List< T > children = node.getChildren();
			if ( !children.isEmpty() )
				nodes.addAll( children );
		}
	}
}
