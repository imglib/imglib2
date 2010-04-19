package mpicbg.imglib.algorithm.roi;

import java.util.LinkedList;

import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.ComplexType;
import mpicbg.imglib.type.logic.BitType;

public abstract class StatisticalOperation<T extends ComplexType<T>> extends ROIAlgorithm<T, T> {
	//Member classes
	
	/**
	 * Implements a strategy for populating the sorted list associated with this
	 * StatisticalOperation.
	 * 
	 * This could mean either re-populating and resorting this list on every iteration,
	 * which might be reasonably efficient for small structuring elements, or it could
	 * mean being smart about the boundaries, since we expect our cursor to move maybe
	 * only one position for every iteration.
	 * 
	 * @param <R> Image storage type.
	 */
	public interface StatisticsCollectionStrategy<R extends ComplexType<R>> 
	{
		public void collectStats(LinkedList<R> list, RegionOfInterestCursor<R> cursor, int[] pos);
	}
	
	/**
	 * Simple, dumb statistics collection implementation.  Resorts every time, hopefully in a
	 * O(n log(n)) manner, with respect to strel size.
	 * 
	 * @param <R> Image storage type.
	 */
	public class SimpleCollectionStrategy<R extends ComplexType<R>> 
		implements StatisticsCollectionStrategy<R>
	{
		private final LocalizableByDimCursor<BitType> strelCursor;
		
		public SimpleCollectionStrategy()
		{
			strelCursor = strel.createLocalizableByDimCursor();
		}
		
		public void collectStats(LinkedList<R> list, RegionOfInterestCursor<R> cursor, int[] pos)
		{
			list.clear();
			
			while(cursor.hasNext())
			{
				cursor.fwd();
				strelCursor.setPosition(cursor);
				
				if (strelCursor.getType().get())
				{
					R type = cursor.getType().clone();
					int i = 0;
					while(i < list.size() && type.compareTo(list.get(i)) > 0)
					{
						++i;
					}
					list.add(i, type.clone());							
				}
						
			}
		}
		
	}
	
	/*
	 * As of this writing, there are no other collection strategy classes written, as you can tell.
	 * 
	 * To be clear, here we're concerned with strel operations, ie, operations of a shaped element
	 * with respect to an input image.
	 * 
	 * Now, here's how I intend to implement a more efficient strel operation.  If you're reading
	 * this, maybe you'll get to it ahead of me ;).
	 * 
	 * First, the StructuringElement class will be updated so that it can produce cursors that
	 * iterate over its edges with respect to each dimension (two per dimension - one for positive
	 * motion, the other for negative).  This class will use those cursors in tandem with the ROI
	 * cursor to store image data from the region of interest, in the current state.  In the next
	 * state, the strel patch will have moved.  If it moves a distance of only one pixel, we can do
	 * the following:
	 * 
	 * One of the lists we stored in the previous state will contain exactly the values that need
	 * to be removed from the sorted list, so remove them.  This should be an O(n log(n)) operation
	 * in the number of edge values.  Next, grab the cursor from the strel that corresponds to the
	 * location of the pixels that must be added newly.  Iterate over those pixels, adding their
	 * values to the sorted list.  This should also be O(n log(n)) in the number of edge pixels.
	 * 
	 * Note that here, n is approximately the dth root of the n in the simple collection method,
	 * meaning we get a pretty nice speed-up when n is big enough.  In particular, n should be
	 * large with respect to d + 1, where d is the dimensionality.
	 */
	
	//Member variables
	
	private final StructuringElement strel;
	private final LinkedList<T> statList;
	private final int[] lastPosition;
	private final LocalizableByDimCursor<T> outputCursor;
	private boolean init = false;
	private StatisticsCollectionStrategy<T> statsStrategy;

	
	//Member functions
	
	public StatisticalOperation(final Image<T> imageIn, final StructuringElement strel) {
		this(imageIn, strel, null);
	}
	
	public StatisticalOperation(final Image<T> imageIn, final StructuringElement inStrel,
			final OutOfBoundsStrategyFactory<T> inOutFactory) {
		super(imageIn.createType(), imageIn, inStrel.getDimensions(), inOutFactory);
		strel = inStrel;
		statList = new LinkedList<T>();
		lastPosition = new int[strel.getNumDimensions()];
		outputCursor = getOutputImage().createLocalizableByDimCursor();
		statsStrategy = new SimpleCollectionStrategy<T>();		
	}

	public void reset()
	{
		init = false;
	}

	public boolean isInit()
	{
		return init;
	}
	
	public StructuringElement getStrel()
	{
		return strel;
	}
	
	protected LinkedList<T> getList()
	{
		return statList;
	}
	
	public int[] getLastPosition()
	{
		return lastPosition;
	}
	
	@Override
	protected boolean patchOperation(final int[] position,
			final RegionOfInterestCursor<T> cursor) {
		statsStrategy.collectStats(statList, cursor, position);
		outputCursor.setPosition(position);
		
		statsOp(outputCursor);
		
		System.arraycopy(position, 0, lastPosition, 0, position.length);
		init = true;
		return true;
	}

	protected abstract void statsOp(LocalizableByDimCursor<T> cursor);
	
}
