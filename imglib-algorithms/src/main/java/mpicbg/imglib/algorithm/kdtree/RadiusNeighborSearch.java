package mpicbg.imglib.algorithm.kdtree;

import mpicbg.imglib.algorithm.kdtree.node.Leaf;

public class RadiusNeighborSearch<T extends Leaf<T>>
{
	final protected KDTree<T> kdTree;

	public RadiusNeighborSearch( final KDTree<T> kdTree ) 
	{
		this.kdTree = kdTree;
	}

	public KDTree<T> getKDTree() { return kdTree; } 

	public T[] findNeighbors( final T point, final double radius )
	{
		return null;
	}
}
