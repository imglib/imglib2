/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.algorithm.kdtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.collection.KDTree;
import net.imglib2.collection.KDTreeNode;

/**
 * The volumetric search uses a K-D tree to search for all hyper-rectangular
 * nodes that contain a given point.
 * 
 * You can use this via the RandomAccessible<List<I>> interface: Get the
 * RandomAccess<List<I>> interface Localize it to your point get() performs the
 * search, returning the list.
 * 
 * 
 * @author Lee Kamentsky
 * @author Tobias Pietzsch
 */
public class VolumetricSearch< I extends RealInterval > implements RandomAccessible< List< I >>
{
	final int numDimensions;

	final KDTree< IntervalWrapper< I > > kdtree;

	public VolumetricSearch( final List< I > intervals )
	{
		if ( intervals.isEmpty() )
		{
			numDimensions = 0;
		}
		else
		{
			numDimensions = intervals.get( 0 ).numDimensions();
		}
		final ArrayList< IntervalWrapper< I > > wrappers = new ArrayList< IntervalWrapper< I > >( intervals.size() );
		for ( final I interval : intervals )
			wrappers.add( new IntervalWrapper< I >( interval ) );
		kdtree = new KDTree< IntervalWrapper< I > >( wrappers, wrappers );
	}

	private static class IntervalWrapper< I extends RealInterval > implements RealLocalizable
	{
		final I interval;

		final int n;

		public IntervalWrapper( final I interval )
		{
			this.interval = interval;
			this.n = interval.numDimensions();
		}

		public I get()
		{
			return interval;
		}

		@Override
		public int numDimensions()
		{
			return 2 * n;
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < n; ++d )
			{
				position[ d ] = ( float ) interval.realMin( d );
				position[ d + n ] = ( float ) interval.realMax( d );
			}
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < n; ++d )
			{
				position[ d ] = interval.realMin( d );
				position[ d + n ] = interval.realMax( d );
			}
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return ( float ) getDoublePosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return ( d < n ) ? interval.realMin( d ) : interval.realMax( d - n );
		}

	}

	/**
	 * Find all intervals that contain a given point
	 * 
	 * @param pt
	 *            the point in question
	 * @return list of all intervals containing the point.
	 */
	public List< I > find( final RealLocalizable pt )
	{
		final double[] position = new double[ numDimensions ];
		pt.localize( position );
		final LinkedList< I > list = new LinkedList< I >();
		if ( kdtree.getRoot() == null )
			return list;

		final Stack< KDTreeNode< IntervalWrapper< I > > > toDo = new Stack< KDTreeNode< IntervalWrapper< I > > >();
		toDo.push( kdtree.getRoot() );
		while ( toDo.size() > 0 )
		{
			final KDTreeNode< IntervalWrapper< I > > node = toDo.pop();
			final int k = node.getSplitDimension();

			// check this interval
			final I interval = node.get().get();
			boolean good = true;
			for ( int i = 0; i < numDimensions; i++ )
			{
				if ( ( position[ i ] < interval.realMin( i ) ) || ( position[ i ] > interval.realMax( i ) ) )
				{
					good = false;
					break;
				}
			}
			if ( good )
				list.add( interval );

			// possibly add children
			if ( k < numDimensions )
			{
				// The coordinate is a minimum.
				// If it is greater than the position, only take the left branch
				// which still could be lower.
				// Otherwise (coordinate is smaller/equal position, take the
				// right branch as well
				if ( node.left != null )
					toDo.push( node.left );
				if ( node.right != null && node.getSplitCoordinate() <= position[ k ] )
					toDo.push( node.right );
			}
			else
			{
				// The coordinate is a maximum.
				// If it is smaller than the position, only take the right
				// branch
				// which still could be higher.
				// Otherwise (coordinate is larger/equal position, take the left
				// branch as well
				if ( node.right != null )
					toDo.push( node.right );
				if ( node.left != null && node.getSplitCoordinate() >= position[ k - numDimensions ] )
					toDo.push( node.left );
			}
		}

		return list;
	}

	@Override
	public int numDimensions()
	{
		return numDimensions;
	}

	private class VolumetricSearchRandomAccess extends Point implements RandomAccess< List< I > >
	{
		VolumetricSearchRandomAccess()
		{
			super( numDimensions );
		}

		@Override
		public List< I > get()
		{
			return find( this );
		}

		@Override
		public VolumetricSearchRandomAccess copy()
		{
			final VolumetricSearchRandomAccess myCopy = new VolumetricSearchRandomAccess();
			myCopy.setPosition( this );
			return myCopy;
		}

		@Override
		public VolumetricSearchRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}

	@Override
	public RandomAccess< List< I > > randomAccess()
	{
		return new VolumetricSearchRandomAccess();
	}

	@Override
	public RandomAccess< List< I > > randomAccess( final Interval interval )
	{
		return randomAccess();
	}
}
