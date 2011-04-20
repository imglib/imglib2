package net.imglib2.algorithm.roi;

import java.util.Arrays;

import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

/**
 * OrderStatistics provides the framework to create Order Statistic operations.  It operates
 * by cursing over the input {@link Image}, and collecting a sorted list of the pixels "covered" by
 * a {@link StructuringElement}.  This list is made available to children classes, which are
 * responsible for setting the pixel value at the current position in the output Image.
 * 
 * @author Larry Lindsey
 *
 * @param <T> The input- and output-{@link Image} type.
 */
public abstract class OrderStatistics<T extends RealType<T>> extends ROIAlgorithm<T, T> {
	//Member variables
	
	private final double[] statArray;
	public long statsTime;
	
	//Member functions
	
	public OrderStatistics(final Img<T> imageIn, long[][] path) {
        this(imageIn, path, new OutOfBoundsConstantValueFactory<T,Img<T>>(imageIn.firstElement().createVariable()));
    }
	
	public OrderStatistics(final Img<T> imageIn, long[][] path,
                           OutOfBoundsFactory<T,Img<T>> oobFactory) {
		super(imageIn.factory(), imageIn.firstElement().createVariable(),
		        new StructuringElementCursor<T>(
		                imageIn.randomAccess(oobFactory), path));
		statsTime = 0;
		statArray = new double[path.length];
	}

	public void collectStats(final StructuringElementCursor <T> cursor)
    {
        int i = 0;
        while(cursor.hasNext())
        {
            cursor.fwd();
            statArray[i++] = cursor.getType().getRealDouble();
        }

        Arrays.sort(statArray);
    }
	
	protected double[] getArray()
	{
	    return statArray;
	}
			
	@Override
	protected boolean patchOperation(
            final StructuringElementCursor<T> cursor,
            final T outputType) {
	    long p = System.currentTimeMillis();
		collectStats(cursor);
		statsTime += System.currentTimeMillis() - p;
		statsOp(outputType);
		
		return true;
	}
		
	/**
	 * Perform the order statistic operation, then set the value of the given type.
	 * @param outputType the type whose value is to be set.  Belongs to the output Image.
	 */
	protected abstract void statsOp(final T outputType);
	
}
