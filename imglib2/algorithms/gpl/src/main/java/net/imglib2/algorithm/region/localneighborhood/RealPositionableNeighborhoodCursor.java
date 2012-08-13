package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Cursor;

/**
 * Interface for {@link Cursor} iterating over a {@link RealPositionableAbstractNeighborhood}. 
 * Since these neighborhood are defined over a calibrated interval, cursor iterating over them
 * can return their distance measured from the neighborhood center in calibrated units.

 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 */
public interface RealPositionableNeighborhoodCursor<T> extends Cursor<T> {

	/**
	 * Return the square distance measured from the center of the neighborhood to the current
	 * cursor position, in <b>calibrated</b> units.
	 */
	public double getDistanceSquared();

}
