package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.algorithm.histogram.HistogramBinMapper;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;

/**
 * Created by IntelliJ IDEA.
 * User: larry
 * Date: 2/7/11
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MedianHistogramFilter<T extends RealType<T>> extends HistogramStatistics<T>
{

    private final HistogramBinMapper<T> mapper;

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
	}

	@Override
	protected void statsOp(final T outputType) {
	    final int n = super.getIntArray().length;
        final int t = (n + 1) / 2;
        int count = 0, i;
        for (i = 0; i < n && count < t; ++i)
        {
            count += super.getIntArray()[i];
        }
		outputType.set(mapper.invMap(i));
	}

}
