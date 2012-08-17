package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccessibleInterval;

public interface Neighborhoods
{
	public < T > RandomAccessibleInterval< Neighborhood< T > > neighborhoods( final RandomAccessibleInterval< T > source );

	public < T > RandomAccessibleInterval< Neighborhood< T > > neighborhoodsSingle( final RandomAccessibleInterval< T > source );
}
