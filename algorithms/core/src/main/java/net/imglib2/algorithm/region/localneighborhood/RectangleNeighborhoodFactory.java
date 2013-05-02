package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;

public interface RectangleNeighborhoodFactory< T >
{
	public Neighborhood< T > create( final long[] position, final long[] currentMin, final long[] currentMax, final Interval span, final RandomAccess< T > sourceRandomAccess );
}
