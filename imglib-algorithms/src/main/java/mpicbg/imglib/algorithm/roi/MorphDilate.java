package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;

/**
 * Dilation morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MorphDilate<T extends RealType<T>> extends StatisticalOperation<T> {

    public MorphDilate(final Image<T> imageIn,
            int[] size, OutOfBoundsStrategyFactory<T> oobFactory) {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
    }
    
    public MorphDilate(final Image<T> imageIn,
            int[][] path, OutOfBoundsStrategyFactory<T> oobFactory)
    {
        super(imageIn, path, oobFactory);
        setName(imageIn.getName() + " Dilated");
    }
    
    public MorphDilate(final Image<T> imageIn,
            int[] size) {
        this(imageIn, StructuringElementCursor.sizeToPath(size));       
    }
    
    public MorphDilate(final Image<T> imageIn,
            int[][] path)
    {
        super(imageIn, path);
        setName(imageIn.getName() + " Dilated");
    }
	
	
	@Override
	protected void statsOp(final T outputType) {
		outputType.setReal(getArray()[getArray().length - 1]);
	}

}
