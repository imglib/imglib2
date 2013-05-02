package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;

public interface Neighborhood< T > extends IterableInterval< T >, Localizable
{
	public Interval getStructuringElementBoundingBox();
}
