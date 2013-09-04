package net.imglib2.algorithm.morphology;

import net.imglib2.EuclideanSpace;
import net.imglib2.IterableInterval;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.Util;

class MorphologyUtils
{

	static final Neighborhood< BitType > getNeighborhood( final Shape shape, final EuclideanSpace space )
	{
		final int numDims = space.numDimensions();
		final long[] dimensions = Util.getArrayFromValue( 1l, numDims );
		final ArrayImg< BitType, BitArray > img = ArrayImgs.bits( dimensions );
		final IterableInterval< Neighborhood< BitType >> neighborhoods = shape.neighborhoods( img );
		final Neighborhood< BitType > neighborhood = neighborhoods.cursor().next();
		return neighborhood;
	}
}
