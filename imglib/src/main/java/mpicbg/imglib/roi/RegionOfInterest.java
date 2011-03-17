/**
 * 
 */
package mpicbg.imglib.roi;

import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.type.logic.BitType;

/**
 * @author leek
 *
 * A RegionOfInterest defines a set of points in a space.
 * The "get" value will tell you whether a point is in the ROI.
 */
public interface RegionOfInterest extends RandomAccess<BitType> {

}
