/**
 * 
 */
package mpicbg.imglib.roi;

import mpicbg.imglib.RealRandomAccessible;
import mpicbg.imglib.type.logic.BitType;

/**
 * @author leek
 *
 * A RegionOfInterest defines a set of points in a space.
 * The "get" value from BitType will tell you whether a point is in or out.
 */
public interface RegionOfInterest extends RealRandomAccessible<BitType> {
}
