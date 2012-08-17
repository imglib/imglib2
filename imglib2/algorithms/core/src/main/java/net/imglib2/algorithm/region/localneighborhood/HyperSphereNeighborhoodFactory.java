package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccess;

/**
 * INCOMPLETE !!!
 *
 * TODO
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface HyperSphereNeighborhoodFactory< T >
{
	public Neighborhood< T > create( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess );
}
