/**
 * 
 */
package net.imglib2.roi;

import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.type.logic.BitType;

/**
 * A RegionOfInterest defines a set of points in a space.
 * The "get" value from BitType will tell you whether a point is in or out.
 *
 * @author Lee Kamentsky
 */
public interface RegionOfInterest extends RealRandomAccessibleRealInterval<BitType> {
}
