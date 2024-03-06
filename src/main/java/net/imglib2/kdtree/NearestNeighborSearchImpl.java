package net.imglib2.kdtree;

import net.imglib2.RealLocalizable;

/**
 * Nearest-neighbor search on {@link KDTreeImpl}.
 * Results are node indices.
 */
public class NearestNeighborSearchImpl
{
	private final KDTreeImpl tree;
	private final int numDimensions;
	private final int numPoints;
	private final double[] pos;
	private int bestIndex;
	private double bestSquDistance;
	private final double[] axisDiffs;
	private final int[] awayChilds;

	public NearestNeighborSearchImpl( final KDTreeImpl tree )
	{
		this.tree = tree;
		numDimensions = tree.numDimensions();
		numPoints = tree.size();
		pos = new double[ numDimensions ];
		bestIndex = -1;
		bestSquDistance = Double.POSITIVE_INFINITY;
		final int depth = tree.depth();
		axisDiffs = new double[ depth + 1 ];
		awayChilds = new int[ depth + 1 ];
	}

	public void search( final RealLocalizable p )
	{
		p.localize( pos );
		int current = tree.root();
		int depth = 0;
		bestSquDistance = ( bestIndex >= 0 ) ? tree.squDistance( bestIndex, pos ) : Double.POSITIVE_INFINITY;
		while ( true )
		{
			final double squDistance = tree.squDistance( current, pos );
			if ( squDistance < bestSquDistance )
			{
				bestSquDistance = squDistance;
				bestIndex = current;
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
				while ( awayChilds[ depth ] >= numPoints || axisDiffs[ depth ] > bestSquDistance )
				{
					if ( --depth == 0 )
					{
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

	public int bestIndex()
	{
		return bestIndex;
	}

	public double bestSquDistance()
	{
		return bestSquDistance;
	}

	public NearestNeighborSearchImpl copy()
	{
		final NearestNeighborSearchImpl copy = new NearestNeighborSearchImpl( tree );
		System.arraycopy( pos, 0, copy.pos, 0, pos.length );
		copy.bestIndex = bestIndex;
		copy.bestSquDistance = bestSquDistance;
		return copy;
	}
}
