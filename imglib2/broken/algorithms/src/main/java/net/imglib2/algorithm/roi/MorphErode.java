
package net.imglib2.algorithm.roi;

import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

/**
 * Erosion morphological operation.
 * 
 * @param <T> {@link Image} type.
 * @author Larry Lindsey
 */
public class MorphErode<T extends RealType<T> & Comparable<T>> extends ROIAlgorithm<T,T> {
    public MorphErode(final Img<T> imageIn,
            long[] size, OutOfBoundsFactory<T,Img<T>> oobFactory) {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
    }
    
    public MorphErode(final Img<T> imageIn,
            long[][] path, OutOfBoundsFactory<T,Img<T>> oobFactory)
    {
        super(imageIn.factory(), imageIn.firstElement().createVariable(),
                new StructuringElementCursor<T>(
                        imageIn.randomAccess(oobFactory),
                        path)
        );
        setName(imageIn + " Eroded");
    }
    
    public MorphErode(final Img<T> imageIn,
            long[] size) {
        this(imageIn, StructuringElementCursor.sizeToPath(size));
    }
    
    public MorphErode(final Img<T> imageIn,
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
            if(strelCursor.getType().compareTo(outputType) < 0)
            {
                outputType.set(strelCursor.getType());
            }
        }

        return true;
    }

}
