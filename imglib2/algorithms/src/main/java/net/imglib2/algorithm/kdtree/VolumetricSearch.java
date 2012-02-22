/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Lee Kamentsky, Tobias Pietzsch
 */
package net.imglib2.algorithm.kdtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.imglib2.AbstractRandomAccess;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.collection.KDTree;
import net.imglib2.collection.KDTreeNode;


/**
 * @author Lee Kamentsky, Tobias Pietzsch
 *
 *The volumetric search uses a K-D tree to search for all hyper-rectangular nodes
 *that contain a given point.
 *
 *You can use this via the RandomAccessible<List<I>> interface:
 *   Get the RandomAccess<List<I>> interface
 *   Localize it to your point
 *   get() performs the search, returning the list.
 *
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
				// Otherwise (coordinate is smaller/equal position, take the right branch as well
				if ( node.left != null )
					toDo.push( node.left );
				if ( node.right != null && node.getSplitCoordinate() <= position[ k ] )
					toDo.push( node.right );
			}
			else
			{
				// The coordinate is a maximum.
				// If it is smaller than the position, only take the right branch
				// which still could be higher.
				// Otherwise (coordinate is larger/equal position, take the left branch as well
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

	@Override
	public AbstractRandomAccess< List< I >> randomAccess()
	{
		return new AbstractRandomAccess< List< I >>( numDimensions )
		{

			@Override
			public void fwd( final int d )
			{
				this.position[ d ]++;
			}

			@Override
			public void bck( final int d )
			{
				this.position[ d ]--;
			}

			@Override
			public void move( final long distance, final int d )
			{
				this.position[ d ] += distance;
			}

			@Override
			public void setPosition( final int[] position )
			{
				for ( int i = 0; i < numDimensions; i++ )
				{
					this.position[ i ] = position[ i ];
				}
			}

			@Override
			public void setPosition( final long[] position )
			{
				for ( int i = 0; i < numDimensions; i++ )
				{
					this.position[ i ] = position[ i ];
				}
			}

			@Override
			public void setPosition( final long position, final int d )
			{
				this.position[ d ] = position;
			}

			@Override
			public List< I > get()
			{
				return find( this );
			}

			@Override
			public AbstractRandomAccess< List< I >> copy()
			{
				final AbstractRandomAccess< List< I >> myCopy = randomAccess();
				myCopy.setPosition( this );
				return myCopy;
			}

			@Override
			public AbstractRandomAccess< List< I >> copyRandomAccess()
			{
				return copy();
			}
		};
	}

	@Override
	public RandomAccess< List< I >> randomAccess( final Interval interval )
	{
		return randomAccess();
	}
}
