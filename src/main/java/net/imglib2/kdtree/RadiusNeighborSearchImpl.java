package net.imglib2.kdtree;

import java.util.Arrays;
import net.imglib2.RealLocalizable;

import static net.imglib2.kdtree.KDTreeUtils.quicksort;
import static net.imglib2.kdtree.KDTreeUtils.reorder;

/**
 * Radius neighbor search on {@link KDTreeImpl}.
 * Results are node indices.
 */
public class RadiusNeighborSearchImpl
{
	private final KDTreeImpl tree;
	private final int numDimensions;
	private final int numPoints;
	private final double[] pos;
	private final double[] axisDiffs;
	private final int[] awayChilds;
	private final Neighbors neighbors;

	public RadiusNeighborSearchImpl( final KDTreeImpl tree )
	{
		this.tree = tree;
		numDimensions = tree.numDimensions();
		numPoints = tree.size();
		pos = new double[ numDimensions ];
		final int depth = tree.depth();
		axisDiffs = new double[ depth + 1 ];
		awayChilds = new int[ depth + 1 ];
		neighbors = new Neighbors();
	}

	public void search( final RealLocalizable p, final double radius, final boolean sortResults )
	{
		assert radius >= 0;
		final double squRadius = radius * radius;
		p.localize( pos );
		neighbors.clear();
		int current = tree.root();
		int depth = 0;
		while ( true )
		{
			final double squDistance = tree.squDistance( current, pos );
			if ( squDistance < squRadius )
			{
				neighbors.add( squDistance, current );
			}

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
				while ( awayChilds[ depth ] >= numPoints || axisDiffs[ depth ] > squRadius )
				{
					if ( --depth == 0 )
					{
						if ( sortResults )
						{
							neighbors.sort();
						}
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

	public int numNeighbors()
	{
		return neighbors.size;
	}

	public int bestIndex( final int i )
	{
		return neighbors.indices[ i ];
	}

	public double bestSquDistance( final int i )
	{
		return neighbors.distances[ i ];
	}

	public RadiusNeighborSearchImpl copy()
	{
		final RadiusNeighborSearchImpl copy = new RadiusNeighborSearchImpl( tree );
		System.arraycopy( pos, 0, copy.pos, 0, pos.length );
		copy.neighbors.makeCopyOf( neighbors );
		return copy;
	}

	static class Neighbors
	{
		double[] distances;
		int[] indices;
		int size;

		Neighbors()
		{
			final int capacity = 10;
			distances = new double[ capacity ];
			indices = new int[ capacity ];
		}

		void clear()
		{
			size = 0;
		}

		void add( final double distance, final int index )
		{
			if ( distances.length <= size )
			{
				// reallocate
				final int newLength = distances.length * 2;
				distances = Arrays.copyOf( distances, newLength );
				indices = Arrays.copyOf( indices, newLength );
			}
			distances[ size ] = distance;
			indices[ size ] = index;
			++size;
		}

		void sort()
		{
			final int[] order = new int[ size ];
			Arrays.setAll( order, i -> i );
			quicksort( 0, size - 1, distances, order );
			System.arraycopy( reorder( distances, order ), 0, distances, 0, size );
			System.arraycopy( reorder( indices, order ), 0, indices, 0, size );
		}

		void makeCopyOf( final Neighbors other )
		{
			distances = other.distances.clone();
			indices = other.indices.clone();
			size = other.size;
		}
	}
}
