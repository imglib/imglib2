package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.RealType;

/**
 * Dilation morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MorphDilate<T extends RealType<T> & Comparable<T>> extends ROIAlgorithm<T, T> {

    public MorphDilate(final Image<T> imageIn,
            int[] size, OutOfBoundsStrategyFactory<T> oobFactory)
    {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
    }
    
    public MorphDilate(final Image<T> imageIn,
            int[][] path, OutOfBoundsStrategyFactory<T> oobFactory)
    {
        super(imageIn.getImageFactory(),
                new StructuringElementCursor<T>(
                        imageIn.createLocalizableByDimCursor(oobFactory),
                        path)
        );
        setName(imageIn.getName() + " Dilated");
    }
    
    public MorphDilate(final Image<T> imageIn,
            int[] size) {
        this(imageIn, StructuringElementCursor.sizeToPath(size));       
    }
    
    public MorphDilate(final Image<T> imageIn,
            int[][] path)
    {
        this(imageIn, path, new OutOfBoundsStrategyValueFactory<T>());
    }

    @Override
    protected boolean patchOperation(StructuringElementCursor<T> strelCursor,
                                     T outputType) {

        if (strelCursor.hasNext())
        {
            strelCursor.fwd();
            outputType.set(strelCursor.getType());
        }
        else
        {
            return false;
        }

        while (strelCursor.hasNext())
        {
            strelCursor.fwd();
            if(strelCursor.getType().compareTo(outputType) > 0)
            {
                outputType.set(strelCursor.getType());
            }
        }

        return true;
    }
}
