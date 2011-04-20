package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.img.Img;
import mpicbg.imglib.outofbounds.OutOfBoundsConstantValueFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.numeric.RealType;

/**
 * Dilation morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MorphDilate<T extends RealType<T> & Comparable<T>> extends ROIAlgorithm<T, T> {

    public MorphDilate(final Img<T> imageIn,
            long[] size, OutOfBoundsFactory<T,Img<T>> oobFactory)
    {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
    }
    
    public MorphDilate(final Img<T> imageIn,
            long[][] path, OutOfBoundsFactory<T,Img<T>> oobFactory)
    {
        super(imageIn.factory(), imageIn.firstElement().createVariable(),
                new StructuringElementCursor<T>(
                        imageIn.randomAccess(oobFactory),
                        path)
        );
        setName(imageIn + " Dilated");
    }
    
    public MorphDilate(final Img<T> imageIn,
            long[] size) {
        this(imageIn, StructuringElementCursor.sizeToPath(size));       
    }
    
    public MorphDilate(final Img<T> imageIn,
            long[][] path)
    {
        this(imageIn, path, new OutOfBoundsConstantValueFactory<T,Img<T>>(imageIn.firstElement().createVariable()));
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