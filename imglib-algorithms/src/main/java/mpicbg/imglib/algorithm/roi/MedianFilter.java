package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;


/**
 * Median filter / morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MedianFilter<T extends RealType<T>> extends OrderStatistics<T> {

    public MedianFilter(final Image<T> imageIn,
            int[] size, OutOfBoundsStrategyFactory<T> oobFactory) {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
    }
    
    public MedianFilter(final Image<T> imageIn,
            int[][] path, OutOfBoundsStrategyFactory<T> oobFactory)
    {
        super(imageIn, path, oobFactory);
        setName(imageIn.getName() + " Median Filter");
    }
    
	public MedianFilter(final Image<T> imageIn,
	        int[] size) {
		this(imageIn, StructuringElementCursor.sizeToPath(size));		
	}
	
	public MedianFilter(final Image<T> imageIn,
	        int[][] path)
	{
	    super(imageIn, path);
	    setName(imageIn.getName() + " Median Filter");
	}

	@Override
	protected void statsOp(final T outputType) {		
	    int n = super.getArray().length;		
		outputType.setReal(super.getArray()[n / 2]);
	}

}
