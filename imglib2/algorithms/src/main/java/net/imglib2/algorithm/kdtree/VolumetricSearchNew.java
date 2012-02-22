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
import net.imglib2.RealPoint;
import net.imglib2.collection.KDTree;
import net.imglib2.collection.KDTreeNode;

public class VolumetricSearchNew< I extends RealInterval > implements RandomAccessible< List< I >>
{
	final int numDimensions;

	final KDTree< I > kdtree;

	public VolumetricSearchNew( final List< I > intervals )
	{
		if ( intervals.isEmpty() )
		{
			numDimensions = 0;
		}
		else
		{
			numDimensions = intervals.get( 0 ).numDimensions();
		}
		kdtree = new KDTree< I >( intervals, makePoints( intervals ) );
	}

	private ArrayList< RealPoint > makePoints( final List< I > intervals )
	{
		final ArrayList< RealPoint > points = new ArrayList< RealPoint >( intervals.size() );
		final double[] position = new double[ 2 * numDimensions ];
		for ( final I interval : intervals )
		{
			for ( int d = 0; d < numDimensions; ++d )
			{
				position[ d ] = interval.realMin( d );
				position[ d + numDimensions ] = interval.realMax( d );
			}
			points.add( new RealPoint( position ) );
		}
		return points;
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

		final Stack< KDTreeNode< I >> toDo = new Stack< KDTreeNode< I >>();
		toDo.push( kdtree.getRoot() );
		while ( toDo.size() > 0 )
		{
			final KDTreeNode< I > node = toDo.pop();
			final int k = node.getSplitDimension();

			// check this interval
			final I interval = node.get();
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
