package mpicbg.imglib.algorithm.roi;


import mpicbg.imglib.algorithm.histogram.Histogram;
import mpicbg.imglib.algorithm.histogram.HistogramBinMapper;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.RealType;

public abstract class HistogramStatistics<T extends RealType<T>> extends OrderStatistics<T>
{

    private final int[] statArray;
    private final double[] doubleStatArray;
    private final Histogram<T> histogram;

    public HistogramStatistics(final Image<T> imageIn, int[][] path, HistogramBinMapper<T> binMapper) {
        this(imageIn, path, new OutOfBoundsStrategyValueFactory<T>(),
                binMapper);
    }

	public HistogramStatistics(final Image<T> imageIn, int[][] path,
                           OutOfBoundsStrategyFactory<T> oobFactory,
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
