/**
 * 
 */
package mpicbg.imglib.roi;

import mpicbg.imglib.IterableInterval;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.type.Type;

/**
 * @author leek
 *
 * An IterableRegionOfInterest provides cursors that allow
 * a user to iterate over the pixels in the region of interest.
 * 
 */
public interface IterableRegionOfInterest extends RegionOfInterest {
	/**
	 * Given a sampler in the ROI's space, provide an iterable that can
	 * make iterators over the space, sampling pixels from the sampler.
	 * 
	 * You can then use these cursors to get at all of the pixels in the ROI.
	 * 
	 * @param <T>
	 * @param src - a RandomAccessible that can generate RandomAccess objects.
	 *              The cursors that IterableInterval generates will use "src"
	 *              to generate a RandomAccess to sample the space.
	 * @return
	 */
	public <T extends Type<T>> IterableInterval<T> getIterableIntervalOverROI(RandomAccessible<T> src);

}
