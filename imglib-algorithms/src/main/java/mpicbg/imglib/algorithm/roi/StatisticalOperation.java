package mpicbg.imglib.algorithm.roi;

import java.util.Arrays;

import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.RealType;

/**
 * StatisticalOperation provides the framework to create Order Statistic operations.  It operates
 * by cursing over the input {@link Image}, and collecting a sorted list of the pixels "covered" by
 * a {@link StructuringElement}.  This list is made available to children classes, which are
 * responsible for setting the pixel value at the current position in the output Image.
 * 
 * @author Larry Lindsey
 *
 * @param <T> The input- and output-{@link Image} type.
 */
public abstract class StatisticalOperation<T extends RealType<T>> extends ROIAlgorithm<T, T> {
	//Member variables
	
	private final double[] statArray;
	public long statsTime;
	
	//Member functions
	
	public StatisticalOperation(final Image<T> imageIn, int[][] path) {
        this(imageIn, path, new OutOfBoundsStrategyValueFactory<T>());
    }
	
	public StatisticalOperation(final Image<T> imageIn, int[][] path, 
	        OutOfBoundsStrategyFactory<T> oobFactory) {
		super(new ImageFactory<T>(imageIn.createType(), imageIn.getContainerFactory()),
		        new StructuringElementCursor<T>(
		                imageIn.createLocalizableByDimCursor(oobFactory), path));
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
