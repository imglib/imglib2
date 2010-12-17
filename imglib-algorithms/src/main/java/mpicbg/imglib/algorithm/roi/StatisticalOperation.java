package mpicbg.imglib.algorithm.roi;

import java.util.Collections;
import java.util.LinkedList;

import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
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
	
	private final LinkedList<T> statList;
	
	//Member functions
	
	public StatisticalOperation(final Image<T> imageIn,
	        final StructuringElementCursor<T> inStrelCursor) {
		super(new ImageFactory<T>(imageIn.createType(), imageIn.getContainerFactory()),
		        inStrelCursor);

		statList = new LinkedList<T>();
	}

	public void collectStats(final StructuringElementCursor <T> cursor)
    {
        statList.clear();
        
        while(cursor.hasNext())
        {
            T type;
            cursor.fwd();
            type = cursor.getType().createVariable();
            type.set(cursor.getType());
            statList.add(type);
        }
        
        Collections.sort(statList);
    }
	
	protected LinkedList<T> getList()
	{
		return statList;
	}
		
	@Override
	protected boolean patchOperation(
            final StructuringElementCursor<T> cursor,
            final T outputType) {
		collectStats(cursor);
		
		statsOp(outputType);
		
		return true;
	}

		
	/**
	 * Perform the order statistic operation, then set the value of the given type.
	 * @param outputType the type whose value is to be set.  Belongs to the output Image.
	 */
	protected abstract void statsOp(final T outputType);
	
}
