package net.imglib2.algorithm.roi;

import net.imglib2.algorithm.histogram.HistogramBinMapper;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

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

    public MedianHistogramFilter(final Img<T> imageIn,
            long[] size, OutOfBoundsFactory<T,Img<T>> oobFactory,
            HistogramBinMapper<T> binMapper)
    {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory, binMapper);
    }

    public MedianHistogramFilter(final Img<T> imageIn,
            long[][] path, OutOfBoundsFactory<T,Img<T>> oobFactory,
            HistogramBinMapper<T> binMapper)
    {
        super(imageIn, path, oobFactory, binMapper);
        setName(imageIn + " Median Histogram Filter");
        mapper = binMapper;
    }

	public MedianHistogramFilter(final Img<T> imageIn,
	        long[] size, HistogramBinMapper<T> binMapper) {
		this(imageIn, StructuringElementCursor.sizeToPath(size), binMapper);
	}

	public MedianHistogramFilter(final Img<T> imageIn,
	        long[][] path, HistogramBinMapper<T> binMapper)
	{
	    super(imageIn, path, binMapper);
	    setName(imageIn + " Median Histogram Filter");
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
