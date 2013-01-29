
package net.imglib2.algorithm.roi;

import net.imglib2.algorithm.histogram.Histogram;
import net.imglib2.algorithm.histogram.HistogramBinMapper;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public abstract class HistogramStatistics<T extends RealType<T>> extends OrderStatistics<T>
{

    private final int[] statArray;
    private final double[] doubleStatArray;
    private final Histogram<T> histogram;

    public HistogramStatistics(final Img<T> imageIn, long[][] path, HistogramBinMapper<T> binMapper) {
        this(imageIn, path, new OutOfBoundsConstantValueFactory<T,Img<T>>(imageIn.firstElement().createVariable()),
                binMapper);
    }

	public HistogramStatistics(final Img<T> imageIn, long[][] path,
                           OutOfBoundsFactory<T,Img<T>> oobFactory,
                           HistogramBinMapper<T> binMapper) {
		super(imageIn, path, oobFactory);
		statsTime = 0;
        histogram = new Histogram<T>(binMapper, super.getStrelCursor());
        statArray = histogram.getHistogram();
        doubleStatArray = new double[statArray.length];
	}

	public void collectStats(final StructuringElementCursor <T> cursor)
    {
        histogram.reset();
        histogram.process();
    }

    protected int[] getIntArray()
    {
        return statArray;
    }

	protected double[] getArray()
	{
        for (int i = 0; i < statArray.length; ++i)
        {
            doubleStatArray[i] = statArray[i];
        }
	    return doubleStatArray;
	}

}
