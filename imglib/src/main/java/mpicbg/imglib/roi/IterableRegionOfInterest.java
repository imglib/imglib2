/**
 * 
 */
package mpicbg.imglib.roi;

import mpicbg.imglib.Iterator;

/**
 * @author leek
 *
 * An IterableRegionOfInterest provides cursors that allow
 * a user to iterate over the pixels in the region of interest.
 * 
 */
public interface IterableRegionOfInterest extends RegionOfInterest, Iterator {

}
