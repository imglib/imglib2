package mpicbg.imglib.algorithm.roi;


import mpicbg.imglib.algorithm.histogram.Histogram;
import mpicbg.imglib.algorithm.histogram.HistogramBinMapper;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.outofbounds.OutOfBoundsConstantValueFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.numeric.RealType;

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