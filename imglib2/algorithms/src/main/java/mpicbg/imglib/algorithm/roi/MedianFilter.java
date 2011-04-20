package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.img.Img;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.numeric.RealType;


/**
 * Median filter / morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MedianFilter<T extends RealType<T>> extends OrderStatistics<T> {

    public MedianFilter(final Img<T> imageIn,
            long[] size, OutOfBoundsFactory<T,Img<T>> oobFactory) {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
    }
    
    public MedianFilter(final Img<T> imageIn,
            long[][] path, OutOfBoundsFactory<T,Img<T>> oobFactory)
    {
        super(imageIn, path, oobFactory);
        setName(imageIn + " Median Filter");
    }
    
	public MedianFilter(final Img<T> imageIn,
	        long[] size) {
		this(imageIn, StructuringElementCursor.sizeToPath(size));		
	}
	
	public MedianFilter(final Img<T> imageIn,
	        long[][] path)
	{
	    super(imageIn, path);
	    setName(imageIn + " Median Filter");
	}

	@Override
	protected void statsOp(final T outputType) {		
	    int n = super.getArray().length;		
		outputType.setReal(super.getArray()[n / 2]);
	}

}