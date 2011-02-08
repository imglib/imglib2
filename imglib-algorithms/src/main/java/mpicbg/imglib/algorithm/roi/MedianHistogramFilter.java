package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.algorithm.histogram.HistogramBinMapper;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;

public class MedianHistogramFilter<T extends RealType<T>> extends HistogramStatistics<T>
{

    private final HistogramBinMapper<T> mapper;
    private final int countThreshold;

    public MedianHistogramFilter(final Image<T> imageIn,
            int[] size, OutOfBoundsStrategyFactory<T> oobFactory,
            HistogramBinMapper<T> binMapper)
    {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory, binMapper);
    }

    public MedianHistogramFilter(final Image<T> imageIn,
            int[][] path, OutOfBoundsStrategyFactory<T> oobFactory,
            HistogramBinMapper<T> binMapper)
    {
        super(imageIn, path, oobFactory, binMapper);
        setName(imageIn.getName() + " Median Histogram Filter");
        mapper = binMapper;
        countThreshold = (1 + getStrelCursor().getPathLength()) / 2;
    }

	public MedianHistogramFilter(final Image<T> imageIn,
	        int[] size, HistogramBinMapper<T> binMapper) {
		this(imageIn, StructuringElementCursor.sizeToPath(size), binMapper);
	}

	public MedianHistogramFilter(final Image<T> imageIn,
	        int[][] path, HistogramBinMapper<T> binMapper)
	{
	    super(imageIn, path, binMapper);
	    setName(imageIn.getName() + " Median Histogram Filter");
        mapper = binMapper;
        countThreshold = (1 + getStrelCursor().getPathLength()) / 2;
	}

	@Override
	protected void statsOp(final T outputType) {
	    final int n = super.getIntArray().length;
        int count = 0, i;
        for (i = 0; i < n && count < countThreshold; ++i)
        {
            count += super.getIntArray()[i];
        }

        //System.out.println("n " + n + ", t " + countThreshold + ", count " + count + ", index " + i);
        //System.out.println("Setting " + mapper.invMap(i));

		outputType.set(mapper.invMap(i));
	}

}
