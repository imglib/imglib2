package net.imglib2.kdtree;

import net.imglib2.RealLocalizable;


public class NearestNeighborSearchOnKDTree< T extends RealLocalizable > 
{
	protected KDTree< T > tree;
	
	protected final int n;
	protected final double[] pos;

	protected Node< T > bestPoint;
	protected double bestSquDistance;
	
	public NearestNeighborSearchOnKDTree( KDTree< T > tree )
	{
		n = tree.numDimensions();
		pos = new double[ n ];
		this.tree = tree;
	}
	
	public Node< T > search( RealLocalizable p )
	{
		p.localize( pos );
		bestSquDistance = Double.MAX_VALUE;
		searchNode( tree.getRoot() );
		return bestPoint;
	}
	
	protected void searchNode( Node< T > current )
	{
		// consider the current node
		final double distance = current.squDistanceTo( pos );
		if ( distance < bestSquDistance )
		{
			bestSquDistance = distance;
			bestPoint = current;
		}
		
		final double axisDiff = pos[ current.getSplitDimension() ] - current.getSplitCoordinate();
		final double axisSquDistance = axisDiff * axisDiff;
		final boolean leftIsNearBranch = axisDiff < 0;

		// search the near branch
		final Node< T > nearChild = leftIsNearBranch ? current.left : current.right;
		final Node< T > awayChild = leftIsNearBranch ? current.right : current.left;
		if ( nearChild != null )
			searchNode( nearChild );

	    // search the away branch - maybe
		if ( ( axisSquDistance <= bestSquDistance ) && ( awayChild != null ) )
			searchNode( awayChild );
	}
}
